package com.jgcomptech.tools.tests;

import com.jgcomptech.tools.ExceptionUtils;
import com.jgcomptech.tools.Misc;
import com.jgcomptech.tools.SecurityTools;
import com.jgcomptech.tools.authc.SessionManager;
import com.jgcomptech.tools.authc.UserManager;
import com.jgcomptech.tools.authc.UserRoleManager;
import com.jgcomptech.tools.authz.PermissionManager;
import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.DatabaseType;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

import static com.jgcomptech.tools.SecurityTools.RSAHashes.*;
import static org.junit.Assert.*;

public class Tests {
    /** Tests the {@link Misc#ConvertBytes(double)} method. */
    @Test
    public void TestConvertBytes() {
        var result = Misc.ConvertBytes(107374182400d);
        assertEquals(result, "100 GB");

        result = Misc.ConvertBytes(1099511627775d);
        assertEquals(result, "1023.99 GB");

        result = Misc.ConvertBytes(1099511627776d);
        assertEquals(result, "1 TB");
    }

    /**
     * Tests the {@link SecurityTools.RSAHashes#encryptToString(PublicKey, String)} and
     * {@link SecurityTools.RSAHashes#decryptFromString(PrivateKey, String)} methods.
     */
    @Test
    public void TestRSAEncryptDecrypt() {
        try {
            final var pair = generateKeyPair();
            final var message = "Hello World";
            final var secret = encryptToString(pair.getPublic(), message);
            final var recovered_message = decryptFromString(pair.getPrivate(), secret);
            assertEquals(new String(recovered_message, StandardCharsets.UTF_8), message);
        } catch(final IOException | GeneralSecurityException e) {
            MessageBox.show(e.getMessage(), "Error", MessageBoxIcon.ERROR);
        }
    }

    /** Tests the {@link PermissionManager} class. */
    public void TestPermissionManager() {
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

    /** Tests the {@link SessionManager} class and runs {@link Tests#TestPermissionManager}. */
    @Test
    public void TestSessionManager() {
        TestPermissionManager();

        try(final var db = new Database("./userdb.db", DatabaseType.H2)) {
            final var userManager = new UserManager(db);
            userManager.createUser("admin", "1234", UserRoleManager.SystemUserRoles.ADMIN);

            ExceptionUtils.assertThrownIllegalArgumentException(() -> new SessionManager(null));

            final var manager = userManager.getSessionManager();

            try {
                manager.clone();
                fail( "This method should have thrown CloneNotSupportedException!" );
            } catch (final CloneNotSupportedException ignore) { }

            manager.getEventListeners().setOnSessionOpened(e -> {});
            manager.getEventListeners().setOnSessionClosed(e -> {});
            manager.getEventListeners().setOnLoginSuccess(e -> {});
            manager.getEventListeners().setOnLoginFailure(e -> {});

            assertFalse(manager.isUserLoggedIn());
            manager.loginUser("admin");
            assertTrue(manager.isUserLoggedIn());
            assertEquals("admin", manager.getLoggedInUsername());
            assertEquals(manager.getLoggedInUserRole(), UserRoleManager.SystemUserRoles.ADMIN.getRole());
            manager.logoutUser();

            ExceptionUtils.assertThrownIllegalArgumentException(() -> manager.loginUser(null));
            ExceptionUtils.assertThrownIllegalArgumentException(() -> manager.loginUser(""));
        } catch (final Exception ignore) { }
    }

//    /** Tests the {@link UserAccount} object. */
//    @Test
//    public void TestUserAccount() {
//        try(Database db = new Database("./userdb.db", DatabaseType.H2)) {
//            UserManager userManager = new UserManager(db);
//            userManager.createUser("jlgager", "1234", UserRoleManager.SystemUserRoles.EDITOR);
//            UserAccount account = new UserAccount(userManager, "jlgager");
//            assertTrue(account.getUsername().equals("jlgager"));
//            assertTrue(account.setPassword("1234"));
//            assertTrue(account.checkPasswordMatches("1234"));
//            assertTrue(account.setUserRole(UserRoleManager.SystemUserRoles.ADMIN));
//            assertTrue(account.getUserRole().equals(UserRoleManager.SystemUserRoles.ADMIN.getRole()));
//
//            try {
//                new UserAccount(null, null);
//                fail( "This method should have thrown IllegalArgumentException!" );
//            } catch (IllegalArgumentException ignore) { }
//            try {
//                new UserAccount(userManager, null);
//                fail( "This method should have thrown IllegalArgumentException!" );
//            } catch (IllegalArgumentException ignore) { }
//            try {
//                new UserAccount(userManager, "");
//                fail( "This method should have thrown IllegalArgumentException!" );
//            } catch (IllegalArgumentException ignore) { }
//
//            try {
//                account.setPassword(null);
//                fail( "This method should have thrown IllegalArgumentException!" );
//            } catch (IllegalArgumentException ignore) { }
//            try {
//                account.checkPasswordMatches(null);
//                fail( "This method should have thrown IllegalArgumentException!" );
//            } catch (IllegalArgumentException ignore) { }
//            try {
//                account.setUserRole("");
//                fail( "This method should have thrown IllegalArgumentException!" );
//            } catch (IllegalArgumentException ignore) { }
//        } catch (Exception ignore) { }
//    }

    
}
