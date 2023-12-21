package org.abos.dungeon.core;

import org.abos.common.CollectionUtil;
import org.abos.common.Serializable;
import org.abos.dungeon.core.entity.Item;
import org.abos.dungeon.core.entity.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Inventory implements Iterable<ItemStack>, Serializable {

    public static final int DEFAULT_INVENTORY_CAPACITY = 10;

    public static final int DEFAULT_STACK_CAPACITY = 10;

    protected final Map<Item, List<Integer>> items = new HashMap<>();

    protected int inventoryCapacity;

    protected int stackCapacity;

    protected int size;

    protected boolean locked;

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

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
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

    /**
     * Adds the specified item to the inventory if possible.
     * @param item the item to add, not {@code null}
     * @return {@code true} if there was space to add the item successfully, else {@code false}
     * @throws NullPointerException If {@code item} refers to {@code null}.
     * @throws IllegalStateException If adding an item was attempted while this inventory is locked.
     */
    public boolean addItem(final Item item) {
        Objects.requireNonNull(item);
        if (isLocked()) {
            throw new IllegalStateException("Inventory is currently locked!");
        }
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
     * Adds all the item stacks to this inventory if possible.
     * @param stacks the stacks to add
     * @return {@code true} if all items of all stacks have been added successfully, else {@code false}
     * @throws NullPointerException If {@code stacks} or any of its elements refers to {@code null}
     * @throws IllegalStateException If adding items was attempted while this inventory is locked.
     */
    public boolean addAll(final Iterable<ItemStack> stacks) {
        boolean addedAll = true;
        for (ItemStack stack : stacks) {
            for (Item item : stack) {
                addedAll &= addItem(item);
            }
        }
        return addedAll;
    }

    /**
     * Removes one of the specified item from the inventory if possible.
     * @param item The item to remove. {@code null} means the method will return immediately without changing the inventory.
     * @param stackIndex From which stack to remove the item. Can be {@code null},
     *                   in that case the first non-full stack or (if not existent) the last full stack is chosen.
     * @return {@code true} if the item could be removed. {@code false} if not, especially because of an invalid stack index
     * or because the item wasn't in the inventory to begin with.
     * @throws IllegalStateException If removing an item was attempted while this inventory is locked.
     */
    public boolean removeItem(final Item item, Integer stackIndex) {
        if (isLocked()) {
            throw new IllegalStateException("Inventory is currently locked!");
        }
        if (item == null || (stackIndex != null && stackIndex < 0)) {
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
     * Removes all the item stacks to this inventory if possible.
     * @param stacks the stacks to remove
     * @return {@code true} if all items of all stacks have been removed successfully, else {@code false}
     * @throws NullPointerException If {@code stacks} or any of its elements refers to {@code null}
     * @throws IllegalStateException If removing items was attempted while this inventory is locked.
     */
    public boolean removeAll(Iterable<ItemStack> stacks) {
        boolean removedAll = true;
        for (ItemStack stack : stacks) {
            for (Item item : stack) {
                removedAll &= removeItem(item, null);
            }
        }
        return removedAll;
    }

    /**
     * Returns an unmodifiable view of the items in this inventory.
     * @return an unmodifiable view, may be empty but not {@code null}
     * @implNote the generated view is never cached
     */
    public List<ItemStack> getItemView() {
        return CollectionUtil.getAlphabeticalOrder(items).stream()
                .flatMap(entry -> entry.getValue().stream().map(amount -> new ItemStack(entry.getKey(), amount)))
                .toList();
    }

    /**
     * Returns an iterator over the inventory in form of {@link ItemStack item stacks}.
     * The remove operation is not supported. Changing the inventory during the use of
     * this iterator leaves the iterator unchanged.
     * @return an iterator over the inventory, not {@code null}
     * @implNote Simply calls {@link Iterable#iterator()} on {@link #getItemView()}, so it's a pretty expensive operation.
     */
    @Override
    public Iterator<ItemStack> iterator() {
        return getItemView().iterator();
    }

    public int countAll(final Item item) {
        return CollectionUtil.countAll(items, item);
    }

    public int countAll(final String itemName) {
        return countAll(CollectionUtil.getByName(Item.REGISTRY, itemName));
    }

    public int countAll(final Class<? extends Item> keyClass) {
        return items.entrySet().stream()
                .filter(entry -> keyClass.isInstance(entry.getKey()))
                .mapToInt(entry -> entry.getValue().size())
                .sum();
    }

    public boolean contains(final Inventory other) {
        for (Item item : other.items.keySet()) {
            if (countAll(item) < other.countAll(item)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory that)) return false;
        return inventoryCapacity == that.inventoryCapacity && stackCapacity == that.stackCapacity && items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, inventoryCapacity, stackCapacity);
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
            final Item item = CollectionUtil.getByName(Item.REGISTRY, name);
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
        result.size = size;
        return result;
    }

}
