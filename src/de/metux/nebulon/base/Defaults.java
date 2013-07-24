package de.metux.nebulon.base;

/**
 * Built-In config defaults
 */
public class Defaults {
	/** default keytype for score computation **/
	public static final String score_keytype = "SHA-256";

	/**
	 * default encryption cipher
	 *
	 * @NULL		no encryption (just for testing)
	 * @AES256		OpenSSL-based AES-256
	 * @AES256@Z		OpenSSL-based AES-256 - with zip-compressed cleartext
	 *
	 * JCE-based ciphers (slow!)
	 *
	 * AES/ECB/PKCS5Padding
	 * Blowfish/ECB/PKCS5Padding
	 */
	public static final String crypt_ciphertype = "@AES256@Z";

	/** log timings for cryptstore **/
	public static final boolean cryptstore_timing = false;

	/** log debug messages from cryptscore **/
	public static final boolean cryptstore_debug = false;

	/** log debug messages from zip deflate/inflate **/
	public static final boolean zip_debug = false;

	/** default deflate compression level **/
	public static final int zip_level = 9;
}
