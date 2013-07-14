package de.metux.nebulon.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

public class FileIO {

	public static final boolean createFilePath(String filename) {
		return new File(filename).getParentFile().mkdirs();
	}

	public static final boolean createFilePath(File f) {
		return f.getParentFile().mkdirs();
	}

	public static final byte[] readBytes(InputStream in)
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

	public static final byte[] loadBinaryFile(String name) {
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

	public static final String byteArray2Hex(final byte[] data) {
		if (data == null)
			return "NULL";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
}
