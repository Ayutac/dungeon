package org.abos.dungeon.cmd;

import org.abos.common.CollectionUtil;
import org.abos.dungeon.core.*;
import org.abos.dungeon.core.crafting.Crafting;
import org.abos.dungeon.core.crafting.CraftingOutput;
import org.abos.dungeon.core.entity.Item;
import org.abos.dungeon.core.entity.LivingEntity;
import org.abos.dungeon.core.reward.DefaultRewardFactory;
import org.abos.dungeon.core.reward.Reward;
import org.abos.dungeon.core.task.Information;
import org.abos.dungeon.core.task.Question;
import org.abos.dungeon.core.task.DefaultTaskFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A command line implementation of the {@link Player}.
 */
public class CmdPlayer extends Player {

    /**
     * The CMD input.
     */
    protected final Scanner scanner = new Scanner(System.in);

    /**
     * Creates a new {@link CmdPlayer} instance.
     * @param startRoom The room the player starts in. Can be different from {@link Dungeon#getStartRoom()},
     *                  but shouldn't be the exit room. Not {@code null}.
     * @param inventory the player's inventory, not {@code null}
     * @throws NullPointerException If {@code startRoom} or {@code inventory} refers to {@code null}.
     */
    public CmdPlayer(final Room startRoom, final Inventory inventory) {
        super(startRoom, inventory);
    }

    @Override
    protected Room selectDoor() {
        String selectionString;
        int selection;
        while (true) {
            System.out.printf("Select one door between 0 and %d: ", currentRoom.getDoorCount()-1);
            selectionString = scanner.nextLine();
            selectionString = selectionString.toLowerCase();
            if (selectionString.equals("no") || selectionString.equals("exit") || selectionString.equals("quit")) {
                if (leaveDungeon()) {
                    return null;
                }
                continue;
            }
            try {
                selection = Integer.parseInt(selectionString);
                if (selection == -1) {
                    if (leaveDungeon()) {
                        return null;
                    }
                    continue;
                }
                if (selection >= currentRoom.getDoorCount() || selection < 0) {
                    continue;
                }
                if (selection == Room.RETURN_ID && currentRoom.getId() == Room.START_ID) {
                    if (leaveDungeon()) {
                        return null;
                    }
                    continue;
                }
                return currentRoom.getRoomBehindDoor(selection);
            }
            catch (NumberFormatException ex) {/* Ignore. */}
        }
    }

    /**
     * Prompts the player to leave the dungeon.
     * @return {@code true} if the player wants to leave the dungeon, else {@code false}
     */
    protected boolean leaveDungeon() {
        String answer;
        System.out.print("Really leave the dungeon? (Y/N) ");
        while (true) {
            answer = scanner.nextLine().toLowerCase();
            if (answer.equals("y")) {
                return true;
            }
            if (answer.equals("n")) {
                return false;
            }
        }
    }

    private String getInputAfterText() {
        System.out.print(' ');
        return scanner.nextLine();
    }

    private String displayText(final String text, final boolean requireConfirmation) {
        System.out.print(text);
        if (requireConfirmation) {
            return getInputAfterText();
        }
        return null;
    }

    @Override
    public void displayInformation(Information information) {
        displayText(information.getText(), true);
    }

    @Override
    public boolean displayQuestion(final Question question) {
        String playerAnswer = displayText(question.getQuestion(), true);
        return playerAnswer.equals(question.getAnswer());
    }

    @Override
    protected void displayRewardAcquisition(final Reward reward, final int lostAmount) {
        if (lostAmount == 0) {
            System.out.printf(Reward.PREFORMATTED_REWARD_MSG, reward.entity().getName(), reward.amount());
        }
        else {
            System.out.printf(Reward.PREFORMATTED_REWARD_WITH_LOSS_MSG, reward.entity().getName(), reward.amount(), lostAmount);
        }
        getInputAfterText();
    }

    private void internalDisplayInventory(final Inventory inventory) {
        if (inventory.isEmpty()) {
            System.out.print("Inventory is empty!");
        }
        else {
            final String preformatted = "%s: %d";
            System.out.printf(StreamSupport.stream(inventory.spliterator(), false)
                    .map(s -> String.format(preformatted, s.item().getName(), s.amount()))
                    .collect(Collectors.joining("%n")));
        }
    }

    @Override
    public void displayInventory(final Inventory inventory) {
        internalDisplayInventory(inventory);
        getInputAfterText();
    }

    @Override
    public void displayMenagerie() {
        if (menagerie.isEmpty()) {
            System.out.print("Menagerie is empty!");
        }
        else {
            final String preformatted = "%s (%d/%d): %s";
            System.out.printf(menagerie.stream()
                    .map(c -> String.format(preformatted, c.getName(), c.getCurrentHealthPoints(), c.getMaxHealthPoints(), c.getDescription()))
                    .collect(Collectors.joining("%n")));
        }
        getInputAfterText();
    }

    @Override
    public void displayCraftingIngredients() {
        internalDisplayInventory(inventory);
        System.out.println();
    }

    @Override
    protected void displayCraftingResult(final CraftingOutput output) {
        System.out.println("You crafted:");
        internalDisplayInventory(output);
        getInputAfterText();
    }

    @Override
    protected Item selectItem(final String msg) {
        return CollectionUtil.getByName(Item.REGISTRY, displayText(msg, true));
    }

    public static void main(String[] args) throws IOException {
        final String saveFilePath = "game.sav";
        final Random random = new Random(0);
        Item.init();
        LivingEntity.init();
        Crafting.init();
        final Dungeon dungeon;
        final Player player;
        boolean testGame = true;
        if (testGame) {
            try (final DataInputStream dis = new DataInputStream(new FileInputStream(saveFilePath))) {
                dungeon = Dungeon.readObject(dis, random, new DefaultTaskFactory(random), new DefaultRewardFactory(random));
                player = Player.readObject(dis, dungeon, CmdPlayer::new);
//                dungeon = new Dungeon(random, new DefaultTaskFactory(random), new DefaultRewardFactory(random));
//                player = new CmdPlayer(dungeon.getStartRoom(), new Inventory(Inventory.DEFAULT_INVENTORY_CAPACITY, Inventory.DEFAULT_STACK_CAPACITY));
            }
            while (player.getCurrentRoom() != null) {
                player.enterNextRoom();
                try (final DataOutputStream dos = new DataOutputStream(new FileOutputStream(saveFilePath))) {
                    dungeon.writeObject(dos);
                    player.writeObject(dos);
                }
            }
            final int tc = player.getClearedTaskCount();
            final int ms = player.getMenagerieSize();
            System.out.printf("%d task%s cleared, %d pet%s collected, highest room: %d%n", tc, tc == 1 ? "" : "s", ms, ms == 1 ? "" : "s", player.getHighestRoomNumber());
            player.displayMenagerie();
            player.displayInventory(player.getInventory());
        }
        else {
            dungeon = new Dungeon(random, new DefaultTaskFactory(random), new DefaultRewardFactory(random));
            player = new CmdPlayer(dungeon.getStartRoom(), new Inventory(Inventory.DEFAULT_INVENTORY_CAPACITY, Inventory.DEFAULT_STACK_CAPACITY));
            player.getInventory().addItem(CollectionUtil.getByName(Item.REGISTRY, "Raspberry"));
            player.getInventory().addItem(CollectionUtil.getByName(Item.REGISTRY, "Stick"));
            player.craft();
        }
    }
}
