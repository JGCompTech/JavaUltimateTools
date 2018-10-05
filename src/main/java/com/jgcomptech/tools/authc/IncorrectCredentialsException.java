package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Thrown when attempting to authenticate with credential(s) that do not match the actual
 * credentials associated with the account principal.
 *
 * <p>For example, this exception might be thrown if a user's password is &quot;secret&quot; and
 * &quot;secrets&quot; was entered by mistake.
 *
 * <p>Whether or not an application wishes to let
 * the user know if they entered incorrect credentials is at the discretion of those
 * responsible for defining the view and what happens when this exception occurs.
 * @since 1.5.0
 */
public class IncorrectCredentialsException extends CredentialsException {
    /**
     * Creates a new IncorrectCredentialsException.
     */
    public IncorrectCredentialsException() { }

    /**
     * Constructs a new IncorrectCredentialsException.
     * @param message the reason for the exception
     */
    public IncorrectCredentialsException(final String message) { super(message); }

    /**
     * Constructs a new IncorrectCredentialsException.
     * @param cause the underlying Throwable that caused this exception to be thrown
     */
    public IncorrectCredentialsException(final Throwable cause) { super(cause); }

    /**
     * Constructs a new IncorrectCredentialsException.
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown
     */
    public IncorrectCredentialsException(final String message, final Throwable cause) { super(message, cause); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown IncorrectCredentialsException!" );
        } catch (final IncorrectCredentialsException ignore) { }
    }
}
