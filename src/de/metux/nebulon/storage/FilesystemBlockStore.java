package de.metux.nebulon.storage;

import de.metux.nebulon.base.IBasicBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.base.BlockInfo;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.io.File;

public class FilesystemBlockStore implements IBasicBlockStore {

	public String root;

	public FilesystemBlockStore(String r) {
		root = r;
	}

	public String getBlockPathname(Score score) {
		String path = root+"/"+score.keytype;
		String s2 = score.key;

		while (s2.length() > 4) {
			path += "/"+s2.substring(0,3);
			s2 = s2.substring(4);
		}

		return path+"/"+s2+".bin";
	}

	/* get a data block -- including payload */
	public BlockInfo getBlock(Score score) {
		try {
			BlockInfo inf = new BlockInfo();
			RandomAccessFile f = new RandomAccessFile(getBlockPathname(score), "r");
			inf.data = f.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f.length());
			inf.size = f.length();
			inf.encoding = "raw";
			inf.score = score;
			return inf;
		} catch (FileNotFoundException e) {
			System.err.println("FilesystemBlockStore: requested block not found: "+score);
			return null;
		} catch (IOException e) {
			System.err.println("FilesystemBlockStore: IO error for block "+score+": "+e);
			return null;
		}
	}

	/* get block metadata -- w/o payload */
	public BlockInfo statBlock(Score score) {
		try {
			BlockInfo inf = new BlockInfo();
			RandomAccessFile f = new RandomAccessFile(getBlockPathname(score), "r");
			inf.data = null;
			inf.size = f.length();
			inf.encoding = "raw";
			inf.score = score;
			return inf;
		} catch (FileNotFoundException e) {
			System.err.println("FilesystemBlockStore: requested block not found: "+score);
			return null;
		} catch (IOException e) {
			System.err.println("FilesystemBlockStore: IO error for block "+score+": "+e);
			return null;
		}
	}

	/* store a block with associated data - no key generation/checking */
	public boolean storeBlock(Score score, byte[] content) {
		String filename = getBlockPathname(score);
		String tmpname = filename+".tmp";
		File tmpfile = new File(filename);
		tmpfile.delete();

		try {
			RandomAccessFile f = new RandomAccessFile(tmpname, "w");
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

		return tmpfile.renameTo(new File(filename));
	}

	/* delete a block */
	public boolean deleteBlock(Score k) {
		return false;
	}
}
