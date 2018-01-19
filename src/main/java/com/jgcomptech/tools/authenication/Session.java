package com.jgcomptech.tools.authenication;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An object representing a user login session.
 * @since 1.4.0
 */
public class Session {
    private final String username;
    private final UserRole userRole;

    /**
     * Creates an instance of a session.
     * @param username the username of the logged in user
     * @param userRole the user role of the logged in user
     */
    public Session(final String username, final UserRole userRole) {
        this.username = username;
        this.userRole = userRole;
    }

    /**
     * Returns the username of the logged in user.
     * @return the username of the logged in user
     */
    public String getUsername() { return username; }

    /**
     * Returns the user role of the logged in user.
     * @return the user role of the logged in user
     */
    public UserRole getUserRole() { return userRole; }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Session)) return false;

        Session session = (Session) o;

        return new EqualsBuilder()
                .append(getUsername(), session.getUsername())
                .append(getUserRole(), session.getUserRole())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getUsername())
                .append(getUserRole())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("username", username)
                .append("userRole", userRole)
                .toString();
    }
}
