package com.jgcomptech.tools.authz;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple implementation of the AuthorizationInfo interface that
 * stores roles and permissions as internal attributes.
 * @since 1.5.0
 */
public class SimpleAuthorizationInfo implements AuthorizationInfo {

    /**
     * The internal roles collection.
     */
    private Set<String> roles;

//    /**
//     * Collection of all string-based permissions associated with the account.
//     */
//    private Set<String> stringPermissions;
//
//    /**
//     * Collection of all object-based permissions associated with the account.
//     */
//    private Set<Permission> objectPermissions;

    /**
     * Default no-argument constructor.
     */
    public SimpleAuthorizationInfo() { }

    /**
     * Creates a new instance with the specified roles and no permissions.
     * @param roles the roles assigned to the realm account
     */
    public SimpleAuthorizationInfo(final Set<String> roles) {
        this.roles = roles;
    }

    /**
     * Returns all assigned roles.
     * @return all assigned roles
     */
    @Override
    public Set<String> getRoles() {
        if (roles == null) roles = new HashSet<>();
        return roles;
    }

//    /**
//     * Sets the roles assigned to the account.
//     * @param roles the roles assigned to the account
//     */
//    public void setRoles(final Set<String> roles) { this.roles = roles; }
//
//    /**
//     * Adds (assigns) a role to those associated with the account.
//     * If the account doesn't yet have any roles,
//     * a new roles collection (a Set) will be created automatically.
//     * @param role the role to add to those associated with the account
//     */
//    public void addRole(final String role) {
//        if (roles == null) roles = new HashSet<>();
//        roles.add(role);
//    }
//
//    /**
//     * Adds (assigns) multiple roles to those associated with the account.
//     * If the account doesn't yet have any roles,
//     * a new roles collection (a Set) will be created automatically.
//     * @param roles the roles to add to those associated with the account
//     */
//    public void addRoles(final Collection<String> roles) {
//        if (this.roles == null) this.roles = new HashSet<>();
//        this.roles.addAll(roles);
//    }

//    /**
//     * Returns all assigned String-based permissions.
//     * @return all assigned String-based permissions
//     */
//    @Override
//    public Set<String> getStringPermissions() {
//        if (stringPermissions == null) stringPermissions = new HashSet<>();
//        return stringPermissions;
//    }

//    /**
//     * Sets the string-based permissions assigned directly to the account.
//     * The permissions set here, in addition to any {@link #getObjectPermissions() object permissions}
//     * constitute the total permissions assigned directly to the account.
//     * @param stringPermissions the string-based permissions assigned directly to the account
//     */
//    public void setStringPermissions(final Set<String> stringPermissions) {
//        this.stringPermissions = stringPermissions;
//    }
//
//    /**
//     * Adds (assigns) a permission to those directly associated with the account.
//     * If the account doesn't yet have any direct permissions,
//     * a new permission collection (a Set&lt;String&gt;) will be created automatically.
//     * @param permission the permission to add to those directly assigned to the account
//     */
//    public void addStringPermission(final String permission) {
//        if (stringPermissions == null) stringPermissions = new HashSet<>();
//        stringPermissions.add(permission);
//    }
//
//    /**
//     * Adds (assigns) multiple permissions to those associated directly with the account.
//     * If the account doesn't yet have any string-based permissions,
//     * a new permissions collection (a Set&lt;String&gt;) will be created automatically.
//     * @param permissions the permissions to add to those associated directly with the account
//     */
//    public void addStringPermissions(final Collection<String> permissions) {
//        if (stringPermissions == null) stringPermissions = new HashSet<>();
//        stringPermissions.addAll(permissions);
//    }

//    /**
//     * Returns all assigned object-based permissions.
//     * @return all assigned object-based permissions
//     */
//    @Override
//    public Set<Permission> getObjectPermissions() {
//        if (objectPermissions == null) objectPermissions = new HashSet<>();
//        return objectPermissions;
//    }

//    /**
//     * Sets the object-based permissions assigned directly to the account.
//     * The permissions set here, in addition to any
//     * {@link #getStringPermissions() string permissions} constitute the
//     * total permissions assigned directly to the account.
//     * @param objectPermissions the object-based permissions assigned directly to the account
//     */
//    public void setObjectPermissions(final Set<Permission> objectPermissions) {
//        this.objectPermissions = objectPermissions;
//    }
//
//    /**
//     * Adds (assigns) a permission to those directly associated with the account.
//     * If the account doesn't yet have any direct permissions, a new permission
//     * collection (a Set&lt;{@link Permission Permission}&gt;) will be created automatically.
//     * @param permission the permission to add to those directly assigned to the account.
//     */
//    public void addObjectPermission(final Permission permission) {
//        if (objectPermissions == null) objectPermissions = new HashSet<>();
//        objectPermissions.add(permission);
//    }
//
//    /**
//     * Adds (assigns) multiple permissions to those associated directly with the account.
//     * If the account doesn't yet have any object-based permissions,
//     * a new permissions collection (a Set&lt;{@link Permission Permission}&gt;) will be created automatically.
//     * @param permissions the permissions to add to those associated directly with the account.
//     */
//    public void addObjectPermissions(final Collection<Permission> permissions) {
//        if (objectPermissions == null) objectPermissions = new HashSet<>();
//        objectPermissions.addAll(permissions);
//    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof SimpleAuthorizationInfo)) return false;

        final var simpleAuthorizationInfo = (SimpleAuthorizationInfo) o;

        return new EqualsBuilder()
                .append(roles, simpleAuthorizationInfo.roles)
//                .append(stringPermissions, simpleAuthorizationInfo.stringPermissions)
//                .append(objectPermissions, simpleAuthorizationInfo.objectPermissions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(roles)
//                .append(stringPermissions)
//                .append(objectPermissions)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("roles", roles)
//                .append("stringPermissions", stringPermissions)
//                .append("objectPermissions", objectPermissions)
                .toString();
    }
}

