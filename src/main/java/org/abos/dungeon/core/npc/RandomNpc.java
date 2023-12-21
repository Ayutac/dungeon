package org.abos.dungeon.core.npc;

import org.abos.common.CollectionUtil;
import org.abos.dungeon.core.Dungeon;
import org.abos.dungeon.core.Inventory;
import org.abos.dungeon.core.Player;
import org.abos.dungeon.core.Room;
import org.abos.dungeon.core.crafting.Crafting;
import org.abos.dungeon.core.entity.Item;
import org.abos.dungeon.core.entity.LivingEntity;
import org.abos.dungeon.core.reward.DefaultRewardFactory;
import org.abos.dungeon.core.task.DefaultTaskFactory;

import java.util.Random;

/**
 * An NPC that decides things randomly.
 */
public class RandomNpc extends AbstractNpc {

    protected final boolean forwardOnly;

    /**
     * Creates a new {@link RandomNpc} instance.
     *
     * @param startRoom The room the NPC starts in. Can be different from {@link Dungeon#getStartRoom()},
     *                  but shouldn't be the exit room. Not {@code null}.
     * @param inventory the player's inventory, not {@code null}
     * @param random a {@link Random} instance
     * @param forwardOnly if the NPC should never use door 0
     * @throws NullPointerException If {@code startRoom}, {@code inventory} or {@code random} refers to {@code null}.
     */
    public RandomNpc(final Room startRoom, final Inventory inventory, final Random random, final boolean forwardOnly) {
        super(startRoom, inventory, random);
        this.forwardOnly = forwardOnly;
    }

    @Override
    protected Room selectDoor() {
        final int selection = random.nextInt(forwardOnly ? 1 : 0, currentRoom.getDoorCount());
        if (currentRoom.getId() == 0 && selection == 0) {
            // otherwise the ID of the exit rooms throws an IooB
            return null;
        }
        return currentRoom.getRoomBehindDoor(selection);
    }

    @Override
    protected Item selectItem(String msg) {
        return CollectionUtil.getRandomEntry(inventory.getAllItems(), random);
    }

    /**
     * Does a test run with the NPC.
     * @param args ignored
     */
    public static void main(String[] args) {
        final Random random = new Random();
        Item.init();
        LivingEntity.init();
        Crafting.init();
        final Dungeon dungeon;
        final Player player;
        dungeon = new Dungeon(random, new DefaultTaskFactory(random), new DefaultRewardFactory(random));
        player = new RandomNpc(dungeon.getStartRoom(), new Inventory(Inventory.DEFAULT_INVENTORY_CAPACITY, Inventory.DEFAULT_STACK_CAPACITY), random, true);
        final int maxSteps = 100_000;
        int steps = 0;
        while (player.getCurrentRoom() != null && steps < maxSteps) {
            player.enterNextRoom();
            steps++;
        }
        final int tc = player.getClearedTaskCount();
        final int ms = player.getMenagerieSize();
        System.out.printf("%d task%s cleared, %d pet%s collected, highest room: %d%n", tc, tc == 1 ? "" : "s", ms, ms == 1 ? "" : "s", player.getHighestRoomNumber());
        if (steps == maxSteps) {
            System.out.println("Max steps reached!");
        }
        System.out.println(player.getInventory());
    }
}
