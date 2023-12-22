package org.abos.dungeon.cmd;

import org.abos.dungeon.core.Inventory;
import org.abos.dungeon.core.Navigation;
import org.abos.dungeon.core.Player;
import org.abos.dungeon.core.Room;

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
    public void run() {
    }

    /**
     * Runs the game.
     * @param args ignored
     */
    public static void main(String[] args) {
        new CmdNavigation().run();
    }

}
