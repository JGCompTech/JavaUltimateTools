package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.dialogs.LoginDialog;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/** Creates and shows a custom login dialog. */
final class LoginDialogBuilder {
    private LoginDialog dialog;
    private final UserManager userManager;
    private final SessionManager sessionManager;
    private final LoginErrorMessages loginErrorMessages;
    private boolean retryLoginOnFailure;
    private boolean attemptNewSession;
    private final Set<Predicate<UserAccount>> predicates;

    public enum LoginErrors {
        IncorrectCredentialsError,
        LockedAccountError,
        ExpiredCredentialsError,
        DisabledRoleError,
        ExcessiveAttemptsError,
        NONE
    }

    LoginDialogBuilder(final SessionManager sessionManager,
                              final UserManager userManager,
                              final LoginErrorMessages loginErrorMessages) {
        this.sessionManager = sessionManager;
        this.userManager = userManager;
        this.loginErrorMessages = loginErrorMessages;
        this.retryLoginOnFailure = true;
        this.predicates = new HashSet<>();
        //Create base login dialog with no error message
        requestDialog(LoginErrors.NONE);
    }

    /**
     * Sets the title of the login dialog.
     * @param title the title to set
     * @return the current instance of the builder
     */
    LoginDialogBuilder setDialogTitle(final String title) {
        dialog.setTitle(title);
        return this;
    }

    /**
     * Sets the icon path of the login dialog.
     * @param path the path to set
     * @return the current instance of the builder
     */
    LoginDialogBuilder setDialogIconPath(final String path) {
        dialog.setIconPath(path);
        return this;
    }

    /**
     * Returns a list of the predicates that are checked for after dialog submission.
     * @return a list of the predicates that are checked for after dialog submission
     */
    Set<Predicate<UserAccount>> getPredicates() {
        return Collections.unmodifiableSet(predicates);
    }

    /**
     * Sets the predicates that are checked for after dialog submission.
     * @param predicates the list of predicates to set
     * @return the current instance of the builder
     */
    LoginDialogBuilder setPredicates(final Set<Predicate<UserAccount>> predicates) {
        this.predicates.clear();
        this.predicates.addAll(predicates);
        return this;
    }

    /**
     * Adds a predicate to the list of predicates that are checked for after dialog submission.
     * @param predicate the predicate to add
     * @return the current instance of the builder
     */
    LoginDialogBuilder addPredicate(final Predicate<UserAccount> predicate) {
        this.predicates.add(predicate);
        return this;
    }

    /**
     * Checks if the dialog will try again after login failure.
     * @return true, if the dialog will try again after login failure
     */
    boolean isRetryLoginOnFailure() {
        return retryLoginOnFailure;
    }

    /**
     * Sets if the dialog will try again after login failure.
     * @param retryLoginOnFailure the value to set
     * @return the current instance of the builder
     */
    LoginDialogBuilder setRetryLoginOnFailure(final boolean retryLoginOnFailure) {
        this.retryLoginOnFailure = retryLoginOnFailure;
        return this;
    }

    /**
     * Checks if the login dialog should try to create a login session after dialog submission.
     * @return true, if the login dialog should try to create a login session after dialog submission
     */
    boolean isAttemptNewSession() {
        return attemptNewSession;
    }

    /**
     * Sets if the login dialog should try to create a login session after dialog submission
     * @param attemptNewSession the value to set
     * @return the current instance of the builder
     */
    LoginDialogBuilder setAttemptNewSession(final boolean attemptNewSession) {
        this.attemptNewSession = attemptNewSession;
        return this;
    }

    /**
     * Creates a new instance of the login dialog with the specified error.
     * @param loginError the error to set
     */
    private void requestDialog(final LoginErrors loginError) {
        //Save previous tital and icon path
        var dialogTitle = "";
        var dialogIconPath = "";
        if(dialog != null) {
            dialogTitle = dialog.getTitle();
            dialogIconPath = dialog.getIconPath();
        }

        switch (loginError) {
            case IncorrectCredentialsError:
                dialog = new LoginDialog(loginErrorMessages.getIncorrectCredentialsError(), true);
                break;
            case LockedAccountError:
                dialog = new LoginDialog(loginErrorMessages.getLockedAccountError(), true);
                break;
            case ExpiredCredentialsError:
                dialog = new LoginDialog(loginErrorMessages.getExpiredCredentialsError(), true);
                break;
            case DisabledRoleError:
                dialog = new LoginDialog(loginErrorMessages.getDisabledRoleError(), true);
                break;
            case ExcessiveAttemptsError:
                dialog = new LoginDialog(loginErrorMessages.getExcessiveAttemptsError(), true);
                break;
            case NONE:
                dialog = new LoginDialog("", false);
                break;
        }

        dialog.setTitle(dialogTitle);
        dialog.setIconPath(dialogIconPath);
    }

    /**
     * Shows the login dialog and waits for submission, afterwards processes result.
     * @return the result of the login process
     */
    boolean show() {
        //Optional<Pair<String, String>>
        final var result = dialog.showAndWait();

        var userPasswordMatches = false;
        var predicatesMatch = true;
        final String username;
        final UserAccount user;
        final String password;

        //Check if dialog was submitted and cancel was not pressed
        if(result.isPresent()) {
            username = result.get().getKey().toLowerCase();
            //Check if user exists and if not fail
            if(userManager.userExists(username)) {
                user = userManager.getUser(username);
                password = result.get().getValue();
            } else {
                sessionManager.getEvents().getEventLoginFailure().fireEvent(this);
                return retryLoginOnFailure && showWithError(LoginErrors.IncorrectCredentialsError);
            }
        } else return false;

        //Check if password matches and if not fail below
        //NOTE: getUsername will never be null because of userExists check above, so just ignore that warning
        if(userManager.checkPasswordMatches(user.getUsername(), password)) {
            sessionManager.getEvents().getEventLoginSuccess().fireEvent(this, user);
            userPasswordMatches = true;
        }

        //Check if any preset predicates match
        for (final var predicate : predicates) {
            if(!predicate.test(user)) predicatesMatch = false;
        }

        //If password or predicats do not match, then fail
        if (!userPasswordMatches || !predicatesMatch) {
            sessionManager.getEvents().getEventLoginFailure().fireEvent(this, user);
            return retryLoginOnFailure && showWithError(LoginErrors.IncorrectCredentialsError);
        }

        //Attempts to create a new login session if specified
        if(attemptNewSession) {
            try {
                return sessionManager.loginUser(user.getUsername());
            } catch (final IllegalStateException e) {
                //Will occur if the role assigned to the user is disabled
                sessionManager.getEvents().getEventLoginFailure().fireEvent(this, user);
                return retryLoginOnFailure && showWithError(LoginErrors.DisabledRoleError);
            } catch (final ExpiredCredentialsException e) {
                //Will occur if the user password is expired
                sessionManager.getEvents().getEventLoginFailure().fireEvent(this, user);
                return retryLoginOnFailure && showWithError(LoginErrors.ExpiredCredentialsError);
            } catch (final LockedAccountException e) {
                //Will occur if the user account is locked
                sessionManager.getEvents().getEventLoginFailure().fireEvent(this, user);
                return retryLoginOnFailure && showWithError(LoginErrors.LockedAccountError);
            }
        } else return true;
    }

    /**
     * Shows the login dialog and waits for submission, afterwards processes result.
     * @param loginError the specified login error
     * @return the result of the login process
     */
    private boolean showWithError(final LoginErrors loginError) {
        requestDialog(loginError);
        return show();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof LoginDialogBuilder)) return false;

        final var loginDialogBuilder = (LoginDialogBuilder) o;

        return new EqualsBuilder()
                .append(retryLoginOnFailure, loginDialogBuilder.retryLoginOnFailure)
                .append(attemptNewSession, loginDialogBuilder.attemptNewSession)
                .append(dialog, loginDialogBuilder.dialog)
                .append(userManager, loginDialogBuilder.userManager)
                .append(sessionManager, loginDialogBuilder.sessionManager)
                .append(loginErrorMessages, loginDialogBuilder.loginErrorMessages)
                .append(getPredicates(), loginDialogBuilder.getPredicates())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(dialog)
                .append(userManager)
                .append(sessionManager)
                .append(loginErrorMessages)
                .append(retryLoginOnFailure)
                .append(attemptNewSession)
                .append(getPredicates())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("dialog", dialog)
                .append("userManager", userManager)
                .append("sessionManager", sessionManager)
                .append("loginErrorMessages", loginErrorMessages)
                .append("retryLoginOnFailure", retryLoginOnFailure)
                .append("attemptNewSession", attemptNewSession)
                .append("predicates", predicates)
                .toString();
    }
}