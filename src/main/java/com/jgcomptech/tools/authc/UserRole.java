package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.authz.PermissionManager;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An object representing a user account role.
 * @since 1.4.0
 * @since 1.5.0 Updated permission integration
 */
public class UserRole {
    private final String name;
    private final HashSet<String> permissions = new HashSet<>();
    private boolean enabled;
    private final PermissionManager permissionManager = PermissionManager.getInstance();

    public UserRole(final String name) {
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty!");
        } else this.name = name;
        enable();
    }

    /**
     * Returns the name of the user role.
     * @return the name of the user role
     */
    public String getName() { return name; }

    @Override
    public String toString() { return name; }

    /**
     * Checks if the user role is enabled.
     * @return true if the user role is enabled
     */
    public boolean isEnabled() { return enabled; }

    /** Enables the user role. */
    public void enable() { enabled = true; }

    /**
     * Attempts to disable the user role.
     * @throws IllegalStateException if the user role is a system role because system roles cannot be disabled
     */
    public void disable() {
        if(UserRoleManager.SystemUserRoles.getRoles().contains(name)) {
            throw new IllegalStateException("This role is a system role and cannot be disabled!");
        }
        enabled = false;
    }

    /**
     * Returns a list of permissions assigned to the user role.
     * @return a list of permissions assigned to the user role
     */
    public Set<String> getPermissions() { return Collections.unmodifiableSet(permissions); }

    /**
     * Checks if the user role has the specified permission.
     * @param name the name of the permission to check
     * @return true if the user role has the specified permission
     * @since 1.5.0
     */
    public boolean hasPermission(final String name) {
        if(!permissionManager.doesPermissionExist(name)) return false;
        if(permissions.contains(name)) return true;
        else if (name.contains(":")) {
            final var topParentName = name.substring(0, name.indexOf(":"));
            return permissions.contains(topParentName);
        }
        else return false;
    }

    /**
     * Checks if the user role has the specified permissions.
     * @param names the names of the permissions to check
     * @return true if the user role has the specified permissions
     * @since 1.5.0
     */
    public boolean hasPermissions(final String... names) {
        return Arrays.stream(names).allMatch(this::hasPermission);
    }

    /**
     * Adds the specified permission to the user role.
     * @param name the name of the permission to add
     * @return true if permission is added successfully
     * @throws IllegalArgumentException if attempting to add to the NONE system role
     */
    public boolean addPermission(final String name) {
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty!");
        }
        if(this.name.equals(UserRoleManager.SystemUserRoles.NONE.getName())) {
            throw new IllegalArgumentException("Permissions cannot be added to user role NONE!");
        }
        if(hasPermission(name)) return false;
        else {
            if(permissionManager.getPermissionsNames().contains(name)) {
                if(permissions.contains(name)) return false;
                else {
                    permissions.add(name);
                    return true;
                }
            } else return false;
        }
    }

    /**
     * Adds the specified permissions to the user role.
     * @param names the names of the permissions to add
     * @return true if permission is added successfully
     * @throws IllegalArgumentException if attempting to add to the NONE system role
     */
    public boolean addPermissions(final String... names) {
        return Arrays.stream(names).allMatch(this::addPermission);
    }

    /**
     * Implicitly adds the specified permission to the user role even if it is already inherited.
     * @param name the name of the permission to add
     * @return true if permission is added successfully
     * @throws IllegalArgumentException if attempting to add to the NONE system role
     * @since 1.5.0
     */
    public boolean addImplicitPermission(final String name) {
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty!");
        }
        if(this.name.equals(UserRoleManager.SystemUserRoles.NONE.getName())) {
            throw new IllegalArgumentException("Permissions cannot be added to user role NONE!");
        }
        if(permissionManager.getPermissionsNames().contains(name)) {
            if(permissions.contains(name)) return false;
            else {
                permissions.add(name);
                return true;
            }
        } else return false;
    }

    /**
     * Implicitly adds the specified permissions to the user role even if they are already inherited.
     * @param names the name of the permissions to add
     * @return true if permissions are added successfully
     * @throws IllegalArgumentException if attempting to add to the NONE system role
     * @since 1.5.0
     */
    public boolean addImplicitPermissions(final String... names) {
        return Arrays.stream(names).allMatch(this::addImplicitPermission);
    }

    /**
     * Removes the specified permission from the user role. Inherited permissions are not removed.
     * @param name the name of the permission to remove
     * @return true if permission is removed successfully
     */
    public boolean removePermission(final String name) {
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty!");
        }

        if(!permissions.contains(name)) return false;
        else {
            permissions.remove(name);
            return true;
        }
    }

    /**
     * Removes the specified permissions from the user role. Inherited permissions are not removed.
     * @param names the names of the permissions to remove
     * @return true if permissions are removed successfully
     */
    public boolean removePermissions(final String... names) {
        return Arrays.stream(names).allMatch(this::removePermission);
    }

    /**
     * Creates a new instance of the UserRole modification builder.
     * @return a new instance of the UserRole modification builder
     */
    public Builder modify() { return new Builder(this); }

    /**
     * A builder class to be used to modify UserRoles.
     * @since 1.5.0
     */
    public class Builder {
        final UserRole role;
        public Builder(final UserRole role) {
            this.role = role;
        }

        /**
         * Adds the specified permission to the user role.
         * @param name the name of the permission to add
         * @return the current instance of the builder
         * @throws IllegalStateException if permission failed to be added
         * @throws IllegalArgumentException if attempting to add to the NONE system role
         */
        public Builder add(final String name) {
            final var result = role.addPermission(name);
            if(!result) throw new IllegalStateException(
                    "Modification Failed! Could Not Add Permission \""
                            + name + "\" to user role \"" + role.getName() + "\"!");
            return this;
        }

        /**
         * Adds the specified permissions to the user role.
         * @param names the names of the permissions to add
         * @return the current instance of the builder
         * @throws IllegalStateException if permissions failed to be added
         * @throws IllegalArgumentException if attempting to add to the NONE system role
         */
        public Builder add(final String... names) {
            final var result = Arrays.stream(names).allMatch(role::addPermission);
            if(!result) throw new IllegalStateException(
                    "Modification Failed! Could Not Add Permissions \""
                            + name + "\" to user role \"" + role.getName() + "\"!");
            return this;
        }

        /**
         * Implicitly adds the specified permission to the user role even if it is already inherited.
         * @param name the name of the permission to add
         * @return the current instance of the builder
         * @throws IllegalStateException if permission failed to be added
         * @throws IllegalArgumentException if attempting to add to the NONE system role
         * @since 1.5.0
         */
        public Builder addImplicit(final String name) {
            final var result = role.addImplicitPermission(name);
            if(!result) throw new IllegalStateException(
                    "Modification Failed! Could Not Add Implicit Permission \""
                            + name + "\" to user role \"" + role.getName() + "\"!");
            return this;
        }

        /**
         * Implicitly adds the specified permissions to the user role even if they are already inherited.
         * @param names the name of the permissions to add
         * @return the current instance of the builder
         * @throws IllegalStateException if permission failed to be added
         * @throws IllegalArgumentException if attempting to add to the NONE system role
         * @since 1.5.0
         */
        public Builder addImplicit(final String... names) {
            final var result = Arrays.stream(names).allMatch(role::addImplicitPermission);
            if(!result) throw new IllegalStateException(
                    "Modification Failed! Could Not Add Implicit Permissions \""
                            + name + "\" to user role \"" + role.getName() + "\"!");
            return this;
        }

        /**
         * Removes the specified permission from the user role. Inherited permissions are not removed.
         * @param name the name of the permission to remove
         * @return the current instance of the builder
         * @throws IllegalStateException if permission failed to be removed
         */
        public Builder remove(final String name) {
            final var result = role.removePermission(name);
            if(!result) throw new IllegalStateException(
                    "Modification Failed! Could Not Remove Permission \""
                            + name + "\" from user role \"" + role.getName() + "\"!");
            return this;
        }

        /**
         * Removes the specified permissions from the user role. Inherited permissions are not removed.
         * @param names the names of the permissions to remove
         * @return the current instance of the builder
         * @throws IllegalStateException if permissions failed to be removed
         */
        public Builder remove(final String... names) {
            final var result = Arrays.stream(names).allMatch(role::removePermission);
            if (!result) throw new IllegalStateException(
                    "Modification Failed! Could Not Remove Permissions \""
                            + name + "\" from user role \"" + role.getName() + "\"!");
            return this;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;

            if (!(o instanceof Builder)) return false;

            final var builder = (Builder) o;

            return new EqualsBuilder()
                    .append(role, builder.role)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(role)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("role", role)
                    .toString();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof UserRole)) return false;

        final var userRole = (UserRole) o;

        return new EqualsBuilder()
                .append(enabled, userRole.enabled)
                .append(name, userRole.name)
                .append(permissions, userRole.permissions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(permissions)
                .append(enabled)
                .toHashCode();
    }
}
