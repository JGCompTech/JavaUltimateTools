package com.jgcomptech.tools.databasetools;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Allows setting and retrieval of settings values from a database
 */
public class Settings {
    /**
     * Creates a Settings table in the specified database
     * @param db the database to create the table in
     * @param suppressExistsError if true suppresses error if the table already exists
     * @throws SQLException if error occurs
     */
    public static void createTable(Database db, boolean suppressExistsError) throws SQLException {
        final String TABLE_NAME = "Settings";
        final String ID_FIELD = "Id";
        final String NAME_FIELD = "Name";
        final String VALUE_FIELD = "Value";
        String USE_AUTO_INCREMENT = "AUTO_INCREMENT";

        if(db.getDbType() == DatabaseType.SQLite) { USE_AUTO_INCREMENT = ""; }

        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ( "
                + ID_FIELD + " INTEGER NOT NULL PRIMARY KEY " + USE_AUTO_INCREMENT +
                ", " + NAME_FIELD + " nvarchar(100) NOT NULL" +
                ", " + VALUE_FIELD + " nvarchar(100) NOT NULL)";

        db.createTable(TABLE_NAME, createTableQuery, suppressExistsError);
    }

    /**
     * Returns a setting from the settings table, if setting doesn't exist, returns an empty string
     * @param db database that contains the settings table
     * @param settingName name of the setting to return a value
     * @return value of the specified setting
     * @throws SQLException if error occurs
     */
    public static String getValue(Database db, String settingName) throws SQLException {
        final String TABLE_NAME = "Settings";
        final String NAME_FIELD = "Name";
        final String VALUE_FIELD = "Value";

        settingName = settingName.toLowerCase();

        String value = "";

        if(exists(db, settingName)) {
            String searchTableQuery = "SELECT " + VALUE_FIELD + " FROM " + TABLE_NAME +
                    " WHERE " + NAME_FIELD + " = '" + settingName + "'";

            ResultSet result = db.executeQuery(searchTableQuery);

            result.next();
            value = result.getString(VALUE_FIELD);
        }

        return value;
    }

    /**
     * Sets a setting in the settings table, if setting doesn't exist, it is created
     * @param db database that contains the settings table
     * @param settingName name of the setting to set its value
     * @param settingValue value to set
     * @return true if setting is set successfully
     * @throws SQLException if error occurs
     */
    public static boolean setValue(Database db, String settingName, String settingValue) throws SQLException {
        final String TABLE_NAME = "Settings";
        final String NAME_FIELD = "Name";
        final String VALUE_FIELD = "Value";

        settingName = settingName.toLowerCase();

        String updateSettingQuery;

        if(exists(db, settingName)) {
            updateSettingQuery = "UPDATE " + TABLE_NAME + " " +
                    "SET " + VALUE_FIELD + " = '" + settingValue + "' " +
                    "WHERE " + NAME_FIELD + " = '" + settingName + "'";

        } else {
            updateSettingQuery = "INSERT INTO " + TABLE_NAME + " (" + NAME_FIELD + ", " + VALUE_FIELD + ") " +
                    "VALUES ('" + settingName + "', '" + settingValue + "')";
        }

        int result = db.executeUpdate(updateSettingQuery);
        return result == 1;
    }

    /**
     * Checks if a setting exists in the settings table
     * @param db database that contains the settings table
     * @param settingName setting to check for
     * @return true if setting exists
     * @throws IllegalStateException if a setting is defined multiple times
     * @throws SQLException if error occurs
     */
    public static boolean exists(Database db, String settingName) throws SQLException {
        final String TABLE_NAME = "Settings";
        final String NAME_FIELD = "Name";
        final String VALUE_FIELD = "Value";

        settingName = settingName.toLowerCase();

        String searchTableQuery = "SELECT " + VALUE_FIELD + " FROM " + TABLE_NAME +
                " WHERE " + NAME_FIELD + " = '" + settingName + "'";

        int rows = Database.getResultRows(db.executeQuery(searchTableQuery));

        if(rows == 1) {
            return true;
        } else if (rows > 1) {
            throw new IllegalStateException("Duplicate Settings Rows Found For \"" + settingName + "\"");
        } else {
            return false;
        }
    }

    // This class should only be called statically
    private Settings() { super(); }
}
