package com.jgcomptech.tools;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Contains methods for dealing with exceptions.
 * @since 1.5.0
 */
public class ExceptionUtils {
    /**
     * Asserts that the specified runnable throws IllegalArgumentException
     * and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrownIllegalArgumentException(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown IllegalArgumentException!" );
        } catch (final IllegalArgumentException ignore) { }
    }

    /**
     * Asserts that the specified runnable throws IllegalStateException
     * and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrownIllegalStateException(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown IllegalStateException!" );
        } catch (final IllegalStateException ignore) { }
    }

    /**
     * Asserts that the specified runnable throws RuntimeException
     * and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrownRuntimeException(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown RuntimeException!" );
        } catch (final RuntimeException ignore) { }
    }
}
