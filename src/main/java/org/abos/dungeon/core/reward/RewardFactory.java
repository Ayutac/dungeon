package org.abos.dungeon.core.reward;

import org.abos.dungeon.core.task.DefaultTaskFactory;

import java.util.Random;
import java.util.function.Function;

public interface RewardFactory extends Function<Integer, Reward> {

    /**
     * Returns the {@link Random} instance used by this {@link DefaultTaskFactory}
     */
    Random random();

}
