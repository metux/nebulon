package de.metux.nebulon.base;

import de.metux.nebulon.util.FileIO;

public class CryptKey {
	public String cipher;
	public byte[] key;

	public CryptKey(String c, byte[] k) {
		cipher = c;
		key = k;
	}

	public CryptKey(String c, String k) {
		cipher = c;
		key = FileIO.hexStringToByteArray(k);
	}

	public static final CryptKey parse(String s) {
		String s2[] = s.split(":");
		return new CryptKey(s2[0], s2[1]);
	}

	public final String toString() {
		return cipher+":"+FileIO.byteArray2Hex(key);
	}

	public final void print(StringBuilder sb) {
		sb.append(cipher);
		sb.append(":");
		sb.append(FileIO.byteArray2Hex(key));
	}
}
