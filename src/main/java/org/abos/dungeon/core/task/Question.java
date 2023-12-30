package org.abos.dungeon.core.task;

import org.abos.common.CollectionUtil;
import org.abos.common.ErrorUtil;
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

    /**
     * Creates a new {@link Question} instance.
     * @param question the question to ask, not {@code null}
     * @param answer the answer to the question, not {@code null}
     * @throws NullPointerException If any parameter refers to {@code null}.
     */
    public Question(final String question, final String answer) {
        this.question = Objects.requireNonNull(question);
        this.answer = answer.toLowerCase();
    }

    /**
     * Returns the question string as to be displayed for the user.
     * @return the question, not {@code null}
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Returns the answer string, guaranteed to be lowercase.
     * @return the answer in lowercase, not {@code null}
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
        return Math.min(getFactorUpperLimit(roomNumber)/2, 11);
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
        final int factorUpperLimit = getFactorUpperLimit(roomNumber);
        while (true) {
            a = random.nextInt(factorUpperLimit);
            if (random.nextBoolean()) {
                a = -a;
            }
            b = random.nextInt(factorUpperLimit);
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
     * Creates a new {@link Question} instance about mod with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about mod
     */
    public static Question getModQuestion(final Random random, final int roomNumber) {
        final int a = random.nextInt(getSummandUpperLimit(roomNumber));
        int b = 0;
        while (b == 0) {
            b = random.nextInt(getFactorUpperLimit(roomNumber));
        }
        return new Question(String.format("What is %d mod %d?", a, b), Integer.toString(a % b));
    }

    /**
     * Creates a new {@link Question} instance about basic arithmetic operations (+,-,*,/,mod) with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about basic arithmetic operations
     */
    public static Question getSimpleArithmQuestion(final Random random, final int roomNumber) {
        return switch (random.nextInt(5)) {
            case 0 -> getAdditionQuestion(random, roomNumber);
            case 1 -> getSubtractionQuestion(random, roomNumber);
            case 2 -> getMultiplicationQuestion(random, roomNumber);
            case 3 -> getDivisionQuestion(random, roomNumber);
            case 4 -> getModQuestion(random, roomNumber);
            default -> ErrorUtil.unreachableCode();
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
     * Creates a new {@link Question} instance about the greatest common divisor with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about the greatest common divisor
     */
    public static Question getGcdQuestion(final Random random, final int roomNumber) {
        int a = random.nextInt(getSummandUpperLimit(roomNumber));
        if (random.nextBoolean()) {
            a = -a;
        }
        int b = random.nextInt(getSummandUpperLimit(roomNumber));
        if (random.nextBoolean()) {
            b = -b;
        }
        return new Question(String.format("What is gcd(%d,%d)?", a, b), Integer.toString(MathUtil.gcd(a,b)));
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
     * Creates a new {@link Question} instance about complex multiplication with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about complex multiplication
     */
    public static Question getComplexMultiplicationQuestion(final Random random, final int roomNumber) {
        int a = 0, b = 0, c = 0, d = 0;
        int[] result = null;
        final int factorUpperLimit = getFactorUpperLimit(roomNumber);
        while (result == null) {
            a = random.nextInt(factorUpperLimit);
            if (random.nextBoolean()) {
                a = -a;
            }
            b = random.nextInt(factorUpperLimit);
            if (random.nextBoolean()) {
                b = -b;
            }
            c = random.nextInt(factorUpperLimit);
            if (random.nextBoolean()) {
                c = -c;
            }
            d = random.nextInt(factorUpperLimit);
            if (random.nextBoolean()) {
                d = -d;
            }
            try {
                result = MathUtil.multiplyComplex(a, b, c, d);
            }
            catch (ArithmeticException ex) {/* Ignore and retry. */}
        }
        return new Question(String.format("What is (%d%+di) * (%d%+di)?", a, b, c, d),
                String.format("%d%+di", result[0], result[1]));
    }

    /**
     * Creates a new {@link Question} instance about advanced arithmetic operations (²,√,gcd,!) with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about advanced arithmetic operations
     */
    public static Question getAdvancedArithmQuestion(final Random random, final int roomNumber) {
        return switch (random.nextInt(5)) {
            case 0 -> getSquareQuestion(random, roomNumber);
            case 1 -> getSquareRootQuestion(random, roomNumber);
            case 2 -> getGcdQuestion(random, roomNumber);
            case 3 -> getFactorialQuestion(random, roomNumber);
            case 4 -> getComplexMultiplicationQuestion(random, roomNumber);
            default -> ErrorUtil.unreachableCode();
        };
    }

    /**
     * Creates a new {@link Question} instance about the root of a randomly generated quadratic polynomial.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about the roots of a quadratic polynomial
     */
    public static Question getQuadraticRootQuestion(final Random random, final int roomNumber) {
        int factor = Math.max(1,random.nextInt(getDigitUpperLimit(roomNumber)));
        if (random.nextBoolean()) {
            factor = -factor;
        }
        int root1 = 0, root2 = 0;
        int[] coefficients = null;
        while (coefficients == null) {
            try {
                root1 = random.nextInt(getFactorUpperLimit(roomNumber));
                if (random.nextBoolean()) {
                    root1 = -root1;
                }
                root2 = random.nextInt(getFactorUpperLimit(roomNumber));
                if (random.nextBoolean()) {
                    root2 = -root2;
                }
                coefficients = MathUtil.quadraticCoefficients(factor, root1, root2);
            }
            catch (ArithmeticException ex) {/* Ignore and retry. */}
        }
        final boolean bigger = random.nextBoolean();
        final StringBuilder polynomial = new StringBuilder();
        if (coefficients[0] == -1) {
            polynomial.append('-');
        }
        else if (coefficients[0] != 1) {
            polynomial.append(coefficients[0]);
        }
        polynomial.append("x²");
        if (coefficients[1] != 0) {
            if (coefficients[1] > 0) {
                polynomial.append('+');
            }
            polynomial.append(coefficients[1]);
            polynomial.append('x');
        }
        if (coefficients[2] != 0) {
            if (coefficients[2] > 0) {
                polynomial.append('+');
            }
            polynomial.append(coefficients[2]);
        }
        return new Question(String.format("What is the %s root of %s?", bigger ? "bigger" : "smaller", polynomial), Integer.toString(bigger ? Math.max(root1, root2) : Math.min(root1, root2)));
    }

    /**
     * Creates a new {@link Question} instance about the digit of a constant with randomly generated content.
     * @param random a {@link Random} instance
     * @param roomNumber the room number this question is for, for difficulty adjustments
     * @return a new and randomized {@link Question} instance about the digit of a constant
     */
    public static Question getDigitQuestion(final Random random, final int roomNumber) {
        final int position = Math.max(1,random.nextInt(getDigitUpperLimit(roomNumber)));
        final Map.Entry<String, Double> constant = CollectionUtil.getRandomEntry(CONSTANTS, random);
        final int factor = (int)Math.round(Math.pow(10, position-1));
        final int digit = (int)Math.floor(constant.getValue()*factor) % 10;
        return new Question(String.format("What is the %d. digit of %s?", position, constant.getKey()), Integer.toString(digit));
    }

}
