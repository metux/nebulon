package de.metux.nebulon.base;

import de.metux.nebulon.util.FileIO;
import java.io.IOException;

/**
 * Exception: integrity check of encrypted block/file failed
 */
public class CryptIntegrityException extends IOException {

	byte[] got_key;
	byte[] expected_key;

	static final long serialVersionUID = 1234567;

	public CryptIntegrityException(byte[] gk, byte[] ek) {
		got_key = gk;
		expected_key = ek;
	}

	public String toString() {
		return "crypt integrity broken: got="+FileIO.byteArray2Hex(got_key)+" expected="+FileIO.byteArray2Hex(expected_key);
	}
}
