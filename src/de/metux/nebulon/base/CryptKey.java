package de.metux.nebulon.base;

import de.metux.nebulon.util.FileIO;

public class CryptKey {
	public String cipher;
	public byte[] key;

	public CryptKey(String c, byte[] k) {
		cipher = c;
		key = k;
	}

	public static CryptKey parse(String s) {
		String s2[] = s.split(":");
		return new CryptKey(s2[0], FileIO.hexStringToByteArray(s2[1]));
	}
}
