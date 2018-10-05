package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Thrown to indicate that a user manager checked exception was thrown, usually a SQLException.
 * @since 1.4.1
 */
public class UserManagerException extends RuntimeException {
    /**
     * Creates a new UserManagerException.
     */
    public UserManagerException() { }

    /**
     * Constructs a new UserManagerException.
     * @param message the reason for the exception
     */
    public UserManagerException(final String message) { super(message); }

    /**
     * Constructs a new UserManagerException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public UserManagerException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new UserManagerException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public UserManagerException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown UserManagerException!" );
        } catch (final UserManagerException ignore) { }
    }
}
