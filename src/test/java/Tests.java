import com.jgcomptech.tools.Misc;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
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
        KeyPair pair = generateKeyPair();
        String message = "Hello World";
        String secret = encryptToString(pair.getPublic(), message);
        byte[] recovered_message = decryptFromString(pair.getPrivate(), secret);
        Assert.assertEquals(new String(recovered_message, StandardCharsets.UTF_8), message);
    }
}
