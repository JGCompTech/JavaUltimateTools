package com.jgcomptech.tools.securitytools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHashes {
    public static String createSaltString(int size) throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[size];
        sr.nextBytes(salt);
        for(int i = 0; i<16; i++) {
            System.out.print(salt[i] & 0x00FF);
            System.out.print(" ");
        }

        // Return a Base64 string representation of the random number.
        return Base64.getEncoder().encodeToString(salt);
    }

    public static SecureRandom createSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch(NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] createSaltByte(int size) {
        byte[] salt = new byte[size];
        createSecureRandom().nextBytes(salt);

        // Return a Base64 string representation of the random number.
        return salt;
    }

    /**
     * Creates a SHA512 Hash
     */
    public static String createHash(String passwordToHash, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();

            for(byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    /**
     * Creates a SHA512 Hash
     */
    public static String createHash(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(Base64.getDecoder().decode(salt));
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();

            for(byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    /**
     * Checks if login hashes match
     *
     * @param enteredPassword Password to validate.
     * @param databasePassword Password from database to check against.
     * @param databaseSalt Password salt from database.
     * @return True if hashes match.
     */
    public static boolean checkHashesMatch(String enteredPassword, String databasePassword, String databaseSalt) {
        return (databasePassword.equals(createHash(enteredPassword, databaseSalt)));
    }
}
