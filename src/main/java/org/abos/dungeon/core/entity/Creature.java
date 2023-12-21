package org.abos.dungeon.core.entity;

import org.abos.common.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * A {@link LivingEntity} implementation based on {@link AbstractEntity}.
 */
public class Creature extends AbstractEntity implements LivingEntity, Serializable {

    private static List<Creature> templates;

    /**
     * Name of the file containing the different creatures.
     */
    public static final String LIST_FILE_NAME = "creatureList.csv";

    /**
     * @see #getMaxHealthPoints()
     */
    protected final int maxHp;

    /**
     * @see #getCurrentHealthPoints()
     */
    protected int currentHp;

    /**
     * Copies a creature from a given template.
     * @param original the template, not {@code null}
     * @throws NullPointerException If {@code original} refers to {@code null}.
     */
    public Creature(Creature original) {
        this(original.name, original.description, original.maxHp);
        this.currentHp = original.currentHp;
    }

    public Creature(final String name, final String description, final int maxHp) {
        super(name, description);
        if (maxHp < 0) {
            throw new IllegalArgumentException("Max health points must be positive!");
        }
        this.maxHp = maxHp;
        this.currentHp = maxHp;
    }

    @Override
    public int getMaxHealthPoints() {
        return maxHp;
    }

    @Override
    public int getCurrentHealthPoints() {
        return currentHp;
    }

    public static List<Creature> getTemplates() {
        if (templates == null) {
            templates = LivingEntity.TEMPLATE_REGISTRY.stream().filter(Creature.class::isInstance).map(Creature.class::cast).toList();
        }
        return templates;
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
