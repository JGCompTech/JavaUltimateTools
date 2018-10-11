package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.ExceptionUtils;
import com.jgcomptech.tools.databasetools.jdbc.Database;
import com.jgcomptech.tools.databasetools.jdbc.DatabaseType;
import org.junit.Test;

import static org.junit.Assert.*;

public class SessionManagerTest {
    /** Tests the {@link SessionManager} class. */
    @Test
    public void testSessionManager() {
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
}