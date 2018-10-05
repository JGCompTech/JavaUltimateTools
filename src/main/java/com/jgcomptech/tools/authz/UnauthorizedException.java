package com.jgcomptech.tools.authz;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Thrown to indicate a requested operation or access to a requested resource is not allowed.
 * @since 1.5.0
 */
public class UnauthorizedException extends AuthorizationException {
    /**
     * Creates a new UnauthorizedException.
     */
    public UnauthorizedException() { }

    /**
     * Constructs a new UnauthorizedException.
     * @param message the reason for the exception
     */
    public UnauthorizedException(final String message) { super(message); }

    /**
     * Constructs a new UnauthorizedException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public UnauthorizedException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new UnauthorizedException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public UnauthorizedException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown UnauthorizedException!" );
        } catch (final UnauthorizedException ignore) { }
    }
}
