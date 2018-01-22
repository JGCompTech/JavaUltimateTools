package com.jgcomptech.tools.tests;

import com.jgcomptech.tools.CollectionUtils;
import com.jgcomptech.tools.Misc;
import com.jgcomptech.tools.SecurityTools;
import com.jgcomptech.tools.authenication.*;
import com.jgcomptech.tools.databasetools.jbdc.Database;
import com.jgcomptech.tools.databasetools.jbdc.DatabaseType;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import com.jgcomptech.tools.permissions.PermissionManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;

import static com.jgcomptech.tools.SecurityTools.RSAHashes.*;

public class Tests {
    /** Tests the {@link Misc#ConvertBytes(double)} method. */
    @Test
    public void TestConvertBytes() {
        String result = Misc.ConvertBytes(107374182400d);
        Assert.assertEquals(result, "100 GB");

        result = Misc.ConvertBytes(1099511627775d);
        Assert.assertEquals(result, "1023.99 GB");

        result = Misc.ConvertBytes(1099511627776d);
        Assert.assertEquals(result, "1 TB");
    }

    /**
     * Tests the {@link SecurityTools.RSAHashes#encryptToString(PublicKey, String)} and
     * {@link SecurityTools.RSAHashes#decryptFromString(PrivateKey, String)} methods.
     */
    @Test
    public void TestRSAEncryptDecrypt() {
        try {
            final KeyPair pair = generateKeyPair();
            final String message = "Hello World";
            final String secret = encryptToString(pair.getPublic(), message);
            final byte[] recovered_message = decryptFromString(pair.getPrivate(), secret);
            Assert.assertEquals(new String(recovered_message, StandardCharsets.UTF_8), message);
        } catch(IOException | GeneralSecurityException e) {
            MessageBox.show(e.getMessage(), "Error", MessageBoxIcon.ERROR);
        }
    }

    /** Tests the {@link PermissionManager} class. */
    public void TestPermissionManager() {
        PermissionManager manager = PermissionManager.getInstance();

        try {
            manager.clone();
            Assert.fail( "This method should have thrown CloneNotSupportedException!" );
        } catch (CloneNotSupportedException ignore) { }

        Assert.assertFalse(manager.removePermission("admin"));
        Assert.assertFalse(manager.removePermission("edit"));
        Assert.assertFalse(manager.removePermission("create"));
        Assert.assertFalse(manager.removePermission("read"));
        Assert.assertFalse(manager.addCustomPermission("admin"));
        Assert.assertFalse(manager.addCustomPermission("edit"));
        Assert.assertFalse(manager.addCustomPermission("create"));
        Assert.assertFalse(manager.addCustomPermission("read"));
        Assert.assertTrue(manager.getReadPermission() != null);
        Assert.assertTrue(manager.getEditPermission() != null);
        Assert.assertTrue(manager.getCreatePermission() != null);
        Assert.assertTrue(manager.getReadPermission() != null);
        Assert.assertFalse(manager.isAdminPermissionEnabled());
        Assert.assertFalse(manager.isEditPermissionEnabled());
        Assert.assertFalse(manager.isCreatePermissionEnabled());
        Assert.assertFalse(manager.isReadPermissionEnabled());
        manager.setAdminPermission(true);
        manager.setEditPermission(true);
        manager.setCreatePermission(true);
        manager.setReadPermission(true);
        Assert.assertTrue(manager.isAdminPermissionEnabled());
        Assert.assertTrue(manager.isEditPermissionEnabled());
        Assert.assertTrue(manager.isCreatePermissionEnabled());
        Assert.assertTrue(manager.isReadPermissionEnabled());
        manager.loadPermissions(false);
        Assert.assertFalse(manager.isAdminPermissionEnabled());
        Assert.assertFalse(manager.isEditPermissionEnabled());
        Assert.assertFalse(manager.isCreatePermissionEnabled());
        Assert.assertFalse(manager.isReadPermissionEnabled());
        manager.loadPermissions(true);
        Assert.assertTrue(manager.isAdminPermissionEnabled());
        Assert.assertTrue(manager.isEditPermissionEnabled());
        Assert.assertTrue(manager.isCreatePermissionEnabled());
        Assert.assertTrue(manager.isReadPermissionEnabled());

        manager.loadPermissions(UserRoleManager.SystemUserRoles.NONE);
        Assert.assertFalse(manager.isAdminPermissionEnabled());
        Assert.assertFalse(manager.isEditPermissionEnabled());
        Assert.assertFalse(manager.isCreatePermissionEnabled());
        Assert.assertFalse(manager.isReadPermissionEnabled());
        manager.loadPermissions(UserRoleManager.SystemUserRoles.ADMIN);
        Assert.assertTrue(manager.isAdminPermissionEnabled());
        Assert.assertTrue(manager.isEditPermissionEnabled());
        Assert.assertTrue(manager.isCreatePermissionEnabled());
        Assert.assertTrue(manager.isReadPermissionEnabled());
        manager.loadPermissions(UserRoleManager.SystemUserRoles.EDITOR);
        Assert.assertFalse(manager.isAdminPermissionEnabled());
        Assert.assertTrue(manager.isEditPermissionEnabled());
        Assert.assertTrue(manager.isCreatePermissionEnabled());
        Assert.assertTrue(manager.isReadPermissionEnabled());
        manager.loadPermissions(UserRoleManager.SystemUserRoles.AUTHOR);
        Assert.assertFalse(manager.isAdminPermissionEnabled());
        Assert.assertFalse(manager.isEditPermissionEnabled());
        Assert.assertTrue(manager.isCreatePermissionEnabled());
        Assert.assertTrue(manager.isReadPermissionEnabled());
        manager.loadPermissions(UserRoleManager.SystemUserRoles.BASIC);
        Assert.assertFalse(manager.isAdminPermissionEnabled());
        Assert.assertFalse(manager.isEditPermissionEnabled());
        Assert.assertFalse(manager.isCreatePermissionEnabled());
        Assert.assertTrue(manager.isReadPermissionEnabled());

        Assert.assertTrue(manager.getPermissionsNames().size() == 4);

        String name = "custom_1";
        Assert.assertTrue(manager.addCustomPermission(name));
        Assert.assertTrue(manager.getPermissionsNames().size() == 5);
        Assert.assertTrue(manager.isPermissionDisabled(name));
        Assert.assertFalse(manager.isPermissionEnabled(name));
        Assert.assertTrue(manager.enablePermission(name));
        Assert.assertFalse(manager.isPermissionDisabled(name));
        Assert.assertTrue(manager.isPermissionEnabled(name));
        Assert.assertTrue(manager.disablePermission(name));
        Assert.assertTrue(manager.isPermissionDisabled(name));
        Assert.assertFalse(manager.isPermissionEnabled(name));

        Assert.assertFalse(manager.addExistingChildPermission("1", name));
        Assert.assertFalse(manager.addExistingChildPermission("1", "5"));

        String name2 = "custom_2";
        Assert.assertTrue(manager.addAndEnableCustomPermission(name2));
        Assert.assertTrue(manager.addExistingChildPermission(name2, name));
        Assert.assertTrue(manager.getPermissionChildren(name).size() ==1);
        Assert.assertTrue(manager.getPermissionsNames().size() == 6);

        Assert.assertTrue(manager.removePermission(name2));
        Assert.assertTrue(manager.getPermissionsNames().size() == 5);

        Assert.assertTrue(manager.removePermission(name));
        Assert.assertTrue(manager.getPermissionsNames().size() == 4);

        Assert.assertTrue(manager.setPermissionOnEnabled("admin", e -> {}));
        Assert.assertTrue(manager.setPermissionOnDisabled("admin", e -> {}));
        Assert.assertFalse(manager.setPermissionOnEnabled("1", e -> {}));
        Assert.assertFalse(manager.setPermissionOnDisabled("1", e -> {}));
        manager.setOnPermissionsApplied(e -> {});
        manager.setOnAllPermissionsEnabled(e -> {});
        manager.setOnAllPermissionsDisabled(e -> {});

        Assert.assertFalse(manager.isPermissionEnabled("1"));
        Assert.assertTrue(manager.isPermissionDisabled("1"));
    }

    /** Tests the {@link SessionManager} class and runs {@link Tests#TestPermissionManager}. */
    @Test
    public void TestSessionManager() {
        TestPermissionManager();

        try(Database db = new Database("./userdb.db", DatabaseType.H2)) {
            UserManager userManager = new UserManager(db);
            userManager.createUser("admin", "1234", UserRoleManager.SystemUserRoles.EDITOR);

            try {
                new SessionManager(null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            SessionManager manager = userManager.getSessionManager();

            try {
                manager.clone();
                Assert.fail( "This method should have thrown CloneNotSupportedException!" );
            } catch (CloneNotSupportedException ignore) { }

            manager.setOnSessionOpened(e -> {});
            manager.setOnSessionClosed(e -> {});
            manager.setOnLoginSuccess(e -> {});
            manager.setOnLoginFailure(e -> {});

            Assert.assertFalse(manager.isLoggedIn());
            manager.loginUser("admin");
            Assert.assertTrue(manager.isLoggedIn());
            Assert.assertTrue(manager.getLoggedInUsername().equals("admin"));
            Assert.assertTrue(manager.getLoggedInUserRole().equals(UserRoleManager.SystemUserRoles.EDITOR.getRole()));
            manager.logoutUser();

            try {
                manager.loginUser(null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                manager.loginUser("");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }
        } catch (Exception ignore) { }
    }

    /** Tests the {@link UserAccount} object. */
    @Test
    public void TestUserAccount() {
        try(Database db = new Database("./userdb.db", DatabaseType.H2)) {
            UserManager userManager = new UserManager(db);
            userManager.createUser("jlgager", "1234", UserRoleManager.SystemUserRoles.EDITOR);
            UserAccount account = new UserAccount(userManager, "jlgager");
            Assert.assertTrue(account.getUsername().equals("jlgager"));
            Assert.assertTrue(account.setPassword("1234"));
            Assert.assertTrue(account.checkPasswordMatches("1234"));
            Assert.assertTrue(account.setUserRole(UserRoleManager.SystemUserRoles.ADMIN));
            Assert.assertTrue(account.getUserRole().equals(UserRoleManager.SystemUserRoles.ADMIN.getRole()));

            try {
                new UserAccount(null, null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }
            try {
                new UserAccount(userManager, null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }
            try {
                new UserAccount(userManager, "");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                account.setPassword(null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }
            try {
                account.checkPasswordMatches(null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }
            try {
                account.setUserRole("");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }
        } catch (Exception ignore) { }
    }

    /** Tests the {@link UserManager} class. */
    @Test
    public void TestUserManager() {
        try(Database db = new Database("./userdb.db", DatabaseType.H2)) {
            UserManager userManager = new UserManager(db);
            userManager.createUser("jlgager", "1234", UserRoleManager.SystemUserRoles.EDITOR);
            UserAccount account = userManager.getUser("jlgager");
            Assert.assertTrue(userManager.userExists("jlgager"));
            Assert.assertTrue(userManager.getUsernameList().contains("jlgager"));
            Assert.assertTrue(CollectionUtils.doesItemExistInHashSet(userManager.getUsersList(),
                    u -> u.getUsername().equals("jlgager")));
            Assert.assertTrue(userManager.setPassword("jlgager", "1234"));
            Assert.assertTrue(userManager.checkPasswordMatches("jlgager", "1234"));
            Assert.assertTrue(userManager.setUserRole("jlgager", UserRoleManager.SystemUserRoles.ADMIN));
            Assert.assertTrue(userManager.getUserRole("jlgager")
                    .equals(UserRoleManager.SystemUserRoles.ADMIN.getRole()));
            Assert.assertTrue(userManager.deleteUser("jlgager"));

            try {
                new UserManager(null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.createUser(null, null, "");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.createUser("", null, "");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.createUser("1", null, "");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.createUser("1", "", "");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.getUser(null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.getUser("");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.deleteUser(null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.deleteUser("");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.userExists(null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.userExists("");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.getUserRole(null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.getUserRole("");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.getUserRole("1");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.setUserRole(null, "");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.setUserRole("", "");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.setUserRole("1", "");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.setPassword(null, null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.setPassword("", null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.setPassword("1", null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.checkPasswordMatches(null, null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.checkPasswordMatches("", null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.checkPasswordMatches("1", null);
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }

            try {
                userManager.checkPasswordMatches("1", "");
                Assert.fail( "This method should have thrown IllegalArgumentException!" );
            } catch (IllegalArgumentException ignore) { }
        } catch (SQLException ignored) { }
    }

    /** Tests the {@link UserRole} object. */
    @Test
    public void TestUserRole() {
        try {
            new UserRole(null);
            Assert.fail( "This method should have thrown IllegalArgumentException!" );
        } catch (IllegalArgumentException ignore) { }
        try {
            new UserRole("");
            Assert.fail( "This method should have thrown IllegalArgumentException!" );
        } catch (IllegalArgumentException ignore) { }

        UserRole role = new UserRole("custom_1");
        Assert.assertTrue(role.getName().equals("custom_1"));
        Assert.assertTrue(role.isEnabled());
        role.disable();
        Assert.assertFalse(role.isEnabled());
        Assert.assertTrue(role.addPermission("admin"));
        Assert.assertTrue(role.removePermission("admin"));
        Assert.assertFalse(role.addPermission("1"));

        try {
            role.addPermission(null);
            Assert.fail( "This method should have thrown IllegalArgumentException!" );
        } catch (IllegalArgumentException ignore) { }
        try {
            role.addPermission("");
            Assert.fail( "This method should have thrown IllegalArgumentException!" );
        } catch (IllegalArgumentException ignore) { }

        try {
            UserRole none = new UserRole("none");
            none.addPermission("1");
            Assert.fail( "This method should have thrown IllegalArgumentException!" );
        } catch (IllegalArgumentException ignore) { }

        try {
            UserRole none = new UserRole("none");
            none.disable();
            Assert.fail( "This method should have thrown IllegalStateException!" );
        } catch (IllegalStateException ignore) { }

        try {
            role.removePermission(null);
            Assert.fail( "This method should have thrown IllegalArgumentException!" );
        } catch (IllegalArgumentException ignore) { }
        try {
            role.removePermission("");
            Assert.fail( "This method should have thrown IllegalArgumentException!" );
        } catch (IllegalArgumentException ignore) { }
    }
}
