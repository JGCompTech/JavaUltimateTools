package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Thrown during the authentication process when the system determines the submitted credential(s)
 * has expired and will not allow login.
 *
 * <p>This is most often used to alert a user that their credentials (e.g. password or
 * cryptography key) has expired and they should change the value.  In such systems, the component
 * invoking the authentication might catch this exception and redirect the user to an appropriate
 * view to allow them to update their password or other credentials mechanism.
 * @since 1.5.0
 */
public class ExpiredCredentialsException extends CredentialsException {
    /**
     * Creates a new ExpiredCredentialsException.
     */
    public ExpiredCredentialsException() { }

    /**
     * Constructs a new ExpiredCredentialsException.
     * @param message the reason for the exception
     */
    public ExpiredCredentialsException(final String message) { super(message); }

    /**
     * Constructs a new ExpiredCredentialsException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public ExpiredCredentialsException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new ExpiredCredentialsException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public ExpiredCredentialsException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown ExpiredCredentialsException!" );
        } catch (final ExpiredCredentialsException ignore) { }
    }
}
