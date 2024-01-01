package org.abos.dungeon.cmd;

import org.abos.dungeon.core.Inventory;
import org.abos.dungeon.core.MenuEntry;
import org.abos.dungeon.core.Navigation;
import org.abos.dungeon.core.Player;
import org.abos.dungeon.core.Room;
import org.abos.dungeon.core.TurnEntry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class CmdNavigation extends Navigation {

    protected final Scanner scanner = new Scanner(System.in);

    @Override
    protected void displayInfoMessage(String msg) {
        System.out.println(msg);
    }

    @Override
    protected void displayErrorMessage(final Exception ex) {
        ex.printStackTrace();
    }

    @Override
    protected MenuEntry displayMenu(boolean mainMenu) {
        MenuEntry choice = null;
        while (choice == null) {
            for (MenuEntry entry : MenuEntry.values()) {
                if (mainMenu && (entry == MenuEntry.SAVE_GAME || entry == MenuEntry.BACK)) {
                    continue;
                }
                System.out.printf("%d - %s%n", entry.ordinal(), entry.getName());
            }
            System.out.print("Choose menu entry: ");
            try {
                choice = MenuEntry.values()[Integer.parseInt(scanner.nextLine())];
            }
            catch (NumberFormatException | IndexOutOfBoundsException ex) {/* Ignore. */}
        }
        return choice;
    }

    @Override
    protected TurnEntry displayTurnChoices() {
        TurnEntry choice = null;
        while (choice == null) {
            for (TurnEntry entry : TurnEntry.values()) {
                System.out.printf("%d - %s%n", entry.ordinal(), entry.getName());
            }
            System.out.print("Choose turn action: ");
            try {
                choice = TurnEntry.values()[Integer.parseInt(scanner.nextLine())];
            }
            catch (NumberFormatException | IndexOutOfBoundsException ex) {/* Ignore. */}
        }
        return choice;
    }

    @Override
    protected Player createPlayerWith(final Room startRoom, final Inventory inventory) {
        return new CmdPlayer(startRoom, inventory);
    }

    @Override
    protected String selectSaveGame(boolean load) {
        System.out.print("Select save file: ");
        final String file = scanner.nextLine();
        final Path path = Path.of(file);
        if (load && !Files.isReadable(path)) {
            System.err.println("File cannot be opened!");
            return null;
        }
        if (!load && Files.isReadable(path)) {
            System.out.print("Really overwrite? (Y/N) ");
            if (!scanner.nextLine().equalsIgnoreCase("y")) {
                return null;
            }
        }
        return file;
    }

    @Override
    protected void loadGame() {
        super.loadGame();
        System.out.printf("You are in room %d.%n", player.getCurrentRoom().getId());
    }

    @Override
    protected void displayOptions() {
        System.out.println("No options yet!");
    }

    @Override
    protected void displayCredits() {
        loadCredits().forEach(System.out::println);
    }

    /**
     * Runs the game.
     * @param args ignored
     */
    public static void main(String[] args) {
        new CmdNavigation().run();
    }

}
