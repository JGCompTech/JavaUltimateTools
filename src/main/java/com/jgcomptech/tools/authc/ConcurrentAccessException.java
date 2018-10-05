package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Thrown when an authentication attempt has been received for an account that has already been
 * authenticated (i.e. logged-in), and the system is configured to prevent such concurrent access.
 *
 * <p>This is useful when an application must ensure that only one person is logged-in to a single
 * account at any given time.
 *
 * <p>Sometimes account names and passwords are lazily given away
 * to many people for easy access to a system.  Such behavior is undesirable in systems where
 * users are accountable for their actions, such as in government applications, or when licensing
 * agreements must be maintained, such as those which only allow 1 user per paid license.
 *
 * <p>By disallowing concurrent access, such systems can ensure that each authenticated session
 * corresponds to one and only one user at any given time.
 * @since 1.5.0
 */
public class ConcurrentAccessException extends AccountException {
    /**
     * Creates a new ConcurrentAccessException.
     */
    public ConcurrentAccessException() { }

    /**
     * Constructs a new ConcurrentAccessException.
     * @param message the reason for the exception
     */
    public ConcurrentAccessException(final String message) { super(message); }

    /**
     * Constructs a new ConcurrentAccessException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public ConcurrentAccessException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new ConcurrentAccessException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public ConcurrentAccessException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown ConcurrentAccessException!" );
        } catch (final ConcurrentAccessException ignore) { }
    }
}
