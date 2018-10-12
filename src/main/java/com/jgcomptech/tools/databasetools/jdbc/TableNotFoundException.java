package com.jgcomptech.tools.databasetools.jdbc;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.fail;

/**
 * Thrown to indicate that a table does not exist.
 * @since 1.4.0
 */
public class TableNotFoundException extends RuntimeException {
    /**
     * Constructs an {@code TableNotFoundException} with the specified table name.
     * @param tableName the table name
     */
    public TableNotFoundException(final String tableName) { super('"' + tableName + "\" Table Not Found!"); }

    /**
     * Asserts that the specified runnable throws this exception and if not throws an junit assertion failure.
     * @param runnable the runnable to verify
     */
    public static void assertThrown(@NotNull final Runnable runnable) {
        try {
            runnable.run();
            fail( "This method should have thrown TableNotFoundException!" );
        } catch (final TableNotFoundException ignore) { }
    }
}
