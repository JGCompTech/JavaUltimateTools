package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Exception thrown due to a problem with the credential(s) submitted for an
 * account during the authentication process.
 * @since 1.5.0
 */
public class CredentialsException extends AuthenticationException {
    /**
     * Creates a new CredentialsException.
     */
    public CredentialsException() { }

    /**
     * Constructs a new CredentialsException.
     * @param message the reason for the exception
     */
    public CredentialsException(final String message) { super(message); }

    /**
     * Constructs a new CredentialsException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public CredentialsException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new CredentialsException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public CredentialsException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown CredentialsException!" );
        } catch (final CredentialsException ignore) { }
    }
}
