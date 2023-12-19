package org.abos.dungeon.core.task;

import org.abos.common.CollectionUtil;
import org.abos.common.MathUtil;
import org.abos.dungeon.core.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * A {@link Task} that ask the {@link Player} a question they must answer.
 */
public class Question implements Task {

    /**
     * A collection of mathematical constants, mapped from their display name to their value.
     */
    protected static final Map<String, Double> CONSTANTS = new HashMap<>();

    /**
     * @see #getQuestion() 
     */
    protected final String question;

    /**
     * @see #getAnswer() 
     */
    protected final String answer;

    static {
        CONSTANTS.put("Pi", Math.PI);
        CONSTANTS.put("Euler's number e", Math.E);
        CONSTANTS.put("the Golden Ratio Φ", (Math.sqrt(5d)+1)/2);
        CONSTANTS.put("ln 2", Math.log(2d));
        CONSTANTS.put("Pythagoras' constant √2", Math.sqrt(2d));
        CONSTANTS.put("Theodorus' constant √3", Math.sqrt(3d));
        CONSTANTS.put("the Euler-Mascheroni constant γ", 0.57721566490153286060);
    }

    public Question(final String question, final String answer) {
        this.question = Objects.requireNonNull(question);
        this.answer = Objects.requireNonNull(answer).toLowerCase();
    }

    /**
     * Returns the question string as to be displayed for the user.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Returns the answer string, guaranteed to be lowercase.
     */
    public String getAnswer() {
        return answer;
    }

    @Override
    public void accept(final Player player) {
        if (player.displayQuestion(this)) {
            player.clearCurrentTask();
        }
    }
    
    @Override
    public void writeObject(final DataOutputStream dos) throws IOException {
        dos.writeUTF(question);
        dos.writeUTF(answer);
    }

    /**
     * Reads a {@link Question} instance from the specified stream.
     * @param dis the {@link DataInputStream} to read from
     * @return a new {@link Question} instance
     * @throws IOException If an I/O exception occurs.
     */
    public static Question readObject(final DataInputStream dis) throws IOException {
        return new Question(dis.readUTF(), dis.readUTF());
    }

    /**
     * Gives an upper limit for summands in generated questions based on the specified room number.
     */
    protected static int getSummandUpperLimit(final int roomNumber) {
        if (roomNumber == Integer.MAX_VALUE) {
            return roomNumber;
        }
        return roomNumber + 1;
    }

    /**
     * Gives an upper limit for factors in generated questions based on the specified room number.
     */
    protected static int getFactorUpperLimit(final int roomNumber) {
        return (int)Math.round(Math.sqrt(roomNumber)) + 3;
    }

    /**
     * Gives an upper limit for factorial arguments in generated questions based on the specified room number.
     */
    protected static int getFactorialUpperLimit(final int roomNumber) {
        // max is 10!
        return Math.min(getFactorUpperLimit(roomNumber), 11);
    }

    /**
     * Gives an upper limit for digit positions in generated questions based on the specified room number.
     */
    protected static int getDigitUpperLimit(final int roomNumber) {
        return (int)Math.round(Math.log10(roomNumber+1))+1;
    }

    /**
     * Creates a new {@link Question} instance about addition with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about addition
     */
    public static Question getAdditionQuestion(final Random random, final int roomNumber) {
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
        return new Question(String.format("What is %d + %d?", a, b), Integer.toString(a+b));
    }

    /**
     * Creates a new {@link Question} instance about subtraction with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about subtraction
     */
    public static Question getSubtractionQuestion(final Random random, final int roomNumber) {
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
        return new Question(String.format("What is %d - %d?", a, b), Integer.toString(a-b));
    }

    /**
     * Creates a new {@link Question} instance about multiplication with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about multiplication
     */
    public static Question getMultiplicationQuestion(final Random random, final int roomNumber) {
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
        return new Question(String.format("What is %d * %d?", a, b), Integer.toString(a*b));
    }

    /**
     * Creates a new {@link Question} instance about division with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about division
     */
    public static Question getDivisionQuestion(final Random random, final int roomNumber) {
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
        return new Question(String.format("What is %d / %d?", a*b, b), Integer.toString(a));
    }

    /**
     * Creates a new {@link Question} instance about basic arithmetic operations (+,-,*,/) with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about basic arithmetic operations
     */
    public static Question getSimpleArithmQuestion(final Random random, final int roomNumber) {
        return switch (random.nextInt(4)) {
            case 0 -> getAdditionQuestion(random, roomNumber);
            case 1 -> getSubtractionQuestion(random, roomNumber);
            case 2 -> getMultiplicationQuestion(random, roomNumber);
            case 3 -> getDivisionQuestion(random, roomNumber);
            default -> throw new AssertionError("Unreachable code reached!");
        };
    }

    /**
     * Creates a new {@link Question} instance about squaring with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about squaring
     */
    public static Question getSquareQuestion(final Random random, final int roomNumber) {
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
        return new Question(String.format("What is %d²?", a), Integer.toString(a*a));
    }

    /**
     * Creates a new {@link Question} instance about taking the square root with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about taking the square root
     */
    public static Question getSquareRootQuestion(final Random random, final int roomNumber) {
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
        return new Question(String.format("What is √%d?", a*a), Integer.toString(a));
    }

    /**
     * Creates a new {@link Question} instance about the factorial with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about the factorial
     */
    public static Question getFactorialQuestion(final Random random, final int roomNumber) {
        final int a = random.nextInt(getFactorialUpperLimit(roomNumber));
        return new Question(String.format("What is %d!?", a), Integer.toString(MathUtil.factorial(a)));
    }

    /**
     * Creates a new {@link Question} instance about the digit of a constant with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about the digit of a constant
     */
    public static Question getDigitQuestion(final Random random, final int roomNumber) {
        final int position = 1 + random.nextInt(getDigitUpperLimit(roomNumber));
        final Map.Entry<String, Double> constant = CollectionUtil.getRandomEntry(CONSTANTS, random);
        final int factor = (int)Math.round(Math.pow(10, position-1));
        final int digit = (int)Math.floor(constant.getValue()*factor) % 10;
        return new Question(String.format("What is the %d. digit of %s?", position, constant.getKey()), Integer.toString(digit));
    }

}