package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.authz.SimpleAuthorizationInfo;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An object representing a user account.
 * @since 1.4.0
 * @since 1.5.0 made class immutable
 */
public class UserAccount implements Account {
    /** The authentication information (principals and credentials) for this account. */
    private final SimpleAuthenticationInfo authcInfo;
    /** The authorization information for this account. */
    private final SimpleAuthorizationInfo authzInfo;

    /**
     * Maps a user in the database to a object.
     * @param username the username
     * @param creationDate the date and time that the user was created
     * @param locked if the account is locked
     * @param hasPasswordExpiration if the password is set to expire
     * @param passwordExpirationDate the date the password is set to expire
     */
    public UserAccount(final String username, final LocalDateTime creationDate,
                       final boolean locked, final boolean hasPasswordExpiration,
                       final LocalDateTime passwordExpirationDate) {
        this(username, creationDate, locked, hasPasswordExpiration,
                passwordExpirationDate, new HashSet<>());
    }

    /**
     * Maps a user in the database to a object.
     * @param username the username
     * @param creationDate the date and time that the user was created
     * @param locked specifies if the account is locked
     * @param hasPasswordExpiration specifies if the password has an expiration date
     * @param passwordExpirationDate the password expiration date
     * @param roles the user roles to add
     */
    public UserAccount(final String username, final LocalDateTime creationDate,
                       final boolean locked, final boolean hasPasswordExpiration,
                       final LocalDateTime passwordExpirationDate,
                       final Set<String> roles) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty!");
        }
        authcInfo = new SimpleAuthenticationInfo(username, locked, creationDate,
                passwordExpirationDate, hasPasswordExpiration);
        authzInfo = new SimpleAuthorizationInfo(roles);
    }

    /**
     * Returns the username.
     * @return the username
     */
    @Override
    public String getUsername() { return authcInfo.getUsername(); }

    /**
     * Returns the Account's assigned roles.
     * @return the Account's assigned roles
     */
    @Override
    public Collection<String> getRoles() { return authzInfo.getRoles(); }

//    /**
//     * Returns all String-based permissions assigned to this Account.
//     * @return all String-based permissions assigned to this Account
//     */
//    @Override
//    public Collection<String> getStringPermissions() { return authzInfo.getStringPermissions(); }
//
//    /**
//     * Returns all object-based permissions assigned directly to this Account.
//     * @return all object-based permissions assigned directly to this Account
//     */
//    @Override
//    public Collection<Permission> getObjectPermissions() { return authzInfo.getObjectPermissions(); }

    /**
     * Returns {@code true} if this Account is locked and thus cannot be used to login.
     * @return {@code true} if this Account is locked and thus cannot be used to login
     */
    @Override
    public boolean isLocked() { return authcInfo.isLocked(); }

    /**
     * Returns whether or not the Account's password is expired.
     * This usually indicates that the password would need to changed before the account could be used.
     * @return whether or not the Account's credentials are expired
     * @since 1.5.0 changed implementation
     */
    @Override
    public boolean isPasswordExpired() { return authcInfo.isPasswordExpired(); }

    /**
     * Returns the date and time the Account was initially created.
     * @return the date and time the Account was initially created
     * @since 1.5.0
     */
    @Override
    public LocalDateTime getCreationDate() { return authcInfo.getCreationDate(); }

    /**
     * Returns the expiration date of the current password.
     * @return the expiration date of the current password
     * @since 1.5.0
     */
    @Override
    public LocalDateTime getPasswordExpirationDate() { return authcInfo.getPasswordExpirationDate(); }

    /**
     * Returns {@code true} if a password expiration date is set.
     * @return {@code true} if a password expiration date is set.
     * @since 1.5.0
     */
    @Override
    public boolean hasPasswordExpiration() { return authcInfo.hasPasswordExpiration(); }
}
