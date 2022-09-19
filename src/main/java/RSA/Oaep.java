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

    public byte[] encryptionOaep(BigInteger n, byte[] message, BigInteger e)
            throws DataFormatException, NoSuchAlgorithmException {

        this.md = MessageDigest.getInstance("sha1");

        int k = (n.bitLength() + 7) / 8;
        int mLen = message.length;

        if (mLen > (k - 2 * this.md.getDigestLength() - 2)) {
            throw new DataFormatException("Block size too large");
        }

        byte[] PS = new byte[k - mLen - 2 * this.md.getDigestLength() - 2];

        byte[] DB = Bytes.concat(this.md.digest(), PS, new byte[] { 0x01 }, message);

        SecureRandom rng = new SecureRandom();
        byte[] seed = new byte[this.md.getDigestLength()];
        rng.nextBytes(seed);

        MaskFunction mgf1 = new MaskFunction(this.md);
        byte[] dbMask = mgf1.generateMask(seed,
                k - this.md.getDigestLength() - 1);

        // f. Let maskedDB = DB \xor dbMask.
        byte[] maskedDB = Bytes.xor(DB, dbMask);

        // g. Let seedMask = MGF(maskedDB, hLen).
        byte[] seedMask = mgf1.generateMask(maskedDB,
                this.md.getDigestLength());

        // h. Let maskedSeed = seed \xor seedMask.
        byte[] maskedSeed = Bytes.xor(seed, seedMask);

        /*
         * i. Concatenate a single octet with hexadecimal value 0x00,
         * maskedSeed, and maskedDB to form an encoded message EM of
         * length k octets as
         * 
         * EM = 0x00 || maskedSeed || maskedDB.
         */
        byte[] EM = Bytes.concat(new byte[] { 0x00 }, maskedSeed, maskedDB);

        /*
         * a. Convert the encoded message EM to an integer message
         * representative m (see Section 4.2):
         * m = OS2IP (EM).
         */
        BigInteger m = new BigInteger(1, EM);

        /*
         * b. Apply the RSAEP encryption primitive (Section 5.1.1) to the RSA
         * public key (n, e) and the message representative m to produce
         * an integer ciphertext representative c:
         * c = RSAEP ((n, e), m).
         */
        BigInteger c = m.modPow(e, n);

        /*
         * c. Convert the ciphertext representative c to a ciphertext C of
         * length k octets (see Section 4.1):
         * C = I2OSP (c, k).
         */
        byte[] ciphertext = Bytes.toFixedLenByteArray(c, k);

        if (ciphertext.length != k) {
            throw new DataFormatException();
        }

        return ciphertext;
    }

    public byte[] decryptionOaep(BigInteger n, byte[] ciphertxt, BigInteger e) throws DataFormatException {

        int k = (n.bitLength() + 7) / 8;

        if (ciphertxt.length != k) {
            throw new DataFormatException();
        }

        // c. If k < 2hLen + 2, output "decryption error" and stop

        if (k < (2 * this.md.getDigestLength() + 2)) {
            throw new DataFormatException("Decryption error");
        }

        /*
         * 2. RSA decryption:
         * a. Convert the ciphertext C to an integer ciphertext
         * representative c (see Section 4.2):
         * 
         * c = OS2IP (C).
         */
        BigInteger c = new BigInteger(1, ciphertxt);

        /*
         * b. Apply the RSADP decryption primitive (Section 5.1.2) to the
         * RSA private key K and the ciphertext representative c to
         * produce an integer message representative m:
         * 
         * m = RSADP (K, c).
         */
        BigInteger m = c.modPow(e, n);

        /*
         * c. Convert the message representative m to an encoded message EM
         * of length k octets (see Section 4.1):
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
         * If there is no octet with hexadecimal value 0x01 to separate PS
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

        // 4. Output the message M.
        int mLen = DB.length - i;
        byte[] M = new byte[mLen];
        System.arraycopy(DB, i, M, 0, mLen);
        return M;
    }

}
