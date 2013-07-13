package de.metux.nebulon.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.Formatter;

public class FileIO {
	public static boolean createFilePath(String filename) {
		return new File(filename).getParentFile().mkdirs();
	}

	public static byte[] readBytes(InputStream in)
	throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len;
		while ((len = in.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		in.close();
		byte[] b = out.toByteArray();
		out.close();
		return b;
	}

	public static byte[] loadBinaryFile (String name) {
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(name));
			byte[] theBytes = new byte[dis.available()];
			dis.read(theBytes, 0, dis.available());
			dis.close();
			return theBytes;
		} catch (IOException ex) {
		}
		return null;
	}

	public static String byteArray2Hex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
}
