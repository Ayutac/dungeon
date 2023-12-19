package org.abos.dungeon.core.task;

import org.abos.common.Serializable;
import org.abos.dungeon.core.Player;
import org.abos.dungeon.core.Room;

import java.util.function.Consumer;

/**
 * Marks a task the player has to solve in a {@link Room}.
 * The {@link Player} knows their completed tasks, {@link Player#hasClearedTask(int)}.
 */
public interface Task extends Consumer<Player>, Serializable {

    // might be extended in the future

}
