package org.abos.dungeon.core.reward;

import org.abos.dungeon.core.entity.Creature;
import org.abos.dungeon.core.entity.Entity;
import org.abos.dungeon.core.entity.Item;
import org.abos.dungeon.core.entity.PermanentUpgrade;

import java.util.Objects;

public enum RewardType {

    ITEM(Item.class),
    CREATURE(Creature.class),
    PERMANENT_UPGRADE(PermanentUpgrade.class);

    private final Class<? extends Entity> entityClass;

    RewardType(final Class<? extends Entity> entityClass) {
        this.entityClass = Objects.requireNonNull(entityClass);
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }
}
