package com.jgcomptech.tools.authz;

import com.jgcomptech.tools.authc.UserRole;
import com.jgcomptech.tools.authc.UserRoleManager;
import com.jgcomptech.tools.events.EventHandler;
import com.jgcomptech.tools.events.EventManager;
import com.jgcomptech.tools.events.EventTarget;
import com.jgcomptech.tools.events.PermissionEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages permissions to be used to enable or disable parts of your application.
 * @since 1.4.0
 * @since 1.5.0 Changed implementation
 */
public final class PermissionManager extends EventTarget<PermissionEvent> {
    private HashMap<String, Permission> permissions;
    private final EventManager eventManager = EventManager.getInstance();
    private PermissionEvent eventPermissionsApplied;
    private PermissionEvent eventAllPermissionsEnabled;
    private PermissionEvent eventAllPermissionsDisabled;

    private static PermissionManager instance;

    /** Prevents instantiation of this utility class. */
    private PermissionManager() { }

    /**
     * Returns the instance of the PermissionManager, if one doesn't exist it is created.
     * @return the instance of the PermissionManager
     */
    public static synchronized PermissionManager getInstance() {
        if(instance == null) {
            instance = new PermissionManager();
            instance.permissions = new HashMap<>();
            instance.addCustomPermission(SystemPermissions.Admin.name, null);
            instance.addCustomPermission(SystemPermissions.Edit.name, null);
            instance.addCustomPermission(SystemPermissions.Create.name, null);
            instance.addCustomPermission(SystemPermissions.Read.name, null);

            try {
                instance.eventPermissionsApplied =
                        instance.eventManager.registerNewEvent("permissionsApplied",
                        PermissionEvent.class,
                        instance,
                        PermissionEvent.PERMISSIONS_APPLIED);
            } catch(final Exception e) {
                throw new IllegalStateException("sessionPermissionsApplied Event Failed To Load!");
            }

            try {
                instance.eventAllPermissionsEnabled =
                        instance.eventManager.registerNewEvent("permissionsAllEnabled",
                                PermissionEvent.class,
                                instance,
                                PermissionEvent.PERMISSIONS_ALL_ENABLED);
            } catch(final Exception e) {
                throw new IllegalStateException("sessionPermissionsApplied Event Failed To Load!");
            }

            try {
                instance.eventAllPermissionsDisabled =
                        instance.eventManager.registerNewEvent("permissionsAllDisabled",
                                PermissionEvent.class,
                                instance,
                                PermissionEvent.PERMISSIONS_ALL_DISABLED);
            } catch(final Exception e) {
                throw new IllegalStateException("sessionPermissionsApplied Event Failed To Load!");
            }
        }
        return instance;
    }

    //region System Permissions

    /** A list of the implemented permission types. */
    public enum SystemPermissions {
        Admin("admin"),
        Edit("edit"),
        Create("create"),
        Read("read");

        private final String name;

        SystemPermissions(final String name) {
            this.name = name;
        }

        public String getName() { return name; }

        public static Set<String> getNames() {
            return Arrays.stream(values()).map(p -> p.name).collect(Collectors.toCollection(HashSet::new));
        }

        @Override
        public String toString() { return name; }
    }

    /**
     * Enables basic event handlers with System.Out logging for all events.
     * @since 1.5.0
     */
    public void enableDebugLogging() {
        setOnPermissionsApplied(e -> {
            final var userRole = (UserRole) e.getArgs().get(0);
            System.out.println("EVENT: " + userRole.getName() + " Permissions Applied!");
        });
        setOnAllPermissionsEnabled(e ->
                System.out.println("EVENT: All permissions enabled!"));
        setOnAllPermissionsDisabled(e ->
                System.out.println("EVENT: All permissions disabled!"));
        final EventHandler<PermissionEvent> permissionEnabled = e -> {
            final var permission = e.getPermission();
            System.out.println("EVENT: Permission " + permission.getName() + " Enabled!");
        };
        final EventHandler<PermissionEvent> permissionDisabled = e -> {
            final var permission = e.getPermission();
            System.out.println("EVENT: Permission " + permission.getName() + " Disabled!");
        };

        getAdminPermission().setOnEnabled(permissionEnabled);
        getAdminPermission().setOnDisabled(permissionDisabled);

        getEditPermission().setOnEnabled(permissionEnabled);
        getEditPermission().setOnDisabled(permissionDisabled);

        getCreatePermission().setOnEnabled(permissionEnabled);
        getCreatePermission().setOnDisabled(permissionDisabled);

        getReadPermission().setOnEnabled(permissionEnabled);
        getReadPermission().setOnDisabled(permissionDisabled);
    }

    /**
     * Disables basic event handlers with System.Out logging for all events.
     * @since 1.5.0
     */
    public void disableDebugLogging() {
        setOnPermissionsApplied(null);
        setOnAllPermissionsEnabled(null);
        setOnAllPermissionsDisabled(null);

        getAdminPermission().setOnEnabled(null);
        getAdminPermission().setOnDisabled(null);

        getEditPermission().setOnEnabled(null);
        getEditPermission().setOnDisabled(null);

        getCreatePermission().setOnEnabled(null);
        getCreatePermission().setOnDisabled(null);

        getReadPermission().setOnEnabled(null);
        getReadPermission().setOnDisabled(null);
    }

    /**
     * Returns true if Admin permission is enabled.
     * @return true if Admin permission is enabled
     */
    public boolean isAdminPermissionEnabled() { return permissions.get(SystemPermissions.Admin.name).isEnabled(); }
    /**
     * Returns true if Edit permission is enabled.
     * @return true if Edit permission is enabled
     */
    public boolean isEditPermissionEnabled() { return permissions.get(SystemPermissions.Edit.name).isEnabled(); }
    /**
     * Returns true if Create permission is enabled.
     * @return true if Create permission is enabled
     */
    public boolean isCreatePermissionEnabled() { return permissions.get(SystemPermissions.Create.name).isEnabled(); }
    /**
     * Returns true if Read permission is enabled.
     * @return true if Read permission is enabled
     */
    public boolean isReadPermissionEnabled() { return permissions.get(SystemPermissions.Read.name).isEnabled(); }

    /**
     * Manually sets the status of the Admin permission.
     * @param value the value to set
     */
    public void setAdminPermission(final boolean value) {
        permissions.get(SystemPermissions.Admin.name).setEnabled(value); }
    /**
     * Manually sets the status of the Edit permission.
     * @param value the value to set
     */
    public void setEditPermission(final boolean value) {
        permissions.get(SystemPermissions.Edit.name).setEnabled(value); }
    /**
     * Manually sets the status of the Create permission.
     * @param value the value to set
     */
    public void setCreatePermission(final boolean value) {
        permissions.get(SystemPermissions.Create.name).setEnabled(value); }
    /**
     * Manually sets the status of the Read permission.
     * @param value the value to set
     */
    public void setReadPermission(final boolean value) {
        permissions.get(SystemPermissions.Read.name).setEnabled(value); }

    /**
     * Returns the Admin permission.
     * @return the permission object
     */
    public Permission getAdminPermission() { return permissions.get(SystemPermissions.Admin.name); }
    /**
     * Returns the Edit permission.
     * @return the permission object
     */
    public Permission getEditPermission() { return permissions.get(SystemPermissions.Edit.name); }
    /**
     * Returns the Create permission.
     * @return the permission object
     */
    public Permission getCreatePermission() { return permissions.get(SystemPermissions.Create.name); }
    /**
     * Returns the Read permission.
     * @return the permission object
     */
    public Permission getReadPermission() { return permissions.get(SystemPermissions.Read.name); }

    //endregion System Permissions

    /**
     * Returns a HashMap of all existing permission objects.
     * @return a HashMap of all existing permission objects
     */
    public HashMap<String, Permission> getPermissions() { return permissions; }

    /**
     * Returns a list of names of existing permissions.
     * @return a list of names of existing permissions
     */
    public Set<String> getPermissionsNames() { return permissions.keySet(); }

    /**
     * Returns the specified permission.
     * @param name the name of the permission to lookup
     * @return the specified permission
     */
    public Permission getPermission(final String name) {
        if(doesPermissionExist(name)) return permissions.get(name);
        else throw new AuthorizationException("\"" + name + "\" Permission Not Found!");
    }

    /**
     * Checks if the specified permission exists.
     * @param name the name of the permisssion to check
     * @return true if the specified permission exists
     * @since 1.5.0
     */
    public boolean doesPermissionExist(final String name) {
        return permissions.containsKey(name);
    }

    /**
     * Removes the specified permission.
     * @param name the name of the permission to remove
     * @return false if the permission does not exist or is a system permission
     * @since 1.5.0 now uses a parallel stream allowing a large list of permissions
     */
    public boolean removePermission(final String name) {
        if(!SystemPermissions.getNames().contains(name) && permissions.containsKey(name)) {
            permissions.values()
                    .parallelStream()
                    .forEach(p -> p.getChildPermissions().removeIf(c -> c.equals(name)));
            permissions.remove(name);
            return true;
        } else return false;
    }

    /**
     * Enables the specified permission and all its children.
     * @param name the name of the permission to enable
     * @return false if the permission does not exist
     */
    public boolean enablePermission(final String name) {
        if(!permissions.containsKey(name)) return false;
        else {
            getPermission(name).enable();
            return true;
        }
    }

    /**
     * Disables the specified permission and all its children.
     * @param name the name of the permission to disable
     * @return false if the permission does not exist
     */
    public boolean disablePermission(final String name) {
        if(!permissions.containsKey(name)) return false;
        else {
            getPermission(name).disable();
            return true;
        }
    }

    /**
     * Checks if the specified permission is enabled.
     * @param name the name of the permission to check
     * @return false if the permission is disabled or if the permission does not exist
     */
    public boolean isPermissionEnabled(final String name) {
        return permissions.containsKey(name) && getPermission(name).isEnabled();
    }

    /**
     * Checks if the specified permission is enabled.
     * @param name the name of the permission to check
     * @return true if the permission is disabled or if the permission does not exist
     */
    public boolean isPermissionDisabled(final String name) {
        return !permissions.containsKey(name) || getPermission(name).isDisabled();
    }

    /**
     * Returns a list of child permissions of the specified permission.
     * @param name the name of the permission
     * @return a list of child permissions of the specified permission.
     * Returns null if the permission does not exist.
     */
    public HashSet<String> getPermissionChildren(final String name) {
        return permissions.containsKey(name) ? permissions.get(name).getChildPermissions() : null;
    }

    /**
     * Adds new permission with the specified name and parent name, disabled by default.
     * @param name the name of the new permission
     * @param parentName the name of the parent permission, null or empty string if none
     * @return false if permission already exists
     */
    public boolean addCustomPermission(final String name, final String parentName) {
        if(name.equals(parentName)) return false;
        else if(parentName == null || parentName.trim().isEmpty()) {
            if(permissions.containsKey(name)) return false;
            else {
                permissions.put(name, new Permission(name, null));
                return true;
            }
        } else  {
            if(!permissions.containsKey(parentName)) return false;
            else if(permissions.containsKey(parentName + ":" + name)) return false;
            else {
                final var newName = parentName + ":" + name;
                permissions.put(newName, new Permission(name, parentName));
                final var parent = permissions.get(parentName);
                parent.getChildPermissions().add(newName);
                if(parent.isEnabled()) enablePermission(newName);
                else disablePermission(newName);
                return true;
            }
        }
    }

    /**
     * Adds new permission with the specified name and parent name, enabled by default.
     * @param name the name of the new permission
     * @param parentName the name of the parent permission, null or empty string if none
     * @return false if permission already exists
     */
    public boolean addAndEnableCustomPermission(final String name, final String parentName) {
        if(addCustomPermission(name, parentName)) {
            return parentName == null || parentName.trim().isEmpty()
                    ? enablePermission(name)
                    : enablePermission(parentName + ":" + name);
        } else return false;
    }

    /**
     * Sets the EventHandler for the OnEnabled event for the specified permission.
     * @param name the name of the permission to add EventHandler to
     * @param e the EventHandler to set
     * @return false if the permission does not exist
     */
    public boolean setPermissionOnEnabled(final String name, final EventHandler<PermissionEvent> e) {
        if(permissions.containsKey(name)) {
            permissions.get(name).setOnEnabled(e);
            return true;
        } else return false;
    }

    /**
     * Sets the EventHandler for the OnDisabled event for the specified permission.
     * @param name the name of the permission to add EventHandler to
     * @param e the EventHandler to set
     * @return false if the permission does not exist
     */
    public boolean setPermissionOnDisabled(final String name, final EventHandler<PermissionEvent> e) {
        if(permissions.containsKey(name)) {
            permissions.get(name).setOnDisabled(e);
            return true;
        } else return false;
    }

    /**
     * Sets the event handler that will fire when multiple permissions loaded.
     * @param e the event handler
     */
    public void setOnPermissionsApplied(final EventHandler<PermissionEvent> e) {
        if(e == null) removeEventHandler(PermissionEvent.PERMISSIONS_APPLIED);
        else addEventHandler(PermissionEvent.PERMISSIONS_APPLIED, e);
    }

    /**
     * Sets the event handler that will fire when multiple permissions loaded.
     * @param e the event handler
     */
    public void setOnAllPermissionsEnabled(final EventHandler<PermissionEvent> e) {
        if(e == null) removeEventHandler(PermissionEvent.PERMISSIONS_ALL_ENABLED);
        else addEventHandler(PermissionEvent.PERMISSIONS_ALL_ENABLED, e);
    }

    /**
     * Sets the event handler that will fire when multiple permissions loaded.
     * @param e the event handler
     */
    public void setOnAllPermissionsDisabled(final EventHandler<PermissionEvent> e) {
        if(e == null) removeEventHandler(PermissionEvent.PERMISSIONS_ALL_DISABLED);
        else addEventHandler(PermissionEvent.PERMISSIONS_ALL_DISABLED, e);
    }

    /**
     * Sets all permissions, if enableAll is true, all are enabled and false all are disabled.
     * @param enableAll if true all are enabled and if false all are disabled
     * @since 1.5.0 now uses a parallel stream allowing a large list of permissions
     */
    public void loadPermissions(final boolean enableAll) {
        if(enableAll) permissions.values().parallelStream().forEach(Permission::enable);
        else permissions.values().parallelStream().forEach(Permission::disable);

        if(enableAll) eventAllPermissionsEnabled.fireEvent(this);
        else eventAllPermissionsDisabled.fireEvent(this);
    }

    /**
     * Sets the correct permissions according to the specified user type.
     * @param userRole the user role to use to set permissions
     */
    public void loadPermissions(final UserRoleManager.SystemUserRoles userRole) { loadPermissions(userRole.getRole()); }

    /**
     * Sets the correct permissions according to the specified user type.
     * @param userRole the user role to use to set permissions
     * @since 1.5.0 now uses a parallel stream allowing a large list of permissions
     */
    public void loadPermissions(final UserRole userRole) {
        getAdminPermission().disable();
        getEditPermission().disable();
        getCreatePermission().disable();
        getReadPermission().disable();
        userRole.getPermissions().parallelStream().forEach(this::enablePermission);
        eventPermissionsApplied.fireEvent(this, userRole);
    }

    @Override public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning Permission Manager Is Not Allowed!"); }
}
