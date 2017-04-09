package com.jgcomptech.tools.permissions;

import com.jgcomptech.tools.authenication.SessionManager;

import java.util.HashMap;

/** Manages permissions to be used to enable or disable parts of your application */
public final class PermissionManager {
    private HashMap<PermissionType, Permission> permissions;

    private static PermissionManager instance = null;

    private PermissionManager() { /*Exists only to defeat instantiation.*/ }

    /**
     * Returns the instance of the PermissionManager, if one doesn't exist it is created
     * @return the instance of the PermissionManager
     */
    public static synchronized PermissionManager getInstance() {
        if(instance == null) {
            instance = new PermissionManager();
            instance.permissions = new HashMap<>();
        }
        return instance;
    }

    /** A list of the implemented permission types */
    public enum PermissionType {
        Admin,
        Edit,
        Create,
        Read
    }

    /**
     * Returns true if Admin permission is enabled
     * @return true if Admin permission is enabled
     */
    public boolean isAdminPermissionEnabled() { return permissions.get(PermissionType.Admin).isEnabled(); }
    /**
     * Returns true if Edit permission is enabled
     * @return true if Edit permission is enabled
     */
    public boolean isEditPermissionEnabled() { return permissions.get(PermissionType.Edit).isEnabled(); }
    /**
     * Returns true if Create permission is enabled
     * @return true if Create permission is enabled
     */
    public boolean isCreatePermissionEnabled() { return permissions.get(PermissionType.Create).isEnabled(); }
    /**
     * Returns true if Read permission is enabled
     * @return true if Read permission is enabled
     */
    public boolean isReadPermissionEnabled() { return permissions.get(PermissionType.Read).isEnabled(); }

    /**
     * Manually sets the status of the Admin permission
     * @param value the value to set
     */
    public void setAdminPermission(boolean value) { permissions.get(PermissionType.Admin).setEnabled(value); }
    /**
     * Manually sets the status of the Edit permission
     * @param value the value to set
     */
    public void setEditPermission(boolean value) { permissions.get(PermissionType.Edit).setEnabled(value); }
    /**
     * Manually sets the status of the Create permission
     * @param value the value to set
     */
    public void setCreatePermission(boolean value) { permissions.get(PermissionType.Create).setEnabled(value); }
    /**
     * Manually sets the status of the Read permission
     * @param value the value to set
     */
    public void setReadPermission(boolean value) { permissions.get(PermissionType.Read).setEnabled(value); }

    /**
     * Returns a HashMap of all the set permission objects
     * @return a HashMap of all the set permission objects
     * @throws IllegalStateException if permissions have not been set
     */
    public HashMap<PermissionType, Permission> getPermissions() {
        if(permissions.isEmpty()) throw new IllegalStateException("Permissions Table Empty!");
        return (HashMap<PermissionType, Permission>) permissions.clone();
    }

    /**
     * Sets all permissions, if setAllTo is true all are enabled and false all are disabled
     * @param setAllTo if true all are enabled and if false all are disabled
     */
    public void loadPermissions(boolean setAllTo) {
        clearPermissions();
        final Permission adminPermission = new Permission();
        final Permission editPermission = new Permission();
        final Permission createPermission = new Permission();
        final Permission readPermission = new Permission();

        if(setAllTo) {
            adminPermission.enable();
            editPermission.enable();
            createPermission.enable();
            readPermission.enable();
        } else {
            adminPermission.disable();
            editPermission.disable();
            createPermission.disable();
            readPermission.disable();
        }

        permissions.put(PermissionType.Admin, adminPermission);
        permissions.put(PermissionType.Edit, editPermission);
        permissions.put(PermissionType.Create, createPermission);
        permissions.put(PermissionType.Read, readPermission);
    }
    /**
     * Sets the correct permissions according to the specified user type
     * @param userType the user type to use to set permissions
     */
    public void loadPermissions(SessionManager.UserType userType) {
        clearPermissions();
        final Permission adminPermission = new Permission();
        final Permission editPermission = new Permission();
        final Permission createPermission = new Permission();
        final Permission readPermission = new Permission();
        switch(userType) {
            case ADMIN:
                adminPermission.enable();
                editPermission.enable();
                createPermission.enable();
                readPermission.enable();
                break;
            case EDITOR:
                editPermission.enable();
                createPermission.enable();
                readPermission.enable();
                break;
            case AUTHOR:
                createPermission.enable();
                readPermission.enable();
                break;
            case BASIC:
                readPermission.enable();
                break;
            case NONE:
                break;
        }

        permissions.put(PermissionType.Admin, adminPermission);
        permissions.put(PermissionType.Edit, editPermission);
        permissions.put(PermissionType.Create, createPermission);
        permissions.put(PermissionType.Read, readPermission);
    }
    /** Removes all permissions from table, run before permissions are loaded */
    public void clearPermissions() { permissions = new HashMap<>(); }

    @Override public Object clone() throws CloneNotSupportedException { throw new CloneNotSupportedException(); }
}
