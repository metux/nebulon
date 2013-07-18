package de.metux.nebulon.base;

public class Defaults {
	// default score keytype
	public static final String score_keytype = "SHA-256";

	// OpenSSL-based AES-256
//	public static final String crypt_ciphertype = "@AES256";
	public static final String crypt_ciphertype = "@AES256@Z";	// with GZip cleartext compression

	// No encryption -- just for testing
//	public static final String crypt_ciphertype= "@NULL";

	// JCE-based ciphers
//	public static final String crypt_ciphertype = "AES/ECB/PKCS5Padding";
//	public static final String crypt_ciphertype = "Blowfish/ECB/PKCS5Padding";

	// log timings for cryptstore
	public static final boolean cryptstore_timing = false;

	public static final boolean cryptstore_debug = false;

	public static final boolean zip_debug = false;
}
