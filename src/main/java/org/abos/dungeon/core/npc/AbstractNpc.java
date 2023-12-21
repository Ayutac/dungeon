package org.abos.dungeon.core.npc;

import org.abos.dungeon.core.Dungeon;
import org.abos.dungeon.core.Inventory;
import org.abos.dungeon.core.Player;
import org.abos.dungeon.core.Room;
import org.abos.dungeon.core.crafting.CraftingOutput;
import org.abos.dungeon.core.reward.Reward;
import org.abos.dungeon.core.task.Information;
import org.abos.dungeon.core.task.Question;

import java.util.Objects;
import java.util.Random;

/**
 * NPC template of a {@link Player}.
 */
public abstract class AbstractNpc extends Player {

    protected final Random random;

    /**
     * Creates a new {@link Player} instance.
     *
     * @param startRoom The room the player starts in. Can be different from {@link Dungeon#getStartRoom()},
     *                  but shouldn't be the exit room. Not {@code null}.
     * @param inventory the player's inventory, not {@code null}
     * @param random a {@link Random} instance
     * @throws NullPointerException If {@code startRoom}, {@code inventory} or {@code random} refers to {@code null}.
     */
    public AbstractNpc(final Room startRoom, final Inventory inventory, final Random random) {
        super(startRoom, inventory);
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Returns the {@link Random} instance of this NPC.
     * @return a random instance, not {@code null}
     */
    public Random random() {
        return random;
    }

    @Override
    public void displayInformation(Information information) {
        /* Empty on purpose. */
    }

    @Override
    public boolean displayQuestion(Question question) {
        // assume NPC answered correctly
        return true;
    }

    @Override
    protected void displayCraftingIngredients() {
        /* Empty on purpose. */
    }

    @Override
    protected void displayCraftingResult(CraftingOutput output) {
        /* Empty on purpose. */
    }

    @Override
    protected void displayRewardAcquisition(Reward reward, int lostAmount) {
        /* Empty on purpose. */
    }

    @Override
    public void displayInventory(Inventory inventory) {
        /* Empty on purpose. */
    }

    @Override
    public void displayMenagerie() {
        /* Empty on purpose. */
    }
}
