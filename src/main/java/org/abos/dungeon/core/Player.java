package org.abos.dungeon.core;

import org.abos.common.Serializable;

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
     * Message to display when a hamster is found.
     */
    protected static final String HAMSTER_ACQUISITION_MSG = "You pick up a hamster you found in the room. Hello little friend!";

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
     * A set of all hamsters the {@link Player} collected, represented by the ID of the hamster's {@link Room}.
     */
    protected final Set<Integer> collectedHamsters = new HashSet<>();

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
                collectHamster();
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
    protected abstract void displayInformation(final Information information);

    /**
     * Display a {@link Question} instance to the {@link Player} and listens to the player's answer.
     * @param question the question to ask
     * @return {@code true} if the player successfully answered the question, else {@code false}.
     */
    protected abstract boolean displayQuestion(final Question question);

    /**
     * Collect the hamster in the room if there is one to collect.
     */
    protected void collectHamster() {
        if (currentRoom.awardHamster()) {
            collectedHamsters.add(currentRoom.getId());
            displayHamsterAcquisition();
        }
    }

    /**
     * Displays the acquisition of a hamster to the {@link Player}.
     */
    protected abstract void displayHamsterAcquisition();

    /**
     * Returns the number of hamsters the {@link Player} has currently in his possession.
     * @return a non-negative number indicating the collected hamsters
     */
    public int getHamsterCount() {
        return collectedHamsters.size();
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
        dos.writeInt(collectedHamsters.size());
        for (Integer hamster : collectedHamsters) {
            dos.writeInt(hamster);
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
        final int hamsterCount = dis.readInt();
        final Set<Integer> hamsters = new HashSet<>();
        for (int i = 0; i < hamsterCount; i++) {
            hamsters.add(dis.readInt());
        }
        final Inventory inventory = Inventory.readObject(dis);
        final Player result = constructor.apply(dungeon.getRoom(currentRoom), inventory);
        result.highestRoomNumber = highestRoomNumber;
        result.clearedTasks.addAll(clearedTasks);
        result.collectedHamsters.addAll(hamsters);
        return result;
    }
    
}
