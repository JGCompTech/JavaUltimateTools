package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * General exception thrown due to an error during the Authentication process.
 * @since 1.5.0
 */
public class AuthenticationException extends RuntimeException {
    /**
     * Creates a new AuthenticationException.
     */
    public AuthenticationException() { }

    /**
     * Constructs a new AuthenticationException.
     * @param message the reason for the exception
     */
    public AuthenticationException(final String message) { super(message); }

    /**
     * Constructs a new AuthenticationException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public AuthenticationException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new AuthenticationException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public AuthenticationException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown AuthenticationException!" );
        } catch (final AuthenticationException ignore) { }
    }
}
