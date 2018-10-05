package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.authz.AuthorizationInfo;

/**
 * Interface used for account objects that combines authentication info and authorization info.
 * @since 1.5.0
 */
public interface Account extends AuthenticationInfo, AuthorizationInfo { }
