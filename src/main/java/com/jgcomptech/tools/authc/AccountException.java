package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Exception thrown due to a problem with the account
 * under which an authentication attempt is being executed.
 * @since 1.5.0
 */
public class AccountException extends AuthenticationException {
    /**
     * Creates a new AccountException.
     */
    public AccountException() { }

    /**
     * Constructs a new AccountException.
     * @param message the reason for the exception
     */
    public AccountException(final String message) { super(message); }

    /**
     * Constructs a new AccountException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public AccountException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new AccountException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public AccountException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown AccountException!" );
        } catch (final AccountException ignore) { }
    }
}
