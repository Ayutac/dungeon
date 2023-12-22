package org.abos.dungeon.core.task;

import java.util.function.Function;

public interface TaskFactory extends Function<Integer, Task> {
    // Might be extended in the future.
}
