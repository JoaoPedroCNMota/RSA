package RSA;

import java.lang.Object;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import RSA.hashingUtils.shaCommonUtils;
import RSA.Utils.Bytes;

public class MaskFunction {

	private final MessageDigest digest;

	public MaskFunction(MessageDigest digest) {
		this.digest = digest;
	}

	public byte[] generateMask(byte[] mgfSeed, int maskLen) {
		// (maskLen / hLen) - 1
		int hashCount = (maskLen + this.digest.getDigestLength() - 1) / this.digest.getDigestLength();

		byte[] mask = new byte[0];

		for (int i = 0; i < hashCount; i++) {
			this.digest.update(mgfSeed);
			this.digest.update(new byte[3]);
			this.digest.update((byte) i);
			byte[] hash = this.digest.digest();

			mask = Bytes.concat(mask, hash);
		}
		byte[] output = new byte[maskLen];
		System.arraycopy(mask, 0, output, 0, output.length);
		System.out.println(output);
		return output;
	}
}