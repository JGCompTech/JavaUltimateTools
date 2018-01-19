package com.jgcomptech.tools.authenication;

import com.jgcomptech.tools.permissions.PermissionManager;

import java.util.HashMap;
import java.util.HashSet;

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
        SystemUserRoles.ADMIN.getRole().addPermission(PermissionManager.SystemPermissions.Admin.getName());
        SystemUserRoles.ADMIN.getRole().addPermission(PermissionManager.SystemPermissions.Edit.getName());
        SystemUserRoles.ADMIN.getRole().addPermission(PermissionManager.SystemPermissions.Create.getName());
        SystemUserRoles.ADMIN.getRole().addPermission(PermissionManager.SystemPermissions.Read.getName());

        SystemUserRoles.EDITOR.getRole().addPermission(PermissionManager.SystemPermissions.Edit.getName());
        SystemUserRoles.EDITOR.getRole().addPermission(PermissionManager.SystemPermissions.Create.getName());
        SystemUserRoles.EDITOR.getRole().addPermission(PermissionManager.SystemPermissions.Read.getName());

        SystemUserRoles.AUTHOR.getRole().addPermission(PermissionManager.SystemPermissions.Create.getName());
        SystemUserRoles.AUTHOR.getRole().addPermission(PermissionManager.SystemPermissions.Read.getName());

        SystemUserRoles.BASIC.getRole().addPermission(PermissionManager.SystemPermissions.Read.getName());

        addExistingUserRole(SystemUserRoles.ADMIN.role);
        addExistingUserRole(SystemUserRoles.EDITOR.role);
        addExistingUserRole(SystemUserRoles.AUTHOR.role);
        addExistingUserRole(SystemUserRoles.BASIC.role);
        addExistingUserRole(SystemUserRoles.NONE.role);
    }

    /** A List of the implemented system user roles. */
    public enum SystemUserRoles {
        /** Has Admin, Edit, Create and Read Permissions. */
        ADMIN("admin", new UserRole("admin")),
        /** Has Edit, Create and Read Permissions. */
        EDITOR("editor", new UserRole("editor")),
        /** Has Create and Read Permissions. */
        AUTHOR("author", new UserRole("author")),
        /** Has Read Permissions. */
        BASIC("basic", new UserRole("basic")),
        /** Does not have any Permissions. */
        NONE("none", new UserRole("none"));
        private final String name;
        private final UserRole role;

        SystemUserRoles(final String name, final UserRole role) {
            this.name = name;
            this.role = role;
            getRole().enable();
        }

        public String getName() { return name; }

        public UserRole getRole() { return role; }

        public static HashSet<String> getRoles() {
            HashSet<String> types = new HashSet<>();
            for(SystemUserRoles type : values()) {
                types.add(type.name);
            }
            return types;
        }
    }

    /**
     * Creates a new role and adds it to the list.
     * @param name the name of the new role
     * @return the new role as a UserRole object
     */
    public UserRole createUserRole(final String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name Cannot Be Empty!");
        }

        UserRole role = null;
        if(!userRoles.containsKey(name)) {
            role = new UserRole(name);
            userRoles.put(name, role);
        }
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
    public HashMap<String, UserRole> getUserRoles() { return userRoles; }

    /**
     * Returns the specified user role.
     * @param name the name of the user role to return
     * @return the specified user role
     */
    public UserRole getUserRole(final String name) { return userRoles.get(name); }
}
