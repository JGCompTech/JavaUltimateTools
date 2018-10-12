package com.jgcomptech.tools.databasetools.jdbc.builders;

import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.TypedStatement;

import java.sql.SQLException;

/**
 * A builder class for creating an UPDATE sql statement to update a row in a table.
 * @since 1.4.0
 */
public class UpdateBuilder extends SQLBuilder {
    /**
     * Generates sql code to update a row in a table.
     * @param tableName the name of the table
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if this is not the first method called
     */
    public UpdateBuilder UPDATE(final String tableName) {
        assertSQLIsEmpty();
        if(tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table Name Cannot Be Empty!");
        } else {
            getSql().append("UPDATE ").append(tableName).append(' ');
            return this;
        }
    }

    /**
     * Sets the values to replace in the table.
     * @param columnName the name of the column
     * @param value the value to set
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if the UPDATE statement is not called first
     */
    public UpdateBuilder SET(final String columnName, final String value) {
        assertSQLIsNotEmpty();
        if(columnName == null || columnName.trim().isEmpty() || value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Field Name And Value Cannot Be Empty!");
        } else {
            if(!getSql().toString().contains("SET ")) {
                getSql().append("SET ").append(columnName).append(" = '").append(value).append("' ");
            } else {
                getSql().append(", ").append(columnName).append(" = '").append(value).append("' ");
            }
            return this;
        }
    }

    /**
     * Specifies a condition to constrain the SELECT statement
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @param value the expected value
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE(final String columnName, final String value) {
        new WhereBuilder(getSql()).where(columnName, false, false, value);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement
     * and if this is not the first WHERE added it is separated by OR.
     * @param columnName the column name to check against
     * @param value the expected value
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_OR(final String columnName, final String value) {
        new WhereBuilder(getSql()).where(columnName, false, true, value);
        return this;
    }

    /**
     * Specifies an inverse condition using NOT to constrain the SELECT statement
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @param value the expected value
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_NOT(final String columnName, final String value) {
        new WhereBuilder(getSql()).where(columnName, true, false, value);
        return this;
    }

    /**
     * Specifies an inverse condition using NOT to constrain the SELECT statement
     * and if this is not the first WHERE added it is separated by OR.
     * @param columnName the column name to check against
     * @param value the expected value
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_NOT_OR(final String columnName, final String value) {
        new WhereBuilder(getSql()).where(columnName, true, true, value);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the specified column is NULL
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_IS_NULL(final String columnName) {
        new WhereBuilder(getSql()).whereIsNull(columnName, false, false);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the specified column is NOT NULL
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_IS_NOT_NULL(final String columnName) {
        new WhereBuilder(getSql()).whereIsNull(columnName, true, false);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the specified column is NULL
     * and if this is not the first WHERE added it is separated by OR.
     * @param columnName the column name to check against
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_IS_NULL_OR(final String columnName) {
        new WhereBuilder(getSql()).whereIsNull(columnName, false, true);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the specified column is NOT NULL
     * and if this is not the first WHERE added it is separated by OR.
     * @param columnName the column name to check against
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_IS_NOT_NULL_OR(final String columnName) {
        new WhereBuilder(getSql()).whereIsNull(columnName, true, true);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the column matches one of the
     * specified values.
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @param values a list of expected values
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_IN(final String columnName, final String... values) {
        new WhereBuilder(getSql()).whereIn(columnName, false, false, values);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the column does not match one of the
     * specified values.
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @param values a list of expected values
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_NOT_IN(final String columnName, final String... values) {
        new WhereBuilder(getSql()).whereIn(columnName, true, false, values);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the column matches one of the
     * specified values.
     * and if this is not the first WHERE added it is separated by OR.
     * @param columnName the column name to check against
     * @param values a list of expected values
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_IN_OR(final String columnName, final String... values) {
        new WhereBuilder(getSql()).whereIn(columnName, false, true, values);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the column does not match one of the
     * specified values.
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @param values a list of expected values
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_NOT_IN_OR(final String columnName, final String... values) {
        new WhereBuilder(getSql()).whereIn(columnName, true, true, values);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if the specified column value equals
     * the start or stop values or falls between those values
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @param start the inclusive beginning value
     * @param stop the inclusive ending value
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_BETWEEN(final String columnName, final String start, final String stop) {
        new WhereBuilder(getSql()).whereBetween(columnName, false, false, start, stop);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if the specified column value does not equal
     * the start or stop values and does not fall between those values
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @param start the inclusive beginning value
     * @param stop the inclusive ending value
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_NOT_BETWEEN(final String columnName, final String start, final String stop) {
        new WhereBuilder(getSql()).whereBetween(columnName, true, false, start, stop);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if the specified column value equals
     * the start or stop values or falls between those values
     * and if this is not the first WHERE added it is separated by OR.
     * @param columnName the column name to check against
     * @param start the inclusive beginning value
     * @param stop the inclusive ending value
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_BETWEEN_OR(final String columnName, final String start, final String stop) {
        new WhereBuilder(getSql()).whereBetween(columnName, false, true, start, stop);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if the specified column value does not equal
     * the start or stop values and does not fall between those values
     * and if this is not the first WHERE added it is separated by OR.
     * @param columnName the column name to check against
     * @param start the inclusive beginning value
     * @param stop the inclusive ending value
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_NOT_BETWEEN_OR(final String columnName, final String start, final String stop) {
        new WhereBuilder(getSql()).whereBetween(columnName, true, true, start, stop);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if any record exists in the sub query
     * and if this is not the first WHERE added it is separated by AND.
     * @param builder the {@code UpdateBuilder} containing the sql statement to retrieve results from
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_EXISTS(final QueryBuilder builder) {
        new WhereBuilder(getSql()).whereExists(builder, false, false);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if any record exists in the sub query
     * and if this is not the first WHERE added it is separated by OR.
     * @param builder the {@code UpdateBuilder} containing the sql statement to retrieve results from
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_EXISTS_OR(final QueryBuilder builder) {
        new WhereBuilder(getSql()).whereExists(builder, false, true);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if no records exist in the sub query
     * and if this is not the first WHERE added it is separated by AND.
     * @param builder the {@code UpdateBuilder} containing the sql statement to retrieve results from
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_NOT_EXISTS(final QueryBuilder builder) {
        new WhereBuilder(getSql()).whereExists(builder, true, false);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if no records exist in the sub query
     * and if this is not the first WHERE added it is separated by OR.
     * @param builder the {@code UpdateBuilder} containing the sql statement to retrieve results from
     * @return the instance of the builder to continue building
     */
    public UpdateBuilder WHERE_NOT_EXISTS_OR(final QueryBuilder builder) {
        new WhereBuilder(getSql()).whereExists(builder, true, true);
        return this;
    }

    private void assertSQLIsEmpty() {
        if(!getSql().toString().trim().isEmpty()) {
            throw new IllegalStateException("UPDATE Can Only Be Added To Beginning Of Statement!");
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
     * @return the result of the statement as a {@code ResultSet}
     * @throws SQLException if a database access error occurs
     */
    public int buildAndUpdate(final Database db) throws SQLException {
        setStatement(buildPreparedStatement(db));
        getStatement().closeOnCompletion();
        return getStatement().executeUpdate();
    }

    /**
     * Runs buildPreparedStatement, executes the statement and returns the the number of rows updated.
     * This method should be used when the returned row count may exceed {@link Integer#MAX_VALUE}.
     * @param db the database to request the {@code PreparedStatement} object from
     * @return the result of the statement as a {@code ResultSet}
     * @throws SQLException if a database access error occurs
     */
    public long buildAndLargeUpdate(final Database db) throws SQLException {
        setStatement(buildPreparedStatement(db));
        getStatement().closeOnCompletion();
        return getStatement().executeLargeUpdate();
    }
}
