package org.abos.dungeon.core.entity;

import java.util.Objects;

/**
 * Immutable item stacks.
 * @param item an item, not {@code null}
 * @param amount the amount, must be positive
 */
public record ItemStack(Item item, int amount) {

    public ItemStack(final Item item, final int amount) {
        this.item = Objects.requireNonNull(item);
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive!");
        }
        this.amount = amount;
    }

}
