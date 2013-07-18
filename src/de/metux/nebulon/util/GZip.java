package de.metux.nebulon.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZip {

	public static final byte[] compress(byte[] data) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
		gzipOutputStream.write(data);
		gzipOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	public static final byte[] uncompress(byte[] data) throws IOException {
		return FileIO.readBytes(new GZIPInputStream(new ByteArrayInputStream(data)));
	}
}
