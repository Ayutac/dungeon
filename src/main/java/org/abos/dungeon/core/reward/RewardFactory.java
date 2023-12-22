package org.abos.dungeon.core.reward;

import org.abos.dungeon.core.Player;

import java.util.function.BiFunction;

public interface RewardFactory extends BiFunction<Integer, Player, Reward> {
    // Might be extended in the future.
}
