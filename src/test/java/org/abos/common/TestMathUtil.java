package org.abos.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MathUtil}.
 */
public class TestMathUtil {

    /**
     * Tests {@link MathUtil#factorial(int)}.
     */
    @Test
    public void testFactorial() {
        //noinspection ResultOfMethodCallIgnored
        Assertions.assertThrows(ArithmeticException.class, () -> MathUtil.factorial(-1));
        Assertions.assertEquals(1, MathUtil.factorial(0));
        Assertions.assertEquals(1, MathUtil.factorial(1));
        Assertions.assertEquals(2, MathUtil.factorial(2));
        Assertions.assertEquals(6, MathUtil.factorial(3));
        Assertions.assertEquals(24, MathUtil.factorial(4));
        Assertions.assertEquals(120, MathUtil.factorial(5));
        Assertions.assertEquals(720, MathUtil.factorial(6));
    }

    /**
     * Tests {@link MathUtil#isPrime(int)}.
     */
    @Test
    public void testIsPrime() {
        Assertions.assertFalse(MathUtil.isPrime(1));
        Assertions.assertTrue(MathUtil.isPrime(2));
        Assertions.assertTrue(MathUtil.isPrime(3));
        Assertions.assertFalse(MathUtil.isPrime(4));
        Assertions.assertFalse(MathUtil.isPrime(50));
        Assertions.assertTrue(MathUtil.isPrime(101));
        Assertions.assertFalse(MathUtil.isPrime(100));
        Assertions.assertTrue(MathUtil.isPrime(43));
        Assertions.assertTrue(MathUtil.isPrime(-37));
        Assertions.assertFalse(MathUtil.isPrime(-38));
    }

    /**
     * Tests {@link MathUtil#isFibonacci(int)}.
     */
    @Test
    public void testIsFibonacci() {
        Assertions.assertTrue(MathUtil.isFibonacci(1));
        Assertions.assertTrue(MathUtil.isFibonacci(2));
        Assertions.assertTrue(MathUtil.isFibonacci(3));
        Assertions.assertFalse(MathUtil.isFibonacci(4));
        Assertions.assertTrue(MathUtil.isFibonacci(5));
        Assertions.assertFalse(MathUtil.isFibonacci(-8));
        Assertions.assertTrue(MathUtil.isFibonacci(13));
        Assertions.assertFalse(MathUtil.isFibonacci(22));
        Assertions.assertTrue(MathUtil.isFibonacci(55));
        Assertions.assertFalse(MathUtil.isFibonacci(196417));
        Assertions.assertTrue(MathUtil.isFibonacci(196418));
        Assertions.assertFalse(MathUtil.isFibonacci(196419));
    }
    
}
