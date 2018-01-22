package com.jgcomptech.tools.exceptions;

/**
 * Thrown to indicate that a table does not exist.
 * @since 1.4.0
 */
public class TableNotFoundException extends RuntimeException {
    /**
     * Constructs an {@link TableNotFoundException} with the specified table name.
     * @param tableName the table name
     */
    public TableNotFoundException(final String tableName) { super('"' + tableName + "\" Table Not Found!"); }
}
