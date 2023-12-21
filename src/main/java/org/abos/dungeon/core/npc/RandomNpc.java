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

    /**
     * Creates a new {@link Player} instance.
     *
     * @param startRoom The room the player starts in. Can be different from {@link org.abos.dungeon.core.Dungeon#getStartRoom()},
     *                  but shouldn't be the exit room. Not {@code null}.
     * @param inventory the player's inventory, not {@code null}
     * @param random a {@link Random} instance
     * @throws NullPointerException If {@code startRoom}, {@code inventory} or {@code random} refers to {@code null}.
     */
    public RandomNpc(final Room startRoom, final Inventory inventory, final Random random) {
        super(startRoom, inventory, random);
    }

    @Override
    protected Room selectDoor() {
        final int selection = random.nextInt(currentRoom.getDoorCount());
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

    public static void main(String[] args) {
        final Random random = new Random();
        Item.init();
        LivingEntity.init();
        Crafting.init();
        final Dungeon dungeon;
        final Player player;
        dungeon = new Dungeon(random, new DefaultTaskFactory(random), new DefaultRewardFactory(random));
        player = new RandomNpc(dungeon.getStartRoom(), new Inventory(Inventory.DEFAULT_INVENTORY_CAPACITY, Inventory.DEFAULT_STACK_CAPACITY), random);
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
