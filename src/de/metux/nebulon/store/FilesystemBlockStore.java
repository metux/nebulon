package de.metux.nebulon.store;

import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.util.FileIO;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;

/**
 * Simple filesystem-based block store:
 * stores each block in a separate file (possibly ZIP compressed),
 * leading parts of the score (string representation) are used for forming directory name
 */
public class FilesystemBlockStore implements IBlockStore {

	private String root;
	private boolean compress;

	/**
	 * Constructor: by root directory and compression flag
	 *
	 * @param	r	root directory of the store in the hosts filesystem
	 * @param	c	true if new blocks should be ZIP compressed
	 */
	public FilesystemBlockStore(String r, boolean c) {
		root = r;
		compress = c;
	}

	/**
	 * compute block filename prefix by score
	 *
	 * @param	score	score to compute filename from
	 * @result	local block filename prefix
	 */
	private String _blockpath(Score score) {
		String path = root+"/"+score.keytype;
		String s2 = FileIO.byteArray2Hex(score.key);

		while (s2.length() > 4) {
			path += "/"+s2.substring(0,4);
			s2 = s2.substring(4);
		}

		return path+"/"+s2;
	}

	/**
	 * compute block filename for gzip compressed blocks
	 *
	 * @param	score	score to compute filename from
	 * @result	local block filename
	 */
	private String getGZipBlockPathname(Score score) {
		return _blockpath(score)+".gz";
	}

	/**
	 * compute block filename for uncompressed blocks
	 *
	 * @param	score	score to compute filename from
	 * @result	local block filename
	 */
	private String getBlockPathname(Score score) {
		return _blockpath(score)+".bin";
	}

	/**
	 * load block by given score
	 *
	 * @param	score	score of the block to be loaded
	 * @result	byte array with the block data - null if block not found
	 * @throws	java.io.IOException
	 */
	public byte[] get(Score score) throws IOException {
		byte[] data = get_raw(score);
		return ((data == null) ? get_gzip(score) : data);
	}

	/**
	 * load an uncompressed/raw block by score
	 *
	 * @param	score	score of the block to be loaded
	 * @result	byte array with the block data - null if block not found
	 * @throws	java.io.IOException
	 */
	private byte[] get_raw(Score score) throws IOException {
		try {
			RandomAccessFile f = new RandomAccessFile(getBlockPathname(score), "r");
			byte[] buffer = new byte[(int)f.length()];
			f.readFully(buffer);
			return buffer;
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * load an gzip-compressed block by score
	 *
	 * @param	score	score of the block to be loaded
	 * @result	byte array with the block data - null if block not found
	 * @throws	java.io.IOException
	 */
	private byte[] get_gzip(Score score) throws IOException {
		try {
			return FileIO.readBytes(new GZIPInputStream(new FileInputStream(getGZipBlockPathname(score))));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * store data block with gzip compression
	 *
	 * @param	content	byte array of data to be stored
	 * @result	score of the stored block
	 * @throws	java.io.IOException
	 */
	private Score put_gzip(byte[] content) throws IOException {
		Score score = Score.compute(content);
		String filename = getGZipBlockPathname(score);
		String tmpname = filename+".tmp";
		FileIO.createFilePath(filename);
		File tmpfile = new File(tmpname);

		try {
			GZIPOutputStream os = new GZIPOutputStream(new FileOutputStream(tmpname));
			os.write(content);
			os.flush();
			os.close();
		} catch (IOException e) {
			tmpfile.delete();
			throw e;
		}

		if (!tmpfile.renameTo(new File(filename)))
			throw new IOException("FilesystemBlockStore: failed to rename: "+tmpname+" => "+filename);

		return score;
	}

	/**
	 * store a block without compression
	 *
	 * @param	content	byte array of data to be stored
	 * @result	score of the stored block
	 * @throws	java.io.IOException
	 */
	private Score put_raw(byte[] content) throws IOException {
		Score score = Score.compute(content);
		String filename = getBlockPathname(score);
		FileIO.createFilePath(filename);
		String tmpname = filename+".tmp";
		File tmpfile = new File(tmpname);
		tmpfile.delete();

		try {
			RandomAccessFile f = new RandomAccessFile(tmpname, "rw");
			f.write(content);
			f.close();
		} catch (IOException e) {
			tmpfile.delete();
			throw e;
		}

		if (!tmpfile.renameTo(new File(filename)))
			throw new IOException("FilesystemBlockStore: failed to rename: "+tmpfile+" => "+filename);

		return score;
	}

	/**
	 * store a block - decides compression on configuration (variable: compress)
	 *
	 * @param	data	data to be stored
	 * @result		Score of the stored block
	 * @throws	java.io.IOException
	 */
	public Score put(byte[] data) throws IOException {
		return (compress ? put_gzip(data) : put_raw(data));
	}

	/**
	 * delete a block from the store
	 *
	 * @param	score	score of the object to be deleted
	 * @result		true when object was present and had been deleted
	 * @throws	java.io.IOException
	 */
	public boolean delete(Score score) throws IOException {
		/** trick to defeat shortcut evaluation */
		boolean raw = new File(getBlockPathname(score)).delete();
		boolean gz = new File(getBlockPathname(score)).delete();
		return (raw || gz);
	}
}
