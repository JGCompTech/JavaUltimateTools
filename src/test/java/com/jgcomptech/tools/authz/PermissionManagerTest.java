package com.jgcomptech.tools.authz;

import com.jgcomptech.tools.authc.UserRoleManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class PermissionManagerTest {
    /** Tests the {@link PermissionManager} class. */
    @Test
    public void testPermissionManager() {
        final var manager = PermissionManager.getInstance();

        try {
            manager.clone();
            fail( "This method should have thrown CloneNotSupportedException!" );
        } catch (final CloneNotSupportedException ignore) { }

        manager.getPermissions().clear();

        assertTrue(manager.addCustomPermission("admin", null));
        assertTrue(manager.addCustomPermission("edit", null));
        assertTrue(manager.addCustomPermission("create", null));
        assertTrue(manager.addCustomPermission("read", null));

        assertFalse(manager.removePermission("admin"));
        assertFalse(manager.removePermission("edit"));
        assertFalse(manager.removePermission("create"));
        assertFalse(manager.removePermission("read"));
        assertFalse(manager.addCustomPermission("admin", null));
        assertFalse(manager.addCustomPermission("edit", null));
        assertFalse(manager.addCustomPermission("create", null));
        assertFalse(manager.addCustomPermission("read", null));
        assertNotNull(manager.getReadPermission());
        assertNotNull(manager.getEditPermission());
        assertNotNull(manager.getCreatePermission());
        assertNotNull(manager.getReadPermission());
        assertFalse(manager.isAdminPermissionEnabled());
        assertFalse(manager.isEditPermissionEnabled());
        assertFalse(manager.isCreatePermissionEnabled());
        assertFalse(manager.isReadPermissionEnabled());
        manager.setAdminPermission(true);
        manager.setEditPermission(true);
        manager.setCreatePermission(true);
        manager.setReadPermission(true);
        assertTrue(manager.isAdminPermissionEnabled());
        assertTrue(manager.isEditPermissionEnabled());
        assertTrue(manager.isCreatePermissionEnabled());
        assertTrue(manager.isReadPermissionEnabled());
        manager.loadPermissions(false);
        assertFalse(manager.isAdminPermissionEnabled());
        assertFalse(manager.isEditPermissionEnabled());
        assertFalse(manager.isCreatePermissionEnabled());
        assertFalse(manager.isReadPermissionEnabled());
        manager.loadPermissions(true);
        assertTrue(manager.isAdminPermissionEnabled());
        assertTrue(manager.isEditPermissionEnabled());
        assertTrue(manager.isCreatePermissionEnabled());
        assertTrue(manager.isReadPermissionEnabled());

        manager.loadPermissions(UserRoleManager.SystemUserRoles.NONE);
        assertFalse(manager.isAdminPermissionEnabled());
        assertFalse(manager.isEditPermissionEnabled());
        assertFalse(manager.isCreatePermissionEnabled());
        assertFalse(manager.isReadPermissionEnabled());
        manager.loadPermissions(UserRoleManager.SystemUserRoles.ADMIN);
        assertTrue(manager.isAdminPermissionEnabled());
        assertTrue(manager.isEditPermissionEnabled());
        assertTrue(manager.isCreatePermissionEnabled());
        assertTrue(manager.isReadPermissionEnabled());
        manager.loadPermissions(UserRoleManager.SystemUserRoles.EDITOR);
        assertFalse(manager.isAdminPermissionEnabled());
        assertTrue(manager.isEditPermissionEnabled());
        assertTrue(manager.isCreatePermissionEnabled());
        assertTrue(manager.isReadPermissionEnabled());
        manager.loadPermissions(UserRoleManager.SystemUserRoles.AUTHOR);
        assertFalse(manager.isAdminPermissionEnabled());
        assertFalse(manager.isEditPermissionEnabled());
        assertTrue(manager.isCreatePermissionEnabled());
        assertTrue(manager.isReadPermissionEnabled());
        manager.loadPermissions(UserRoleManager.SystemUserRoles.BASIC);
        assertFalse(manager.isAdminPermissionEnabled());
        assertFalse(manager.isEditPermissionEnabled());
        assertFalse(manager.isCreatePermissionEnabled());
        assertTrue(manager.isReadPermissionEnabled());

        assertEquals(4, manager.getPermissionsNames().size());

        final var name = "custom_1";
        assertTrue(manager.addCustomPermission(name, null));
        assertEquals(5, manager.getPermissionsNames().size());
        assertTrue(manager.isPermissionDisabled(name));
        assertFalse(manager.isPermissionEnabled(name));
        assertTrue(manager.enablePermission(name));
        assertFalse(manager.isPermissionDisabled(name));
        assertTrue(manager.isPermissionEnabled(name));
        assertTrue(manager.disablePermission(name));
        assertTrue(manager.isPermissionDisabled(name));
        assertFalse(manager.isPermissionEnabled(name));

//        String name2 = "custom_2";
//        assertTrue(manager.addAndEnableCustomPermission(name2, null));
//        assertTrue(manager.addExistingChildPermission(name2, name));
//        assertTrue(manager.getPermissionChildren(name).size() ==1);
//        assertTrue(manager.getPermissionsNames().size() == 6);
//
//        assertTrue(manager.removePermission(name2));
//        assertTrue(manager.getPermissionsNames().size() == 5);
//
//        assertTrue(manager.removePermission(name));
//        assertTrue(manager.getPermissionsNames().size() == 4);
//
//        assertTrue(manager.setPermissionOnEnabled("admin", e -> {}));
//        assertTrue(manager.setPermissionOnDisabled("admin", e -> {}));
//        assertFalse(manager.setPermissionOnEnabled("1", e -> {}));
//        assertFalse(manager.setPermissionOnDisabled("1", e -> {}));
//        manager.setOnPermissionsApplied(e -> {});
//        manager.setOnAllPermissionsEnabled(e -> {});
//        manager.setOnAllPermissionsDisabled(e -> {});
//
//        assertFalse(manager.isPermissionEnabled("1"));
//        assertTrue(manager.isPermissionDisabled("1"));
    }
}