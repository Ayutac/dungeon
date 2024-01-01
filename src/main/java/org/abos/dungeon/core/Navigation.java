package org.abos.dungeon.core;

import org.abos.common.ErrorUtil;
import org.abos.common.Randomizer;
import org.abos.dungeon.core.crafting.Crafting;
import org.abos.dungeon.core.entity.Item;
import org.abos.dungeon.core.entity.LivingEntity;
import org.abos.dungeon.core.reward.DefaultRewardFactory;
import org.abos.dungeon.core.task.DefaultTaskFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

public abstract class Navigation implements Runnable, Randomizer {

    protected static final String CREDITS_FILE_NAME = "credits.txt";

    /**
     * @see #random()
     */
    protected final Random random;

    protected Dungeon dungeon;

    protected Player player;

    /**
     * Creates a new {@link Navigation} instance.
     */
    public Navigation() {
        random = new Random();
        Item.init();
        LivingEntity.init();
        Crafting.init();
    }

    @Override
    public Random random() {
        return random;
    }

    protected abstract void displayInfoMessage(final String msg);

    protected abstract void displayErrorMessage(final Exception ex);

    /**
     * Displays the menu and returns the user's choice.
     * @return the menu entry selected by the user, not {@code null}
     */
    protected abstract MenuEntry displayMenu(final boolean mainMenu);

    protected void executeMenuChoice(final MenuEntry choice, final boolean mainMenu) {
        switch (choice) {
            case NEW_GAME -> {
                newGame();
                if (mainMenu) {
                    startGame();
                }
            }
            case SAVE_GAME -> saveGame();
            case LOAD_GAME -> {
                loadGame();
                if (mainMenu) {
                    startGame();
                }
            }
            case OPTIONS -> displayOptions();
            case CREDITS -> displayCredits();
            case BACK, EXIT -> {/* Do nothing. */}
            default -> ErrorUtil.unknownEnumEntry(choice);
        }
    }

    protected abstract TurnEntry displayTurnChoices();

    /**
     * Executes the turn choice.
     * @param choice the choice the player made
     * @return {@code true} if the game shall be continued, else {@code false}.
     */
    protected boolean executeTurnChoice(final TurnEntry choice) {
        switch (choice) {
            case DOOR -> {
                return player.enterNextRoom();
            }
            case CRAFT -> player.craft();
            case MENU -> {
                final MenuEntry menuChoice = displayMenu(false);
                if (menuChoice == MenuEntry.EXIT) {
                    return false;
                }
                executeMenuChoice(menuChoice, false);
            }
            default -> ErrorUtil.unknownEnumEntry(choice);
        }
        return true;
    }

    @Override
    public void run() {
        executeMenuChoice(displayMenu(true), true);
    }

    protected void startGame() {
        while (executeTurnChoice(displayTurnChoices()));
    }

    protected void newGame() {
        dungeon = new Dungeon(random, new DefaultTaskFactory(random), new DefaultRewardFactory(random));
        player = createPlayer();
    }

    protected Player createPlayer() {
        return createPlayerWith(dungeon.getStartRoom(), new Inventory(Inventory.DEFAULT_INVENTORY_CAPACITY, Inventory.DEFAULT_STACK_CAPACITY));
    }

    protected abstract Player createPlayerWith(final Room startRoom, final Inventory inventory);

    protected void saveGame() {
        final String saveGame = selectSaveGame(false);
        if (saveGame == null) {
            return;
        }
        try (final DataOutputStream dos = new DataOutputStream(new FileOutputStream(saveGame))) {
            dungeon.writeObject(dos);
            player.writeObject(dos);
            displayInfoMessage("Game saved successfully!");
        }
        catch (IOException ex) {
            displayErrorMessage(ex);
        }
    }

    protected void loadGame() {
        final String saveGame = selectSaveGame(true);
        if (saveGame == null) {
            return;
        }
        try (final DataInputStream dis = new DataInputStream(new FileInputStream(saveGame))) {
            dungeon = Dungeon.readObject(dis, random, new DefaultTaskFactory(random), new DefaultRewardFactory(random));
            player = Player.readObject(dis, dungeon, this::createPlayerWith);
            displayInfoMessage("Game loaded successfully!");
        }
        catch (IOException ex) {
            displayErrorMessage(ex);
        }
    }

    /**
     * Let the user select a save game file.
     * @param load if this is used for loading a game or not
     * @return a save game file path or {@code null} if the user changed their mind.
     */
    protected abstract String selectSaveGame(final boolean load);

    protected abstract void displayOptions();

    protected List<String> loadCredits() {
        try {
            final URL url = Navigation.class.getClassLoader().getResource(CREDITS_FILE_NAME);
            return Files.readAllLines(new File(url.getFile()).toPath());
        }
        catch (final IOException ex) {
            System.err.printf("Reading the file %s failed!%n", CREDITS_FILE_NAME);
        }
        return List.of("Credits couldn't be loaded!");
    }

    protected abstract void displayCredits();

}
