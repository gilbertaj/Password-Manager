package ssbanerjee.passwordmanager;


/**
 * Created by Gilbert on 4/30/2017.
 */

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class Crypto {
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = KEY_LENGTH/8;
    private static final int IV_LENGTH = 12;
    private static final int ITERATIONS = 100000;
    private static final String FACTORY_INSTANCE = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_INSTANCE = "AES/GCM/NoPadding";
    private static final String DELIMITER = "~";

    private static SecretKey masterKey;


    public byte[] makeSalt() {
        SecureRandom random = new SecureRandom();
        byte[] result = new byte[SALT_LENGTH];
        random.nextBytes(result);

        return result;
    }

    public byte[] makeIv() {
        SecureRandom random = new SecureRandom();
        byte[] result = new byte[IV_LENGTH];
        random.nextBytes(result);

        return result;
    }

    public void makeKey(String password, byte[] salt) throws Exception {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY_INSTANCE);
        byte[] passwordHash = factory.generateSecret(keySpec).getEncoded();
        masterKey = new SecretKeySpec(passwordHash, "AES");
    }

    public SecretKey getKey() {
        return masterKey;
    }

    public String encrypt(String message) throws Exception{
        byte[] iv = makeIv();

        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
        GCMParameterSpec parameters = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, parameters);
        byte[] result = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        return String.format("%s%s%s", Base64.encodeToString(iv, Base64.NO_WRAP),
                DELIMITER, Base64.encodeToString(result, Base64.NO_WRAP));
    }

    public String decrypt(String message) throws Exception{
        String[] messageArray = message.split(DELIMITER);
        if(messageArray.length != 2) {
            throw new IllegalArgumentException("Invalid encryption format");
        }
        byte[] iv = Base64.decode(messageArray[0], Base64.NO_WRAP);
        byte[] text = Base64.decode(messageArray[1], Base64.NO_WRAP);

        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
        GCMParameterSpec parameters = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, masterKey, parameters);
        byte[] temp = cipher.doFinal(text);
        String result = new String(temp, StandardCharsets.UTF_8);

        return result;
    }
}
