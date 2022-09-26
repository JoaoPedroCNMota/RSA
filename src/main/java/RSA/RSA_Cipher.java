package RSA;

import RSA.utils.Util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RSA_Cipher {

    public static List<BigInteger> encrypt(String message, BigInteger publicKey, BigInteger n){
        List<BigInteger> encryptedChars = new ArrayList<>();
        for (int i = 0; i < message.length(); i++) {
            BigInteger c = BigInteger.valueOf(message.charAt(i));
            encryptedChars.add(c.pow(publicKey.intValue()).mod(n));
        }
        return encryptedChars;
    }

    public static String dencrypt(List<BigInteger> encryptedMessage, BigInteger privateKey, BigInteger n){
        StringBuilder message = new StringBuilder("");
        encryptedMessage.forEach(m -> {
           message.append(m.pow(privateKey.intValue()).mod(n));
        });
        return message.toString();
    }

}
