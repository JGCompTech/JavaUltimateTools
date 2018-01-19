package com.jgcomptech.tools.databasetools.jbdc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class that creates a PreparedStatement without having to type native sql code.
 * @since 1.4.0
 */
public class TypedStatement {
    /** A list of possible statement types. */
    public enum Type {
        CREATE,
        DELETE,
        INSERT,
        QUERY,
        UPDATE,
    }

    private final PreparedStatement statement;
    private final String sql;
    private final Type type;

    /**
     * Creates a query SELECT statement.
     * @param builder the QueryBuilder that contains the statement
     * @throws SQLException if a database access error occurs
     */
    public TypedStatement(final QueryBuilder builder) throws SQLException {
        statement = builder.getStatement();
        statement.closeOnCompletion();
        sql = builder.toString();
        type = Type.QUERY;
    }

    /**
     * Creates an UPDATE statement.
     * @param builder the UpdateBuilder that contains the statement
     * @throws SQLException if a database access error occurs
     */
    public TypedStatement(final UpdateBuilder builder) throws SQLException {
        statement = builder.getStatement();
        statement.closeOnCompletion();
        sql = builder.toString();
        type = Type.UPDATE;
    }

    /**
     * Creates an DELETE statement.
     * @param builder the DeleteBuilder that contains the statement
     * @throws SQLException if a database access error occurs
     */
    public TypedStatement(final DeleteBuilder builder) throws SQLException {
        statement = builder.getStatement();
        statement.closeOnCompletion();
        sql = builder.toString();
        type = Type.DELETE;
    }

    /**
     * Creates an INSERT statement.
     * @param builder the InsertBuilder that contains the statement
     * @throws SQLException if a database access error occurs
     */
    public TypedStatement(final InsertBuilder builder) throws SQLException {
        statement = builder.getStatement();
        statement.closeOnCompletion();
        sql = builder.toString();
        type = Type.INSERT;
    }

    /**
     * Creates a table CREATE statement.
     * @param builder the TableBuilder that contains the statement
     * @throws SQLException if a database access error occurs
     */
    public TypedStatement(final TableBuilder builder) throws SQLException {
        statement = builder.getStatement();
        statement.closeOnCompletion();
        sql = builder.toString();
        type = Type.CREATE;
    }

    /**
     * Creates a index CREATE statement.
     * @param builder the IndexBuilder that contains the statement
     * @throws SQLException if a database access error occurs
     */
    public TypedStatement(final IndexBuilder builder) throws SQLException {
        statement = builder.getStatement();
        statement.closeOnCompletion();
        sql = builder.toString();
        type = Type.CREATE;
    }

    /**
     * Creates a new instance of the QueryBuilder.
     * @return a new instance of the QueryBuilder
     */
    public static QueryBuilder newQuery() { return new QueryBuilder(); }

    /**
     * Creates a new instance of the UpdateBuilder.
     * @return a new instance of the UpdateBuilder
     */
    public static UpdateBuilder newUpdate() { return new UpdateBuilder(); }

    /**
     * Creates a new instance of the DeleteBuilder.
     * @return a new instance of the DeleteBuilder
     */
    public static DeleteBuilder newDelete() { return new DeleteBuilder(); }

    /**
     * Creates a new instance of the InsertBuilder.
     * @return a new instance of the InsertBuilder
     */
    public static InsertBuilder newInsert() { return new InsertBuilder(); }

    /**
     * Creates a new instance of the TableBuilder.
     * @return a new instance of the TableBuilder
     */
    public static TableBuilder newTable() { return new TableBuilder(); }

    /**
     * Creates a new instance of the IndexBuilder.
     * @return a new instance of the IndexBuilder
     */
    public static IndexBuilder newIndex() { return new IndexBuilder(); }

    /**
     * Executes the statement and returns the result-set.
     * @return the result of the statement as a {@code ResultSet}
     * @throws SQLException if a database access error occurs
     * @throws UnsupportedOperationException if statement is not a query SELECT statement
     */
    public ResultSet executeQuery() throws SQLException {
        if(type == Type.QUERY) return statement.executeQuery();
        else throw new UnsupportedOperationException("Statement Cannot Be Run As A Query! The type is " + type);
    }

    /**
     * Executes the statement and returns the number of rows updated.
     * @return the result of the statement as the number of rows updated
     * @throws SQLException if a database access error occurs
     * @throws UnsupportedOperationException if statement is not a INSERT statement
     */
    public int executeInsert() throws SQLException {
        if(type == Type.INSERT) return statement.executeUpdate();
        else throw new UnsupportedOperationException("Statement Cannot Be Run As An Insert! The type is " + type);
    }

    /**
     * Executes the statement and returns the number of rows updated.
     * @return the result of the statement as a {@code ResultSet}
     * @throws SQLException if a database access error occurs
     * @throws UnsupportedOperationException if statement is not a UPDATE statement
     */
    public int executeUpdate() throws SQLException {
        if(type == Type.UPDATE) return statement.executeUpdate();
        else throw new UnsupportedOperationException("Statement Cannot Be Run As An Update! The type is " + type);
    }

    /**
     * Executes the statement and returns the the number of rows updated.
     * This method should be used when the returned row count may exceed {@link Integer#MAX_VALUE}.
     * @return the result of the statement as a {@code ResultSet}
     * @throws SQLException if a database access error occurs
     * @throws UnsupportedOperationException if statement is not a UPDATE statement
     */
    public long executeLargeUpdate() throws SQLException {
        if(type == Type.UPDATE) return statement.executeLargeUpdate();
        else throw new UnsupportedOperationException("Statement Cannot Be Run As An Update! The type is " + type);
    }

    /**
     * Executes the statement and returns true if no errors occurred.
     * @return the result of the statement as a boolean, true if no errors occurred
     * @throws SQLException if a database access error occurs
     * @throws UnsupportedOperationException if statement is not a CREATE statement
     */
    public boolean executeCreate() throws SQLException {
        if(type == Type.CREATE) return statement.execute();
        else throw new UnsupportedOperationException("Statement Cannot Be Run As A Create! The type is " + type);
    }

    /**
     * Returns the {@code PreparedStatement} object that will generate
     * {@code ResultSet} objects for sending SQL statements to the database.
     * @return the {@code PreparedStatement} object
     */
    public PreparedStatement getStatement() { return statement; }

    /**
     * Returns the statement type.
     * @return the statement type
     */
    public Type getType() { return type; }

    /**
     * Returns the sql statement as a string.
     * @return the sql statement as a string
     */
    @Override
    public String toString() { return sql; }
}
