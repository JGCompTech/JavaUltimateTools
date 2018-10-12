package com.jgcomptech.tools.databasetools.jdbc.builders;

import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.TypedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A builder class for creating a SELECT sql statement to query the database.
 * @since 1.4.0
 */
public class QueryBuilder extends SQLBuilder {
    private enum SelectType {
        AVG,
        COUNT,
        SUM,
        MAX,
        MIN,
        DISTINCT_AVG,
        DISTINCT_COUNT,
        DISTINCT_SUM
    }

    /**
     * This statement returns all rows in all columns from the table.
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_ALL() {
        assertSQLIsEmpty();
        getSql().append("SELECT * ");
        return this;
    }

    /**
     * This statement counts all rows in all columns from the result of the the specified query.
     * The result table will have the column name "FinalCount".
     * @param builder the {@code QueryBuilder} containing the sql statement to retrieve results from
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_COUNT_ALL_FROM(final QueryBuilder builder) {
        assertSQLIsEmpty();
        getSql().append("SELECT COUNT(*) ").append("AS FinalCount ");
        getSql().append("FROM (").append(builder.toString().replace(";", "")).append(')');
        return this;
    }

    /**
     * This statement returns all rows with only the specified columns from the table.
     * @param columnNames the names of the columns to return data from
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT(final String... columnNames) {
        assertSQLIsEmpty();
        getSql().append("SELECT ");
        if(columnNames == null || columnNames.length == 0) getSql().append("* ");
        else {
            var firstAdded = false;
            for (final var columnName : columnNames) {
                if(firstAdded) {
                    getSql().append(", ").append(columnName);
                } else getSql().append(columnName);
                firstAdded = true;
            }

            getSql().append(' ');
        }
        return this;
    }

    /**
     * This statement returns all rows with only the specified columns from the table excluding duplicate rows.
     * @param columnNames the names of the columns to return data from
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_DISTINCT(final String... columnNames) {
        assertSQLIsEmpty();
        getSql().append("SELECT DISTINCT ");
        if(columnNames == null || columnNames.length == 0) getSql().append("* ");
        else {
            var firstAdded = false;
            for (final var columnName : columnNames) {
                if(firstAdded) {
                    getSql().append(", ").append(columnName);
                } else getSql().append(columnName);
                firstAdded = true;
            }
            getSql().append(' ');
        }

        return this;
    }

    /**
     * This statement returns the number of rows in the table.
     * @param columnName the name of the column that will be used as the column in the result table
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_COUNT(final String columnName) { return selectBuilder(columnName, SelectType.COUNT); }

    /**
     * This statement returns the number of rows in the table excluding duplicate rows.
     * @param columnName the name of the column that will be used as the column in the result table
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_DISTINCT_COUNT(final String columnName) {
        return selectBuilder(columnName, SelectType.DISTINCT_COUNT); }

    /**
     * This statement returns the average of all the values in the specified column.
     * @param columnName the column to average the values
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_AVG(final String columnName) { return selectBuilder(columnName, SelectType.AVG); }

    /**
     * This statement returns the average of all the values in the specified column excluding duplicate values.
     * @param columnName the column to average the values
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_DISTINCT_AVG(final String columnName) {
        return selectBuilder(columnName, SelectType.DISTINCT_AVG); }

    /**
     * This statement returns the sum of all the values in the specified column.
     * @param columnName the column to average the values
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_SUM(final String columnName) { return selectBuilder(columnName, SelectType.SUM); }

    /**
     * This statement returns the sum of all the values in the specified column excluding duplicate values.
     * @param columnName the column to average the values
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_DISTINCT_SUM(final String columnName) {
        return selectBuilder(columnName, SelectType.DISTINCT_SUM); }

    /**
     * This statement returns the lowest value of all the values in the specified column.
     * @param columnName the column to average the values
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_MIN(final String columnName) { return selectBuilder(columnName, SelectType.MIN); }

    /**
     * This statement returns the highest value of all the values in the specified column.
     * @param columnName the column to average the values
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    public QueryBuilder SELECT_MAX(final String columnName) { return selectBuilder(columnName, SelectType.MAX); }

    /**
     * Builds the SELECT statement with the specified statement type.
     * @param columnName the column to use for the query
     * @param type the statement type
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a select statement has already been specified
     */
    private QueryBuilder selectBuilder(final String columnName, final SelectType type) {
        assertSQLIsEmpty();
        if(columnName.trim().isEmpty()) {
            switch(type) {
                case AVG:
                    getSql().append("SELECT AVG(");
                    break;
                case COUNT:
                    getSql().append("SELECT COUNT(");
                    break;
                case SUM:
                    getSql().append("SELECT SUM(");
                    break;
                case MAX:
                    getSql().append("SELECT MAX(");
                    break;
                case MIN:
                    getSql().append("SELECT MIN(");
                    break;
                case DISTINCT_AVG:
                    getSql().append("SELECT AVG(DISTINCT ");
                    break;
                case DISTINCT_COUNT:
                    getSql().append("SELECT COUNT(DISTINCT ");
                    break;
                case DISTINCT_SUM:
                    getSql().append("SELECT SUM(DISTINCT ");
                    break;
            }

            getSql().append(columnName).append(") ");
            return this;
        } else throw new IllegalArgumentException("Column Name Cannot Be Empty!");
    }

    /**
     * Specifies the name of the table to use for the query.
     * @param tableName the name of the table
     * @return the instance of the builder to continue building
     * @throws IllegalStateException if a SELECT statement is not called first
     */
    public QueryBuilder FROM(final String tableName) {
        assertSQLIsNotEmpty();
        if(tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table Name Cannot Be Empty!");
        } else {
            getSql().append("FROM ").append(tableName).append(' ');
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
    public QueryBuilder WHERE(final String columnName, final String value) {
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
    public QueryBuilder WHERE_OR(final String columnName, final String value) {
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
    public QueryBuilder WHERE_NOT(final String columnName, final String value) {
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
    public QueryBuilder WHERE_NOT_OR(final String columnName, final String value) {
        new WhereBuilder(getSql()).where(columnName, true, true, value);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the specified column is NULL
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @return the instance of the builder to continue building
     */
    public QueryBuilder WHERE_IS_NULL(final String columnName) {
        new WhereBuilder(getSql()).whereIsNull(columnName, false, false);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the specified column is NOT NULL
     * and if this is not the first WHERE added it is separated by AND.
     * @param columnName the column name to check against
     * @return the instance of the builder to continue building
     */
    public QueryBuilder WHERE_IS_NOT_NULL(final String columnName) {
        new WhereBuilder(getSql()).whereIsNull(columnName, true, false);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the specified column is NULL
     * and if this is not the first WHERE added it is separated by OR.
     * @param columnName the column name to check against
     * @return the instance of the builder to continue building
     */
    public QueryBuilder WHERE_IS_NULL_OR(final String columnName) {
        new WhereBuilder(getSql()).whereIsNull(columnName, false, true);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks if the specified column is NOT NULL
     * and if this is not the first WHERE added it is separated by OR.
     * @param columnName the column name to check against
     * @return the instance of the builder to continue building
     */
    public QueryBuilder WHERE_IS_NOT_NULL_OR(final String columnName) {
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
    public QueryBuilder WHERE_IN(final String columnName, final String... values) {
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
    public QueryBuilder WHERE_NOT_IN(final String columnName, final String... values) {
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
    public QueryBuilder WHERE_IN_OR(final String columnName, final String... values) {
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
    public QueryBuilder WHERE_NOT_IN_OR(final String columnName, final String... values) {
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
    public QueryBuilder WHERE_BETWEEN(final String columnName, final String start, final String stop) {
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
    public QueryBuilder WHERE_NOT_BETWEEN(final String columnName, final String start, final String stop) {
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
    public QueryBuilder WHERE_BETWEEN_OR(final String columnName, final String start, final String stop) {
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
    public QueryBuilder WHERE_NOT_BETWEEN_OR(final String columnName, final String start, final String stop) {
        new WhereBuilder(getSql()).whereBetween(columnName, true, true, start, stop);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if any record exists in the sub query
     * and if this is not the first WHERE added it is separated by AND.
     * @param builder the {@code QueryBuilder} containing the sql statement to retrieve results from
     * @return the instance of the builder to continue building
     */
    public QueryBuilder WHERE_EXISTS(final QueryBuilder builder) {
        new WhereBuilder(getSql()).whereExists(builder, false, false);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if any record exists in the sub query
     * and if this is not the first WHERE added it is separated by OR.
     * @param builder the {@code QueryBuilder} containing the sql statement to retrieve results from
     * @return the instance of the builder to continue building
     */
    public QueryBuilder WHERE_EXISTS_OR(final QueryBuilder builder) {
        new WhereBuilder(getSql()).whereExists(builder, false, true);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if no records exist in the sub query
     * and if this is not the first WHERE added it is separated by AND.
     * @param builder the {@code QueryBuilder} containing the sql statement to retrieve results from
     * @return the instance of the builder to continue building
     */
    public QueryBuilder WHERE_NOT_EXISTS(final QueryBuilder builder) {
        new WhereBuilder(getSql()).whereExists(builder, true, false);
        return this;
    }

    /**
     * Specifies a condition to constrain the SELECT statement that checks
     * if no records exist in the sub query
     * and if this is not the first WHERE added it is separated by OR.
     * @param builder the {@code QueryBuilder} containing the sql statement to retrieve results from
     * @return the instance of the builder to continue building
     */
    public QueryBuilder WHERE_NOT_EXISTS_OR(final QueryBuilder builder) {
        new WhereBuilder(getSql()).whereExists(builder, true, true);
        return this;
    }

    /**
     * Sorts the result-set in ascending order by the specified column.
     * @param columnName the name of the column to sort
     * @return the instance of the builder to continue building
     */
    public QueryBuilder ORDER_BY(final String columnName) {
        assertSQLIsNotEmpty();
        if(!columnName.trim().isEmpty()) {
            if(getSql().toString().contains("FROM")) {
                getSql().append("ORDER BY ").append(columnName).append(' ');
            } else throw new IllegalStateException("No Statement Exists To Order Results");
            return this;
        } throw new IllegalArgumentException("Field Name Cannot Be Empty!");
    }

    /**
     * Sorts the result-set in ascending or descending order by the specified column.
     * @param columnName the name of the column to sort
     * @param byDescending if true sorts descending otherwise sorts ascending
     * @return the instance of the builder to continue building
     */
    public QueryBuilder ORDER_BY(final String columnName, final boolean byDescending) {
        assertSQLIsNotEmpty();
        if(!columnName.trim().isEmpty()) {
            if(getSql().toString().contains("FROM")) {
                if(byDescending) getSql().append("ORDER BY ").append(columnName).append(" DESC ");
                else getSql().append("ORDER BY ").append(columnName).append(" ASC ");
            } else throw new IllegalStateException("No Statement Exists To Order Results");
            return this;
        } throw new IllegalArgumentException("Field Name Cannot Be Empty!");
    }

    private void assertSQLIsEmpty() {
        if(!getSql().toString().trim().isEmpty()) {
            throw new IllegalStateException("SELECT Can Only Be Added To Beginning Of Statement!");
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
     * Runs buildPreparedStatement, executes the statement and returns the result-set.
     * @param db the database to request the {@code PreparedStatement} object from
     * @return the result of the statement as a {@code ResultSet}
     * @throws SQLException if a database access error occurs
     */
    public ResultSet buildAndExecute(final Database db) throws SQLException {
        setStatement(buildPreparedStatement(db));
        return getStatement().executeQuery();
    }

    /**
     * Runs buildPreparedStatement, executes the statement and returns the number of rows in the result-set.
     * @param db the database to request the {@code PreparedStatement} object from
     * @return the result of the statement as the number of rows updated
     * @throws SQLException if a database access error occurs
     */
    public int buildExecuteAndGetNumRows(final Database db) throws SQLException {
        setStatement(buildPreparedStatement(db));
        try(final var rs = getStatement().executeQuery()) {
            var totalRows = 0;
            try { while(rs.next()) totalRows++; }
            catch(final SQLException ex) { return 0; }
            return totalRows;
        }
    }
}
