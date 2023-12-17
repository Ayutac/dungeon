package org.abos.dungeon.core;

import org.abos.common.CollectionUtil;
import org.abos.common.MathUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Question implements Task {

    protected static final Map<String, Double> CONSTANTS = new HashMap<>();

    protected final String question;

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
        this.answer = Objects.requireNonNull(answer);
    }

    public String getQuestion() {
        return question;
    }

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
    
    public static Question readObject(final DataInputStream dis) throws IOException {
        return new Question(dis.readUTF(), dis.readUTF());
    }

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

    public static Question getSimpleArithmQuestion(final Random random, final int roomNumber) {
        return switch (random.nextInt(4)) {
            case 0 -> getAdditionQuestion(random, roomNumber);
            case 1 -> getSubtractionQuestion(random, roomNumber);
            case 2 -> getMultiplicationQuestion(random, roomNumber);
            case 3 -> getDivisionQuestion(random, roomNumber);
            default -> throw new AssertionError("Unreachable code reached!");
        };
    }

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

    public static Question getFactorialQuestion(final Random random, final int roomNumber) {
        final int a = random.nextInt(getFactorialUpperLimit(roomNumber));
        return new Question(String.format("What is %d!?", a), Integer.toString(MathUtil.factorial(a)));
    }

    public static Question getDigitQuestion(final Random random, final int roomNumber) {
        final int position = 1 + random.nextInt(getDigitUpperLimit(roomNumber));
        final Map.Entry<String, Double> constant = CollectionUtil.getRandomEntry(CONSTANTS, random);
        final int factor = (int)Math.round(Math.pow(10, position-1));
        final int digit = (int)Math.floor(constant.getValue()*factor) % 10;
        return new Question(String.format("What is the %d. digit of %s?", position, constant.getKey()), Integer.toString(digit));
    }

}
