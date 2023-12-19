package org.abos.dungeon.core.reward;

import org.abos.common.MathUtil;
import org.abos.dungeon.core.entity.Creature;

import java.util.Objects;
import java.util.Random;

public class DefaultRewardFactory implements RewardFactory {

    protected final Random random;

    public DefaultRewardFactory(final Random random) {
        this.random = Objects.requireNonNull(random);
    }

    public Random random() {
        return random;
    }

    /**
     * Takes in the room number and returns an appropriate reward.
     * @param roomNumber the room number
     * @return the reward or {@code null} if there is no reward
     */
    @Override
    public Reward apply(Integer roomNumber) {
        if (MathUtil.isPrime(roomNumber)) {
            if (random().nextDouble() < 0.2) {
                var templates = Creature.getTemplates();
                return new Reward(RewardType.CREATURE, new Creature(templates.get(random().nextInt(templates.size()))), 1);
            }
            return null;
        }
        return null;
    }
}
