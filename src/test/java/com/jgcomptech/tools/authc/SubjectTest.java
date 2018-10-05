package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.ExceptionUtils;
import com.jgcomptech.tools.authz.PermissionManager;
import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.DatabaseType;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class SubjectTest {
    @Test
    public void SubjectTest() {
        try (final var db = new Database("./userdb.db", DatabaseType.H2)) {
            final var manager = AuthManager.getNewInstance(db, null, "Java Ultimate Tools");
            final var subject = manager.getSubject();

            manager.createUser("admin", "1234", UserRoleManager.SystemUserRoles.ADMIN);
            manager.createUser("editor", "1234", UserRoleManager.SystemUserRoles.EDITOR);

            final var permissionManager = PermissionManager.getInstance();
            permissionManager.addAndEnableCustomPermission("ban_user", "admin");

            assertTrue(manager.userExists("admin"));

            assertEquals(UserRoleManager.SystemUserRoles.ADMIN.getRole(), manager.getUserRole("admin"));

            //If this fails delete database because cleanup never happened
            assertTrue(manager.checkPasswordMatches("admin", "1234"));

            CredentialsException.assertThrown(() -> {
                final var badToken = new UsernamePasswordToken(null, "".toCharArray());
                subject.login(badToken);
            });


            CredentialsException.assertThrown(() -> {
                final var badToken = new UsernamePasswordToken("admin", "".toCharArray());
                badToken.setPassword(null);
                subject.login(badToken);
            });


            CredentialsException.assertThrown(() -> {
                final var badToken = new UsernamePasswordToken(null, "".toCharArray());
                badToken.setPassword(null);
                subject.login(badToken);
            });


            var token = new UsernamePasswordToken("admin", "1234".toCharArray());

            token.setRememberMe(true);
            assertTrue(subject.login(token));

            ConcurrentAccessException.assertThrown(subject::login);

            assertTrue(subject.setPassword("pass"));
            token.setRememberMe(false);
            assertTrue(subject.logout());

            CredentialsException.assertThrown(subject::login);

            token.setUsername("admin");
            token.setPassword("1234".toCharArray());
            assertFalse(subject.login(token));

            token.setUsername("admin1234");
            token.setPassword("1234".toCharArray());
            assertFalse(subject.login(token));

            token.setUsername("admin");
            token.setPassword("pass".toCharArray());
            token.setRememberMe(true);
            assertTrue(token.isRememberMe());
            assertTrue(subject.login(token));
            assertTrue(subject.isAuthenticated());
            assertEquals("admin", subject.getUsername());
            assertEquals(UserRoleManager.SystemUserRoles.ADMIN.getRole(), subject.getUserRole());

            ExceptionUtils.assertThrownIllegalArgumentException(() -> subject.setPassword(null));

            assertTrue(subject.isRemembered());

            assertTrue(subject.hasPermission("admin"));

            assertTrue(subject.hasPermission("admin:ban_user"));

            assertTrue(subject.hasPermissions(new HashSet<>(Arrays.asList("admin", "admin:ban_user"))));

            assertTrue(subject.setPassword("1234"));

            assertTrue(subject.logout());

            assertFalse(subject.logout());

            token.clear();
            token = null;
            permissionManager.removePermission("admin:ban_user");

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }
}