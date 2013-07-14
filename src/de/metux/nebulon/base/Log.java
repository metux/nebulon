package de.metux.nebulon.base;

public class Log {
	public static final void info(String text) {
		System.err.println("[INFO] "+text);
	}

	public static final void info(String text, Exception e) {
		System.err.println("[INFO] "+text+" "+e);
	}

	public static final void err(String text) {
		System.err.println("[ERR] "+text);
	}

	public static final void err(String text, Exception e) {
		System.err.println("[ERR] "+text+" "+e);
	}
}
