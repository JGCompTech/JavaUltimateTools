package com.jgcomptech.tools.databasetools.jdbc.builders;

import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.TypedStatement;

import java.sql.SQLException;

/**
 * A builder class for creating an INSERT sql statement to add a row to a table.
 * @since 1.4.0
 */
public class InsertBuilder extends SQLBuilder {
    /**
     * Generates sql code to insert a row into a table.
     * @param tableName the name of the table
     * @param columnNames the name of the columns
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if this is not the first method called
     */
    public InsertBuilder INSERT_INTO(final String tableName, final String... columnNames) {
        assertSQLIsEmpty();
        if(tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table Name Cannot Be Empty!");
        } if(columnNames == null || columnNames.length == 0) {
            throw new IllegalArgumentException("Column Names Cannot Be Empty!");
        } else {
            getSql().append("INSERT INTO ").append(tableName).append(" (");
            var firstAdded = false;
            for (final var columnName : columnNames) {
                if(firstAdded) {
                    getSql().append(", ").append(columnName);
                } else getSql().append(columnName);
                firstAdded = true;
            }

            getSql().append(") ");
            return this;
        }
    }

    /**
     * Finishes the INSERT statement specifying the values to insert.
     * The order of the VALUES statements must match the column order in the INSERT statement.
     * @param values the values to insert into the new row
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if the INSERT_INTO statement is not called first
     */
    public InsertBuilder VALUES(final String... values) {
        assertSQLIsNotEmpty();
        if(values == null || values.length == 0) {
            throw new IllegalArgumentException("Column Names Cannot Be Empty!");
        } else {
            getSql().append("VALUES (");
            var firstAdded = false;
            for (final var value : values) {
                if(firstAdded) {
                    getSql().append(", '").append(value).append('\'');
                } else getSql().append('\'').append(value).append('\'');
                firstAdded = true;
            }

            getSql().append(") ");
            return this;
        }
    }

    private void assertSQLIsEmpty() {
        if(!getSql().toString().trim().isEmpty()) {
            throw new IllegalStateException("INSERT INTO Can Only Be Added To Beginning Of Statement!");
        }
    }

    private void assertSQLIsNotEmpty() {
        if(getSql().toString().trim().isEmpty()) {
            throw new IllegalStateException("Statement Empty, Please Add Statement!");
        }
    }

    /**
     * Runs buildPreparedStatement and passes this object to a new instance of {@link TypedStatement}.
     * @param db the database to request the {@code PreparedStatement} object from
     * @return a new instance of {@link TypedStatement}
     * @throws SQLException if a database access error occurs
     */
    @Override
    public TypedStatement build(final Database db) throws SQLException {
        setStatement(buildPreparedStatement(db));
        return new TypedStatement(this);
    }

    /**
     * Runs buildPreparedStatement, executes the statement and returns the number of rows updated.
     * @param db the database to request the {@code PreparedStatement} object from
     * @return the result of the statement as the number of rows updated
     * @throws SQLException if a database access error occurs
     */
    public int buildAndInsert(final Database db) throws SQLException {
        setStatement(buildPreparedStatement(db));
        getStatement().closeOnCompletion();
        return getStatement().executeUpdate();
    }
}
