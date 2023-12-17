package org.abos.dungeon.cmd;

import org.abos.dungeon.core.*;

import java.util.Random;
import java.util.Scanner;

public class CmdPlayer extends Player {

    final Scanner scanner = new Scanner(System.in);

    public CmdPlayer(Room startRoom) {
        super(startRoom);
    }

    @Override
    protected Room selectDoor() {
        String selectionString;
        int selection;
        Room selectedRoom;
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
                selectedRoom = currentRoom.getRoomBehindDoor(selection);
                if (selectedRoom.isExit()) {
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
        scanner.nextLine();
    }

    @Override
    protected boolean displayQuestion(final Question question) {
        System.out.print(question.getQuestion());
        String playerAnswer = scanner.nextLine();
        return playerAnswer.equals(question.getAnswer());
    }

    public static void main(String[] args) {
        final Random random = new Random(0);
        final Dungeon dungeon = new Dungeon(random, new TaskFactory(random));
        final Player player = new CmdPlayer(dungeon.getStartRoom());
        while (player.getCurrentRoom() != null) {
            player.enterNextRoom();
        }
        System.out.printf("%d task cleared, highest room: %d%n", player.getClearedTaskCount(), player.getHighestRoomNumber());
    }
}
