package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.authz.PermissionManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all user account roles.
 * @since 1.4.0
 */
public final class UserRoleManager {
    private static UserRoleManager instance;
    private final HashMap<String, UserRole> userRoles = new HashMap<>();

    /**
     * Returns the singleton instance of the UserRoleManager.
     * @return the singleton instance of the UserRoleManager
     */
    public static UserRoleManager getInstance() {
        if(instance == null) instance = new UserRoleManager();
        return instance;
    }

    /** Creates an instance of the UserRoleManager. */
    private UserRoleManager() {
        final var Admin = PermissionManager.SystemPermissions.Admin.getName();
        final var Edit = PermissionManager.SystemPermissions.Edit.getName();
        final var Create = PermissionManager.SystemPermissions.Create.getName();
        final var Read = PermissionManager.SystemPermissions.Read.getName();

        SystemUserRoles.ADMIN.getRole().modify().add(Admin, Edit, Create, Read);

        SystemUserRoles.EDITOR.getRole().modify().add(Edit, Create, Read);

        SystemUserRoles.AUTHOR.getRole().modify().add(Create, Read);

        SystemUserRoles.BASIC.getRole().modify().add(Read);

        addExistingUserRole(SystemUserRoles.ADMIN.getRole());
        addExistingUserRole(SystemUserRoles.EDITOR.getRole());
        addExistingUserRole(SystemUserRoles.AUTHOR.getRole());
        addExistingUserRole(SystemUserRoles.BASIC.getRole());
        addExistingUserRole(SystemUserRoles.NONE.getRole());
    }

    /** A List of the implemented system user roles. */
    public enum SystemUserRoles {
        /** Has Admin, Edit, Create and Read Permissions. */
        ADMIN(new UserRole("admin")),
        /** Has Edit, Create and Read Permissions. */
        EDITOR(new UserRole("editor")),
        /** Has Create and Read Permissions. */
        AUTHOR(new UserRole("author")),
        /** Has Read Permissions. */
        BASIC(new UserRole("basic")),
        /** Does not have any Permissions. */
        NONE(new UserRole("none"));
        private final UserRole role;

        SystemUserRoles(final UserRole role) {
            this.role = role;
            this.role.enable();
        }

        public String getName() { return role.getName(); }

        public UserRole getRole() { return role; }

        public static HashSet<String> getRoles() {
            return Arrays.stream(values()).map(p -> p.role.getName()).collect(Collectors.toCollection(HashSet::new));
        }
    }

    /**
     * Creates a new role and adds it to the list.
     * @param name the name of the new role
     * @return the new role as a UserRole object
     */
    public UserRole createUserRole(final String name) {
        if(name == null || name.trim().trim().isEmpty()) {
            throw new IllegalArgumentException("Name Cannot Be Empty!");
        }

        final UserRole role;
        if(!userRoles.containsKey(name)) {
            role = new UserRole(name);
            userRoles.put(name, role);
        } else role = userRoles.get(name);
        return role;
    }

    /**
     * Adds an existing role to the list.
     * @param role the role to be added
     */
    public void addExistingUserRole(final UserRole role) {
        if(role == null) {
            throw new IllegalArgumentException("Role Cannot Be Null!");
        }
        if(!userRoles.containsKey(role.getName())) {
            userRoles.put(role.getName(), role);
        }
    }

    /**
     * Returns a list of the current existing user roles.
     * @return a list of the current existing user roles
     */
    public Map<String, UserRole> getUserRoles() { return Collections.unmodifiableMap(userRoles); }

    /**
     * Returns the specified user role.
     * @param name the name of the user role to return
     * @return the specified user role
     */
    public UserRole getUserRole(final String name) { return userRoles.get(name); }
}
