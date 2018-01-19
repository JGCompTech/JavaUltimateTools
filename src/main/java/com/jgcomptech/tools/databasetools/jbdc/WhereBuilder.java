package com.jgcomptech.tools.databasetools.jbdc;

/**
 * A builder class for creating a WHERE sql statement to set a constraint on another statement.
 * @since 1.4.0
 */
public class WhereBuilder {
    private final StringBuilder sql;

    WhereBuilder(final StringBuilder sql) { this.sql = sql; }

    void where(final String columnName, final boolean useNot,
                                     final boolean useOr, final String value) {
        assertSQLIsNotEmpty();
        if(!columnName.isEmpty()) {
            if(!value.isEmpty()) {
                String compare = useNot ? "NOT " : "";
                sql.append(sql.toString().contains("WHERE") ? (useOr ? "OR " : "AND ") : "WHERE ");
                sql.append(compare).append(columnName).append(" = '").append(value).append("' ");
            } else throw new IllegalArgumentException("Value Cannot Be Empty!");
        } else throw new IllegalArgumentException("Field Name Cannot Be Empty!");
    }

    void whereIsNull(final String columnName, final boolean useNot, final boolean useOr) {
        assertSQLIsNotEmpty();
        if(!columnName.isEmpty()) {
            String compare = useNot ? " IS NOT NULL " : " IS NULL ";
            sql.append(sql.toString().contains("WHERE") ? (useOr ? "OR " : "AND ") : "WHERE ");
            sql.append(columnName).append(compare);
        } else throw new IllegalArgumentException("Field Name Cannot Be Empty!");
    }

    void whereIn(final String columnName, final boolean useNot,
                                        final boolean useOr, final String[] values) {
        assertSQLIsNotEmpty();
        if(!columnName.isEmpty()) {
            if(values == null || values.length == 0) {
                throw new IllegalArgumentException("Values Cannot Be Empty!");
            } else {
                String compare = useNot ? " NOT IN (" : " IN (";
                sql.append(sql.toString().contains("WHERE") ? (useOr ? "OR " : "AND ") : "WHERE ");
                sql.append(columnName).append(compare);
                boolean firstAdded = false;
                for (String value : values) {
                    if(firstAdded) {
                        sql.append(", '").append(value).append('\'');
                    } else sql.append('\'').append(value).append('\'');
                    firstAdded = true;
                }
                sql.append(") ");
            }
        } else throw new IllegalArgumentException("Field Name Cannot Be Empty!");
    }

    void whereBetween(final String columnName, final boolean useNot,
                                             final boolean useOr, final String start, final String stop) {
        assertSQLIsNotEmpty();
        if(!columnName.isEmpty()) {
            if(start.isEmpty() || stop.isEmpty()) {
                throw new IllegalArgumentException("Values Cannot Be Empty!");
            } else {
                String compare = useNot ? " NOT BETWEEN " : " BETWEEN ";

                sql.append(sql.toString().contains("WHERE") ? useOr ? "OR " : "AND " : "WHERE ");
                sql.append(columnName).append(compare)
                        .append('\'').append(start).append("' AND '").append(stop).append("' ");
            }
        } else throw new IllegalArgumentException("Field Name Cannot Be Empty!");
    }

    void whereExists(final QueryBuilder builder, final boolean useNot, final boolean useOr) {
        assertSQLIsNotEmpty();
        if(builder == null || builder.toString().isEmpty()) {
            throw new IllegalArgumentException("Builder Cannot Be Null Or Empty!");
        } else {
            String compare = useNot ? "NOT EXISTS (" : "EXISTS (";

            sql.append(sql.toString().contains("WHERE") ? useOr ? "OR " : "AND " : "WHERE ");
            sql.append(compare).append(builder.toString().replace(";", "")).append(") ");
        }
    }

    private void assertSQLIsNotEmpty() {
        if(sql.toString().isEmpty()) {
            throw new IllegalStateException("Statement Empty, Please Add Statement!");
        }
    }

    @Override
    public String toString() {
        return sql.toString();
    }
}
