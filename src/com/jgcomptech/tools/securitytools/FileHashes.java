package com.jgcomptech.tools.securitytools;

import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHashes {
    /**
     * Read the file and calculate the checksum
     *
     * @param type     the hash type to use
     * @param filename the file to read
     * @return the hex representation of the hash using uppercase chars
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some
     *                               other reason cannot be opened for reading
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

    public enum HashType {
        MD5,
        SHA1,
        SHA256,
        SHA384,
        SHA512
    }
}
