package org.abos.common;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Additional utilities where {@link Math} isn't enough.
 */
public final class MathUtil {
    
    private static final Deque<Integer> CACHED_PRIMES = new LinkedList<>();

    private static final Deque<Integer> CACHED_FIBONACCI = new LinkedList<>();

    private static int biggestNumberCheckedForPrime;

    private static int biggestNumberCheckedForFibonacci;
    
    private MathUtil() {
        /* No instantiation. */
    }

    static {
        // make sure list is not empty
        CACHED_PRIMES.addLast(2);
        biggestNumberCheckedForPrime = 2;
        // make sure list has at least two entries
        CACHED_FIBONACCI.add(1);
        CACHED_FIBONACCI.add(1);
        biggestNumberCheckedForFibonacci = 1;
    }

    /**
     * Checks if two double values are equal for a certain precision.
     * @param a the first double
     * @param b the second double
     * @param epsilon the error margin
     * @return {@code true} if the two doubles are the same within the error margin, else {@code false}.
     */
    public static boolean equalsPrecision(final double a, final double b, final double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    /**
     * Unchecked recursive greatest common divisor algorithm.
     * @param a a positive number greater than b (unchecked)
     * @param b a positive number smaller than a (unchecked)
     * @return the greatest common divisor of the two numbers
     * @implNote Uses Euclid's algorithm.
     */
    private static int innerGcd(final int a, final int b) {
        if (a % b == 0) {
            return b;
        }
        return innerGcd(b, a % b);
    }

    /**
     * Greatest common divisor algorithm.
     * @param a an integer
     * @param b another integer
     * @return The greatest common divisor of the two numbers, always a positive number.
     * @implSpec Special case: {@code gcd(0, 0) == Integer.MAX_VALUE}.
     * @implNote Uses Euclid's algorithm.
     */
    public static int gcd(final int a, final int b) {
        if (a == 0 && b == 0) {
            return Integer.MAX_VALUE;
        }
        if (a == 0) {
            return Math.abs(b);
        }
        if (b == 0) {
            return Math.abs(a);
        }
        if (a == b || a == -b) {
            return Math.abs(a);
        }
        if (a < 0) {
            if (b < 0) {
                if (a < b) {
                    return innerGcd(-a, -b);
                }
                else {
                    return innerGcd(-b, -a);
                }
            }
            else {
                if (-a < b) {
                    return innerGcd(b, -a);
                }
                else {
                    return innerGcd(-a, b);
                }
            }
        }
        if (b < 0) {
            if (a < -b) {
                return innerGcd(-b, a);
            }
            else {
                return innerGcd(a, -b);
            }
        }
        if (a < b) {
            return innerGcd(b, a);
        }
        return innerGcd(a, b);
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

    /**
     * Returns the coefficients a,b,c from ax²+bx+c, given via A*(x-B)*(x-C).
     * @param factor the scalar factor
     * @param root1 one root of the quadratic polynomial
     * @param root2 the other root of the quadratic polynomial
     * @return The coefficients of the quadratic polynomial, starting with the leading coefficient.
     * The returned array always has length 3.
     * @throws ArithmeticException If the coefficients couldn't be calculated because of overflow.
     */
    public static int[] quadraticCoefficients(final int factor, final int root1, final int root2) {
        return new int[] {factor, Math.multiplyExact(Math.negateExact(factor), Math.addExact(root1, root2)), Math.multiplyExact(factor, Math.multiplyExact(root1, root2))};
    }

    /**
     * Calculates the scalar product of the two given vectors, that is, v<sup>t</sup> * w.
     * @param v the first vector
     * @param w the second vector
     * @return the scalar product of the two vectors
     * @throws NullPointerException If any parameter refers to {@code null}.
     * @throws ArithmeticException If the vectors are of different length or the scalar product couldn't be calculated because of overflow.
     * @implSpec Two empty arrays return 0.
     */
    public static int scalarProduct(final int[] v, final int [] w) {
        if (v.length != w.length) {
            throw new ArithmeticException("v and w must have the same dimensions!");
        }
        int sum = 0;
        for (int i = 0; i < v.length; i++) {
            sum = Math.addExact(sum, Math.multiplyExact(v[i], w[i]));
        }
        return sum;
    }

    /**
     * Checks if the given vector is the null vector.
     * @param v the vector to check
     * @return {@code true} if all entries of the vector are 0, otherwise {@code false}.
     * @throws NullPointerException If {@code v} refers to {@code null}.
     * @implSpec The empty array returns {@code true}.
     */
    public static boolean isNullVector(final int[] v) {
        for (int j : v) {
            if (j != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the two given vectors are linearly dependent (within an epsilon).
     * @param v the first vector
     * @param w the second vector
     * @param epsilon the allowed amount of rounding error when dividing
     * @return {@code true} if the two vectors are linearly dependent, else {@code false}.
     * @throws NullPointerException If any parameter refers to {@code null}.
     * @throws ArithmeticException If the vectors are of different length.
     * @implSpec Two one-dimensional or zero-dimensional arrays are always linearly dependent.
     */
    public static boolean areDependent(final int[] v, final int[] w, final double epsilon) {
        if (v.length != w.length) {
            throw new ArithmeticException("v and w must have the same dimensions!");
        }
        if (v.length == 0 || v.length == 1 || isNullVector(v) || isNullVector(w)) {
            return true;
        }
        double scalar = Double.NaN;
        for (int i = 0; i < v.length; i++) {
            if (w[i] == 0) {
                continue;
            }
            scalar = ((double)v[i]) / w[i];
            break;
        }
        for (int i = 0; i < v.length; i++) {
            if (!equalsPrecision(scalar, ((double)v[i]) / w[i], epsilon)) {
                return false;
            }
        }
        return true;
    }
    
    private static void cacheIfPrime(final int n) {
        final double sqrt = Math.sqrt(n);
        for (int prime : CACHED_PRIMES) {
            if (prime > sqrt) {
                CACHED_PRIMES.addLast(n);
                return;
            }
            if (n % prime == 0) {
                return; // no new prime
            }
        }
    }

    /**
     * Tests if the specified integer is prime. An integer is defined to be prime
     * if it has exactly two distinct positive divisors.
     * @param n the number to check
     * @return {@code true} if n is prime, else {@code false}.
     * @implNote Primes are cached, so first call of method for a given n may take
     * significantly longer than second call with the same n. To actually
     * test if n is prime, the Sieve of Erasthonese is used.
     */
    public static boolean isPrime(final int n) {
        if (n < 0) {
            return isPrime(-n);
        }
        if (n < CACHED_PRIMES.getFirst()) {
            return false;
        }
        if (n <= biggestNumberCheckedForPrime) {
            return CACHED_PRIMES.contains(n);
        }
        // this ensures the primes remain ordered by size and none is missed
        for (int k = CACHED_PRIMES.getLast() + 1; k <= n; k++) {
            cacheIfPrime(k);
        }
        biggestNumberCheckedForPrime = n;
        return CACHED_PRIMES.contains(n);
    }

    /**
     * Tests if the specified integer is fibonacci. The fibonacci numbers
     * are 1, 1, and then the sum of the previous two fibonacci numbers, e.g. 2, 3, 5, 8, 13, ...
     * @param n the number to check
     * @return {@code true} if n is fibonacci, else {@code false}.
     */
    public static boolean isFibonacci(final int n) {
        if (n < 1) {
            return false;
        }
        int lastFib;
        while (n > biggestNumberCheckedForFibonacci) {
            lastFib = CACHED_FIBONACCI.pollLast();
            try {
                biggestNumberCheckedForFibonacci = Math.addExact(lastFib, CACHED_FIBONACCI.getLast());
            }
            catch (ArithmeticException ex) {
                // we can't go higher than this
                return false;
            }
            CACHED_FIBONACCI.addLast(lastFib);
            CACHED_FIBONACCI.addLast(biggestNumberCheckedForFibonacci);
        }
        return CACHED_FIBONACCI.contains(n);
    }

}
