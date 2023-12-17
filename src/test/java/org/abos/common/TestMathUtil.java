package org.abos.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestMathUtil {
    
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
    
}
