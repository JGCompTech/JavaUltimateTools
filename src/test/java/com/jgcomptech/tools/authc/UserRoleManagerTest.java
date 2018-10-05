package com.jgcomptech.tools.authc;

import com.jgcomptech.tools.ExceptionUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserRoleManagerTest {

    @Test
    public void createUserRole() {
        final var manager = UserRoleManager.getInstance();
        ExceptionUtils.assertThrownIllegalArgumentException(() -> manager.createUserRole(null));

        ExceptionUtils.assertThrownIllegalArgumentException(() -> manager.createUserRole(""));

        assertEquals("admin", manager.createUserRole("admin").getName());
        assertEquals("newRole", manager.createUserRole("newRole").getName());
    }

    @Test
    public void addExistingUserRole() {
        ExceptionUtils.assertThrownIllegalArgumentException(() -> {
            UserRoleManager.getInstance().addExistingUserRole(null);
        });
    }

    @Test
    public void getUserRoles() {
        assertTrue(UserRoleManager.getInstance().getUserRoles().size() >= 5);
    }

    @Test
    public void getUserRole() {
        assertEquals("admin", UserRoleManager.getInstance().getUserRole("admin").getName());
    }
}