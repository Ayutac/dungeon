package org.abos.dungeon.core;

import org.abos.common.CollectionUtil;
import org.abos.common.MathUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;

public abstract class Question extends Task {

    protected static final Map<String, Double> CONSTANTS = new HashMap<>();

    protected final String question;

    protected final String answer;

    static {
        CONSTANTS.put("Pi", Math.PI);
        CONSTANTS.put("Euler's number e", Math.E);
        CONSTANTS.put("Golden Ratio Φ", (Math.sqrt(5d)+1)/2);
        CONSTANTS.put("ln 2", Math.log(2d));
        CONSTANTS.put("Pythagoras' constant √2", Math.sqrt(2d));
        CONSTANTS.put("Theodorus' constant √3", Math.sqrt(3d));
        CONSTANTS.put("Euler-Mascheroni constant γ", 0.57721566490153286060);
    }

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

    protected static int getSummandUpperLimit(final int roomNumber) {
        if (roomNumber == Integer.MAX_VALUE) {
            return roomNumber;
        }
        return roomNumber + 1;
    }

    protected static int getFactorUpperLimit(final int roomNumber) {
        return (int)Math.round(Math.sqrt(roomNumber)) + 3;
    }

    protected static int getFactorialUpperLimit(final int roomNumber) {
        // max is 10!
        return Math.min(getFactorUpperLimit(roomNumber), 11);
    }

    protected static int getDigitUpperLimit(final int roomNumber) {
        return (int)Math.round(Math.log10(roomNumber+1))+1;
    }

    public static Question getAdditionQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        int a, b;
        while (true) {
            a = random.nextInt(getSummandUpperLimit(roomNumber));
            if (random.nextBoolean()) {
                a = -a;
            }
            b = random.nextInt(getSummandUpperLimit(roomNumber));
            try {
                //noinspection ResultOfMethodCallIgnored
                Math.addExact(a, b);
                break;
            }
            catch (ArithmeticException ex) {/* Ignore and retry. */}
        }
        return constructor.apply(String.format("What is %d + %d? ", a, b), Integer.toString(a+b));
    }

    public static Question getSubtractionQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        int a, b;
        while (true) {
            a = random.nextInt(getSummandUpperLimit(roomNumber));
            if (random.nextBoolean()) {
                a = -a;
            }
            b = random.nextInt(getSummandUpperLimit(roomNumber));
            try {
                //noinspection ResultOfMethodCallIgnored
                Math.subtractExact(a, b);
                break;
            }
            catch (ArithmeticException ex) {/* Ignore and retry. */}
        }
        return constructor.apply(String.format("What is %d - %d? ", a, b), Integer.toString(a-b));
    }

    public static Question getMultiplicationQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        int a, b;
        while (true) {
            a = random.nextInt(getFactorUpperLimit(roomNumber));
            if (random.nextBoolean()) {
                a = -a;
            }
            b = random.nextInt(getFactorUpperLimit(roomNumber));
            try {
                //noinspection ResultOfMethodCallIgnored
                Math.multiplyExact(a, b);
                break;
            }
            catch (ArithmeticException ex) {/* Ignore and retry. */}
        }
        return constructor.apply(String.format("What is %d * %d? ", a, b), Integer.toString(a*b));
    }

    public static Question getDivisionQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        int a, b;
        while (true) {
            a = random.nextInt(getFactorUpperLimit(roomNumber));
            if (random.nextBoolean()) {
                a = -a;
            }
            b = random.nextInt(getFactorUpperLimit(roomNumber));
            if (b == 0) {
                b = 1;
            }
            try {
                //noinspection ResultOfMethodCallIgnored
                Math.multiplyExact(a, b);
                break;
            }
            catch (ArithmeticException ex) {/* Ignore and retry. */}
        }
        return constructor.apply(String.format("What is %d / %d? ", a*b, b), Integer.toString(a));
    }

    public static Question getSimpleArithmQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        return switch (random.nextInt(4)) {
            case 0 -> getAdditionQuestion(constructor, random, roomNumber);
            case 1 -> getSubtractionQuestion(constructor, random, roomNumber);
            case 2 -> getMultiplicationQuestion(constructor, random, roomNumber);
            case 3 -> getDivisionQuestion(constructor, random, roomNumber);
            default -> throw new AssertionError("Unreachable code reached!");
        };
    }

    public static Question getSquareQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        int a;
        while (true) {
            a = random.nextInt(getFactorUpperLimit(roomNumber));
            try {
                //noinspection ResultOfMethodCallIgnored
                Math.multiplyExact(a, a);
                break;
            }
            catch (ArithmeticException ex) {/* Ignore and retry. */}
        }
        return constructor.apply(String.format("What is %d²? ", a), Integer.toString(a*a));
    }

    public static Question getSquareRootQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        int a;
        while (true) {
            a = random.nextInt(getFactorUpperLimit(roomNumber));
            try {
                //noinspection ResultOfMethodCallIgnored
                Math.multiplyExact(a, a);
                break;
            }
            catch (ArithmeticException ex) {/* Ignore and retry. */}
        }
        return constructor.apply(String.format("What is √%d? ", a*a), Integer.toString(a));
    }

    public static Question getFactorialQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        final int a = random.nextInt(getFactorialUpperLimit(roomNumber));
        return constructor.apply(String.format("What is %d!? ", a), Integer.toString(MathUtil.factorial(a)));
    }

    public static Question getDigitQuestion(final BiFunction<String, String, Question> constructor, final Random random, final int roomNumber) {
        final int position = 1 + random.nextInt(getDigitUpperLimit(roomNumber));
        final Map.Entry<String, Double> constant = CollectionUtil.getRandomEntry(CONSTANTS, random);
        final int factor = (int)Math.round(Math.pow(10, position-1));
        final int digit = (int)Math.floor(constant.getValue()*factor) % factor;
        return constructor.apply(String.format("What is the %d. digit of %s? ", position, constant.getKey()), Integer.toString(digit));
    }

}
