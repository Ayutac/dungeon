package org.abos.dungeon.cmd;

import org.abos.dungeon.core.Question;
import org.abos.dungeon.core.Task;
import org.abos.dungeon.core.TaskFactory;

import java.util.Objects;
import java.util.Random;

public class CmdTaskFactory implements TaskFactory {

    protected final Random random;

    public CmdTaskFactory(final Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Random random() {
        return random;
    }

    @Override
    public Task apply(Integer integer) {
        return switch (random().nextInt(5)) {
            case 0 -> Question.getSimpleArithmQuestion(CmdQuestion::new, random(), integer);
            case 1 -> Question.getSquareQuestion(CmdQuestion::new, random(), integer);
            case 2 -> Question.getSquareRootQuestion(CmdQuestion::new, random(), integer);
            case 3 -> Question.getFactorialQuestion(CmdQuestion::new, random(), integer);
            case 4 -> Question.getDigitQuestion(CmdQuestion::new, random(), integer);
            default -> throw new AssertionError("Unreachable code reached!");
        };
    }
}
