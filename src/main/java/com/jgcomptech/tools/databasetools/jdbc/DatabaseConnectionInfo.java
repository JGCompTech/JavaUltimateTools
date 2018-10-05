package com.jgcomptech.tools.databasetools.jdbc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Connection info object to be used to connect to a database.
 * @since 1.3.0
 */
public class DatabaseConnectionInfo {
    /**
     * The database path.
     */
    private String path = "";
    /**
     * The database username.
     */
    private String username = "";
    /**
     * The database password.
     */
    private String password = "";
    /**
     * The database type.
     */
    private DatabaseType dbType;

    /**
     * Returns the database path.
     * @return the database path
     * @since 1.4.0
     */
    public String getPath() { return path; }

    /**
     * Sets the database path.
     * @param path the database path
     * @return an instance of this object for use as a builder pattern
     * @since 1.4.0
     */
    public DatabaseConnectionInfo setPath(final String path) {
        this.path = path;
        return this;
    }

    /**
     * Returns the database username.
     * @return the database username
     * @since 1.4.0
     */
    public String getUsername() { return username; }

    /**
     * Sets the database username.
     * @param username the database username
     * @return an instance of this object for use as a builder pattern
     * @since 1.4.0
     */
    public DatabaseConnectionInfo setUsername(final String username) {
        this.username = username;
        return this;
    }

    /**
     * Returns the database password.
     * @return the database password
     * @since 1.4.0
     */
    public String getPassword() { return password; }

    /**
     * Sets the database password.
     * @param password the database password
     * @return an instance of this object for use as a builder pattern
     * @since 1.4.0
     */
    public DatabaseConnectionInfo setPassword(final String password) {
        this.password = password;
        return this;
    }

    /**
     * Returns the database type.
     * @return the database type
     * @since 1.4.0
     */
    public DatabaseType getDBType() { return dbType; }

    /**
     * Sets the database type.
     * @param dbType the database type
     * @return an instance of this object for use as a builder pattern
     * @since 1.4.0
     */
    public DatabaseConnectionInfo setDBType(final DatabaseType dbType) {
        this.dbType = dbType;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof DatabaseConnectionInfo)) return false;

        final var connectionInfo = (DatabaseConnectionInfo) o;

        return new EqualsBuilder()
                .append(path, connectionInfo.path)
                .append(username, connectionInfo.username)
                .append(password, connectionInfo.password)
                .append(dbType, connectionInfo.dbType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(path)
                .append(username)
                .append(password)
                .append(dbType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("path", path)
                .append("username", username)
                .append("dbType", dbType)
                .toString();
    }
}

