package org.abos.dungeon.core.entity;

import java.util.Objects;

/**
 * Abstract implementation of {@link Entity}.
 */
public abstract class AbstractEntity implements Entity {

    /**
     * @see #getName()
     */
    protected final String name;

    /**
     * @see #getDescription()
     */
    protected final String description;

    /**
     * Creates a new {@link AbstractEntity} instance.
     * @param name the name of this entity, not {@code null}
     * @param description the description of this entity, not {@code null}
     * @throws NullPointerException If any parameter refers to {@code null}.
     */
    public AbstractEntity(final String name, final String description) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity thing)) return false;
        return name.equals(thing.name) && description.equals(thing.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
