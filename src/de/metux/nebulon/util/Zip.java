package de.metux.nebulon.util;

import de.metux.nebulon.base.Defaults;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.util.FileIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * simple ZIP compression (deflate/inflate)
 */
public class Zip {

	/**
	 * compress a byte array
	 *
	 * @param	data	byte array to be compressed
	 * @result	compressed data as byte array
	 * @throws	java.io.IOException
	 */
	public static final byte[] compress(byte[] data) throws IOException {
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		DeflaterOutputStream zip = new DeflaterOutputStream(byteout, new Deflater(Defaults.zip_level));
		zip.write(data);
		zip.close();
		byte[] compressed = byteout.toByteArray();

		if (Defaults.zip_debug)
			Log.debug("Compress "+FileIO.byteArray2Hex(Score.computeKey(data))+" => "+FileIO.byteArray2Hex(Score.computeKey(compressed)));

		return compressed;
	}

	/**
	 * decompress a byte array
	 *
	 * @param	data	byte array of compressed data
	 * @result	uncompressed data as byte array
	 * @throws	java.io.IOException
	 */
	public static final byte[] uncompress(byte[] data) throws IOException {
		return FileIO.readBytes(new InflaterInputStream(new ByteArrayInputStream(data)));
	}
}
