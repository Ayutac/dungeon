package org.abos.dungeon.core;

import org.abos.common.Named;

public interface Item extends Named {

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
