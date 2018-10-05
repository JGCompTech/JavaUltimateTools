package com.jgcomptech.tools.authc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

/**
 * A simple implementation of the AuthenticationInfo interface.
 * @since 1.5.0
 */
public class SimpleAuthenticationInfo implements AuthenticationInfo {
    /**
     * The username identifying the account.
     */
    private String username;
    /** Indicates this account is locked. */
    private boolean locked;
    /** The date and time when the account was created. */
    private LocalDateTime creationDate;
    /** The date and time when the account password is set to expire. */
    private LocalDateTime passwordExpirationDate;
    /** Indicates if the password is set to expire in the future. */
    private boolean hasPasswordExpiration;

    /**
     * Default no-argument constructor.
     */
    public SimpleAuthenticationInfo() { }

    public SimpleAuthenticationInfo(final String username, final boolean locked, final LocalDateTime creationDate,
                                    final LocalDateTime passwordExpirationDate, final boolean hasPasswordExpiration) {
        this.username = username;
        this.locked = locked;
        this.creationDate = creationDate;
        this.passwordExpirationDate = passwordExpirationDate;
        this.hasPasswordExpiration = hasPasswordExpiration;
    }

    /**
     * Returns the username.
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Returns {@code true} if this Account is locked and thus cannot be used to login.
     * @return {@code true} if this Account is locked and thus cannot be used to login
     */
    @Override
    public boolean isLocked() {
        return locked;
    }

    /**
     * Returns whether or not the Account's password is expired.
     * This usually indicates that the password would need to changed before the account could be used.
     * @return whether or not the Account's credentials are expired
     * @since 1.5.0 changed implementation
     */
    @Override
    public boolean isPasswordExpired() {
        return hasPasswordExpiration && LocalDateTime.now().isAfter(passwordExpirationDate);
    }

    /**
     * Returns the date and time the Account was initially created.
     * @return the date and time the Account was initially created
     * @since 1.5.0
     */
    @Override
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Returns the expiration date of the current password.
     * @return the expiration date of the current password
     * @since 1.5.0
     */
    @Override
    public LocalDateTime getPasswordExpirationDate() {
        return passwordExpirationDate;
    }

    /**
     * Returns {@code true} if a password expiration date is set.
     * @return {@code true} if a password expiration date is set.
     * @since 1.5.0
     */
    @Override
    public boolean hasPasswordExpiration() {
        return hasPasswordExpiration;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof SimpleAuthenticationInfo)) return false;

        final var simpleAuthenticationInfo = (SimpleAuthenticationInfo) o;

        return new EqualsBuilder()
                .append(username, simpleAuthenticationInfo.username)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(username)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("username", username)
                .toString();
    }
}
