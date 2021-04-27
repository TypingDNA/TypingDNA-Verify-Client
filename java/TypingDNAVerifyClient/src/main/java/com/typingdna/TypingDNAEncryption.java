package com.typingdna;

import com.lambdaworks.crypto.SCrypt;
import com.typingdna.exception.TypingDNAVerifyException;
import lombok.val;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

class TypingDNAEncryption {
    private static final int COST = 16384;
    private static final int BLOCK_SIZE = 8;
    private static final int PARALLELIZATION = 1;
    private static final int KEY_LENGTH = 32;

    public static String encrypt(String data, String secret, String salt) throws TypingDNAVerifyException {
        val encryptionKey = generateSecretKey(secret, salt);
        val iv = generateIv();
        return String.format("%s%s", doEncrypt(data, encryptionKey, iv), bytesToHex(iv.getIV()));
    }

    private static SecretKey generateSecretKey(String secret, String salt) throws TypingDNAVerifyException {
        try {
            val key = SCrypt.scrypt(
                    secret.getBytes(StandardCharsets.UTF_8),
                    salt.getBytes(StandardCharsets.UTF_8),
                    COST,
                    BLOCK_SIZE,
                    PARALLELIZATION,
                    KEY_LENGTH
            );
            return new SecretKeySpec(key, "AES");
        } catch (GeneralSecurityException e) {
            throw new TypingDNAVerifyException("Failed to generate the encryption key");
        }
    }

    private static IvParameterSpec generateIv() {
        val iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static String doEncrypt(String data, SecretKey encryptionKey, IvParameterSpec iv) throws TypingDNAVerifyException {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, iv);
            val encryptedData = cipher.doFinal(data.getBytes());
            return bytesToHex(encryptedData);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new TypingDNAVerifyException("Failed to encrypt data");
        }
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
