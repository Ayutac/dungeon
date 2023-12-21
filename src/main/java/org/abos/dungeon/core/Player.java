package org.abos.dungeon.core;

import org.abos.common.Serializable;
import org.abos.dungeon.core.crafting.Crafting;
import org.abos.dungeon.core.crafting.CraftingInput;
import org.abos.dungeon.core.crafting.CraftingOutput;
import org.abos.dungeon.core.entity.Creature;
import org.abos.dungeon.core.entity.Item;
import org.abos.dungeon.core.reward.Reward;
import org.abos.dungeon.core.task.Information;
import org.abos.dungeon.core.task.Question;
import org.abos.dungeon.core.task.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

/**
 * The player roaming through {@link Room Rooms} in a {@link Dungeon}.
 */
public abstract class Player implements Serializable {

    /**
     * @see #getCurrentRoom()
     */
    protected Room currentRoom;

    /**
     * The room the player was in before the current one. Might be {@code null}.
     */
    protected Room oldRoom;

    /**
     * @see #getHighestRoomNumber()
     */
    protected int highestRoomNumber;

    /**
     * A set of all {@link Task Tasks} the {@link Player} cleared, represented by the ID of the task's {@link Room}.
     */
    protected final Set<Integer> clearedTasks = new HashSet<>();

    /**
     * A set of all creatures the {@link Player} collected.
     */
    protected final List<Creature> menagerie = new LinkedList<>();

    protected final Inventory inventory;

    /**
     * Creates a new {@link Player} instance.
     * @param startRoom The room the player starts in. Can be different from {@link Dungeon#getStartRoom()},
     *                  but shouldn't be the exit room. Not {@code null}.
     * @param inventory the player's inventory, not {@code null}
     * @throws NullPointerException If {@code startRoom} or {@code inventory} refers to {@code null}.
     */
    public Player(final Room startRoom, final Inventory inventory) {
        currentRoom = Objects.requireNonNull(startRoom);
        this.inventory = Objects.requireNonNull(inventory);
    }

    /**
     * Returns the room the {@link Player} is currently in.
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Moves the player to the next room.
     */
    public void enterNextRoom() {
        currentRoom.fillDoors();
        final Room nextRoom = selectDoor();
        oldRoom = currentRoom;
        currentRoom = nextRoom;
        if (currentRoom == null) {
            return;
        }
        final Task newTask = currentRoom.getTask();
        if (newTask == null) {
            return;
        }
        if (!hasClearedTask(currentRoom.getId())) {
            newTask.accept(this);
            if (hasClearedTask(currentRoom.getId())) {
                highestRoomNumber = Math.max(highestRoomNumber, currentRoom.getId());
                collectReward();
            }
            else {
                currentRoom = oldRoom;
            }
        }
    }

    /**
     * Let the {@link Player} select the door of the {@link #getCurrentRoom()} to go through.
     * The rooms behind the doors are guaranteed to be generated
     * when this method is called.
     * @return the next room for the player; {@code null} means dungeon is to be exited
     */
    protected abstract Room selectDoor();

    /**
     * Returns the highest ID of {@link Room Rooms} the {@link Player} cleared.
     */
    public int getHighestRoomNumber() {
        return highestRoomNumber;
    }

    /**
     * If the {@link Player} has cleared the {@link Task} in the specified room.
     * @param roomNumber the room to check
     * @return {@code true} if the task has been cleared, else {@code false}.
     */
    public boolean hasClearedTask(final int roomNumber) {
        return clearedTasks.contains(roomNumber);
    }

    /**
     * Marks the {@link Task} of the current room as cleared.
     * Does nothing if the current room is {@code null}.
     */
    public void clearCurrentTask() {
        if (currentRoom == null) {
            return;
        }
        clearedTasks.add(currentRoom.getId());
    }

    /**
     * Returns how many {@link Task Tasks} the {@link Player} has currenctly cleared.
     * @return a non-negative number indicating the cleared tasks
     */
    public int getClearedTaskCount() {
        return clearedTasks.size();
    }

    /**
     * Display an {@link Information} instance to the {@link Player}.
     * @param information the information to display
     */
    public abstract void displayInformation(final Information information);

    /**
     * Display a {@link Question} instance to the {@link Player} and listens to the player's answer.
     * @param question the question to ask
     * @return {@code true} if the player successfully answered the question, else {@code false}.
     */
    public abstract boolean displayQuestion(final Question question);

    public void craft() {
        displayCraftingIngredients();
        final Item input1 = selectItem("First ingredient?");
        if (input1 == null) {
            displayInformation(new Information(("This item doesn't exist!")));
            return;
        }
        final Item input2 = selectItem("Second ingredient?");
        if (input2 == null) {
            displayInformation(new Information(("This item doesn't exist!")));
            return;
        }
        final CraftingInput input = new CraftingInput(input1, input2);
        if (!inventory.contains(input)) {
            displayInformation(new Information("You don't have these ingredients!"));
            return;
        }
        final CraftingOutput output = Crafting.RECIPES.get(input);
        if (output == null) {
            // TODO display crafting diss
            return;
        }
        if (!inventory.removeAll(input)) {
            throw new AssertionError("Ingredients have vanished!");
        }
        displayCraftingResult(output);
        if (!inventory.addAll(output)) {
            // TODO notify player part of output has been lost
        }
    }

    protected abstract void displayCraftingIngredients();

    protected abstract void displayCraftingResult(final CraftingOutput output);

    protected abstract Item selectItem(final String msg);

    /**
     * Collect the reward in the room if there is one to collect.
     */
    protected void collectReward() {
        final Reward reward = currentRoom.awardReward(this);
        if (reward != null) {
            switch (reward.type()) {
                case CREATURE -> {
                    for (int i = 0; i < reward.amount(); i++) {
                        menagerie.add((Creature)reward.entity());
                    }
                    displayRewardAcquisition(reward, 0);

                }
                case ITEM -> {
                    int lostAmount = 0;
                    for (int i = 0; i < reward.amount(); i++) {
                        if (!inventory.addItem((Item)reward.entity())) {
                            lostAmount++;
                        }
                    }
                    displayRewardAcquisition(reward, lostAmount);
                }
                default -> throw new AssertionError("Unknown type " + reward.type() + " encountered!");
            }
        }
    }

    /**
     * Displays the acquisition of a reward to the {@link Player}.
     */
    protected abstract void displayRewardAcquisition(final Reward reward, final int lostAmount);

    public abstract void displayInventory(final Inventory inventory);

    public abstract void displayMenagerie();

    /**
     * Returns the number of creatures the {@link Player} has currently in his possession.
     * @return a non-negative number indicating the collected creatures
     */
    public int getMenagerieSize() {
        return menagerie.size();
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void writeObject(final DataOutputStream dos) throws IOException {
        dos.writeInt(oldRoom.getId());
        dos.writeInt(highestRoomNumber);
        dos.writeInt(clearedTasks.size());
        for (Integer clearedTask : clearedTasks) {
            dos.writeInt(clearedTask);
        }
        dos.writeInt(menagerie.size());
        for (Creature creature : menagerie) {
            creature.writeObject(dos);
        }
        inventory.writeObject(dos);
    }

    /**
     * Reads an {@link Player} instance from the specified stream.
     * @param dis the {@link DataInputStream} to read from
     * @param dungeon the {@link Dungeon} this player roams in
     * @param constructor a constructor for a concrete player subclass
     * @return a new {@link Player} instance
     * @throws IOException If an I/O exception occurs.
     */
    public static Player readObject(final DataInputStream dis, final Dungeon dungeon, final BiFunction<Room, Inventory, Player> constructor) throws IOException {
        final int currentRoom = dis.readInt();
        final int highestRoomNumber = dis.readInt();
        final int taskCount = dis.readInt();
        final Set<Integer> clearedTasks = new HashSet<>();
        for (int i = 0; i < taskCount; i++) {
            clearedTasks.add(dis.readInt());
        }
        final int creatureCount = dis.readInt();
        final Set<Creature> creatures = new LinkedHashSet<>();
        for (int i = 0; i < creatureCount; i++) {
            creatures.add(Creature.readObject(dis));
        }
        final Inventory inventory = Inventory.readObject(dis);
        final Player result = constructor.apply(dungeon.getRoom(currentRoom), inventory);
        result.highestRoomNumber = highestRoomNumber;
        result.clearedTasks.addAll(clearedTasks);
        result.menagerie.addAll(creatures);
        return result;
    }
    
}
