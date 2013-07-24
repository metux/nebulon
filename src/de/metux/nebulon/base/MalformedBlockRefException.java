package de.metux.nebulon.base;

import java.io.IOException;

/**
 * Exception: malformed string representation of BlockRef
 */
public class MalformedBlockRefException extends IOException {

	static final long serialVersionUID = 1234567;

	public MalformedBlockRefException(String b) {
		super(b);
	}
}
