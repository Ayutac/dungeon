package org.abos.dungeon.core.entity;

import java.util.HashSet;
import java.util.Set;

public interface Item extends Entity {

    Set<Item> itemRegistry = new HashSet<>();

    /**
     * Returns true if and only if the other object is the same item as this one.
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

}
