package com.jgcomptech.tools.authc;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Interface used for objects that contain Authentication info.
 * @since 1.5.0
 */
public interface AuthenticationInfo extends Serializable {
    String getUsername();
    boolean isLocked();
    boolean isPasswordExpired();
    LocalDateTime getCreationDate();
    LocalDateTime getPasswordExpirationDate();
    boolean hasPasswordExpiration();
}
