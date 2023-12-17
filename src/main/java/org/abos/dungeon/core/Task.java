package org.abos.dungeon.core;

import org.abos.common.Serializable;

import java.util.function.Consumer;

/**
 * Marker interface.
 */
public interface Task extends Consumer<Player>, Serializable {

    // might be extended in the future

}
