package com.jgcomptech.tools.databasetools.jbdc;

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
     * @deprecated Added getter and setter.
     * As of release 1.4.0, replaced by {@link #getPath} and {@link #setPath(String)}.
     */
    @Deprecated
    public String Path = "";
    /**
     * The database username.
     * @deprecated Added getter and setter.
     * As of release 1.4.0, replaced by {@link #getUsername} and {@link #setUsername(String)}.
     */
    @Deprecated
    public String Username = "";
    /**
     * The database password.
     * @deprecated Added getter and setter.
     * As of release 1.4.0, replaced by {@link #getPassword()} and {@link #setPassword(String)}.
     */
    @Deprecated
    public String Password = "";
    /**
     * The database type.
     * @deprecated Added getter and setter.
     * As of release 1.4.0, replaced by {@link #getDBType()} and {@link #setDBType(DatabaseType)}.
     */
    @Deprecated
    public DatabaseType DBType;

    /**
     * Returns the database path.
     * @return the database path
     * @since 1.4.0
     */
    public String getPath() { return Path; }

    /**
     * Sets the database path.
     * @param path the database path
     * @return an instance of this object for use as a builder pattern
     * @since 1.4.0
     */
    public DatabaseConnectionInfo setPath(final String path) {
        Path = path;
        return this;
    }

    /**
     * Returns the database username.
     * @return the database username
     * @since 1.4.0
     */
    public String getUsername() { return Username; }

    /**
     * Sets the database username.
     * @param username the database username
     * @return an instance of this object for use as a builder pattern
     * @since 1.4.0
     */
    public DatabaseConnectionInfo setUsername(final String username) {
        Username = username;
        return this;
    }

    /**
     * Returns the database password.
     * @return the database password
     * @since 1.4.0
     */
    public String getPassword() { return Password; }

    /**
     * Sets the database password.
     * @param password the database password
     * @return an instance of this object for use as a builder pattern
     * @since 1.4.0
     */
    public DatabaseConnectionInfo setPassword(final String password) {
        Password = password;
        return this;
    }

    /**
     * Returns the database type.
     * @return the database type
     * @since 1.4.0
     */
    public DatabaseType getDBType() { return DBType; }

    /**
     * Sets the database type.
     * @param dbType the database type
     * @return an instance of this object for use as a builder pattern
     * @since 1.4.0
     */
    public DatabaseConnectionInfo setDBType(final DatabaseType dbType) {
        DBType = dbType;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof DatabaseConnectionInfo)) return false;

        DatabaseConnectionInfo connectionInfo = (DatabaseConnectionInfo) o;

        return new EqualsBuilder()
                .append(getPath(), connectionInfo.getPath())
                .append(getUsername(), connectionInfo.getUsername())
                .append(getPassword(), connectionInfo.getPassword())
                .append(getDBType(), connectionInfo.getDBType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPath())
                .append(getUsername())
                .append(getPassword())
                .append(getDBType())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Path", Path)
                .append("Username", Username)
                .append("DBType", DBType)
                .toString();
    }
}

