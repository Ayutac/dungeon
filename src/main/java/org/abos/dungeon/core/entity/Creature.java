package org.abos.dungeon.core.entity;

import org.abos.common.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Creature extends AbstractEntity implements LivingEntity, Serializable {

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

    @Override
    public void writeObject(final DataOutputStream dos) throws IOException {
        dos.writeUTF(name);
        dos.writeUTF(description);
        dos.writeInt(maxHp);
        dos.writeInt(currentHp);
    }

    public static Creature readObject(final DataInputStream dis) throws IOException {
        final Creature creature = new Creature(dis.readUTF(), dis.readUTF(), dis.readInt());
        creature.currentHp = dis.readInt();
        return creature;
    }
}
