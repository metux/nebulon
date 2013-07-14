package de.metux.nebulon.storage;

import de.metux.nebulon.base.BlockInfo;
import de.metux.nebulon.base.IBasicBlockStore;
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

public class FilesystemBlockStore implements IBasicBlockStore {

	public String root;
	public boolean compress;

	public FilesystemBlockStore(String r, boolean c) {
		root = r;
		compress = c;
	}

	public String getBlockPathname(Score score) {
		String path = root+"/"+score.keytype;
		String s2 = FileIO.byteArray2Hex(score.key);

		while (s2.length() > 4) {
			path += "/"+s2.substring(0,3);
			s2 = s2.substring(4);
		}

		return path+"/"+s2+".bin";
	}

	public String getGZipBlockPathname(Score score) {
		String path = root+"/"+score.keytype;
		String s2 = FileIO.byteArray2Hex(score.key);

		while (s2.length() > 4) {
			path += "/"+s2.substring(0,3);
			s2 = s2.substring(4);
		}

		return path+"/"+s2+".gz";
	}

	public BlockInfo getBlock(Score score) {
		/* first try raw block */
		BlockInfo inf = getBlock_raw(score);
		if (inf != null) {
			return inf;
		}

		return getBlock_gzip(score);
	}

	/* get a data block -- including payload */
	public BlockInfo getBlock_raw(Score score) {
		try {
			BlockInfo inf = new BlockInfo();
			RandomAccessFile f = new RandomAccessFile(getBlockPathname(score), "r");
			inf.size = (int)f.length();
			inf.data = new byte[inf.size];
			f.readFully(inf.data);
			inf.encoding = "raw";
			inf.score = score;
			return inf;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			System.err.println("FilesystemBlockStore: IO error for block "+score+": "+e);
			return null;
		}
	}

	/* get a data block -- including payload */
	public BlockInfo getBlock_gzip(Score score) {
		try {
			BlockInfo inf = new BlockInfo();
			inf.data = FileIO.readBytes(new GZIPInputStream(new FileInputStream(getGZipBlockPathname(score))));
			inf.size = inf.data.length;
			inf.encoding = "gzip";
			inf.score = score;
			return inf;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			System.err.println("FilesystemBlockStore: IO error for block "+score+": "+e);
			return null;
		}
	}

	public boolean storeBlock_gzip(Score score, byte[] content) {
		String filename = getGZipBlockPathname(score);
		String tmpname = filename+".tmp";
		FileIO.createFilePath(filename);

		try {
			GZIPOutputStream os = new GZIPOutputStream(new FileOutputStream(tmpname));
			os.write(content);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			System.err.println("IOException for file: "+tmpname);
			return false;
		}

		if (new File(tmpname).renameTo(new File(filename))) {
			return true;
		} else {
			System.err.println("FilesystemBlockStore: failed to rename: "+tmpname+" => "+filename);
			return false;
		}
	}

	/* store a block with associated data - no key generation/checking */
	public boolean storeBlock_raw(Score score, byte[] content) {
		String filename = getBlockPathname(score);
		FileIO.createFilePath(filename);
		String tmpname = filename+".tmp";
		File tmpfile = new File(tmpname);
		tmpfile.delete();

		try {
			RandomAccessFile f = new RandomAccessFile(tmpname, "rw");
			f.write(content);
			f.close();
		} catch (FileNotFoundException e) {
			System.err.println("FilesystemBlockStore: cannot open file: "+score);
			return false;
		} catch (IOException e) {
			System.err.println("FilesystemBlockStore: error writing to file: "+score+" => "+e);
			tmpfile.delete();
			return false;
		}

		if (tmpfile.renameTo(new File(filename))) {
			return true;
		} else {
			System.err.println("FilesystemBlockStore: failed to rename: "+tmpfile+" => "+filename);
			return false;
		}
	}

	public boolean storeBlock(Score score, byte[] content) {
		if (compress)
			return storeBlock_gzip(score, content);
		else
			return storeBlock_raw(score, content);
	}

	/* delete a block */
	public boolean deleteBlock(Score k) {
		return false;
	}
}
