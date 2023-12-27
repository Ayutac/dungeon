package org.abos.dungeon.core.task;

import org.abos.common.ErrorUtil;
import org.abos.common.Randomizer;

import java.util.Objects;
import java.util.Random;

/**
 * Factory for {@link Task}s.
 */
public class DefaultTaskFactory implements TaskFactory, Randomizer {

    /**
     * @see #random()
     */
    protected final Random random;

    /**
     * Creates a new {@link DefaultTaskFactory}.
     * @param random the {@link Random} instance to use.
     */
    public DefaultTaskFactory(final Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Random random() {
        return random;
    }

    /**
     * Takes in the room number and returns an appropriate task.
     * @param roomNumber the room number
     * @return a task, not {@code null}
     */
    @Override
    public Task apply(Integer roomNumber) {
        return switch (random().nextInt(5)) {
            case 0 -> Question.getSimpleArithmQuestion(random(), roomNumber);
            case 1 -> Question.getAdvancedArithmQuestion(random(), roomNumber);
            case 2 -> Question.getQuadraticRootQuestion(random(), roomNumber);
            case 3 -> Question.getDigitQuestion(random(), roomNumber);
            case 4 -> Information.getRandomInformation(random());
            default -> ErrorUtil.unreachableCode();
        };
    }

}
