package com.jgcomptech.tools.authenication;

import com.jgcomptech.tools.dialogs.LoginDialog;
import com.jgcomptech.tools.events.EventHandler;
import com.jgcomptech.tools.events.SessionEvent;
import javafx.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Manages a single login session to allow a users to login to your application.
 * @since 1.4.0
 */
public final class SessionManager extends SessionActivator {
    private String appIconPath;
    private String programName;
    private String loginErrorText;
    private SessionEvent eventLoginSuccess;
    private SessionEvent eventLoginFailure;
    private SessionEvent eventAdminOverrideStarted;
    private SessionEvent eventAdminOverrideSuccess;
    private SessionEvent eventAdminOverrideFailure;
    private SessionEvent eventUserVerifyStarted;
    private SessionEvent eventUserVerifySuccess;
    private SessionEvent eventUserVerifyFailure;
    //TODO Remove Variable On Next Version
    private boolean useNewLoginTitle;

    /**
     * Creates an instance of the SessionManager.
     * Also creates the sessionLoginSuccess, sessionLoginFailure, multiSessionOpened and multiSessionClosed events.
     * @param userManager the User Manager to use for the session
     */
    public SessionManager(final UserManager userManager) {
        super(userManager, false);
        appIconPath = userManager.getAppIconPath();
        programName = userManager.getProgramName();
        createSessionEvents();
    }

    /**
     * Creates an instance of the SessionManager.
     * Also creates the sessionLoginSuccess, sessionLoginFailure, multiSessionOpened and multiSessionClosed events.
     * @param userManager the User Manager to use for the session
     * @param appIconPath the path to the icon to use for all login dialogs
     */
    public SessionManager(final UserManager userManager, final String appIconPath) {
        super(userManager, false);
        this.appIconPath = appIconPath;
        programName = userManager.getProgramName();
        createSessionEvents();
    }

    /**
     * Creates an instance of the SessionManager.
     * Also creates the sessionLoginSuccess, sessionLoginFailure, multiSessionOpened and multiSessionClosed events.
     * @param userManager the User Manager to use for the session
     * @param appIconPath the path to the icon to use for all login dialogs
     * @param programName the name of the program to use in all login dialogs for the window title
     */
    public SessionManager(final UserManager userManager, final String appIconPath, final String programName) {
        super(userManager, false);
        this.appIconPath = appIconPath;
        this.programName = programName;
        createSessionEvents();
    }

    /**
     * Returns the path to the icon to use for all login dialogs.
     * @return the path to the icon to use for all login dialogs
     */
    public String getAppIconPath() { return appIconPath; }

    /**
     * Sets the path to the icon to use for all login dialogs.
     * @param appIconPath the icon path to set
     */
    public void setAppIconPath(final String appIconPath) { this.appIconPath = appIconPath; }

    /**
     * Returns the name of the program to use in all login dialogs for the window title.
     * @return the name of the program to use in all login dialogs for the window title
     */
    public String getProgramName() { return programName; }

    /**
     * Sets the name of the program to use in all login dialogs for the window title.
     * @param programName the name of the program to set
     */
    public void setProgramName(final String programName) { this.programName = programName; }

    /**
     * Returns the text to use for the error message when invalid credentials are provided.
     * @return the text to use for the error message when invalid credentials are provided
     */
    public String getLoginErrorText() { return loginErrorText; }

    /**
     * Sets the text to use for the error message when invalid credentials are provided
     * @param loginErrorText the error text to set
     */
    public void setLoginErrorText(final String loginErrorText) { this.loginErrorText = loginErrorText; }

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
     * Returns true if an admin user is currently logged in.
     * @return true if an admin user is currently logged in
     */
    public boolean isAdminLoggedIn() {
        return isLoggedIn() && getLoggedInUserRole() == UserRoleManager.SystemUserRoles.ADMIN.getRole();
    }

    /**
     * Logs in a user with the specified username and user role and sets any needed permissions,
     * no password checking is used.
     * Fires sessionOpened event allowing the getUser and getSession methods
     * to be called by the assigned EventHandler.
     * @param username the username of the user
     * @return false if username does not exist or if session already exists
     */
    public boolean loginUser(final String username) { return saLoginUser(username); }

    /**
     * Logs out the currently logged in user and clears any set permissions.
     * Fires sessionClosed event allowing the getUser and getSession methods
     * to be called by the assigned EventHandler.
     * NOTE: if user was deleted from the database while logged in, the getUser event method will return null.
     * @return false if username or session does not exist
     */
    public boolean logoutUser() { return saLogoutUser(null); }

    /**
     * Shows the login dialog window.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if user logged in successfully and false if cancel button was pressed or login failed
     * @since 1.4.1
     */
    public boolean showLoginWindow(final boolean retryLoginOnFailure) {
        useNewLoginTitle = true;
        return showLoginWindow(appIconPath, programName, loginErrorText, retryLoginOnFailure);
    }

    /**
     * Shows the login dialog window without setting a window icon.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param loginTitle the title of the login dialog window
     * @param loginErrorText the error text of the login dialog if the login fails
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if user logged in successfully and false if cancel button was pressed or login failed
     * @deprecated since 1.4.1 as parameters are now global to the SessionManager instance
     */
    @Deprecated
    public boolean showLoginWindow(final String loginTitle,
                                final String loginErrorText,
                                final boolean retryLoginOnFailure) {
        return showLoginWindow(null, loginTitle, loginErrorText,
                retryLoginOnFailure, false); }

    /**
     * Shows the login dialog window.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param appIconPath icon path to use for login dialog window
     * @param loginTitle the title of the login dialog window
     * @param loginErrorText the error text of the login dialog if the login fails
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if user logged in successfully and false if cancel button was pressed or login failed
     * @deprecated since 1.4.1 as parameters are now global to the SessionManager instance
     */
    @Deprecated
    public boolean showLoginWindow(final String appIconPath,
                                final String loginTitle,
                                final String loginErrorText,
                                final boolean retryLoginOnFailure) {
        return showLoginWindow(appIconPath, loginTitle, loginErrorText,
                retryLoginOnFailure, false); }
    /**
     * Shows the login dialog window, if showErrorMessage is true the error message is displayed.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param appIconPath icon path to use for login dialog window
     * @param loginTitle the title of the login dialog window
     * @param loginErrorText the error text of the login dialog if the login fails
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @param showErrorMessage if true the error message is displayed
     * @return true if user logged in successfully and false if cancel button was pressed or login failed
     */
    //TODO Remove Deprecated Methods And Replace With Instance Variables
    private boolean showLoginWindow(final String appIconPath,
                                 final String loginTitle,
                                 final String loginErrorText,
                                 final boolean retryLoginOnFailure,
                                 final boolean showErrorMessage) {
        final LoginDialog dialog = showErrorMessage ? new LoginDialog(loginErrorText, true)
                : new LoginDialog("", false);

        if(useNewLoginTitle) dialog.setTitle(loginTitle + " - Login Required");
        else dialog.setTitle(loginTitle);
        if(appIconPath != null && !appIconPath.isEmpty()) dialog.setIconPath(appIconPath);

        final Optional<Pair<String, String>> result = dialog.showAndWait();

        if(result.isPresent()) {
            if(userManager.userExists(result.get().getKey().toLowerCase())) {
                UserAccount user = userManager.getUser(result.get().getKey().toLowerCase());
                final String password = result.get().getValue();
                if (userManager.checkPasswordMatches(user.getUsername(), password)) {
                    eventLoginSuccess.fireEvent(this, user);
                    useNewLoginTitle = false;
                    return loginUser(user.getUsername());
                } else {
                    eventLoginFailure.fireEvent(this, user);
                    return retryLoginOnFailure && showLoginWindow(appIconPath, loginTitle, loginErrorText,
                            true, true);
                }
            } else {
                eventLoginFailure.fireEvent(this);
                return retryLoginOnFailure && showLoginWindow(appIconPath, loginTitle, loginErrorText,
                        true, true);
            }
        } else {
            useNewLoginTitle = false;
            return false;
        }
    }

    /**
     * Shows the login dialog window requesting an admin user to authenticate.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @param showErrorMessage if true, shows the error message
     * @return true if admin override succeeded and false if cancel button was pressed or override failed
     * @since 1.4.1
     */
    private boolean requestAdminLogin(final boolean retryLoginOnFailure,
                                      final boolean showErrorMessage) {
        LoginDialog dialog = showErrorMessage
                ? new LoginDialog(loginErrorText == null || loginErrorText.isEmpty()
                                    ? loginErrorText
                                    : "Invalid Login! Please Try Again!", true)
                : new LoginDialog("", false);

        if(programName != null && !programName.isEmpty()) {
            dialog.setTitle(programName + " - Admin Override Required");
        } else dialog.setTitle("Admin Override Required");
        if(appIconPath != null && !appIconPath.isEmpty()) dialog.setIconPath(appIconPath);

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if(result.isPresent()) {
            if(userManager.userExists(result.get().getKey().toLowerCase())) {
                UserAccount user = userManager.getUser(result.get().getKey().toLowerCase());
                final String password = result.get().getValue();
                if(userManager.checkPasswordMatches(user.getUsername(), password)) {
                    if(user.getUserRole() == UserRoleManager.SystemUserRoles.ADMIN.getRole()) {
                        eventLoginSuccess.fireEvent(this, user);
                        return true;
                    }
                }
                eventLoginFailure.fireEvent(this, user);
                return retryLoginOnFailure && requestAdminLogin(true, true);
            }
            eventLoginFailure.fireEvent(this);
            return retryLoginOnFailure && requestAdminLogin(true, true);
        } else return false;
    }

    /**
     * Shows the login dialog window requesting the currently logged in username to re-login.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @param showErrorMessage if true, shows the error message
     * @return true if user verified successfully and false if cancel button was pressed or verification failed
     * @since 1.4.1
     */
    private boolean requestUserLogin(final boolean retryLoginOnFailure,
                                     final boolean showErrorMessage) {
        LoginDialog dialog = showErrorMessage
                ? new LoginDialog(loginErrorText == null || loginErrorText.isEmpty()
                ? loginErrorText
                : "Invalid Login! Please Try Again!", true)
                : new LoginDialog("", false);

        if(programName != null && !programName.isEmpty()) {
            dialog.setTitle(programName + " - User Verification Required");
        } else dialog.setTitle("User Verification Required");
        if(appIconPath != null && !appIconPath.isEmpty()) dialog.setIconPath(appIconPath);

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if(result.isPresent()) {
            String username = result.get().getKey().toLowerCase();
            if(userManager.userExists(username)) {
                UserAccount user = userManager.getUser(username);
                final String password = result.get().getValue();
                if(!username.equals(getLoggedInUsername())) {
                    eventLoginFailure.fireEvent(this, user);
                    return retryLoginOnFailure && requestUserLogin(true, true);
                }
                if(userManager.checkPasswordMatches(user.getUsername(), password)) {
                    eventLoginSuccess.fireEvent(this, user);
                    return true;
                }
            }
            eventLoginFailure.fireEvent(this);
            return retryLoginOnFailure && requestUserLogin(true, true);
        } else return false;
    }

    /**
     * Requests an admin user to authenticate to override permissions.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if admin override succeeded and false if cancel button was pressed or override failed
     * @since 1.4.1
     */
    public boolean getAdminOverride(final boolean retryLoginOnFailure) {
        eventAdminOverrideStarted.fireEvent(this);
        boolean userLoggedIn = isLoggedIn()
                && getLoggedInUserRole() == UserRoleManager.SystemUserRoles.ADMIN.getRole();
        if(!userLoggedIn) {
            if(requestAdminLogin(retryLoginOnFailure, false)) {
                eventAdminOverrideSuccess.fireEvent(this);
                userLoggedIn = true;
            } else eventAdminOverrideFailure.fireEvent(this);
        } else eventAdminOverrideSuccess.fireEvent(this, userManager.getUser(getLoggedInUsername()));
        return userLoggedIn;
    }

    /**
     * Requests that the currently logged in username to re-login.
     * Fires either the sessionLoginSuccess or the sessionLoginFailure event
     * allowing the getUser method to be called by the assigned EventHandler.
     * If the user does not exist, getUser will return null.
     * @param retryLoginOnFailure if true the login dialog will be shown again on failure
     * @return true if user verified successfully and false if cancel button was pressed or verification failed
     * @since 1.4.1
     */
    public boolean getUserVerification(final boolean retryLoginOnFailure) {
        eventUserVerifyStarted.fireEvent(this);
        boolean verified = false;

        boolean result = requestUserLogin(retryLoginOnFailure, false);
        if(result) {
            eventUserVerifySuccess.fireEvent(this, userManager.getUser(getLoggedInUsername()));
            verified = true;
        } else eventUserVerifyFailure.fireEvent(this, userManager.getUser(getLoggedInUsername()));
        return verified;
    }

    /**
     * Checks that the logged in user is an admin and if false, requests an admin override.
     * @return true if admin permissions retrieved successfully
     * @since 1.4.1
     */
    public boolean requireAdmin() {
        boolean isAdmin = isAdminLoggedIn();
        if (!isAdmin) isAdmin = getAdminOverride(true);
        return isAdmin;
    }

    /**
     * Checks that the logged in user is an admin,
     * if true, prompts the admin to re-login and
     * if false, requests an admin override.
     * @return true if admin permissions retrieved successfully and admin verification succeeded
     * @since 1.4.1
     */
    public boolean requireAndVerifyAdmin() {
        boolean isAdmin = isAdminLoggedIn();
        isAdmin = !isAdmin ? getAdminOverride(true) : getUserVerification(true);
        return isAdmin;
    }

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

    /**
     * Sets the event handler that will fire when a user login succeeds.
     * The currently assigned event handler is removed if the parameter is null.
     * The specified event handler is assigned to the sessionLoginSuccess event in the EventManager.
     * @param e the event handler
     */
    public void setOnLoginSuccess(final EventHandler<SessionEvent> e) {
        if(e == null) removeEventHandler(SessionEvent.SESSION_LOGIN_SUCCESS);
        else addEventHandler(SessionEvent.SESSION_LOGIN_SUCCESS, e);
    }

    /**
     * Sets the event handler that will fire when a user login fails.
     * The currently assigned event handler is removed if the parameter is null.
     * The specified event handler is assigned to the sessionLoginFailure event in the EventManager.
     * @param e the event handler
     */
    public void setOnLoginFailure(final EventHandler<SessionEvent> e) {
        if(e == null) removeEventHandler(SessionEvent.SESSION_LOGIN_FAILURE);
        else addEventHandler(SessionEvent.SESSION_LOGIN_FAILURE, e);
    }

    public void setOnAdminOverrideStarted(final EventHandler<SessionEvent> e) {
        if(e == null) removeEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_STARTED);
        else addEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_STARTED, e);
    }

    public void setOnAdminOverrideSuccess(final EventHandler<SessionEvent> e) {
        if(e == null) removeEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_SUCCESS);
        else addEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_SUCCESS, e);
    }

    public void setOnAdminOverrideFailure(final EventHandler<SessionEvent> e) {
        if(e == null) removeEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_FAILURE);
        else addEventHandler(SessionEvent.SESSION_ADMIN_OVERRIDE_FAILURE, e);
    }

    public void setOnUserVerifyStarted(final EventHandler<SessionEvent> e) {
        if(e == null) removeEventHandler(SessionEvent.SESSION_USER_VERIFY_STARTED);
        else addEventHandler(SessionEvent.SESSION_USER_VERIFY_STARTED, e);
    }

    public void setOnUserVerifySuccess(final EventHandler<SessionEvent> e) {
        if(e == null) removeEventHandler(SessionEvent.SESSION_USER_VERIFY_SUCCESS);
        else addEventHandler(SessionEvent.SESSION_USER_VERIFY_SUCCESS, e);
    }

    public void setOnUserVerifyFailure(final EventHandler<SessionEvent> e) {
        if(e == null) removeEventHandler(SessionEvent.SESSION_USER_VERIFY_FAILURE);
        else addEventHandler(SessionEvent.SESSION_USER_VERIFY_FAILURE, e);
    }

    private void createSessionEvents() {
        try {
            eventLoginSuccess = eventManager.registerNewEvent("sessionLoginSuccess",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_LOGIN_SUCCESS);
        } catch(Exception e) {
            throw new IllegalStateException("sessionLoginSuccess Event Failed To Load!");
        }
        try {
            eventLoginFailure = eventManager.registerNewEvent("sessionLoginFailure",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_LOGIN_FAILURE);
        } catch(Exception e) {
            throw new IllegalStateException("sessionLoginFailure Event Failed To Load!");
        }
        try {
            eventAdminOverrideStarted = eventManager.registerNewEvent(
                    "adminOverrideStarted",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_ADMIN_OVERRIDE_STARTED);
        } catch (Exception e) {
            throw new IllegalStateException("adminOverrideStarted Event Failed To Load!");
        }
        try {
            eventAdminOverrideSuccess = eventManager.registerNewEvent(
                    "adminOverrideSuccess",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_ADMIN_OVERRIDE_SUCCESS);
        } catch (Exception e) {
            throw new IllegalStateException("adminOverrideSuccess Event Failed To Load!");
        }
        try {
            eventAdminOverrideFailure = eventManager.registerNewEvent(
                    "adminOverrideFailure",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_ADMIN_OVERRIDE_FAILURE);
        } catch (Exception e) {
            throw new IllegalStateException("adminOverrideFailure Event Failed To Load!");
        }
        try {
            eventUserVerifyStarted = eventManager.registerNewEvent(
                    "userVerifyStarted",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_USER_VERIFY_STARTED);
        } catch (Exception e) {
            throw new IllegalStateException("userVerifyStarted Event Failed To Load!");
        }
        try {
            eventUserVerifySuccess = eventManager.registerNewEvent(
                    "userVerifySuccess",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_USER_VERIFY_SUCCESS);
        } catch (Exception e) {
            throw new IllegalStateException("userVerifySuccess Event Failed To Load!");
        }
        try {
            eventUserVerifyFailure = eventManager.registerNewEvent(
                    "userVerifyFailure",
                    SessionEvent.class,
                    this,
                    SessionEvent.SESSION_USER_VERIFY_FAILURE);
        } catch (Exception e) {
            throw new IllegalStateException("userVerifyFailure Event Failed To Load!");
        }
    }
}
