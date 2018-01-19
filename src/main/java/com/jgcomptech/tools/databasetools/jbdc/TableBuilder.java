package com.jgcomptech.tools.databasetools.jbdc;

import java.sql.SQLException;

/**
 * A builder class for creating an CREATE sql statement to create a new table.
 * @since 1.4.0
 */
public class TableBuilder extends SQLBuilder {
    private boolean columnAdded;
    private Database db;
    private String tableName;

    /**
     * Generates sql code for creation of a table.
     * @param tableName the name of the table to create
     * @param db the database object for database type checking
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if this is not the first method called
     * @throws SQLException if a database access error occurs
     */
    public TableBuilder CREATE(final String tableName, final Database db) throws SQLException {
        assertSQLIsEmpty();
        if(tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Table Name Cannot Be Empty!");
        } else if(db == null) {
            throw new IllegalArgumentException("Database Cannot Be Null!");
        } else {
            if(db.getInfo().tableExists(tableName)) throw new IllegalStateException("Table Already Exists!");
            this.tableName = tableName;
            this.db = db;
            getSql().append("CREATE TABLE ").append(tableName).append(" (");
            return this;
        }
    }

    /**
     * Adds a column to the table using the ColumnBuilder class.
     * @param builder the ColumnBuilder instance
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if the CREATE statement is not called first
     */
    public TableBuilder addColumn(final ColumnBuilder builder) {
        assertSQLIsNotEmpty();
        if(columnAdded) getSql().append(", ");
        getSql().append(builder.build(db));
        columnAdded = true;
        return this;
    }

    /**
     * Adds a column to the table using a sql string.
     * @param columnSql the sql statement to create the column
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if the CREATE statement is not called first
     */
    public TableBuilder addColumn(final String columnSql) {
        assertSQLIsNotEmpty();
        if(columnAdded) getSql().append(", ");
        getSql().append(columnSql);
        columnAdded = true;
        return this;
    }

    private void assertSQLIsEmpty() {
        if(!getSql().toString().isEmpty()) {
            throw new IllegalStateException("CREATE Can Only Be Added To Beginning Of Statement!");
        }
    }

    private void assertSQLIsNotEmpty() {
        if(getSql().toString().isEmpty()) {
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
     * Uses the db object passed into the constructor.
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
