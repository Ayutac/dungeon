package org.abos.dungeon.core;

import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;

public abstract class Question extends Task {

    protected final String question;

    protected final String answer;

    public Question(final String question, final String answer) {
        this.question = Objects.requireNonNull(question);
        this.answer = Objects.requireNonNull(answer);
    }

    @Override
    public void run() {
        if (!isSolved() && displayQuestion()) {
            setSolved(true);
        }
    }

    protected abstract boolean displayQuestion();

    public static int getSummandUpperLimit(final int roomNumber) {
        return roomNumber + 1;
    }

    public static int getFactorUpperLimit(final int roomNumber) {
        return (int)Math.round(Math.sqrt(roomNumber)) + 1;
    }

    public static Question getAdditionQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        final int a = random.nextInt(getSummandUpperLimit(roomNumber));
        final int b = random.nextInt(getSummandUpperLimit(roomNumber));
        return constructor.apply(String.format("What is %d + %d? ", a, b), Integer.toString(a+b));
    }

    public static Question getSubtractionQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        final int a = random.nextInt(getSummandUpperLimit(roomNumber));
        final int b = random.nextInt(getSummandUpperLimit(roomNumber));
        return constructor.apply(String.format("What is %d - %d? ", a, b), Integer.toString(a-b));
    }

    public static Question getMultiplicationQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        final int a = random.nextInt(getFactorUpperLimit(roomNumber));
        final int b = random.nextInt(getFactorUpperLimit(roomNumber));
        return constructor.apply(String.format("What is %d * %d? ", a, b), Integer.toString(a*b));
    }

    public static Question getDivisionQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        final int a = random.nextInt(getFactorUpperLimit(roomNumber));
        int b = random.nextInt(getFactorUpperLimit(roomNumber));
        if (b == 0) {
            b = 1;
        }
        return constructor.apply(String.format("What is %d / %d? ", a*b, b), Integer.toString(a));
    }

}
