package de.metux.nebulon.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

/**
 * Collection of helpers
 */
public class FileIO {

	/**
	 * create directory hierachy to the given filename
	 * similar to shell command: mkdir -p `basename $filename`
	 *
	 * @param	filename	filename to compute pathname from
	 * @result			true if directories had been created
	 */
	public static final boolean createFilePath(String filename) {
		return new File(filename).getParentFile().mkdirs();
	}

	/**
	 * create directory hierachy to the given File object
	 *
	 * @param	f		File object holding the filename
	 * @result			true if directories had been created
	 */
	public static final boolean createFilePath(File f) {
		return f.getParentFile().mkdirs();
	}

	/**
	 * read all available input bytes from input stream into byte array
	 *
	 * @param	in		input stream to read from
	 * @result			byte array holding the read data
	 * @throws	java.io.IOException
	 */
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

	/**
	 * load binary file into byte array
	 *
	 * @param	filename	name of the file to be loaded
	 * @result			byte array with loaded data - null if failed
	 */
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

	/**
	 * convert byte array to hex string
	 *
	 * @param	bytes	byte array to be converted
	 * @result		hex representation of the given byte array
	 */
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

	/**
	 * convert a single byte to hex string representation
	 *
	 * @param	b	byte to be converted to hex string
	 * @result		hex representation of the given byte
	 */
	public static final String toHex(byte b) {
		char[] hexChars = new char[2];
		int v;
		v = b & 0xFF;
		hexChars[0] = hexArray[v >>> 4];
		hexChars[1] = hexArray[v & 0x0F];
		return new String(hexChars);
	}

	/**
	 * convert hex string to byte array
	 *
	 * @param	s	hex string representation to be converted
	 * @result		converted byte array
	 */
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
