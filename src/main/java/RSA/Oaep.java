package RSA;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.DataFormatException;
import RSA.KeyGenerator;

import RSA.Utils.Bytes;

public class Oaep {

    MessageDigest md;
    KeyGenerator keyGenerator;

    /**
     * encrypts a byte[] with RSA-OAEP and returns a byte[]
     * of encrypted values
     * 
     * @param byte[] n byte array for cipher
     * @return byte[] ciphertext byte array of ciphered values
     * @throws DataFormatException
     */
    public byte[] encryptionOaep(BigInteger n, byte[] message, BigInteger e)
            throws DataFormatException, NoSuchAlgorithmException {

        this.md = MessageDigest.getInstance("sha1");

        int k = (n.bitLength() + 7) / 8;
        int mLen = message.length;

        // RFC 2437 PKCS#1 v2.0
        // https://www.rfc-editor.org/rfc/rfc2437

        /*
         * 1. If mLen greater than input => throw error Block size too large
         */
        if (mLen > (k - 2 * this.md.getDigestLength() - 2)) {
            throw new DataFormatException("Block size too large");
        }

        /*
         * 2. Generate an octet string for padding consisting of k - mLen - 2hLen - 2
         * zero octets. The length of the padding string may be zero.
         */
        byte[] PS = new byte[k - mLen - 2 * this.md.getDigestLength() - 2];

        /*
         * 3. Concatenate lHash, PS, a single octet with hexadecimal value
         * 0x01, and the message M to form a data block DB of length k -
         * hLen - 1 octets:
         */
        byte[] DB = Bytes.concat(this.md.digest(), PS, new byte[] { 0x01 }, message);

        // 4. Generate a random octet string seed of length hLen
        // (hLen = hash function output length in octets)
        SecureRandom rng = new SecureRandom();
        byte[] seed = new byte[this.md.getDigestLength()];
        rng.nextBytes(seed);

        // 5. Let dbMask = MGF(seed, k - hLen - 1).
        MaskFunction mgf1 = new MaskFunction(this.md);
        byte[] dbMask = mgf1.generateMask(seed,
                k - this.md.getDigestLength() - 1);

        // 5.1 Xor the data block with its mask:
        byte[] maskedDB = Bytes.xor(DB, dbMask);

        // 5.2 Let seedMask = MGF(maskedDB, hLen).
        byte[] seedMask = mgf1.generateMask(maskedDB,
                this.md.getDigestLength());

        // 5.3 Xor the seed with its mask
        byte[] maskedSeed = Bytes.xor(seed, seedMask);

        /*
         * 6. Concat the octet with hex value 0x00 with the seed and data block
         * masks to generate an encoded msg
         * EM = 0x00 || maskedSeed || maskedDB.
         */
        byte[] EM = Bytes.concat(new byte[] { 0x00 }, maskedSeed, maskedDB);

        /*
         * 7 converts EM to a nonnegative integer.
         * Section 4.2 RFC 2437 (OS2IP)
         */
        BigInteger m = new BigInteger(1, EM);

        /*
         * 8. Apply the RSAEP encryption primitive to the RSA
         * public key and the message m to generate the integer ciphertext c:
         * Section 5.1.1 RSAEP RFC 2437
         */
        BigInteger c = m.modPow(e, n);

        /*
         * 9. Convert the ciphertext representative c to a ciphertext of
         * length k octets (Section 4.1 RFC 2437):
         * C = I2OSP (c, k).
         */
        byte[] ciphertext = Bytes.toFixedLenByteArray(c, k);

        if (ciphertext.length != k) {
            throw new DataFormatException();
        }

        return ciphertext;
    }

    /**
     * Takes a byte[] of encrypted values and uses
     * RSAES-OAEP-DECRYPT to decrypt them
     * 
     * @param byte[] C Array of encrypted values
     * @return byte[] M Array of decrypted values
     * @throws DataFormatException
     */
    public byte[] decryptionOaep(BigInteger n, byte[] ciphertxt, BigInteger e) throws DataFormatException {

        int k = (n.bitLength() + 7) / 8;

        if (ciphertxt.length != k) {
            throw new DataFormatException();
        }

        // 1. If k < 2hLen + 2, output "decryption error" and stop

        if (k < (2 * this.md.getDigestLength() + 2)) {
            throw new DataFormatException("Decryption error");
        }

        /*
         * 2. RSA decryption:
         * a. Convert the ciphertext c to an integer ciphertext
         * representative c (see Section 4.2 RFC 2437):
         * 
         * c = OS2IP (C).
         */
        BigInteger c = new BigInteger(1, ciphertxt);

        /*
         * b. Apply the RSADP decryption primitive (Section 5.1.2 RFC 2437) to the
         * RSA private key K and the ciphertext representative c to
         * produce an integer message representative m:
         * 
         * m = RSADP (K, c).
         */
        BigInteger m = c.modPow(e, n);

        /*
         * C. Convert the message representative m to an encoded message EM
         * of length k octets (see Section 4.1 RFC 2437):
         * 
         * EM = I2OSP (m, k).
         */
        byte[] EM = Bytes.toFixedLenByteArray(m, k);
        if (EM.length != k) {
            throw new DataFormatException();
        }

        /*
         * 3. EME-OAEP decoding:
         * a. If the label L is not provided, let L be the empty string.
         * Let lHash = Hash(L), an octet string of length hLen
         * (see the note in Section 7.1.1).
         * 
         * b. Separate the encoded message EM into a single octet Y,
         * an octet string maskedSeed of length hLen, and an octet
         * string maskedDB of length k - hLen - 1 as
         * 
         * EM = Y || maskedSeed || maskedDB.
         */
        if (EM[0] != 0x00)
            throw new DataFormatException();

        byte[] maskedSeed = new byte[this.md.getDigestLength()];
        System.arraycopy(EM, 1, maskedSeed, 0, maskedSeed.length);

        byte[] maskedDB = new byte[k - this.md.getDigestLength() - 1];
        System.arraycopy(EM,
                1 + this.md.getDigestLength(),
                maskedDB, 0, maskedDB.length);

        // c. Let seedMask = MGF (maskedDB, hLen).
        MaskFunction mgf1 = new MaskFunction(this.md);
        byte[] seedMask = mgf1.generateMask(maskedDB,
                this.md.getDigestLength());

        // d. Let seed = maskedSeed ^ seedMask.
        byte[] seed = Bytes.xor(maskedSeed, seedMask);

        // e. Let dbMask = MGF (seed, k - hLen - 1).
        byte[] dbMask = mgf1.generateMask(seed,
                k - this.md.getDigestLength() - 1);

        // f. Let DB = maskedDB ^ dbMask.
        byte[] DB = Bytes.xor(maskedDB, dbMask);

        /*
         * g. Separate DB into an octet string lHash' of length hLen, a
         * (possibly empty) padding string PS consisting of octets with
         * hexadecimal value 0x00, and a message M as
         * 
         * DB = lHash' || PS || 0x01 || M.
         */
        byte[] lHash1 = new byte[this.md.getDigestLength()];
        System.arraycopy(DB, 0, lHash1, 0, lHash1.length);
        if (!Bytes.equals(this.md.digest(), lHash1))
            throw new DataFormatException("Decryption error");

        /*
         * 4. If there is no octet with hexadecimal value 0x01 to separate PS
         * from M, if lHash does not equal lHash', or if Y is nonzero,
         * output "decryption error" and stop. (See the note below.)
         */
        int i;
        for (i = this.md.getDigestLength(); i < DB.length; i++) {
            if (DB[i] != 0x00)
                break;
        }

        if (DB[i++] != 0x01)
            throw new DataFormatException();

        // 5. Output the message M.
        int mLen = DB.length - i;
        byte[] M = new byte[mLen];
        System.arraycopy(DB, i, M, 0, mLen);
        return M;
    }

}
