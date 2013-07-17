package de.metux.nebulon.crypt;

// import java.security.Security;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
// import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Crypt {

	public static boolean useOpenSSL = false;

//	static {
//		Security.addProvider(new BouncyCastleProvider());
//	}

	public static byte[] encrypt(String ciphertype, byte[] key, byte[] content) throws GeneralSecurityException {
		if (useOpenSSL) {
			return OpenSSL.AES_encrypt(key, content);
		} else {
			Cipher cipher = Cipher.getInstance(ciphertype);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ciphertype));
			return cipher.doFinal(content);
		}
	}

	public static byte[] decrypt(String ciphertype, byte[] key, byte[] content) throws GeneralSecurityException {
		if (useOpenSSL) {
			return OpenSSL.AES_decrypt(key, content);
		} else {
			Cipher cipher = Cipher.getInstance(ciphertype);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ciphertype));
			return cipher.doFinal(content);
		}
	}
}
