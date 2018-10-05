package com.jgcomptech.tools.databasetools.jdbc.builders;

import com.jgcomptech.tools.databasetools.jdbc.DataTypes;
import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.DatabaseType;

import java.util.Locale;

/**
 * A builder class for creating a database table column.
 * @since 1.4.0
 */
public class ColumnBuilder {
    private final StringBuilder sql = new StringBuilder();
    private final String columnName;
    private final DataTypes type;
    private final int length;
    private boolean notNull;
    private boolean unique;
    private boolean primaryKey;
    private boolean autoIncrement;
    private String defaultValue;

    /**
     * Generates sql code for creation of a table column.
     * @param columnName the name of the column
     * @param type the data type of the column
     */
    public ColumnBuilder(final String columnName, final DataTypes type) {
        this.columnName = columnName;
        this.type = type;
        length = 0;
        defaultValue = "";
    }

    /**
     * Generates sql code for creation of a table column.
     * @param columnName the name of the column
     * @param type the data type of the column
     * @param length the max length of the column
     */
    public ColumnBuilder(final String columnName, final DataTypes type, final int length) {
        this.columnName = columnName;
        this.type = type;
        this.length = length;
        defaultValue = "";
    }

    /**
     * Returns the name of the column.
     * @return the name of the column
     */
    public String getColumnName() { return columnName; }

    /**
     * Returns the data type of the column.
     * @return the data type of the column
     */
    public DataTypes getType() { return type; }

    /**
     * Sets the NOT NULL constraint on the column.
     * @return the instance of the column to continue building
     */
    public ColumnBuilder notNull() {
        notNull = true;
        return this;
    }

    /**
     * Sets the UNIQUE constraint on the column.
     * @return the instance of the column to continue building
     */
    public ColumnBuilder unique() {
        unique = true;
        return this;
    }

    /**
     * Sets the PRIMARY KEY constraint on the column.
     * @return the instance of the column to continue building
     */
    public ColumnBuilder primaryKey() {
        primaryKey = true;
        return this;
    }

    /**
     * Sets the AUTO INCREMENT constraint on the column.
     * Depending on the database this may use the IDENTITY constraint instead.
     * @return the instance of the column to continue building
     */
    public ColumnBuilder autoIncrement() {
        autoIncrement = true;
        return this;
    }

    /**
     * Sets the DEFAULT constraint on the column.
     * @param defaultValue the value to set
     * @return the instance of the column to continue building
     */
    public ColumnBuilder defaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Builds the sql statement to create the column.
     * @param db the database object for database type checking
     * @return the sql statement as a string
     */
    public String build(final Database db) {
        if(length != 0) sql.append(columnName).append(' ').append(type.toString().toLowerCase(Locale.ENGLISH))
                .append('(').append(length).append(')');
        else sql.append(columnName).append(' ').append(type.toString().toLowerCase(Locale.ENGLISH));
        if(notNull) sql.append(" NOT NULL");
        if(primaryKey) {
            if(db.getInfo().getDbType() == DatabaseType.HyperSQLDB && !autoIncrement) {
                sql.append(" PRIMARY KEY");
            } else {
                if(!notNull) sql.append(" NOT NULL");
                sql.append(" PRIMARY KEY");
            }
        } else if(unique) sql.append(" UNIQUE");
        if(autoIncrement
                && db.getInfo().getDbType() != DatabaseType.SQLite
                && db.getInfo().getDbType() != DatabaseType.HyperSQLDB) sql.append(" AUTO_INCREMENT");
        if(autoIncrement
                && db.getInfo().getDbType() == DatabaseType.HyperSQLDB) sql.append(" IDENTITY");
        if(!defaultValue.trim().isEmpty()) sql.append(" DEFAULT '").append(defaultValue).append('\'');

        return sql.toString();
    }

    /**
     * Returns the sql statement to create the column
     * and will return an empty string if {@link #build} hasn't been run.
     * @return the sql statement to create the column
     */
    @Override
    public String toString() { return sql.toString(); }
}
