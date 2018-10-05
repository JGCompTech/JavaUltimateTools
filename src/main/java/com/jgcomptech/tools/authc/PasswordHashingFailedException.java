package com.jgcomptech.tools.authc;

import org.jetbrains.annotations.NotNull;

import java.security.GeneralSecurityException;

import static org.junit.Assert.fail;

/**
 * Thrown to indicate that an exception occurred while hashing a user password.
 * @since 1.4.1
 */
public class PasswordHashingFailedException extends RuntimeException {
    /**
     * Constructs a new PasswordHashingFailedException with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public PasswordHashingFailedException(final String message) {
        super(message);
    }

    /**
     * Constructs a new PasswordHashingFailedException with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public PasswordHashingFailedException(final String message, final GeneralSecurityException cause) {
        super(message, cause);
    }

    /**
     * Constructs a new PasswordHashingFailedException with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     */
    public PasswordHashingFailedException(final GeneralSecurityException cause) {
        super(cause);
    }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown PasswordHashingFailedException!" );
        } catch (final PasswordHashingFailedException ignore) { }
    }
}
