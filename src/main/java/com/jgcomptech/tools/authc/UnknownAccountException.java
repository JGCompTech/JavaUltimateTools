package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Thrown when attempting to authenticate with a principal that doesn't exist in the system (e.g.
 * by specifying a username that doesn't relate to a user account).
 *
 * <p>Whether or not an application wishes to alert a user logging in to the system of this fact is
 * at the discretion of those responsible for designing the view and what happens when this
 * exception occurs.
 * @since 1.5.0
 */
public class UnknownAccountException extends AccountException {
    /**
     * Creates a new UnknownAccountException.
     */
    public UnknownAccountException() { }

    /**
     * Constructs a new UnknownAccountException.
     * @param message the reason for the exception
     */
    public UnknownAccountException(final String message) { super(message); }

    /**
     * Constructs a new UnknownAccountException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public UnknownAccountException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new UnknownAccountException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public UnknownAccountException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown UnknownAccountException!" );
        } catch (final UnknownAccountException ignore) { }
    }
}
