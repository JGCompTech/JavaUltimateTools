package com.jgcomptech.tools;

/**
 * Contains methods dealing with numbers
 */
public final class NumberUtils {
    /**
     * Checks if the specified number is prime
     * @param input number to check
     * @return true if specified number is prime
     */
    public boolean isPrime(long input) {
        // fast even test.
        if(input > 2 && (input & 1) == 0) return false;
        // only odd factors need to be tested up to input^0.5
        for(int i = 3; i * i <= input; i += 2) {
            if(input % i == 0) return false;
        }
        return true;
    }

    /**
     * Checks if the specified number is even
     * @param input number to check
     * @return true if the specified number is even
     */
    public static Boolean isEven(int input) { return (input & 1) == 0; }

    /**
     * Checks if the specified number is odd
     * @param input number to check
     * @return true if the specified number is odd
     */
    public static Boolean isOdd(int input) { return (input & 1) != 0; }

    /**
     * Squares the specified number
     * @param input number to edit
     * @return the number squared
     */
    public static double squared(int input) { return Math.pow(input, 2); }

    /**
     * Cubes the specified number
     * @param input number to edit
     * @return the number cubed
     */
    public static double cubed(int input) { return Math.pow(input, 3); }

    /**
     * Returns the square root of the specified number
     * @param input number to edit
     * @return the square root of the number
     */
    public static double squareRoot(int input) { return Math.sqrt(input); }

    /**
     * Checks if the specified number is in range
     * @param input the number to check
     * @param lower the lower bound
     * @param upper the upper bound
     * @param inclusive if true the upper bound is included
     * @return true if the specified number is in range
     */
    public static Boolean isInRange(int input, int lower, int upper, boolean inclusive) {
        if(inclusive) { if (lower <= input && input <= upper) return true; }
        else { if (lower <= input && input < upper) return true; }
        return false;
    }
}
