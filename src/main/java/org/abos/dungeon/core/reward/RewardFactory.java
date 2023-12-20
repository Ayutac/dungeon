package org.abos.dungeon.core.reward;

import org.abos.dungeon.core.Player;
import org.abos.dungeon.core.task.DefaultTaskFactory;

import java.util.Random;
import java.util.function.BiFunction;

public interface RewardFactory extends BiFunction<Integer, Player, Reward> {

    /**
     * Returns the {@link Random} instance used by this {@link DefaultTaskFactory}
     */
    Random random();

}
