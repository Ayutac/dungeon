package org.abos.dungeon.core.task;

import java.util.Random;
import java.util.function.Function;

public interface TaskFactory extends Function<Integer, Task> {

    /**
     * Returns the {@link Random} instance used by this {@link DefaultTaskFactory}
     */
    Random random();

}
