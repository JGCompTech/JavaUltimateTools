package com.jgcomptech.tools.databasetools.jdbc.builders;

import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.DatabaseType;
import com.jgcomptech.tools.databasetools.jdbc.TypedStatement;
import org.jetbrains.annotations.Contract;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An abstract builder class for creating sql statements.
 * @since 1.4.0
 */
public abstract class SQLBuilder {
    private final StringBuilder sql = new StringBuilder();
    private PreparedStatement statement;

    /**
     * Creates a {@code PreparedStatement} object that will generate
     * {@code ResultSet} objects for sending SQL statements to the database.
     * @param db the database to request the {@code PreparedStatement} object from
     * @return a new {@code PreparedStatement} object containing the pre-compiled SQL statement
     * @throws SQLException if a database access error occurs
     */
    public final PreparedStatement buildPreparedStatement(final Database db) throws SQLException {
        if(sql.toString().trim().isEmpty()) { throw new IllegalArgumentException("SQL String Not Set!"); }
        final var resultSetType = db.getInfo().getDbType()
                == DatabaseType.SQLite
                ? ResultSet.TYPE_FORWARD_ONLY
                : ResultSet.TYPE_SCROLL_INSENSITIVE;
        final var sqlText = sql.toString().trim() + ';';
        return db.getConnection().getObject().prepareStatement(sqlText, resultSetType, ResultSet.CONCUR_READ_ONLY);
    }

    /**
     * Runs buildPreparedStatement and passes this object to a new instance of {@link TypedStatement}.
     * @param db the database to request the {@code PreparedStatement} object from
     * @return a new instance of {@link TypedStatement}
     * @throws SQLException if a database access error occurs
     */
    public abstract TypedStatement build(Database db) throws SQLException;

    /**
     * Returns the {@code PreparedStatement} object that will generate
     * {@code ResultSet} objects for sending SQL statements to the database.
     * @return the {@code PreparedStatement} object, null if buildPreparedStatement has not been run
     */
    @Contract(pure = true)
    public final PreparedStatement getStatement() { return statement; }

    protected SQLBuilder setStatement(final PreparedStatement statement) {
        this.statement = statement;
        return this;
    }

    protected StringBuilder getSql() {
        return sql;
    }

    /**
     * Returns the sql statement as a string.
     * @return the sql statement as a string
     */
    @Override
    public String toString() { return sql.toString().trim() + ';'; }
}
