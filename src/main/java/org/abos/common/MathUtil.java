package org.abos.common;

public final class MathUtil {

    private MathUtil() {
        /* No instantiation. */
    }

    /**
     * Calculates the factorial of the specified number, ignoring overflow.
     * @param n the number to take the factorial of
     * @return n!
     * @throws ArithmeticException If n is negative.
     */
    public static int factorial(final int n) throws ArithmeticException {
        if (n == 0 || n == 1) {
            return 1;
        }
        if (n < 0) {
            throw new ArithmeticException("Factorials of negative numbers are not possible!");
        }
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

}
