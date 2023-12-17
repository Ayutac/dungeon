package org.abos.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestMathUtil {
    
    @Test
    public void testIsPrime() {
        Assertions.assertTrue(MathUtil.isPrime(2));
        Assertions.assertTrue(MathUtil.isPrime(3));
        Assertions.assertTrue(MathUtil.isPrime(101));
        Assertions.assertTrue(MathUtil.isPrime(43));
    }
    
}
