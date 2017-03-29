package com.jgcomptech.tools.databasetools;

import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import com.jgcomptech.tools.enums.WMIClasses;

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
     */
    public static void createTable(Database db, boolean suppressExistsError) {
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
     */
    public static String getValue(Database db, String settingName) {
        final String TABLE_NAME = "Settings";
        final String NAME_FIELD = "Name";
        final String VALUE_FIELD = "Value";

        settingName = settingName.toLowerCase();

        String value = "";

        if(exists(db, settingName)) {
            String searchTableQuery = "SELECT " + VALUE_FIELD + " FROM " + TABLE_NAME +
                    " WHERE " + NAME_FIELD + " = '" + settingName + "'";

            try {
                ResultSet result = db.executeQuery(searchTableQuery);

                result.next();
                value = result.getString(VALUE_FIELD);
            } catch(SQLException e) {
                MessageBox.show(e.getMessage(), "Error!", MessageBoxIcon.ERROR);
            }
        }

        return value;
    }

    /**
     * Sets a setting in the settings table, if setting doesn't exist, it is created
     * @param db database that contains the settings table
     * @param settingName name of the setting to set its value
     * @param settingValue value to set
     * @return true if setting is set successfully
     */
    public static boolean setValue(Database db, String settingName, String settingValue) {
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

        try {
            int result = db.executeUpdate(updateSettingQuery);
            if(result == 1) return true;
        } catch(SQLException e) {
            MessageBox.show(e.getMessage(), "Error!", MessageBoxIcon.ERROR);
        }

        return false;
    }

    /**
     * Checks if a setting exists in the settings table
     * @param db database that contains the settings table
     * @param settingName setting to check for
     * @return true if setting exists
     * @throws IllegalStateException if a setting is defined multiple times
     */
    public static boolean exists(Database db, String settingName) {
        final String TABLE_NAME = "Settings";
        final String NAME_FIELD = "Name";
        final String VALUE_FIELD = "Value";

        settingName = settingName.toLowerCase();

        String searchTableQuery = "SELECT " + VALUE_FIELD + " FROM " + TABLE_NAME +
                " WHERE " + NAME_FIELD + " = '" + settingName + "'";

        int rows = 0;
        try {
            rows = Database.getResultRows(db.executeQuery(searchTableQuery));
        } catch(SQLException e) {
            MessageBox.show(e.getMessage(), "Error!", MessageBoxIcon.ERROR);
        }

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
