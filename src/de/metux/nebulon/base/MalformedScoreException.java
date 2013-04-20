package de.metux.nebulon.base;

import java.io.IOException;

public class MalformedScoreException extends IOException {

	static final long serialVersionUID = 1234569;

	public MalformedScoreException(String b) {
		super(b);
	}
}
