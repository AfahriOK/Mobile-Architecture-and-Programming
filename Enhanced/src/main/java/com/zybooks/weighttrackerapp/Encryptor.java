/**
 * @author Afahri Kerr (AfahriAKerr91@gmail.com)
 * @version 2.0
 * @since 12/07/2023
 * Purpose: To enhance security by encrypting user passwords before storage
 */

package com.zybooks.weighttrackerapp;

import android.annotation.SuppressLint;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is used to encrypt the user passwords. It is implemented as a singleton.
 */
public class Encryptor {
    private static Encryptor encryptor;
    private static final IvParameterSpec iv = generateIv();

    /**
     * Method to create a random initialization vector for encryption.
     * @return An initialization vector.
     */
    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private Encryptor() {
    }

    public static synchronized Encryptor getInstance() {
        if (encryptor == null) {
            encryptor = new Encryptor();
        }
        return encryptor;
    }

    /**
     * Method to generate a random salt for encryption keys
     * @return An encryption salt
     */
    public String getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Method to generate a secret key
     * @param pass Password to be encrypted.
     * @param salt Random salt to be added to the password.
     * @return A secret key based on the salt and password combination.
     */
    public static SecretKey generateKey(String pass, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        //Creates a key factory that will use SHA256
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt.getBytes(), 1000, 128);

        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    /**
     * Method to return an encrypted password string
     * @param pass Password to encrypt.
     * @param key Secret key based on password salt.
     * @return Encrypted password string.
     */
    public String encrypt(String pass, SecretKey key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(pass.getBytes());

        return Base64.getEncoder().encodeToString(cipherText);
    }
}
