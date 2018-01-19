package com.jgcomptech.tools.authenication;

import com.jgcomptech.tools.dialogs.LoginDialog;
import com.jgcomptech.tools.events.EventHandler;
import com.jgcomptech.tools.events.SessionEvent;
import javafx.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Manages a single login session to allow a users to login to your application.
 * @since 1.4.0
 */
public final class SessionManager extends SessionActivator {
    /**
     * Creates an instance of the SessionManager.
     * Also creates the sessionLoginSuccess, sessionLoginFailure, multiSessionOpened and multiSessionClosed events.
     * @param userManager the User Manager to use for the session
     */
    public SessionManager(final UserManager userManager) { super(userManager, false); }

    /** Returns the username of the currently logged in user.
     * @return the username of the currently logged in user
     */
    @Nullable
    public String getLoggedInUsername() { return currentSession != null ? currentSession.getUsername() : null; }

    /** Returns the user role of the currently logged in user.
     * @return the user role of the currently logged in user
     */
    @Nullable
    public UserRole getLoggedInUserRole() { return currentSession != null ? currentSession.getUserRole() : null; }

    /** Returns true if a user is currently logged in.
     * @return true if a user is currently logged in
     */
    @Contract(pure = true)
    public boolean isLoggedIn() { return currentSession != null; }

    /**
     * Logs in a user with the specified username and user role and sets any needed permissions,
     * no password checking is used.
     * Fires sessionOpened event allowing the getUser and getSession methods
     * to be called by the assigned EventHandler.
     * @param username the username of the user
     * @return false if username does not exist or if session already exists
     * @throws SQLException if user lookup fails
     */
    public boolean loginUser(final String username) throws SQLException { return saLoginUser(username); }

    /**
     * Logs out the currently logged in user and clears any set permissions.
     * Fires sessionClosed event allowing the getUser and getSession methods
     * to be called by the assigned EventHandler.
     * NOTE: if user was deleted from the database while logged in, the getUser event method will return null.
     * @return false if username or session does not exist
     * @throws SQLException if user lookup fails
     */
    public boolean logoutUser() throws SQLException { return saLogoutUser(null); }

    /**
     * Shows the login dialog window without setting a window icon.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * @param loginTitle the title of the login dialog window
     * @param loginErrorText the error text of the login dialog if the login fails
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if user logged in successfully and false if cancel button was pressed or login failed
     * @throws GeneralSecurityException if error occurs while hashing password
     * @throws SQLException if error occurs while accessing the database
     */
    public boolean showLoginWindow(final String loginTitle,
                                final String loginErrorText,
                                final boolean retryLoginOnFailure) throws GeneralSecurityException, SQLException {
        return showLoginWindow(null, loginTitle, loginErrorText,
                retryLoginOnFailure, false); }

    /**
     * Shows the login dialog window.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * @param appIconPath icon path to use for login dialog window
     * @param loginTitle the title of the login dialog window
     * @param loginErrorText the error text of the login dialog if the login fails
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if user logged in successfully and false if cancel button was pressed or login failed
     * @throws GeneralSecurityException if error occurs while hashing password
     * @throws SQLException if error occurs while accessing the database
     */
    public boolean showLoginWindow(final String appIconPath,
                                final String loginTitle,
                                final String loginErrorText,
                                final boolean retryLoginOnFailure) throws GeneralSecurityException, SQLException {
        return showLoginWindow(appIconPath, loginTitle, loginErrorText,
                retryLoginOnFailure, false); }

    /**
     * Shows the login dialog window, if showErrorMessage is true the error message is displayed.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * @param appIconPath icon path to use for login dialog window
     * @param loginTitle the title of the login dialog window
     * @param loginErrorText the error text of the login dialog if the login fails
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @param showErrorMessage if true the error message is displayed
     * @return true if user logged in successfully and false if cancel button was pressed or login failed
     * @throws GeneralSecurityException if error occurs while hashing password
     * @throws SQLException if error occurs while accessing the database
     */
    private boolean showLoginWindow(final String appIconPath,
                                 final String loginTitle,
                                 final String loginErrorText,
                                 final boolean retryLoginOnFailure,
                                 final boolean showErrorMessage) throws GeneralSecurityException, SQLException {
        if(appIconPath != null && appIconPath.isEmpty()) {
            throw new IllegalArgumentException("App Icon Path Cannot Be An Empty String!");
        }
        final LoginDialog dialog = showErrorMessage ? new LoginDialog(loginErrorText, true)
                : new LoginDialog("", false);

        dialog.setTitle(loginTitle);
        if(appIconPath != null) dialog.setIconPath(appIconPath);

        final Optional<Pair<String, String>> result = dialog.showAndWait();

        if(result.isPresent()) {
            UserAccount user = userManager.getUser(result.get().getKey().toLowerCase());
            final String password = result.get().getValue();
            if(userManager.checkPasswordMatches(user.getUsername(), password)) {
                eventLoginSuccess.fireEvent(this, user);
                return loginUser(user.getUsername());
            } else {
                eventLoginFailure.fireEvent(this, user);
                return retryLoginOnFailure && showLoginWindow(appIconPath, loginTitle, loginErrorText,
                        true, true);
            }
        } else return false;
    }

    /**
     * Sets the event handler that will fire when a user login succeeds.
     * The currently assigned event handler is removed if the parameter is null.
     * The specified event handler is assigned to the sessionLoginSuccess event in the EventManager.
     * @param e the event handler
     */
    public void setOnLoginSuccess(final EventHandler<SessionEvent> e) { saSetOnLoginSuccess(e); }

    /**
     * Sets the event handler that will fire when a user login fails.
     * The currently assigned event handler is removed if the parameter is null.
     * The specified event handler is assigned to the sessionLoginFailure event in the EventManager.
     * @param e the event handler
     */
    public void setOnLoginFailure(final EventHandler<SessionEvent> e) { saSetOnLoginFailure(e); }

    /**
     * Sets the event handler that will fire when the session is opened.
     * The currently assigned event handler is removed if the parameter is null.
     * The specified event handler is assigned to the sessionOpened event in the EventManager.
     * @param e the event handler
     */
    public void setOnSessionOpened(final EventHandler<SessionEvent> e) { saSetOnSessionOpened(e); }

    /**
     * Sets the event handler that will fire when the session is closed.
     * The currently assigned event handler is removed if the parameter is null.
     * The specified event handler is assigned to the sessionClosed event in the EventManager.
     * @param e the event handler
     */
    public void setOnSessionClosed(final EventHandler<SessionEvent> e) { saSetOnSessionClosed(e); }
}
