package com.jgcomptech.tools.authenication;

import com.jgcomptech.tools.permissions.PermissionManager;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;

/**
 * An object representing a user account role.
 * @since 1.4.0
 */
public class UserRole {
    private final String name;
    private final HashSet<String> permissions = new HashSet<>();
    private boolean enabled;
    private final PermissionManager permissionManager = PermissionManager.getInstance();

    public UserRole(final String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty!");
        } else this.name = name;
        enable();
    }

    public String getName() { return name; }

    @Override
    public String toString() { return getName(); }

    public HashSet<String> getPermissions() { return permissions; }

    public boolean isEnabled() { return enabled; }

    public void enable() { enabled = true; }

    public void disable() {
        if(UserRoleManager.SystemUserRoles.getRoles().contains(name)) {
            throw new IllegalStateException("This role is a system role and cannot be disabled!");
        }
        enabled = false;
    }

    public boolean addPermission(final String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty!");
        }
        if(this.name.equals(UserRoleManager.SystemUserRoles.NONE.getName())) {
            throw new IllegalArgumentException("Permissions cannot be added to user role NONE!");
        }
        if(permissions.contains(name)) return false;
        if(permissionManager.getPermissionsNames().contains(name)) {
            permissions.add(name);
            return true;
        } else return false;
    }

    public boolean removePermission(final String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty!");
        }
        if(!permissions.contains(name)) return false;
        else {
            permissions.remove(name);
            return true;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof UserRole)) return false;

        UserRole userRole = (UserRole) o;

        return new EqualsBuilder()
                .append(isEnabled(), userRole.isEnabled())
                .append(getName(), userRole.getName())
                .append(getPermissions(), userRole.getPermissions())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getName())
                .append(getPermissions())
                .append(isEnabled())
                .toHashCode();
    }
}
