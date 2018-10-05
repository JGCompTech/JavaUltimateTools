package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * A special kind of {@code DisabledAccountException}, this exception is thrown when attempting
 * to authenticate and the corresponding account has been disabled explicitly due to being locked.
 *
 * <p>For example, an account can be locked if an administrator explicitly locks an account or
 * perhaps an account can be locked automatically by the system if too many unsuccessful
 * authentication attempts take place during a specific period of time (perhaps indicating a
 * hacking attempt).
 * @since 1.5.0
 */
public class LockedAccountException extends DisabledAccountException {
    /**
     * Creates a new LockedAccountException.
     */
    public LockedAccountException() { }

    /**
     * Constructs a new LockedAccountException.
     * @param message the reason for the exception
     */
    public LockedAccountException(final String message) { super(message); }

    /**
     * Constructs a new LockedAccountException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public LockedAccountException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new LockedAccountException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public LockedAccountException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown LockedAccountException!" );
        } catch (final LockedAccountException ignore) { }
    }
}
