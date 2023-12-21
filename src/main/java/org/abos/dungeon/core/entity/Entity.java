package org.abos.dungeon.core.entity;

import org.abos.common.Describable;

public interface Entity extends Describable {

    /**
     * Returns true if and only if the other object is the same entity as this one.
     * @param obj the object to compare, {@code null} is allowed but will always return {@code false}.
     * @return {@code true} if the items are the same, else {@code false}
     */
    @Override
    boolean equals(Object obj);

    /**
     * Returns the hash code of this item. Different item instances who {@link #equals(Object) equal} each other return the same hash code.
     * @return the hash code of this item
     */
    @Override
    int hashCode();

    /**
     * Returns at least the class and name of this entity.
     * @return a string describing this entity, not {@code null}.
     */
    @Override
    String toString();
}
