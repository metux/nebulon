package de.metux.nebulon.base;

import de.metux.nebulon.util.FileIO;

public class Score {

	public String keytype;
	public byte[] key;

	public Score(String kt, byte[] k) {
		keytype = kt;
		key = k;
	}

	public String toString() {
		return ((keytype==null)?"":keytype)+":"+((key==null)?"":FileIO.byteArray2Hex(key));
	}

	public static Score getSHA256Score(byte[] data) {
		return new Score("SHA256", FileIO.SHA256sum(data));
	}
}
