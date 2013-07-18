package de.metux.nebulon.crypt;

import de.metux.nebulon.util.Zip;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

	public static byte[] encrypt(String ciphertype, byte[] key, byte[] content) throws IOException, GeneralSecurityException {

		if (key == null)
			throw new GeneralSecurityException("null key");
		if (content == null)
			throw new GeneralSecurityException("null content");

		/** transparent content compression **/
		if (ciphertype.endsWith("@Z"))
			return encrypt(ciphertype.substring(0,ciphertype.length()-2),key,Zip.compress(content));

		/** OpenSSL-based AES-256 **/
		if (ciphertype.equals("@AES256"))
			return OpenSSL.AES256_encrypt(key, content);

		/** NULL cipher - just for testing **/
		if (ciphertype.equals("@NULL"))
			return content;

		/** fallback to JCE ciphers **/
		{
			Cipher cipher = Cipher.getInstance(ciphertype);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ciphertype));
			return cipher.doFinal(content);
		}
	}

	public static byte[] decrypt(String ciphertype, byte[] key, byte[] content) throws IOException, GeneralSecurityException {
		if (key == null)
			throw new GeneralSecurityException("null key");
		if (content == null)
			throw new GeneralSecurityException("null content");

		if (ciphertype.endsWith("@Z"))
			return Zip.uncompress(decrypt(ciphertype.substring(0,ciphertype.length()-2),key,content));

		/** OpenSSL-based AES-256 **/
		if (ciphertype.equals("@AES256"))
			return OpenSSL.AES256_decrypt(key, content);

		/** NULL cipher - just for testing **/
		if (ciphertype.equals("@NULL"))
			return content;

		/** fallback to JCE ciphers **/
		{
			Cipher cipher = Cipher.getInstance(ciphertype);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ciphertype));
			return cipher.doFinal(content);
		}
	}
}
