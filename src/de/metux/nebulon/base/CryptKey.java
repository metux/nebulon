package de.metux.nebulon.base;

import de.metux.nebulon.util.FileIO;

/**
 * Encryption key object - holding ciphertype and binary encryption key
 */
public class CryptKey {

	/** Cipher name **/
	public String cipher;

	/** key as byte array **/
	public byte[] key;

	/**
	 * Constructor: using ciphertype and binary key
	 *
	 * @param	c	ciphertype name
	 * @param	k	byte array holding binary key
	 */
	public CryptKey(String c, byte[] k) {
		cipher = c;
		key = k;
	}

	/**
	 * Constructor: using ciphertype and hex representation of key
	 *
	 * @param	c	ciphertype name
	 * @param	k	hex string representaion of the key
	 */
	public CryptKey(String c, String k) {
		cipher = c;
		key = FileIO.hexStringToByteArray(k);
	}

	/**
	 * Parse CryptKey from string representation
	 *
	 * @param	s	string representation of the crypt key
	 * @result		CryptKey object
	 */
	public static final CryptKey parse(String s) {
		String s2[] = s.split(":");
		return new CryptKey(s2[0], s2[1]);
	}

	/**
	 * Get string representation of crypt key: <ciphertype>+":"+<key-as-hex-string>
	 *
	 * @result		string representation of crypt key
	 */
	public final String toString() {
		return cipher+":"+FileIO.byteArray2Hex(key);
	}

	/**
	 * Write string representation into StringBuilder
	 *
	 * @param	sb	StringBuffer to write to
	 */
	public final void print(StringBuilder sb) {
		sb.append(cipher);
		sb.append(":");
		sb.append(FileIO.byteArray2Hex(key));
	}
}
