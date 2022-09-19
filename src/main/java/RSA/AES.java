package RSA;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AES {

    public static byte[] encryptCipher(byte[] IV, byte[] key, String msg) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKeySpec keyUsed = new SecretKeySpec(key,"AES");
        IvParameterSpec initVec = new IvParameterSpec(IV);

        cipher.init(Cipher.ENCRYPT_MODE, keyUsed,initVec);
        byte[] message = msg.getBytes(StandardCharsets.UTF_8);
        return cipher.doFinal(message);
    }

    public static byte[] decryptCipher(byte[] IV, byte[] key, String msg) throws Exception{

        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKeySpec keyUsed = new SecretKeySpec(key,"AES");
        IvParameterSpec initVec = new IvParameterSpec(IV);

        cipher.init(Cipher.DECRYPT_MODE, keyUsed,initVec);
        byte[] message = msg.getBytes(StandardCharsets.UTF_8);
        return cipher.doFinal(message);
    }
}
