package de.metux.nebulon.base;

import java.io.IOException;

/**
 * Exception: malformed string representation of a CryptScore
 */
public class MalformedCryptScoreException extends IOException {

	static final long serialVersionUID = 1234568;

	public MalformedCryptScoreException(String b) {
		super(b);
	}
}