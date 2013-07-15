package de.metux.nebulon.base;

public class CryptKey {
	public String cipher;
	public byte[] key;

	public CryptKey(String c, byte[] k) {
		cipher = c;
		key = k;
	}
}
