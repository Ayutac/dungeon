package org.abos.dungeon.core.entity;

import org.abos.dungeon.core.Player;

import java.util.Objects;

/**
 * Depicts weapon {@link Thing Things} that can be used by the player.
 */
public class Weapon extends Thing {

    /**
     * The file name of the list of weapons.
     */
    public static final String LIST_FILE_NAME = "weaponList.csv";

    protected final WeaponSlot slot;

    protected final int power;

    protected final Item ammunition;

    /**
     * Creates a new {@link Weapon} instance.
     *
     * @param name the name of this weapon, not {@code null}
     * @param description the description of this weapon, not {@code null}
     * @param slot the slot this weapon can be used in, not {@code null}
     * @param power the power of this weapon, negative means healing
     * @param ammunition what item is used up to use this weapon, can be {@code null}
     * @throws NullPointerException If any parameter refers to {@code null}.
     */
    public Weapon(String name, String description, WeaponSlot slot, int power, Item ammunition) {
        super(name, description);
        this.slot = Objects.requireNonNull(slot);
        this.power = power;
        this.ammunition = ammunition;
    }

    /**
     * Returns the weapon slot of this weapon
     * @return the weapon slot, not {@code null}
     */
    public WeaponSlot getSlot() {
        return slot;
    }

    /**
     * Returns the power of this weapon
     * @return the power
     */
    public int getPower() {
        return power;
    }

    /**
     * Returns the ammunition type of this weapon. {@code null} means this weapon doesn't need ammunition.
     * @return the ammunition type
     */
    public Item getAmmunition() {
        return ammunition;
    }

    /**
     * If this weapon can be equipped by the player.
     * @param player the player to test for, not {@code null}
     * @return {@code true} if the player can equip this weapon, else {@code false}
     * @see #canBeUsed(Player)
     */
    public boolean canBeEquipped(final Player player) {
        Objects.requireNonNull(player);
        return false;
    }

    /**
     * If this weapon can be used by the player. Mostly a test to see if ammunition is available.
     * @param player the player to test for, not {@code null}
     * @return if this player can use this weapon, assuming it would be equipped
     * @see #canBeEquipped(Player)
     */
    public boolean canBeUsed(final Player player) {
        Objects.requireNonNull(player);
        if (ammunition == null) {
            return true;
        }
        return player.getInventory().countAll(ammunition) > 0;
    }
}
