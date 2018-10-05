package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.authz.AuthorizationException;
import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Exception thrown when attempting to execute an authorization action when a successful
 * authentication hasn't yet occurred.
 *
 * <p>Authorizations can only be performed after a successful
 * authentication because authorization data (roles, permissions, etc) must always be associated
 * with a known identity.  Such a known identity can only be obtained upon a successful log-in.
 * @since 1.5.0
 */
public class UnauthenticatedException extends AuthorizationException {
    /**
     * Creates a new UnauthenticatedException.
     */
    public UnauthenticatedException() { }

    /**
     * Constructs a new UnauthenticatedException.
     * @param message the reason for the exception
     */
    public UnauthenticatedException(final String message) { super(message); }

    /**
     * Constructs a new UnauthenticatedException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public UnauthenticatedException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new UnauthenticatedException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public UnauthenticatedException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown UnauthenticatedException!" );
        } catch (final UnauthenticatedException ignore) { }
    }
}
