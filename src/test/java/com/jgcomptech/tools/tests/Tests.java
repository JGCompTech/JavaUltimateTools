package com.jgcomptech.tools.tests;

import com.jgcomptech.tools.Misc;
import com.jgcomptech.tools.dialogs.MessageBox;
import com.jgcomptech.tools.dialogs.MessageBoxIcon;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

import static com.jgcomptech.tools.SecurityTools.RSAHashes.*;

public class Tests {
    /**
     * Tests ConvertBytes method
     */
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
     * Tests encryption and decryption methods
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
}
