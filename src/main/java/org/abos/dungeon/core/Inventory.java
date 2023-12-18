package org.abos.dungeon.core;

import org.abos.common.CollectionUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Inventory {

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

}
