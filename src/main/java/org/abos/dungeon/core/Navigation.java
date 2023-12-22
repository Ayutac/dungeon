package org.abos.dungeon.core;

import org.abos.common.Randomizer;
import org.abos.dungeon.core.crafting.Crafting;
import org.abos.dungeon.core.entity.Item;
import org.abos.dungeon.core.entity.LivingEntity;
import org.abos.dungeon.core.reward.DefaultRewardFactory;
import org.abos.dungeon.core.task.DefaultTaskFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public abstract class Navigation implements Runnable, Randomizer {

    /**
     * @see #random()
     */
    protected final Random random;

    protected Dungeon dungeon;

    protected Player player;

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

}
