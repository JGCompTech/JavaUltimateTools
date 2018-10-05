package com.jgcomptech.tools.authz;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Exception thrown if there is a problem during authorization (access control check).
 * @since 1.5.0
 */
public class AuthorizationException extends RuntimeException {
    /**
     * Creates a new AuthorizationException.
     */
    public AuthorizationException() { }

    /**
     * Constructs a new AuthorizationException.
     * @param message the reason for the exception
     */
    public AuthorizationException(final String message) { super(message); }

    /**
     * Constructs a new AuthorizationException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public AuthorizationException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new AuthorizationException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public AuthorizationException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown AuthorizationException!" );
        } catch (final AuthorizationException ignore) { }
    }
}
