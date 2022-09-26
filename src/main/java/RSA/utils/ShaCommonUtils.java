package RSA.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaCommonUtils {

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte h : hash) {
            String hex = Integer.toHexString(0xff & h);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] generateHash(byte[] info) throws NoSuchAlgorithmException {
        MessageDigest dg = MessageDigest.getInstance("SHA-256");
        byte[] code = dg.digest(info);
        return code;
    }
}
