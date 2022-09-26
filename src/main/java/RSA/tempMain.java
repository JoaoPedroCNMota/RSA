package RSA;

import RSA.utils.ShaCommonUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class tempMain {
    public static void main(String[] args) throws Exception {
        KeyGenerator key = new KeyGenerator();
        AES_Encrypt aes = new AES_Encrypt();

        key.generateKeys();

        List<String> publicKeyContent = key.readKeyFromFile(true);
        List<String> privateKeyContent = key.readKeyFromFile(false);

//        List<BigInteger> encryptedMessage = RSA_Cipher.encrypt("Teste", new BigInteger(publicKeyContent.get(1)), new BigInteger(publicKeyContent.get(0)));
//        String message = RSA_Cipher.dencrypt(encryptedMessage, new BigInteger(privateKeyContent.get(1)), new BigInteger(privateKeyContent.get(0)));

        String enc = aes.encrypt("Teste");
        String ret = aes.decrypt(enc);

//        byte[] code = ShaCommonUtils.generateHash(message.getBytes(StandardCharsets.UTF_8));
    }
}
