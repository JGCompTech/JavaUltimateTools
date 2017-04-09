package com.jgcomptech.tools.authenication;

/** An object that supplies custom methods to be used during user login/logout in the Session Manager */
public interface SessionActivator {
    /**
     * Method that is called after login and logout of user to apply permissions.
     * @return a custom error string. If the string is not empty a message box will show with this string.
     */
    String applyPermissions();
    /**
     * Method that is called from the login dialog if the user supplies incorrect login info.
     * @param username the username that was typed into the login dialog. This is supplied for your use.
     */
    void ifLoginFails(String username);
    /**
     * Method that is called from the login dialog if the user supplies correct login info after the Session Manager has successfully logged in the user.
     * @param username the username that was typed into the login dialog. This is supplied for your use.
     */
    void ifLoginSucceeds(String username);
    /**
     * Method is called to set the title of the login dialog window.
     * @return the title to use.
     */
    String getLoginTitle();
    /**
     * Method is called to set the error text of the login dialog, this is only used if the showErrorMessage is set to true.
     * @return the error text to use.
     */
    String getLoginErrorText();
    /**
     * Method is called to specify if you want to show the login dialog again every time the user supplies incorrect login info.
     * @return the boolean value to set
     */
    boolean setDoNotRetryLoginOnFail();
}
