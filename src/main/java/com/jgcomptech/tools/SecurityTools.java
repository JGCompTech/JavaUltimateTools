package com.jgcomptech.tools;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;

/** Contains methods dealing with encryption and hashing. */
public final class SecurityTools {

    /** A list of the Hash Types to be used for hashing string values in the {@link SecurityTools} class. */
    public enum HashType {
        /** MD2. */
        MD2,
        /** MD5. */
        MD5,
        /** SHA-1. */
        SHA1,
        /** SHA-256. */
        SHA256,
        /** SHA-384. */
        SHA384,
        /** SHA-512. */
        SHA512
    }

    /** Contains methods dealing with hashing files. */
    public static final class FileHashes {
        /**
         * Read the file and calculate the checksum.
         * @param type     the hash type to use
         * @param filename the file to read
         * @return the hex representation of the hash using uppercase chars
         * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for reading
         * @throws IOException           if an I/O error occurs
         */
        public static String getFileHash(final HashType type, final String filename)
                throws IOException {
            return getFileHash(type, filename, true);
        }

        /**
         * Read the file and calculate the checksum.
         * @param type     the hash type to use
         * @param filename the file to read
         * @param toUpperCase if true returned hash will be all uppercase
         * @return the hex representation of the hash
         * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for reading
         * @throws IOException           if an I/O error occurs
         */
        public static String getFileHash(final HashType type, final String filename, final boolean toUpperCase)
                throws IOException {
            try(final InputStream stream = new FileInputStream(filename)) {
                var hash = "";
                switch (type) {
                    case MD2:
                        hash = DigestUtils.md2Hex(stream);
                        break;
                    case MD5:
                        hash = DigestUtils.md5Hex(stream);
                        break;
                    case SHA1:
                        hash = DigestUtils.sha1Hex(stream);
                        break;
                    case SHA256:
                        hash = DigestUtils.sha256Hex(stream);
                        break;
                    case SHA384:
                        hash = DigestUtils.sha384Hex(stream);
                        break;
                    case SHA512:
                        hash = DigestUtils.sha512Hex(stream);
                        break;
                }

                return toUpperCase ? hash.toUpperCase(Locale.ENGLISH) : hash;
            }
        }

        /**
         * Save generated hash to the specified file.
         * @param fileName Filename to be saved to
         * @param hash     Hash to be saved
         * @throws FileNotFoundException if the file is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for writing
         * @throws IOException           if an I/O error occurs
         */
        public static void saveToFile(final String hash, final String fileName) throws IOException {
            final var encoded = hash.getBytes(UTF_8);

            try(final var out = new FileOutputStream(fileName)) {
                out.write(encoded);
            }
        }

        /**
         * Save generated Key to the specified file.
         * @param fileName Filename to be saved to
         * @param key Key to be saved
         * @throws FileNotFoundException if the file is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for writing
         * @throws IOException           if an I/O error occurs
         */
        public static void saveToFile(final Key key, final String fileName) throws IOException {
            final var encoded = key.getEncoded();

            try(final var out = new FileOutputStream(fileName)) {
                out.write(encoded);
            }
        }

        /**
         * Read saved hash file to byte array.
         * @param fileName Filename to be read from
         * @return File contents as string
         * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for reading
         * @throws IOException           if an I/O error occurs
         */
        public static byte[] readFromFile(final String fileName) throws IOException {

            try(final var in = new FileInputStream(fileName)) {
                final var bytes = new byte[in.available()];
                in.read(bytes);
                return bytes;
            }
        }

        /** Prevents instantiation of this utility class. */
        private FileHashes() { }
    }

    /** Contains methods dealing with hashing passwords. */
    public static final class PasswordHashes {
        /**
         * Creates a BCrypt salt to use for hashing.
         * @param size Size as int to use as length of salt
         * @return Salt as string
         */
        public static String createBCryptSaltString(final int size) {
            return BCrypt.gensalt(size);
        }

        /**
         * Creates a Secure Random salt to use for hashing.
         * @param size Size as int to use as length of salt
         * @return Salt as string
         * @throws GeneralSecurityException if error occurs
         */
        public static String createSaltString(final int size) throws GeneralSecurityException {
            final var sr = SecureRandom.getInstance("SHA1PRNG");
            final var salt = new byte[size];
            sr.nextBytes(salt);
            // Return a Base64 string representation of the random number.
            return Base64.encodeBase64String(salt);
        }

        /**
         * Creates a Secure Random number.
         * @return Secure random number as a SecureRandom object
         * @throws GeneralSecurityException if error occurs
         */
        public static SecureRandom createSecureRandom() throws GeneralSecurityException {
            return SecureRandom.getInstance("SHA1PRNG", "SUN");
        }

        /**
         * Creates a Secure Random salt to use for hashing.
         * @param size Size as int to use as length of salt
         * @return Salt as byte array
         * @throws GeneralSecurityException if error occurs
         */
        public static byte[] createSaltByte(final int size) throws GeneralSecurityException {
            final var salt = new byte[size];
            createSecureRandom().nextBytes(salt);

            // Return a Base64 string representation of the random number.
            return salt;
        }

        /**
         * Creates a BCrypt Hash.
         * @param passwordToHash Password to hash
         * @param salt Salt as string to use for hashing
         * @return Hashed password as string
         */
        public static String createBCryptHash(final String passwordToHash, final String salt) {
            return BCrypt.hashpw(passwordToHash, salt);
        }

        /**
         * Creates a SHA512 Hash.
         * @param passwordToHash Password to hash
         * @param salt Salt as byte array to use for hashing
         * @return Hashed password as string
         * @throws GeneralSecurityException if error occurs
         */
        public static String createHash(final String passwordToHash, final byte[] salt)
                throws GeneralSecurityException {
            final var md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            final var bytes = md.digest(passwordToHash.getBytes(UTF_8));
            final var sb = new StringBuilder();

            for(final var aByte : bytes) {
                sb.append(Integer.toString((aByte & 255) + 256, 16).substring(1));
            }
            return sb.toString();
        }

        /**
         * Creates a SHA512 Hash.
         * @param passwordToHash Password to hash
         * @param salt Salt as string to use for hashing
         * @return Hashed password as string
         * @throws GeneralSecurityException if error occurs
         */
        public static String createHash(final String passwordToHash, final String salt)
                throws GeneralSecurityException {
            final var md = MessageDigest.getInstance("SHA-512");
            md.update(Base64.decodeBase64(salt));
            final var bytes = md.digest(passwordToHash.getBytes(UTF_8));
            final var sb = new StringBuilder();

            for(final var aByte : bytes) {
                sb.append(Integer.toString((aByte & 255) + 256, 16).substring(1));
            }
            return sb.toString();
        }

        /**
         * Checks if login hashes match.
         * @param enteredPassword  Password to validate.
         * @param databasePassword Password from database to check against.
         * @param databaseSalt     Password salt from database.
         * @return True if hashes match.
         */
        public static boolean checkBCryptHashesMatch(final String enteredPassword,
                                               final String databasePassword,
                                               final String databaseSalt) {
            return (databasePassword.equals(createBCryptHash(enteredPassword, databaseSalt)));
        }

        /**
         * Checks if login hashes match.
         * @param enteredPassword  Password to validate.
         * @param databasePassword Password from database to check against.
         * @param databaseSalt     Password salt from database.
         * @return True if hashes match.
         * @throws GeneralSecurityException if error occurs
         */
        public static boolean checkHashesMatch(final String enteredPassword,
                                               final String databasePassword,
                                               final String databaseSalt)
                throws GeneralSecurityException {
            return (databasePassword.equals(createHash(enteredPassword, databaseSalt)));
        }

        /** Prevents instantiation of this utility class. */
        private PasswordHashes() { }
    }

    /** Contains methods dealing with RSA key files. */
    public static final class RSAFiles {
        /**
         * Saves specified key pair to filename.
         * @param pair Key pair to save
         * @param filename Filename to save to
         * @throws FileNotFoundException if the file is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for writing
         * @throws IOException           if an I/O error occurs
         */
        public static void saveKeyPairToFile(final KeyPair pair, final String filename) throws IOException {
            FileHashes.saveToFile(pair.getPrivate(), filename);
            FileHashes.saveToFile(pair.getPublic(), filename + ".pub");
        }

        /**
         * Reads a public key from a filename.
         * @param filename Filename to save to
         * @return Public key as PublicKey object
         * @throws GeneralSecurityException if error occurs
         * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for reading
         * @throws IOException           if an I/O error occurs
         */
        public static PublicKey readPublicKeyFromFile(final String filename)
                throws GeneralSecurityException, IOException {
            return RSAHashes.readPublicKeyFromBytes(FileHashes.readFromFile(filename));
        }

        /**
         * Reads a private key from a filename.
         * @param filename Filename to save to
         * @return Private key as PrivateKey object
         * @throws GeneralSecurityException if error occurs
         * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for reading
         * @throws IOException           if an I/O error occurs
         */
        public static PrivateKey readPrivateKeyFromFile(final String filename)
                throws GeneralSecurityException, IOException {
            return RSAHashes.readPrivateKeyFromBytes(FileHashes.readFromFile(filename));
        }

        /** Prevents instantiation of this utility class. */
        private RSAFiles() { }
    }

    /** Contains methods dealing with RSA encryption and decryption. */
    public static final class RSAHashes {
        /**
         * Generates a key pair.
         * @return Key pair as a KeyPair object
         * @throws FileNotFoundException if the file is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for writing
         * @throws IOException           if an I/O error occurs
         * @throws GeneralSecurityException if error occurs
         */
        public static KeyPair generateKeyPair() throws IOException, GeneralSecurityException {
            return generateKeyPair(false, ""); }

        /**
         * Generates a key pair and saves them to files matching the specified filename.
         * @param saveToFiles If true KeyPair will be saved to two separate files
         * @param fileName    File name to use to save files
         * @return Key pair as a KeyPair object
         * @throws FileNotFoundException if the file is a directory rather than a regular file, or for
         *                               some other reason cannot be opened for writing
         * @throws IOException           if an I/O error occurs
         * @throws GeneralSecurityException if error occurs
         */
        public static KeyPair generateKeyPair(final boolean saveToFiles, final String fileName)
                throws IOException, GeneralSecurityException {
            final var keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048, PasswordHashes.createSecureRandom());

            final var pair = keyGen.generateKeyPair();

            if(saveToFiles) {
                RSAFiles.saveKeyPairToFile(pair, fileName);
            }

            return pair;
        }

        /**
         * Converts a byte array to a PublicKey object.
         * @param bytes To read from
         * @return Converted public key as PublicKey object
         * @throws GeneralSecurityException if error occurs
         */
        public static PublicKey readPublicKeyFromBytes(final byte[] bytes) throws GeneralSecurityException {
            final var keySpec = new X509EncodedKeySpec(bytes);
            final var keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }

        /**
         * Converts a byte array to a PrivateKey object.
         * @param bytes To read from
         * @return Converted public key as PrivateKey object
         * @throws GeneralSecurityException if error occurs
         */
        public static PrivateKey readPrivateKeyFromBytes(final byte[] bytes) throws GeneralSecurityException {
            final var keySpec = new PKCS8EncodedKeySpec(bytes);
            final var keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }

        /**
         * Encrypts specified text with public key.
         * @param key       Public key to encrypt with
         * @param plainText String to encrypt
         * @return Encrypted text as byte array
         * @throws GeneralSecurityException if error occurs
         */
        public static byte[] encrypt(final PublicKey key, final String plainText) throws GeneralSecurityException {
            final var cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText.getBytes(UTF_8));
        }

        /**
         * Encrypts specified text with public key.
         * @param key       Public key to encrypt with
         * @param plainText String to encrypt
         * @return Encrypted text as string
         * @throws GeneralSecurityException if error occurs
         */
        public static String encryptToString(final PublicKey key, final String plainText)
                throws GeneralSecurityException {
            return Base64.encodeBase64String(encrypt(key, plainText));
        }

        /**
         * Decrypts specified text with private key.
         * @param key Private key to decrypt with
         * @param cipherText String to decrypt
         * @return Decrypted text as byte array
         * @throws GeneralSecurityException if error occurs
         */
        public static byte[] decrypt(final PrivateKey key, final byte[] cipherText) throws GeneralSecurityException {
            final var cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(cipherText);
        }

        /**
         * Decrypts specified text with private key.
         * @param key Private key to decrypt with
         * @param cipherText String to decrypt
         * @return Decrypted text as string
         * @throws GeneralSecurityException if error occurs
         */
        public static byte[] decryptFromString(final PrivateKey key, final String cipherText)
                throws GeneralSecurityException {
            return decrypt(key, Base64.decodeBase64(cipherText));
        }

        /** Prevents instantiation of this utility class. */
        private RSAHashes() { }
    }

    /** Prevents instantiation of this utility class. */
    private SecurityTools() { }
}
