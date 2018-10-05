package com.jgcomptech.tools.authc;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A simple username/password authentication token to support the most widely-used authentication mechanism.
 * <p>&quot;Remember Me&quot; authentications are disabled by default, but if the application developer wishes to allow
 * it for a login attempt, all that is necessary is to call {@link #setRememberMe setRememberMe(true)}.
 * If the underlying {@code SecurityManager} implementation also supports {@code RememberMe} services,
 * the user's identity will be remembered across sessions.
 * <p>Note that this class stores a password as a char[] instead of a String
 * (which may seem more logical).  This is because Strings are immutable and their
 * internal value cannot be overwritten - meaning even a nulled String instance might be accessible in memory at a later
 * time (e.g. memory dump).  This is not good for sensitive information such as passwords. For more information, see the
 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/security/jce/JCERefGuide.html#PBEEx">
 * Java Cryptography Extension Reference Guide</a>.
 * <p>To avoid this possibility of later memory access, the application developer should always call
 * {@link #clear() clear()} after using the token to perform a login attempt.
 * @since 1.5.0
 */
public class UsernamePasswordToken {
    /**
     * The username.
     */
    private String username;

    /**
     * The password, in char[] format.
     */
    private char[] password;

    /**
     * Whether or not 'rememberMe' should be enabled for the corresponding login attempt;
     * default is {@code false}.
     */
    private boolean rememberMe;

    /**
     * JavaBeans compatible no-arg constructor.
     */
    public UsernamePasswordToken() { }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted
     * during an authentication attempt, with a {@code rememberMe}  default of {@code false} .
     *
     * @param username the username submitted for authentication
     * @param password the password character array submitted for authentication
     */
    public UsernamePasswordToken(final String username, final char[] password) {
        this(username, password, false);
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted
     * during an authentication attempt, with a {@code rememberMe}  default of {@code false}
     * <p>This is a convenience constructor and maintains the password internally via a character
     * array, i.e. {@code password.toCharArray();} .  Note that storing a password as a String
     * in your code could have possible security implications as noted in the class JavaDoc.
     *
     * @param username the username submitted for authentication
     * @param password the password string submitted for authentication
     */
    public UsernamePasswordToken(final String username, final String password) {
        this(username, password != null ? password.toCharArray() : null, false);
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted, as well as if the user
     * wishes their identity to be remembered across sessions.
     *
     * @param username   the username submitted for authentication
     * @param password   the password string submitted for authentication
     * @param rememberMe if the user wishes their identity to be remembered across sessions
     */
    public UsernamePasswordToken(final String username, final char[] password, final boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted, as well as if the user
     * wishes their identity to be remembered across sessions.
     * <p>This is a convenience constructor and maintains the password internally via a character
     * array, i.e. {@code password.toCharArray();} .  Note that storing a password as a String
     * in your code could have possible security implications as noted in the class JavaDoc.
     *
     * @param username   the username submitted for authentication
     * @param password   the password string submitted for authentication
     * @param rememberMe if the user wishes their identity to be remembered across sessions
     */
    public UsernamePasswordToken(final String username, final String password, final boolean rememberMe) {
        this(username, password != null ? password.toCharArray() : null, rememberMe);
    }

    /*--------------------------------------------
    |  A C C E S S O R S / M O D I F I E R S    |
    ============================================*/

    /**
     * Returns the username submitted during an authentication attempt.
     *
     * @return the username submitted during an authentication attempt.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for submission during an authentication attempt.
     *
     * @param username the username to be used for submission during an authentication attempt.
     */
    public void setUsername(final String username) {
        this.username = username;
    }


    /**
     * Returns the password submitted during an authentication attempt as a character array.
     *
     * @return the password submitted during an authentication attempt as a character array.
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * Sets the password for submission during an authentication attempt.
     *
     * @param password the password to be used for submission during an authentication attempt.
     */
    public void setPassword(final char[] password) {
        this.password = password;
    }

    /**
     * Simply returns {@link #getUsername() getUsername()}.
     *
     * @return the {@link #getUsername() username}
     */
    public Object getPrincipal() { return username; }

    /**
     * Returns the {@link #getPassword() password} char array.
     *
     * @return the {@link #getPassword() password} char array
     */
    public Object getCredentials() {
        return getPassword();
    }

    /**
     * Returns {@code true}  if the submitting user wishes their identity (principal(s)) to be remembered
     * across sessions, {@code false}  otherwise.  Unless overridden, this value is {@code false}  by default.
     *
     * @return {@code true}  if the submitting user wishes their identity (principal(s)) to be remembered
     *         across sessions, {@code false}  otherwise ({@code false}  by default).
     */
    public boolean isRememberMe() {
        return rememberMe;
    }

    /**
     * Sets if the submitting user wishes their identity (principal(s)) to be remembered across sessions.  Unless
     * overridden, the default value is {@code false} , indicating <em>not</em> to be remembered across sessions.
     *
     * @param rememberMe value indicating if the user wishes their identity (principal(s)) to be remembered across
     *                   sessions.
     */
    public void setRememberMe(final boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    /**
     * Clears out (nulls) the username, password, rememberMe. The password bytes are explicitly set to
     * {@code 0x00}  before nulling to eliminate the possibility of memory access at a later time.
     */
    public void clear() {
        username = null;
        rememberMe = false;

        if (password != null) {
            for (var i = 0; i < password.length; i++) {
                password[i] = 0x00;
            }
            password = null;
        }

    }

    /**
     * Returns the String representation.  It does not include the password in the resulting
     * string for security reasons to prevent accidentally printing out a password
     * that might be widely viewable).
     *
     * @return the String representation of the {@code UsernamePasswordToken} , omitting the password.
     */

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("username", username)
                .append("rememberMe", rememberMe)
                .toString();
    }
}
