package org.abos.dungeon.core;

import org.abos.common.CollectionUtil;
import org.abos.common.Serializable;
import org.abos.dungeon.core.entity.Item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Inventory implements Serializable {

    public static final int DEFAULT_INVENTORY_CAPACITY = 10;

    public static final int DEFAULT_STACK_CAPACITY = 10;

    protected final Map<Item, List<Integer>> items = new HashMap<>();

    protected int inventoryCapacity;

    protected int stackCapacity;

    protected int size;

    public Inventory(final int inventoryCapacity, final int stackCapacity) {
        if (inventoryCapacity <= 0) {
            throw new IllegalArgumentException("Inventory size must be positive!");
        }
        if (stackCapacity <= 0) {
            throw new IllegalArgumentException("Stack size must be positive!");
        }
        this.inventoryCapacity = inventoryCapacity;
        this.stackCapacity = stackCapacity;
    }

    public int size() {
        return size;
    }

    protected Integer getFirstNonFullStack(List<Integer> stacks) {
        int index = 0;
        for (Integer stack : stacks) {
            if (stack < stackCapacity) { // throws NPE
                return index;
            }
            if (stack > stackCapacity) {
                throw new IllegalStateException("Overfull stack detected!");
            }
            index++;
        }
        return null;
    }

    public boolean addItem(final Item item) {
        List<Integer> stacks = items.get(item);
        // first item of kind in inventory
        if (stacks == null) {
            if (size < inventoryCapacity) {
                stacks = new LinkedList<>();
                stacks.add(1);
                items.put(item, stacks);
                size++;
                return true;
            }
            return false;
        }
        // first item of stack in inventory
        final Integer index = getFirstNonFullStack(stacks);
        if (index == null) {
            if (size < inventoryCapacity) {
                stacks.add(1);
                size++;
                return true;
            }
            return false;
        }
        // non-first item of a stack
        stacks.set(index, stacks.get(index) + 1);
        return true;
    }

    /**
     * Removes one of the specified item from the inventory.
     * @param item the item to remove
     * @param stackIndex From which stack to remove the item. Can be {@code null},
     *                   in that case the first non-full stack or (if not existent) the last full stack is chosen.
     * @return {@code true} if the item could be removed. {@code false} if not, especially because of an invalid stack index
     * or because the item wasn't in the inventory to begin with.
     */
    public boolean removeItem(final Item item, Integer stackIndex) {
        if (stackIndex != null && stackIndex < 0) {
            return false;
        }
        final List<Integer> stacks = items.get(item);
        if (stacks == null) {
            return false;
        }
        if (stackIndex != null && stacks.size() <= stackIndex) {
            return false;
        }
        if (stackIndex == null) {
            stackIndex = getFirstNonFullStack(stacks);
            // might still be null if all stacks are full
            if (stackIndex == null) {
                stackIndex = stacks.size() - 1;
            }
        }
        final int value = stacks.get(stackIndex);
        if (value == 1) {
            stacks.remove(stackIndex);
            size--;
            if (stacks.isEmpty()) {
                items.remove(item);
            }
        }
        else {
            stacks.set(stackIndex, value - 1);
        }
        return true;
    }

    /**
     * Returns an unmodifiable view of the items in this inventory.
     * @return an unmodifiable view, may be empty but not {@code null}
     * @implNote the generated view is never cached
     */
    public List<Map.Entry<Item, List<Integer>>> getItemView() {
        final Map<Item, List<Integer>> modifiableView = new HashMap<>();
        for (Map.Entry<Item, List<Integer>> entry : items.entrySet()) {
            modifiableView.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableList(CollectionUtil.getAlphabeticalOrder(modifiableView));
    }

    public int countAll(Item item) {
        return CollectionUtil.countAll(items, item);
    }

    public int countAll(Class<? extends Item> keyClass) {
        return items.entrySet().stream()
                .filter(entry -> keyClass.isInstance(entry.getKey()))
                .mapToInt(entry -> entry.getValue().size())
                .sum();
    }

    @Override
    public void writeObject(DataOutputStream dos) throws IOException {
        dos.writeInt(inventoryCapacity);
        dos.writeInt(stackCapacity);
        dos.writeInt(items.size());
        for (var entry : items.entrySet()) {
            dos.writeUTF(entry.getKey().getName());
            dos.writeInt(entry.getValue().size());
            for (Integer stack : entry.getValue()) {
                dos.writeInt(stack);
            }
        }
    }

    /**
     * Reads an {@link Inventory} instance from the specified stream.
     * @param dis the {@link DataInputStream} to read from
     * @return a new {@link Inventory} instance
     * @throws IllegalStateException If an unknown item name is encountered.
     * @throws IOException If an I/O exception occurs.
     */
    public static Inventory readObject(DataInputStream dis) throws IllegalStateException, IOException {
        final int inventoryCapacity = dis.readInt();
        final int stackCapacity = dis.readInt();
        final Inventory result = new Inventory(inventoryCapacity, stackCapacity);
        final int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            final String name = dis.readUTF();
            final Item item = CollectionUtil.getByName(Item.itemRegistry, name);
            if (item == null) {
                throw new IllegalStateException("Unknown item " + name + " encountered!");
            }
            final List<Integer> stacks = new LinkedList<>();
            final int stackCount = dis.readInt();
            for (int j = 0; j < stackCount; j++) {
                stacks.add(dis.readInt());
            }
            result.items.put(item, stacks);
        }
        return result;
    }

}
