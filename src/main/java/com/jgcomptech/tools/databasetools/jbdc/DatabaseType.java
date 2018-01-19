package com.jgcomptech.tools.databasetools.jbdc;

/**
 * Database type to be used to determine database operations.
 * @since 1.3.0
 */
public enum DatabaseType {
    /**
     * H2 Database at http://www.h2database.com/html/main.html.
     * @since 1.3.0
     */
    H2,
    /**
     * HyperSQL Database at http://hsqldb.org/.
     * @since 1.4.0
     */
    HyperSQLDB,
    /**
     * SQLite Database at https://sqlite.org/.
     * @since 1.3.0
     */
    SQLite
}
