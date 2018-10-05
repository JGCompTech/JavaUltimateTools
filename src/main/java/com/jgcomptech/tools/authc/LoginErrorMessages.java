package com.jgcomptech.tools.authc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * An object containing all login error messages for the login dialog.
 * @since 1.5.0
 */
public final class LoginErrorMessages {
    private String incorrectCredentialsError;
    private String lockedAccountError;
    private String expiredCredentialsError;
    private String disabledRoleError;
    private String excessiveAttemptsError; //Currently In Development

    public LoginErrorMessages() {
        incorrectCredentialsError = "";
        lockedAccountError = "";
        expiredCredentialsError = "";
        disabledRoleError = "";
        excessiveAttemptsError = "";
    }

    public String getIncorrectCredentialsError() {
        return incorrectCredentialsError;
    }

    public LoginErrorMessages setIncorrectCredentialsError(final String incorrectCredentialsError) {
        this.incorrectCredentialsError = incorrectCredentialsError;
        return this;
    }

    public String getLockedAccountError() {
        return lockedAccountError;
    }

    public LoginErrorMessages setLockedAccountError(final String lockedAccountError) {
        this.lockedAccountError = lockedAccountError;
        return this;
    }

    public String getExpiredCredentialsError() {
        return expiredCredentialsError;
    }

    public LoginErrorMessages setExpiredCredentialsError(final String expiredCredentialsError) {
        this.expiredCredentialsError = expiredCredentialsError;
        return this;
    }

    public String getDisabledRoleError() {
        return disabledRoleError;
    }

    public LoginErrorMessages setDisabledRoleError(final String disabledRoleError) {
        this.disabledRoleError = disabledRoleError;
        return this;
    }

    public String getExcessiveAttemptsError() {
        return excessiveAttemptsError;
    }

    public LoginErrorMessages setExcessiveAttemptsError(final String excessiveAttemptsError) {
        this.excessiveAttemptsError = excessiveAttemptsError;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof LoginErrorMessages)) return false;

        final var loginErrorMessages = (LoginErrorMessages) o;

        return new EqualsBuilder()
                .append(incorrectCredentialsError, loginErrorMessages.incorrectCredentialsError)
                .append(lockedAccountError, loginErrorMessages.lockedAccountError)
                .append(expiredCredentialsError, loginErrorMessages.expiredCredentialsError)
                .append(disabledRoleError, loginErrorMessages.disabledRoleError)
                .append(excessiveAttemptsError, loginErrorMessages.excessiveAttemptsError)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(incorrectCredentialsError)
                .append(lockedAccountError)
                .append(expiredCredentialsError)
                .append(disabledRoleError)
                .append(excessiveAttemptsError)
                .toHashCode();
    }
}
