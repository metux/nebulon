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

	public Score(String kt, String k) {
		keytype = kt;
		key = FileIO.hexStringToByteArray(k);
	}

	public String toString() {
		return ((keytype==null)?"":keytype)+":"+((key==null)?"":FileIO.byteArray2Hex(key));
	}

	public static final byte[] computeKey(byte[] data) {
		return computeKey(default_keytype, data);
	}

	public static byte[] computeKey(String keytype, byte[] data) {
		try {
			long start = System.nanoTime();
			byte[] key = MessageDigest.getInstance(keytype).digest(data);
			long end = System.nanoTime();
			Log.debug("Score::computeKey() took "+(end-start)+" ns");
			return key;
		} catch (NoSuchAlgorithmException e) {
			Log.err("Score::computeKey() No such algorithm: "+keytype, e);
			return null;
		}
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

	public static final Score parse(String s) {
		if (s == null)
			s = "";

		String[] s2 = s.split(":");
		if (s2.length == 2)
			return new Score(s2[0], s2[1]);

		Log.err("Score::parse() failed to parse score: \""+s+"\"");
		return null;
	}

	public void print(StringBuilder sb) {
		sb.append(keytype);
		sb.append(":");
		sb.append(FileIO.byteArray2Hex(key));
	}
}
