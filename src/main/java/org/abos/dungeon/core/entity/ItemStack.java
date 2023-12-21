package org.abos.dungeon.core.entity;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Immutable item stacks.
 * @param item an item, not {@code null}
 * @param amount the amount, must be positive
 */
public record ItemStack(Item item, int amount) implements Iterable<Item> {

    public ItemStack(final Item item, final int amount) {
        this.item = Objects.requireNonNull(item);
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive!");
        }
        this.amount = amount;
    }

    @Override
    public Iterator<Item> iterator() {
        return new Iterator<>() {
            int remaining = amount;
            @Override
            public boolean hasNext() {
                return remaining != 0;
            }

            @Override
            public Item next() {
                if (remaining == 0) {
                    throw new NoSuchElementException("Last element had been reached!");
                }
                remaining--;
                return item;
            }
        };
    }
}
