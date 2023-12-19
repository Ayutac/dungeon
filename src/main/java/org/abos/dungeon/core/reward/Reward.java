package org.abos.dungeon.core.reward;

import org.abos.common.CollectionUtil;
import org.abos.common.Serializable;
import org.abos.dungeon.core.entity.Creature;
import org.abos.dungeon.core.entity.Entity;
import org.abos.dungeon.core.entity.Item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public record Reward(RewardType type, Entity entity, int amount) implements Serializable {

    public static final String PREFORMATTED_REWARD_MSG = "Reward: %s (x%d)";

    public static final String PREFORMATTED_REWARD_WITH_LOSS_MSG = "Reward: %s (x%d), but x%d were lost due to full inventory.";

    public Reward(final RewardType type, final Entity entity, final int amount) {
        this.type = Objects.requireNonNull(type);
        if (!type.getEntityClass().isInstance(Objects.requireNonNull(entity))) {
            throw new IllegalArgumentException("Type " + type + " doesn't match " + entity);
        }
        this.entity = entity;
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive!");
        }
        this.amount = amount;
    }

    @Override
    public void writeObject(DataOutputStream dos) throws IOException {
        dos.writeUTF(type.name());
        switch (type) {
            case CREATURE -> ((Creature)entity).writeObject(dos);
            case ITEM -> dos.writeUTF(entity.getName());
            default -> throw new AssertionError("Unknown enum entry " + type.name() + " detected!");
        }
        dos.writeInt(amount);
    }

    public static Reward readObject(DataInputStream dis) throws IllegalStateException, IOException {
        final RewardType type;
        try {
            type = RewardType.valueOf(dis.readUTF());
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        }
        final Entity entity;
        switch (type) {
            case CREATURE -> entity = Creature.readObject(dis);
            case ITEM -> entity = CollectionUtil.getByName(Item.REGISTRY, dis.readUTF());
            default -> throw new IllegalStateException("Unknown enum entry " + type.name() + " detected!");
        }
        return new Reward(type, entity, dis.readInt());
    }
}
