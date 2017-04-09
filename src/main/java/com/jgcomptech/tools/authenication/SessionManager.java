package com.jgcomptech.tools.authenication;

import com.jgcomptech.tools.databasetools.jbdc.Database;
import com.jgcomptech.tools.dialogs.LoginDialog;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import com.jgcomptech.tools.permissions.PermissionManager;
import javafx.util.Pair;

import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Optional;

/** Manages the login session to allow a user to login to your application */
public final class SessionManager {
    /** A List of the implemented user types */
    public enum UserType {
        /** Has Admin, Edit, Create and Read Permissions */
        ADMIN,
        /** Has Edit, Create and Read Permissions */
        EDITOR,
        /** Has Create and Read Permissions */
        AUTHOR,
        /** Has Read Permissions */
        BASIC,
        /** Does not have any Permissions */
        NONE
    }
    private String loggedInUsername;
    private UserType loggedInUserType;
    private boolean loggedIn;
    private Database usersDatabase;
    private String appIconPath;
    private SessionActivator activator;

    private static SessionManager instance = null;

    private SessionManager() { /*Exists only to defeat instantiation.*/ }

    /**
     * Returns the instance of the SessionManager, if one doesn't exist it is created
     * @return the instance of the SessionManager
     */
    public static synchronized SessionManager getInstance(Database usersDatabase, String appIconPath) {
        if(instance == null) instance = new SessionManager();
        instance.usersDatabase = usersDatabase;
        instance.appIconPath = appIconPath;
        return instance;
    }

    /**
     * Sets the Session Activator, this object supplies custom methods to be used during user login/logout
     * @param activator the activator object
     */
    public void setSessionActivator(SessionActivator activator) { this.activator = activator; }

    /** Returns the username of the currently logged in user
     * @return the username of the currently logged in user
     */
    public String getLoggedInUsername() { return loggedInUsername; }
    /** Returns the user type of the currently logged in user
     * @return the user type of the currently logged in user
     */
    public UserType getLoggedInUserType() { return loggedInUserType; }

    /** Returns true if a user is currently logged in
     * @return true if a user is currently logged in
     */
    public boolean isLoggedIn() { return loggedIn; }

    /**
     * Manually logs in a user with the specified username and user type and sets any needed permissions, no password checking is used
     * @param username the username of the user
     * @param userType the user type of the user
     */
    public void loginUser(String username, String userType) {
        loggedInUsername = username;
        loggedInUserType = Enum.valueOf(UserType.class, userType.toUpperCase());
        loggedIn = true;
        PermissionManager.getInstance().loadPermissions(loggedInUserType);
        final String applyErrorText = activator.applyPermissions();

        if(!applyErrorText.isEmpty()) {
            MessageBox.show(applyErrorText, "Session Manager Error",
                    "User permissions failed to apply!", MessageBoxIcon.ERROR);
        }
    }

    /**
     * Logs out the currently logged in user and clears any set permissions
     */
    public void logoutUser() {
        loggedInUsername = "";
        loggedInUserType = UserType.NONE;
        loggedIn = false;
        PermissionManager.getInstance().clearPermissions();
        final String applyErrorText = activator.applyPermissions();

        if(!applyErrorText.isEmpty()) {
            MessageBox.show(applyErrorText, "Session Manager Error",
                    "User permissions failed to apply!", MessageBoxIcon.ERROR);
        }
    }

    /** Shows the login dialog window and does not show the error message */
    public void showLoginWindow() { showLoginWindow(false); }

    /**
     * Shows the login dialog window, if showErrorMessage is true the error message is displayed
     * @param showErrorMessage if true the error message is displayed
     */
    public void showLoginWindow(boolean showErrorMessage) {
        final LoginDialog dialog;
        dialog = showErrorMessage ? new LoginDialog(activator.getLoginErrorText(), true) :
                new LoginDialog("", false);

        dialog.setTitle(activator.getLoginTitle());
        dialog.setIconPath(appIconPath);

        final Optional<Pair<String, String>> result = dialog.showAndWait();

        final String enteredUsername;
        final String enteredPassword;

        if(result.isPresent()) {
            enteredUsername = result.get().getKey().toLowerCase();
            enteredPassword = result.get().getValue();
            checkPasswordMatches(enteredUsername, enteredPassword);
        }
    }

    private void checkPasswordMatches(String username, String password) {
        try {
            if(Database.Users.checkPasswordMatches(usersDatabase, username, password)) {
                final String userType = Database.Users.getUserType(usersDatabase, username);
                loginUser(username, userType);
                activator.ifLoginSucceeds(username);
            } else {
                activator.ifLoginFails(username);
                if(!activator.setDoNotRetryLoginOnFail()) showLoginWindow(true);
            }
        } catch(SQLException | GeneralSecurityException e) {
            MessageBox.show(e.getMessage(), "Session Manager Error", "Method: checkPasswordMatches",
                    MessageBoxIcon.ERROR);
        }
    }

    @Override public Object clone() throws CloneNotSupportedException { throw new CloneNotSupportedException(); }
}
