package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Thrown when attempting to authenticate and the corresponding account has been disabled for
 * some reason.
 * @see LockedAccountException
 * @since 1.5.0
 */
public class DisabledAccountException extends AccountException {
    /**
     * Creates a new DisabledAccountException.
     */
    public DisabledAccountException() { }

    /**
     * Constructs a new DisabledAccountException.
     * @param message the reason for the exception
     */
    public DisabledAccountException(final String message) { super(message); }

    /**
     * Constructs a new DisabledAccountException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public DisabledAccountException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new DisabledAccountException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public DisabledAccountException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown DisabledAccountException!" );
        } catch (final DisabledAccountException ignore) { }
    }
}
