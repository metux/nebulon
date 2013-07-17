package de.metux.nebulon.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

	public final String encryptions_ciphers[] = {
		"Blowfish/ECB/PKCS5Padding",
		"Blowfish/ECB/PKCS5Padding",
		"AES/ECB/PKCS5Padding",
		"AES/ECB/PKCS5Padding"
	};

	public final boolean encryption_gzip[] = { false, true, false, true };
}
