package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.ExceptionUtils;
import com.jgcomptech.tools.authz.PermissionManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserRoleTest {

    /** Tests the {@link UserRole} object. */
    @Test
    public void TestUserRole() {
        ExceptionUtils.assertThrownIllegalArgumentException(() -> new UserRole(null));
        ExceptionUtils.assertThrownIllegalArgumentException(() -> new UserRole(""));

        final var permissionManager = PermissionManager.getInstance();
        final var roleManager = UserRoleManager.getInstance();

        final var role = new UserRole("custom_1");
        final var role2 = role;
        final var role3 = new UserRole("custom_2");
        assertEquals(role, role2);
        assertEquals(role.hashCode(), role2.hashCode());
        assertNotEquals(role, role3);
        assertEquals("custom_1", role.getName());
        assertEquals("custom_1", role.toString());
        assertTrue(role.isEnabled());
        role.disable();
        assertFalse(role.isEnabled());
        assertTrue(role.addPermission("admin"));
        permissionManager.addAndEnableCustomPermission("ban_user", "admin");
        assertTrue(role.hasPermission("admin:ban_user"));
        assertFalse(role.addPermission("admin:ban_user"));
        assertTrue(role.addImplicitPermission("admin:ban_user"));
        assertTrue(role.removePermission("admin"));

        assertFalse(role.addPermission("1234"));
        assertFalse(role.addImplicitPermission("1234"));
        assertFalse(role.removePermissions("1234", "5678"));

        permissionManager.addAndEnableCustomPermission("1", "");
        assertTrue(role.addPermission("1"));
        permissionManager.addAndEnableCustomPermission("2", "");
        permissionManager.addAndEnableCustomPermission("3", "");
        permissionManager.addAndEnableCustomPermission("4", "");
        assertTrue(role.addPermissions("2", "3", "4"));
        assertTrue(role.hasPermission("2"));
        assertTrue(role.hasPermissions("1", "2", "3", "4"));
        roleManager.addExistingUserRole(role);
        assertTrue(roleManager.getUserRole("custom_1").hasPermissions("1", "2", "3", "4"));
        assertEquals(5, role.getPermissions().size());
        assertTrue(role.removePermission("4"));
        assertTrue(role.removePermissions("1", "2", "3"));

        ExceptionUtils.assertThrownIllegalStateException(() -> role.modify().add("1234"));
        ExceptionUtils.assertThrownIllegalStateException(() -> role.modify().add("1234", "5678"));
        ExceptionUtils.assertThrownIllegalStateException(() -> role.modify().addImplicit("1234"));
        ExceptionUtils.assertThrownIllegalStateException(() -> role.modify().addImplicit("4").addImplicit("5678"));
        ExceptionUtils.assertThrownIllegalStateException(() -> role.modify().addImplicit("1234", "5678"));
        ExceptionUtils.assertThrownIllegalStateException(() -> role.modify().remove("1234"));
        ExceptionUtils.assertThrownIllegalStateException(() -> role.modify().remove("1234", "5678"));

        role.modify().remove("admin:ban_user");
        role.modify().add("1", "2");
        role.modify().remove("1", "2");
        role.modify().addImplicit("1", "2");
        role.modify().remove("1", "2");

        ExceptionUtils.assertThrownIllegalStateException(() -> role.modify().remove("admin:ban_user"));

        ExceptionUtils.assertThrownIllegalArgumentException(() -> role.addPermission(null));
        ExceptionUtils.assertThrownIllegalArgumentException(() -> role.addPermission(""));

        ExceptionUtils.assertThrownIllegalArgumentException(() -> role.addImplicitPermission(null));
        ExceptionUtils.assertThrownIllegalArgumentException(() -> role.addImplicitPermission(""));

        ExceptionUtils.assertThrownIllegalArgumentException(() -> {
            final var none = new UserRole("none");
            none.addPermission("1");
        });

        ExceptionUtils.assertThrownIllegalArgumentException(() -> {
            final var none = new UserRole("none");
            none.addImplicitPermission("1");
        });

        ExceptionUtils.assertThrownIllegalStateException(() -> {
            final var none = new UserRole("none");
            none.disable();
        });

        ExceptionUtils.assertThrownIllegalArgumentException(() -> role.removePermission(null));
        ExceptionUtils.assertThrownIllegalArgumentException(() -> role.removePermission(""));
    }
}