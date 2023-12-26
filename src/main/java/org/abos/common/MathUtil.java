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
