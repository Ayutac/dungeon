package org.abos.dungeon.core.entity;

import java.util.Objects;

/**
 * Depicts armor {@link Thing Things} that can be worn by the player.
 */
public class Armor extends Thing {

    /**
     * The file name of the list of armor.
     */
    public static final String LIST_FILE_NAME = "armorList.csv";

    /**
     * @see #getSlot()
     */
    protected final ArmorSlot slot;

    /**
     * @see #getDefenseBonus()
     */
    protected final int defenseBonus;

    /**
     * Creates a new {@link Armor} instance.
     * @param name the name of this armor
     * @param description the description of this armor
     * @param slot the slot for this armor
     * @param defenseBonus the defense bonus of this armor
     * @throws NullPointerException If any parameter refers to {@code null}.
     */
    public Armor(final String name, final String description, final ArmorSlot slot, final int defenseBonus) {
        super(name, description);
        this.slot = Objects.requireNonNull(slot);
        this.defenseBonus = defenseBonus;
    }

    /**
     * Returns the {@link ArmorSlot} of this armor
     * @return the armor slot, not {@code null}
     */
    public ArmorSlot getSlot() {
        return slot;
    }

    /**
     * Returns the defense bonus of this armor.
     * @return the defense bonus
     */
    public int getDefenseBonus() {
        return defenseBonus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Armor armor)) return false;
        if (!super.equals(o)) return false;
        return defenseBonus == armor.defenseBonus && slot == armor.slot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), slot, defenseBonus);
    }
}
