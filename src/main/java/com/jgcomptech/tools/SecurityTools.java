package com.jgcomptech.tools;

import javax.crypto.Cipher;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/** Contains methods dealing with encryption and hashing */
public class SecurityTools {

    /**
     * A list of the Hash Types to be used for hashing string values in the {@link SecurityTools} class
     */
    public enum HashType {
        MD5,
        SHA1,
        SHA256,
        SHA384,
        SHA512
    }

    /** Contains methods dealing with hashing files */
    public static class FileHashes {
        /**
         * Read the file and calculate the checksum
         *
         * @param type     the hash type to use
         * @param filename the file to read
         * @return the hex representation of the hash using uppercase chars
         * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for reading
         * @throws IOException           if an I/O error occurs
         */
        public static String getFileHash(HashType type, String filename) throws IOException {

            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance(type.name());
            } catch(NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try(InputStream input = new FileInputStream(filename)) {

                final byte[] buffer = new byte[8192];
                int len = input.read(buffer);

                while(len != -1) {
                    assert md != null;
                    md.update(buffer, 0, len);
                    len = input.read(buffer);
                }

                assert md != null;
                return new HexBinaryAdapter().marshal(md.digest());
            }
        }

        /**
         * Save generated hash to the specified file
         *
         * @param fileName Filename to be saved to
         * @param hash     Hash to be saved
         * @throws FileNotFoundException if the file is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for writing
         * @throws IOException           if an I/O error occurs
         */
        public static void saveToFile(String hash, String fileName) throws IOException {
            final byte[] encoded = hash.getBytes(StandardCharsets.UTF_8);

            try(final FileOutputStream out = new FileOutputStream(fileName)) {
                out.write(encoded);
            }
        }

        /**
         * Save generated Key to the specified file
         *
         * @param fileName Filename to be saved to
         * @param key Key to be saved
         * @throws FileNotFoundException if the file is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for writing
         * @throws IOException           if an I/O error occurs
         */
        public static void saveToFile(Key key, String fileName) throws IOException {
            final byte[] encoded = key.getEncoded();

            try(FileOutputStream out = new FileOutputStream(fileName)) {
                out.write(encoded);
            }
        }

        /**
         * Read saved hash file to byte array
         *
         * @param fileName Filename to be read from
         * @return File contents as string
         * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for reading
         * @throws IOException           if an I/O error occurs
         */
        public static byte[] readFromFile(String fileName) throws IOException {

            try(FileInputStream in = new FileInputStream(fileName)) {
                final byte[] bytes = new byte[in.available()];
                in.read(bytes);
                return bytes;
            }
        }

        // This class should only be called statically
        private FileHashes() { super(); }
    }

    /** Contains methods dealing with hashing passwords */
    public static class PasswordHashes {
        /**
         * Creates a Secure Random salt to use for hashing
         *
         * @param size Size as int to use as length of salt
         * @return Salt as string
         * @throws GeneralSecurityException if error occurs
         */
        public static String createSaltString(int size) throws GeneralSecurityException {
            final SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            final byte[] salt = new byte[size];
            sr.nextBytes(salt);
                /*for(int i = 0; i < 16; i++) {
                    System.out.print(salt[i] & 255);
                    System.out.print(" ");
                }*/

            // Return a Base64 string representation of the random number.
            return Base64.getEncoder().encodeToString(salt);
        }

        /**
         * Creates a Secure Random number
         *
         * @return Secure random number as a SecureRandom object
         * @throws GeneralSecurityException if error occurs
         * */
        public static SecureRandom createSecureRandom() throws GeneralSecurityException {
            return SecureRandom.getInstance("SHA1PRNG", "SUN");
        }

        /**
         * Creates a Secure Random salt to use for hashing
         *
         * @param size Size as int to use as length of salt
         * @return Salt as byte array
         * @throws GeneralSecurityException if error occurs
         * */
        public static byte[] createSaltByte(int size) throws GeneralSecurityException {
            final byte[] salt = new byte[size];
            createSecureRandom().nextBytes(salt);

            // Return a Base64 string representation of the random number.
            return salt;
        }

        /**
         * Creates a SHA512 Hash
         *
         * @param passwordToHash Password to hash
         * @param salt Salt as byte array to use for hashing
         * @return Hashed password as string
         * @throws GeneralSecurityException if error occurs
         * */
        public static String createHash(String passwordToHash, byte[] salt) throws GeneralSecurityException {
            final MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            final byte[] bytes = md.digest(passwordToHash.getBytes());
            final StringBuilder sb = new StringBuilder();

            for(final byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 255) + 256, 16).substring(1));
            }
            return sb.toString();
        }

        /**
         * Creates a SHA512 Hash
         *
         * @param passwordToHash Password to hash
         * @param salt Salt as string to use for hashing
         * @return Hashed password as string
         * @throws GeneralSecurityException if error occurs
         * */
        public static String createHash(String passwordToHash, String salt) throws GeneralSecurityException {
            final MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(Base64.getDecoder().decode(salt));
            final byte[] bytes = md.digest(passwordToHash.getBytes());
            final StringBuilder sb = new StringBuilder();

            for(final byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 255) + 256, 16).substring(1));
            }
            return sb.toString();
        }

        /**
         * Checks if login hashes match
         *
         * @param enteredPassword  Password to validate.
         * @param databasePassword Password from database to check against.
         * @param databaseSalt     Password salt from database.
         * @return True if hashes match.
         * @throws GeneralSecurityException if error occurs
         */
        public static boolean checkHashesMatch(String enteredPassword, String databasePassword, String databaseSalt)
                throws GeneralSecurityException {
            return (databasePassword.equals(createHash(enteredPassword, databaseSalt)));
        }

        // This class should only be called statically
        private PasswordHashes() { super(); }
    }

    /** Contains methods dealing with RSA key files */
    public static class RSAFiles {
        /**
         * Saves specified key pair to filename
         *
         * @param pair Key pair to save
         * @param filename Filename to save to
         * @throws FileNotFoundException if the file is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for writing
         * @throws IOException           if an I/O error occurs
         * */
        public static void saveKeyPairToFile(KeyPair pair, String filename) throws IOException {
            FileHashes.saveToFile(pair.getPrivate(), filename + "");
            FileHashes.saveToFile(pair.getPublic(), filename + ".pub");
        }

        /**
         * Reads a public key from a filename
         *
         * @param filename Filename to save to
         * @return Public key as PublicKey object
         * @throws GeneralSecurityException if error occurs
         * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for reading
         * @throws IOException           if an I/O error occurs
         */
        public static PublicKey readPublicKeyFromFile(String filename) throws GeneralSecurityException, IOException {
            return RSAHashes.readPublicKeyFromBytes(FileHashes.readFromFile(filename));
        }

        /**
         * Reads a private key from a filename
         *
         * @param filename Filename to save to
         * @return Private key as PrivateKey object
         * @throws GeneralSecurityException if error occurs
         * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for reading
         * @throws IOException           if an I/O error occurs
         */
        public static PrivateKey readPrivateKeyFromFile(String filename) throws GeneralSecurityException, IOException {
            return RSAHashes.readPrivateKeyFromBytes(FileHashes.readFromFile(filename));
        }

        // This class should only be called statically
        private RSAFiles() { super(); }
    }

    /** Contains methods dealing with RSA encryption and decryption */
    public static class RSAHashes {
        /**
         * Generates a key pair
         *
         * @return Key pair as a KeyPair object
         * @throws FileNotFoundException if the file is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for writing
         * @throws IOException           if an I/O error occurs
         * @throws GeneralSecurityException if error occurs
         */
        public static KeyPair generateKeyPair() throws IOException, GeneralSecurityException { return generateKeyPair(false, ""); }

        /**
         * Generates a key pair and saves them to files matching the specified filename
         *
         * @param saveToFiles If true KeyPair will be saved to two separate files
         * @param fileName    File name to use to save files
         * @return Key pair as a KeyPair object
         * @throws FileNotFoundException if the file is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for writing
         * @throws IOException           if an I/O error occurs
         * @throws GeneralSecurityException if error occurs
         */
        public static KeyPair generateKeyPair(boolean saveToFiles, String fileName)
                throws IOException, GeneralSecurityException {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048, PasswordHashes.createSecureRandom());

            final KeyPair pair = keyGen.generateKeyPair();

            if(saveToFiles) {
                RSAFiles.saveKeyPairToFile(pair, fileName);
            }

            return pair;
        }

        /**
         * Converts a byte array to a PublicKey object
         *
         * @param bytes To read from
         * @return Converted public key as PublicKey object
         * @throws GeneralSecurityException if error occurs
         */
        public static PublicKey readPublicKeyFromBytes(byte[] bytes) throws GeneralSecurityException {
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }

        /**
         * Converts a byte array to a PrivateKey object
         *
         * @param bytes To read from
         * @return Converted public key as PrivateKey object
         * @throws GeneralSecurityException if error occurs
         */
        public static PrivateKey readPrivateKeyFromBytes(byte[] bytes) throws GeneralSecurityException {
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }

        /**
         * Encrypts specified text with public key
         *
         * @param key       Public key to encrypt with
         * @param plainText String to encrypt
         * @return Encrypted text as byte array
         * @throws GeneralSecurityException if error occurs
         */
        public static byte[] encrypt(PublicKey key, String plainText) throws GeneralSecurityException {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * Encrypts specified text with public key
         *
         * @param key       Public key to encrypt with
         * @param plainText String to encrypt
         * @return Encrypted text as string
         * @throws GeneralSecurityException if error occurs
         */
        public static String encryptToString(PublicKey key, String plainText) throws GeneralSecurityException {
            return Base64.getEncoder().encodeToString(encrypt(key, plainText));
        }

        /**
         * Decrypts specified text with private key
         *
         * @param key Private key to decrypt with
         * @param cipherText String to decrypt
         * @return Decrypted text as byte array
         * @throws GeneralSecurityException if error occurs
         * */
        public static byte[] decrypt(PrivateKey key, byte[] cipherText) throws GeneralSecurityException {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(cipherText);
        }

        /**
         * Decrypts specified text with private key
         *
         * @param key Private key to decrypt with
         * @param cipherText String to decrypt
         * @return Decrypted text as string
         * @throws GeneralSecurityException if error occurs
         * */
        public static byte[] decryptFromString(PrivateKey key, String cipherText) throws GeneralSecurityException {
            return decrypt(key, Base64.getDecoder().decode(cipherText));
        }

        // This class should only be called statically
        private RSAHashes() { super(); }
    }

    // This class should only be called statically
    private SecurityTools() { super(); }
}
