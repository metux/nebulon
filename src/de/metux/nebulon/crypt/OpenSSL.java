package de.metux.nebulon.crypt;

public final class OpenSSL {
	public static native byte[] AES_encrypt(byte[] key, byte[] content);
	public static native byte[] AES_decrypt(byte[] key, byte[] content);
	public static native byte[] Blowfish_encrypt(byte[] key, byte[] content);
	public static native byte[] Blowfish_decrypt(byte[] key, byte[] content);
	public static native void dummy(String s);
}
