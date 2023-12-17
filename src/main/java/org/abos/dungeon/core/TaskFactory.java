package org.abos.dungeon.core;

import java.util.Random;
import java.util.function.Function;

/**
 * Factory for {@see Task}s.
 */
public interface TaskFactory extends Function<Integer, Task> {

    Random random();

}
