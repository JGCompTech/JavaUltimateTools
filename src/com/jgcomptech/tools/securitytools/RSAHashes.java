package com.jgcomptech.tools.securitytools;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.jgcomptech.tools.securitytools.PasswordHashes.createSecureRandom;

public class RSAHashes {
    public static KeyPair generateKeyPair() {
        return generateKeyPair(false, "");
    }

    public static KeyPair generateKeyPair(boolean saveToFiles, String filename) {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048, createSecureRandom());
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        assert keyGen != null;
        KeyPair pair = keyGen.generateKeyPair();

        RSAFiles.saveKeyPairToFile(pair, filename);

        return pair;
    }

    public static PublicKey readPublicKeyFromBytes(byte[] bytes)
            throws GeneralSecurityException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey readPrivateKeyFromBytes(byte[] bytes)
            throws GeneralSecurityException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static byte[] encrypt(PublicKey key, String plaintext) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        } catch(NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String encryptToString(PublicKey key, String plaintext) {
        return Base64.getEncoder().encodeToString(encrypt(key, plaintext));
    }

    public static byte[] decrypt(PrivateKey key, byte[] ciphertext) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(ciphertext);
        } catch(NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decryptFromString(PrivateKey key, String ciphertext) {
        return decrypt(key, Base64.getDecoder().decode(ciphertext));
    }

    public static String testRSA() {
        KeyPair pair;
        pair = generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();
        String message = "Hello World";
        String secret = encryptToString(publicKey, message);
        System.out.println(secret);
        byte[] recovered_message = decryptFromString(privateKey, secret);
        return new String(recovered_message, StandardCharsets.UTF_8);
    }
}
