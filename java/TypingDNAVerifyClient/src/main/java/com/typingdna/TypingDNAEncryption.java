package com.typingdna;

import com.typingdna.exception.TypingDNAVerifyException;
import lombok.val;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

class TypingDNAEncryption {

    public static String encrypt(String data, String secret, String salt) throws TypingDNAVerifyException {
        val encryptionKey = generateSecretKey(secret, salt);
        val iv = generateIv();
        return String.format("%s%s", doEncrypt(data, encryptionKey, iv), bytesToHex(iv.getIV()));
    }

    private static SecretKey generateSecretKey(String secret, String salt) throws TypingDNAVerifyException {
        try {
            val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            val keySpec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 10000, 256);
            val secretKey = keyFactory.generateSecret(keySpec);

            return new SecretKeySpec(secretKey.getEncoded(), "AES");
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
