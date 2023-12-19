package org.abos.dungeon.cmd;

import org.abos.dungeon.core.*;
import org.abos.dungeon.core.entity.Item;
import org.abos.dungeon.core.entity.Pet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class CmdPlayer extends Player {
    
    protected static final String HAMSTER_ACQUISITION_MSG = Player.HAMSTER_ACQUISITION_MSG + " ";

    final Scanner scanner = new Scanner(System.in);

    public CmdPlayer(final Room startRoom, final Inventory inventory) {
        super(startRoom, inventory);
    }

    @Override
    protected Room selectDoor() {
        String selectionString;
        int selection;
        while (true) {
            System.out.printf("You are in room %d. Select one door between 0 and %d: ", currentRoom.getId(), currentRoom.getDoorCount()-1);
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

    @Override
    protected void displayInformation(Information information) {
        System.out.print(information.getText());
        System.out.print(' ');
        scanner.nextLine();
    }

    @Override
    protected boolean displayQuestion(final Question question) {
        System.out.print(question.getQuestion());
        System.out.print(' ');
        String playerAnswer = scanner.nextLine();
        return playerAnswer.equals(question.getAnswer());
    }

    @Override
    protected void displayHamsterAcquisition() {
        System.out.print(CmdPlayer.HAMSTER_ACQUISITION_MSG);
        scanner.nextLine();
    }

    public static void main(String[] args) throws IOException {
        final String saveFilePath = "game.sav";
        final Random random = new Random(0);
        final Dungeon dungeon;
        final Player player;
        Item.init();
        try (final DataInputStream dis = new DataInputStream(new FileInputStream(saveFilePath))) {
            dungeon = Dungeon.readObject(dis, random, new TaskFactory(random));
            player = Player.readObject(dis, dungeon, CmdPlayer::new);
//            dungeon = new Dungeon(random, new TaskFactory(random));
//            player = new CmdPlayer(dungeon.getStartRoom(), new Inventory(Inventory.DEFAULT_INVENTORY_CAPACITY, Inventory.DEFAULT_STACK_CAPACITY));
        }
        while (player.getCurrentRoom() != null) {
            player.enterNextRoom();
            try (final DataOutputStream dos = new DataOutputStream(new FileOutputStream(saveFilePath))) {
                dungeon.writeObject(dos);
                player.writeObject(dos);
            }
        }
        final int tc = player.getClearedTaskCount();
        final int pc = player.getInventory().countAll(Pet.class);
        System.out.printf("%d task%s cleared, %d pet%s collected, highest room: %d%n", tc, tc == 1 ? "" : "s", pc, pc == 1 ? "" : "s", player.getHighestRoomNumber());
    }
}
