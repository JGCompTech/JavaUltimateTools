package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Thrown when a system is configured to only allow a certain number of authentication attempts
 * over a period of time and the current session has failed to authenticate successfully within
 * that number.  The resulting action of such an exception is application-specific, but
 * most systems either temporarily or permanently lock that account to prevent further
 * attempts.
 * @since 1.5.0
 */
public class ExcessiveAttemptsException extends AccountException {
    /**
     * Creates a new ExcessiveAttemptsException.
     */
    public ExcessiveAttemptsException() { }

    /**
     * Constructs a new ExcessiveAttemptsException.
     * @param message the reason for the exception
     */
    public ExcessiveAttemptsException(final String message) { super(message); }

    /**
     * Constructs a new ExcessiveAttemptsException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public ExcessiveAttemptsException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new ExcessiveAttemptsException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public ExcessiveAttemptsException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown ExcessiveAttemptsException!" );
        } catch (final ExcessiveAttemptsException ignore) { }
    }
}
