package de.metux.nebulon.storage;

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

public class FilesystemBlockStore implements IBlockStore {

	private String root;
	private boolean compress;

	public FilesystemBlockStore(String r, boolean c) {
		root = r;
		compress = c;
	}

	private String _blockpath(Score score) {
		String path = root+"/"+score.keytype;
		String s2 = FileIO.byteArray2Hex(score.key);

		while (s2.length() > 4) {
			path += "/"+s2.substring(0,3);
			s2 = s2.substring(4);
		}

		return path+"/"+s2;
	}

	private String getGZipBlockPathname(Score score) {
		return _blockpath(score)+".gz";
	}

	private String getBlockPathname(Score score) {
		return _blockpath(score)+".bin";
	}

	public byte[] get(Score score) throws IOException {
		byte[] data = get_raw(score);
		return ((data == null) ? get_gzip(score) : data);
	}

	/* get a data block -- including payload */
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

	/* get a data block -- including payload */
	private byte[] get_gzip(Score score) throws IOException {
		try {
			return FileIO.readBytes(new GZIPInputStream(new FileInputStream(getGZipBlockPathname(score))));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

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

	/* store a block with associated data - no key generation/checking */
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

	public Score put(byte[] data) throws IOException {
		return (compress ? put_gzip(data) : put_raw(data));
	}

	/* delete a block */
	public boolean delete(Score score) throws IOException {
		/** trick to defeat shortcut evaluation */
		boolean raw = new File(getBlockPathname(score)).delete();
		boolean gz = new File(getBlockPathname(score)).delete();
		return (raw || gz);
	}
}
