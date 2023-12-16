package org.abos.dungeon.cmd;

import org.abos.dungeon.core.Dungeon;
import org.abos.dungeon.core.Room;

import java.util.Random;
import java.util.Scanner;

public class CmdDungeon extends Dungeon {

    public CmdDungeon(final Random random) {
        super(random);
    }

    public CmdDungeon() {
        super();
    }

    @Override
    protected Room selectDoor() {
        final Scanner scanner = new Scanner(System.in);
        String selectionString;
        int selection;
        Room selectedRoom;
        while (true) {
            System.out.printf("You are in room %d. Select one door between 0 and %d: ", currentRoom.getId(), currentRoom.getDoorCount()-1);
            selectionString = scanner.nextLine();
            selectionString = selectionString.toLowerCase();
            if (selectionString.equals("no") || selectionString.equals("exit") || selectionString.equals("quit")) {
                if (leaveDungeon(scanner)) {
                    return null;
                }
                continue;
            }
            try {
                selection = Integer.parseInt(selectionString);
                if (selection == -1) {
                    if (leaveDungeon(scanner)) {
                        return null;
                    }
                    continue;
                }
                if (selection >= currentRoom.getDoorCount() || selection < 0) {
                    continue;
                }
                selectedRoom = currentRoom.getRoomBehindDoor(selection);
                if (selectedRoom.isExit()) {
                    if (leaveDungeon(scanner)) {
                        return null;
                    }
                    continue;
                }
                return currentRoom.getRoomBehindDoor(selection);
            }
            catch (NumberFormatException ex) {/* Ignore. */}
        }
    }

    protected boolean leaveDungeon(final Scanner scanner) {
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

    public static void main(String[] args) {
        final Dungeon dungeon = new CmdDungeon(new Random(0));
        while (dungeon.getCurrentRoom() != null) {
            dungeon.enterNextRoom();
        }
    }
}
