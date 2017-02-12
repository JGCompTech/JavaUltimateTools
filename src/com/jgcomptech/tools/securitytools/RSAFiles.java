package com.jgcomptech.tools.securitytools;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAFiles {
    public static void saveKeyPairToFile(KeyPair pair, String filename) {
        FileHashes.saveToFile(pair.getPrivate(), filename + "");
        FileHashes.saveToFile(pair.getPublic(), filename + ".pub");
    }

    public static PublicKey readPublicKeyFromFile(String fileName)
            throws GeneralSecurityException {
        return RSAHashes.readPublicKeyFromBytes(FileHashes.readFromFile(fileName));
    }

    public static PrivateKey readPrivateKeyFromFile(String fileName)
            throws GeneralSecurityException {
        return RSAHashes.readPrivateKeyFromBytes(FileHashes.readFromFile(fileName));
    }
}
