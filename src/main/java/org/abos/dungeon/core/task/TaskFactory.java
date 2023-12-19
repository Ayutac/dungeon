package org.abos.dungeon.core.task;

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Factory for {@link Task}s.
 */
public class TaskFactory implements Function<Integer, Task> {

    /**
     * @see #random()
     */
    protected final Random random;

    /**
     * Creates a new {@link TaskFactory}.
     * @param random the {@link Random} instance to use.
     */
    public TaskFactory(final Random random) {
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Returns the {@link Random} instance used by this {@link TaskFactory}
     */
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
        return switch (random().nextInt(6)) {
            case 0 -> Question.getSimpleArithmQuestion(random(), roomNumber);
            case 1 -> Question.getSquareQuestion(random(), roomNumber);
            case 2 -> Question.getSquareRootQuestion(random(), roomNumber);
            case 3 -> Question.getFactorialQuestion(random(), roomNumber);
            case 4 -> Question.getDigitQuestion(random(), roomNumber);
            case 5 -> Information.getRandomInformation(random());
            default -> throw new AssertionError("Unreachable code reached!");
        };
    }

}
