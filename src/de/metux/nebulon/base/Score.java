package de.metux.nebulon.base;

import de.metux.nebulon.util.FileIO;
import de.metux.nebulon.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Score {

	public String keytype;
	public byte[] key;

	public static final String default_keytype = "SHA-256";

	public Score(String kt, byte[] k) {
		keytype = kt;
		key = k;
	}

	public String toString() {
		return ((keytype==null)?"":keytype)+":"+((key==null)?"":FileIO.byteArray2Hex(key));
	}

	public static Score compute(String keytype, byte[] data) {
		try {
			return new Score(keytype, MessageDigest.getInstance(keytype).digest(data));
		} catch (NoSuchAlgorithmException e) {
			Log.err("Score::compute() No such algorithm: "+keytype, e);
			return null;
		}
	}

	public static Score compute(byte[] data) {
		return compute(default_keytype, data);
	}
}
