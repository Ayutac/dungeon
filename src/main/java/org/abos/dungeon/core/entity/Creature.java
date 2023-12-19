package org.abos.dungeon.core.entity;

import java.util.Objects;

public class Creature extends AbstractEntity implements LivingEntity {

    public static final String LIST_FILE_NAME = "creatureList.csv";

    protected final int maxHp;

    protected int currentHp;

    public Creature(String name, String description, int maxHp) {
        super(name, description);
        if (maxHp < 0) {
            throw new IllegalArgumentException("Max health points must be positive!");
        }
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        LivingEntity.livingEntityRegistry.add(this);
    }

    @Override
    public int getMaxHealthPoints() {
        return maxHp;
    }

    @Override
    public int getCurrentHealthPoints() {
        return currentHp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Creature creature)) return false;
        if (!super.equals(o)) return false;
        return maxHp == creature.maxHp && currentHp == creature.currentHp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maxHp, currentHp);
    }
}
