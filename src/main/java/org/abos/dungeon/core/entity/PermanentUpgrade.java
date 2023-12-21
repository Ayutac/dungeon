package org.abos.dungeon.core.entity;

import org.abos.common.ErrorUtil;
import org.abos.dungeon.core.Player;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Permanent upgrades for the player.
 */
public enum PermanentUpgrade implements Entity, Consumer<Player> {

    /**
     * Upgrade for inventory size.
     */
    INVENTORY_SIZE("Inventory Size Upgrade", "Increases the size of the inventory by 1."),

    /**
     * Upgrade for stack size in inventory.
     */
    STACK_SIZE("Inventory Stack Size Upgrade", "Increases the stack size of the inventory by 1.");

    /**
     * @see #getName()
     */
    private final String name;

    /**
     * @see #getDescription()
     */
    private final String description;

    /**
     * Creates a new {@link PermanentUpgrade} instance.
     * @param name the name of the upgrade, not {@code null}
     * @param description the description of the upgrade, not {@code null}
     * @throws NullPointerException If {@code name} or {@code description} refers to {@code null}.
     */
    PermanentUpgrade(final String name, final String description) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void accept(final Player player) {
        switch (this) {
            case INVENTORY_SIZE -> player.getInventory().increaseCapacity();
            case STACK_SIZE -> player.getInventory().increaseStackCapacity();
            default -> ErrorUtil.unknownEnumEntry(this);
        }
    }
}
