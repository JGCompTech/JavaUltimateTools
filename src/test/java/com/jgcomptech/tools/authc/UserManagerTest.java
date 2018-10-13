package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.CollectionUtils;
import com.jgcomptech.tools.ExceptionUtils;
import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.DatabaseType;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class UserManagerTest {
    /** Tests the {@link UserManager} class. */
    @Test
    public void testUserManager() {
        try(final var db = new Database("./userdb.db", DatabaseType.H2)) {
            final var userManager = new UserManager(db);
            userManager.createUser("jlgager", "1234", UserRoleManager.SystemUserRoles.EDITOR);
            userManager.getUser("jlgager");
            assertTrue(userManager.userExists("jlgager"));
            assertTrue(userManager.getUsernameList().contains("jlgager"));
            assertTrue(CollectionUtils.doesItemExistInCollection(userManager.getUsersList(),
                    u -> u.getUsername().equals("jlgager")));
            assertTrue(userManager.setPassword("jlgager", "1234"));
            assertTrue(userManager.checkPasswordMatches("jlgager", "1234"));
            assertTrue(userManager.setUserRole("jlgager", UserRoleManager.SystemUserRoles.ADMIN));
            assertEquals(userManager.getUserRole("jlgager"), UserRoleManager.SystemUserRoles.ADMIN.getRole());
            //assertTrue(userManager.deleteUser("jlgager"));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> new UserManager(null));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.createUser(null, null, "");
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.createUser("", null, "");
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.createUser("1", null, "");
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.createUser("1", "", "");
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.createUser(null, null, UserRoleManager.SystemUserRoles.ADMIN);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.createUser("", null, UserRoleManager.SystemUserRoles.ADMIN);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.createUser("1", null, UserRoleManager.SystemUserRoles.ADMIN);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> userManager.getUser(null));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> userManager.getUser(""));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> userManager.deleteUser(null));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> userManager.deleteUser(""));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> userManager.userExists(null));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> userManager.userExists(""));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> userManager.getUserRole(null));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> userManager.getUserRole(""));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> userManager.getUserRole("1"));

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.setUserRole(null, "");
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.setUserRole("", "");
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.setUserRole("1", "");
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.setPassword(null, null);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.setPassword("", null);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.setPassword("1", null);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.checkPasswordMatches(null, null);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.checkPasswordMatches("", null);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.checkPasswordMatches("1", null);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.checkPasswordMatches("1", "");
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.getSessionManager().logoutUser(null, true);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.getSessionManager().logoutUser("", true);
            });

            ExceptionUtils.assertThrownIllegalArgumentException(() -> {
                userManager.getSessionManager().logoutUser("", false);
            });

            assertFalse(userManager.getSessionManager().logoutUser(null, false));
            assertTrue(userManager.getSessionManager().loginUser("jlgager"));
            assertTrue(userManager.getSessionManager().logoutUser(null, false));
        } catch (final SQLException ignored) { }
    }
}