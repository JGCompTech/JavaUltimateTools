package com.jgcomptech.tools;

import org.jetbrains.annotations.Nullable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/** Contains methods dealing with encryption and hashing */
public class SecurityTools {
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

                byte[] buffer = new byte[8192];
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
         */
        public static void saveToFile(String hash, String fileName) {
            byte[] encoded = hash.getBytes(StandardCharsets.UTF_8);
            FileOutputStream out;
            try {
                out = new FileOutputStream(fileName);
                out.write(encoded);
                out.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Save generated Key to the specified file
         *
         * @param fileName Filename to be saved to
         * @param key Key to be saved
         */
        public static void saveToFile(Key key, String fileName) {
            byte[] encoded = key.getEncoded();
            FileOutputStream out;
            try {
                out = new FileOutputStream(fileName);
                out.write(encoded);
                out.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Read saved hash file to byte array
         *
         * @param fileName Filename to be read from
         * @return File contents as string
         */
        @Nullable
        public static byte[] readFromFile(String fileName) {
            FileInputStream in;
            try {
                in = new FileInputStream(fileName);
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                in.close();
                return bytes;
            } catch(IOException e) {
                e.printStackTrace();
            }

            return null;
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
         */
        public static String createSaltString(int size) {
            try {
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                byte[] salt = new byte[size];
                sr.nextBytes(salt);
                /*for(int i = 0; i < 16; i++) {
                    System.out.print(salt[i] & 255);
                    System.out.print(" ");
                }*/

                // Return a Base64 string representation of the random number.
                return Base64.getEncoder().encodeToString(salt);
            } catch(NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        }

        /**
         * Creates a Secure Random number
         *
         * @return Secure random number as a SecureRandom object
         * */
        @Nullable
        public static SecureRandom createSecureRandom() {
            try {
                return SecureRandom.getInstance("SHA1PRNG", "SUN");
            } catch(NoSuchAlgorithmException | NoSuchProviderException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Creates a Secure Random salt to use for hashing
         *
         * @param size Size as int to use as length of salt
         * @return Salt as byte array
         * */
        public static byte[] createSaltByte(int size) {
            byte[] salt = new byte[size];
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
         * */
        public static String createHash(String passwordToHash, byte[] salt) {
            String generatedPassword = null;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(salt);
                byte[] bytes = md.digest(passwordToHash.getBytes());
                StringBuilder sb = new StringBuilder();

                for(byte aByte : bytes) {
                    sb.append(Integer.toString((aByte & 255) + 256, 16).substring(1));
                }
                generatedPassword = sb.toString();
            } catch(NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return generatedPassword;
        }

        /**
         * Creates a SHA512 Hash
         *
         * @param passwordToHash Password to hash
         * @param salt Salt as string to use for hashing
         * @return Hashed password as string
         * */
        public static String createHash(String passwordToHash, String salt) {
            String generatedPassword = null;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(Base64.getDecoder().decode(salt));
                byte[] bytes = md.digest(passwordToHash.getBytes());
                StringBuilder sb = new StringBuilder();

                for(byte aByte : bytes) {
                    sb.append(Integer.toString((aByte & 255) + 256, 16).substring(1));
                }
                generatedPassword = sb.toString();
            } catch(NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return generatedPassword;
        }

        /**
         * Checks if login hashes match
         *
         * @param enteredPassword  Password to validate.
         * @param databasePassword Password from database to check against.
         * @param databaseSalt     Password salt from database.
         * @return True if hashes match.
         */
        public static boolean checkHashesMatch(String enteredPassword, String databasePassword, String databaseSalt) {
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
         * */
        public static void saveKeyPairToFile(KeyPair pair, String filename) {
            FileHashes.saveToFile(pair.getPrivate(), filename + "");
            FileHashes.saveToFile(pair.getPublic(), filename + ".pub");
        }

        /**
         * Reads a public key from a filename
         *
         * @param filename Filename to save to
         * @return Public key as PublicKey object
         * @throws GeneralSecurityException on failure
         */
        public static PublicKey readPublicKeyFromFile(String filename) throws GeneralSecurityException {
            return RSAHashes.readPublicKeyFromBytes(FileHashes.readFromFile(filename));
        }

        /**
         * Reads a private key from a filename
         *
         * @param filename Filename to save to
         * @return Private key as PrivateKey object
         * @throws GeneralSecurityException on failure
         */
        public static PrivateKey readPrivateKeyFromFile(String filename) throws GeneralSecurityException {
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
         */
        public static KeyPair generateKeyPair() {
            return generateKeyPair(false, "");
        }

        /**
         * Generates a key pair and saves them to files matching the specified filename
         *
         * @param saveToFiles If true KeyPair will be saved to two separate files
         * @param filename    File name to use to save files
         * @return Key pair as a KeyPair object
         */
        public static KeyPair generateKeyPair(boolean saveToFiles, String filename) {
            KeyPairGenerator keyGen = null;
            try {
                keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(2048, PasswordHashes.createSecureRandom());
            } catch(NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            assert keyGen != null;
            KeyPair pair = keyGen.generateKeyPair();

            if(saveToFiles) {
                RSAFiles.saveKeyPairToFile(pair, filename);
            }

            return pair;
        }

        /**
         * Converts a byte array to a PublicKey object
         *
         * @param bytes To read from
         * @return Converted public key as PublicKey object
         * @throws GeneralSecurityException on failure
         */
        public static PublicKey readPublicKeyFromBytes(byte[] bytes) throws GeneralSecurityException {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }

        /**
         * Converts a byte array to a PrivateKey object
         *
         * @param bytes To read from
         * @return Converted public key as PrivateKey object
         * @throws GeneralSecurityException on failure
         */
        public static PrivateKey readPrivateKeyFromBytes(byte[] bytes) throws GeneralSecurityException {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }

        /**
         * Encrypts specified text with public key
         *
         * @param key       Public key to encrypt with
         * @param plaintext String to encrypt
         * @return Encrypted text as byte array
         */
        @Nullable
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

        /**
         * Encrypts specified text with public key
         *
         * @param key       Public key to encrypt with
         * @param plaintext String to encrypt
         * @return Encrypted text as string
         */
        public static String encryptToString(PublicKey key, String plaintext) {
            return Base64.getEncoder().encodeToString(encrypt(key, plaintext));
        }

        /**
         * Decrypts specified text with private key
         *
         * @param key Private key to decrypt with
         * @param ciphertext String to decrypt
         * @return Decrypted text as byte array
         * */
        @Nullable
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

        /**
         * Decrypts specified text with private key
         *
         * @param key Private key to decrypt with
         * @param ciphertext String to decrypt
         * @return Decrypted text as string
         * */
        public static byte[] decryptFromString(PrivateKey key, String ciphertext) {
            return decrypt(key, Base64.getDecoder().decode(ciphertext));
        }

        // This class should only be called statically
        private RSAHashes() { super(); }
    }

    // This class should only be called statically
    private SecurityTools() { super(); }
}
