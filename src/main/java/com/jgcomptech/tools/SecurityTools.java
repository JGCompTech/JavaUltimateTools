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

public class SecurityTools {
    public enum HashType {
        MD5,
        SHA1,
        SHA256,
        SHA384,
        SHA512
    }

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
    }

    public static class PasswordHashes {
        public static String createSaltString(int size) throws NoSuchAlgorithmException {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[size];
            sr.nextBytes(salt);
            for(int i = 0; i < 16; i++) {
                System.out.print(salt[i] & 255);
                System.out.print(" ");
            }

            // Return a Base64 string representation of the random number.
            return Base64.getEncoder().encodeToString(salt);
        }

        @Nullable
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

        /** Creates a SHA512 Hash */
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

        /** Creates a SHA512 Hash */
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
    }

    public static class RSAFiles {
        public static void saveKeyPairToFile(KeyPair pair, String filename) {
            FileHashes.saveToFile(pair.getPrivate(), filename + "");
            FileHashes.saveToFile(pair.getPublic(), filename + ".pub");
        }

        public static PublicKey readPublicKeyFromFile(String fileName) throws GeneralSecurityException {
            return RSAHashes.readPublicKeyFromBytes(FileHashes.readFromFile(fileName));
        }

        public static PrivateKey readPrivateKeyFromFile(String fileName) throws GeneralSecurityException {
            return RSAHashes.readPrivateKeyFromBytes(FileHashes.readFromFile(fileName));
        }
    }

    public static class RSAHashes {
        public static KeyPair generateKeyPair() {
            return generateKeyPair(false, "");
        }

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

            RSAFiles.saveKeyPairToFile(pair, filename);

            return pair;
        }

        public static PublicKey readPublicKeyFromBytes(byte[] bytes) throws GeneralSecurityException {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }

        public static PrivateKey readPrivateKeyFromBytes(byte[] bytes) throws GeneralSecurityException {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }

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

        public static String encryptToString(PublicKey key, String plaintext) {
            return Base64.getEncoder().encodeToString(encrypt(key, plaintext));
        }

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
}
