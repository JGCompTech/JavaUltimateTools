package com.jgcomptech.tools.databasetools.jdbc.builders;

import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.TypedStatement;

import java.sql.SQLException;

/**
 * A builder class for creating a CREATE sql statement to create a new table index.
 * @since 1.4.0
 */
public class IndexBuilder extends SQLBuilder {
    private boolean columnAdded;
    private Database db;
    private String tableName;

    /**
     * Generates sql code for creation of a table index.
     * @param indexName the name of the index to create
     * @param tableName the table name
     * @param db the database object for database type checking
     * @return the instance of the builder to continue building
     * @throws SQLException if a database access error occurs
     * @throws IllegalStateException if this is not the first method called
     */
    public IndexBuilder CREATE(final String indexName,
                               final String tableName,
                               final Database db) throws SQLException {
        assertSQLIsEmpty();
        if(tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table Name Cannot Be Empty!");
        } else if(db == null) {
            throw new IllegalArgumentException("Database Cannot Be Null!");
        } else {
            if(!db.getInfo().tableExists(tableName)) throw new IllegalStateException("Table Not Found!");
            this.tableName = tableName;
            this.db = db;
            getSql().append("CREATE INDEX ").append(indexName).append(" ON ").append(tableName).append(" (");
            return this;
        }
    }

    /**
     * Generates sql code for creation of a table unique index.
     * @param indexName the name of the index to create
     * @param tableName the table name
     * @param db the database object for database type checking
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if this is not the first method called
     * @throws SQLException if a database access error occurs
     */
    public IndexBuilder CREATE_UNIQUE(final String indexName,
                               final String tableName,
                               final Database db) throws SQLException {
        assertSQLIsEmpty();
        if(tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table Name Cannot Be Empty!");
        } else if(db == null) {
            throw new IllegalArgumentException("Database Cannot Be Null!");
        } else {
            if(!db.getInfo().tableExists(tableName)) throw new IllegalStateException("Table Not Found!");
            this.tableName = tableName;
            this.db = db;
            getSql().append("CREATE UNIQUE INDEX ").append(indexName).append(" ON ").append(tableName).append(" (");
            return this;
        }
    }

    /**
     * Adds a column to the table using a sql string.
     * @param columnNames the names of the columns to add
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a CREATE statement is not called first
     */
    public IndexBuilder addColumn(final String... columnNames) {
        assertSQLIsNotEmpty();

        for (final var columnName : columnNames) {
            if(columnAdded) {
                getSql().append(", ").append(columnName);
            } else getSql().append(columnName);
            columnAdded = true;
        }
        return this;
    }

    private void assertSQLIsEmpty() {
        if(!getSql().toString().trim().isEmpty()) {
            throw new IllegalStateException("CREATE Can Only Be Added To Beginning Of Statement!");
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
        getSql().append(')');
        setStatement(buildPreparedStatement(db));
        return new TypedStatement(this);
    }

    /**
     * Runs buildPreparedStatement and passes this object to a new instance of {@link TypedStatement}.
     * @return a new instance of {@link TypedStatement}
     * @throws SQLException if a database access error occurs
     */
    public TypedStatement build() throws SQLException {
        getSql().append(')');
        setStatement(buildPreparedStatement(db));
        return new TypedStatement(this);
    }

    /**
     * Runs buildPreparedStatement, executes the statement and returns true if no errors occurred.
     * @return the result of the statement as a boolean, true if no errors occurred
     * @throws SQLException if a database access error occurs
     */
    public boolean buildAndCreate() throws SQLException {
        getSql().append(')');
        if(!db.getInfo().tableExists(tableName)) {
            setStatement(buildPreparedStatement(db));
            return getStatement().execute();
        } else return false;
    }
}
