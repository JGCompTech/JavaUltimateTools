package com.jgcomptech.tools.authz;

import java.io.Serializable;
import java.util.Collection;

/**
 * Interface used for objects that contain Authorization info.
 * @since 1.5.0
 */
public interface AuthorizationInfo extends Serializable {
    Collection<String> getRoles();
//    Collection<String> getStringPermissions();
//    Collection<Permission> getObjectPermissions();
}
