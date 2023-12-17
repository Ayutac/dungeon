package org.abos.common;

import java.util.Deque;
import java.util.LinkedList;

public final class MathUtil {
    
    private static final Deque<Integer> CACHED_PRIMES = new LinkedList<>();

    private static int biggestNumberChecked;
    
    private MathUtil() {
        /* No instantiation. */
    }

    static {
        // make sure list is not empty
        CACHED_PRIMES.addLast(2);
        biggestNumberChecked = 2;
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
    
    public static boolean isPrime(final int n) {
        if (n < 0) {
            return isPrime(-n);
        }
        if (n < CACHED_PRIMES.getFirst()) {
            return false;
        }
        if (n <= biggestNumberChecked) {
            return CACHED_PRIMES.contains(n);
        }
        // this ensures the primes remain ordered by size and none is missed
        for (int k = CACHED_PRIMES.getLast() + 1; k <= n; k++) {
            cacheIfPrime(k);
        }
        biggestNumberChecked = n;
        return CACHED_PRIMES.contains(n);
    }

}
