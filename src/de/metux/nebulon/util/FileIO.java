package de.metux.nebulon.util;

import java.io.File;

public class FileIO {
	public static boolean createFilePath(String filename) {
		return new File(filename).getParentFile().mkdirs();
	}
}
