package de.metux.nebulon.util;

import java.io.*;
import java.util.zip.*;

public class Encoder {

	public static final byte[] encode_gzip(byte[] data) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
		gzipOutputStream.write(data);
		gzipOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	public static final byte[] decode_gzip(byte[] data) throws IOException {
		return FileIO.readBytes(new GZIPInputStream(new ByteArrayInputStream(data)));
	}
}