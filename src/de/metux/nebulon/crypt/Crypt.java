package de.metux.nebulon.crypt;

import de.metux.nebulon.util.Zip;
import de.metux.openssl.OpenSSL;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encryption helpers
 *
 * Provides an simple interface for byte array encryption
 * Supports JCE- and OpenSSL-based ciphers, as well as zip-compression of cleartext
 *
 * ZIP: blocks may be compressed before encryption.
 * for such blocks, the ciphertype as a "@Z" suffix
 */
public class Crypt {

	/**
	 * encrypt byte array using given cipher/key
	 *
	 * @param	ciphertype	the ciphertype name
	 * @param	key		binary key (byte array)
	 * @param	content		binary data (byte array)
	 * @result			encrypted data as byte array
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
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

	/**
	 * decrypt byte array using given cipher/key
	 *
	 * @param	ciphertype	the ciphertype name
	 * @param	key		binary key (byte array)
	 * @param	content		binary encrypted data (byte array)
	 * @result			decrypted data as byte array
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
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
