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

	final static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

	public static final String byteArray2Hex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for ( int j = 0; j < bytes.length; j++ ) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
						+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}
}
