package de.metux.nebulon.base;

import de.metux.nebulon.util.FileIO;
import java.io.IOException;

public class BlockIntegrityException extends IOException {

	byte[] got_key;
	byte[] expected_key;

	static final long serialVersionUID = 123456;

	public BlockIntegrityException(byte[] gk, byte[] ek) {
		got_key = gk;
		expected_key = ek;
	}

	public String toString() {
		return "block integrity broken: got="+FileIO.byteArray2Hex(got_key)+" expected="+FileIO.byteArray2Hex(expected_key);
	}
}
