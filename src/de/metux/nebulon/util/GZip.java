package de.metux.nebulon.util;

import de.metux.nebulon.base.Score;
import de.metux.nebulon.util.FileIO;
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
		byte[] compressed = byteArrayOutputStream.toByteArray();

		System.err.println("Compress "+FileIO.byteArray2Hex(Score.computeKey(data))+" => "+FileIO.byteArray2Hex(Score.computeKey(compressed)));

		return compressed;
	}

	public static final byte[] uncompress(byte[] data) throws IOException {
		return FileIO.readBytes(new GZIPInputStream(new ByteArrayInputStream(data)));
	}
}
