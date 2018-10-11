package com.jgcomptech.tools.tests;

import com.jgcomptech.tools.Misc;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import static com.jgcomptech.tools.SecurityTools.RSAHashes.*;
import static org.junit.Assert.assertEquals;

public class Tests {
    /** Tests the {@link Misc#ConvertBytes(double)} method. */
    @Test
    public void testConvertBytes() {
        var result = Misc.ConvertBytes(107374182400d);
        assertEquals(result, "100 GB");

        result = Misc.ConvertBytes(1099511627775d);
        assertEquals(result, "1023.99 GB");

        result = Misc.ConvertBytes(1099511627776d);
        assertEquals(result, "1 TB");
    }

    /**
     * Tests the {@link com.jgcomptech.tools.SecurityTools.RSAHashes#encryptToString(java.security.PublicKey, String)}
     * and {@link com.jgcomptech.tools.SecurityTools.RSAHashes#decryptFromString(java.security.PrivateKey, String)}
     * methods.
     */
    @Test
    public void testRSAEncryptDecrypt() {
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
