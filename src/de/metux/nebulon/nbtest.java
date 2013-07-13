package de.metux.nebulon;

import de.metux.nebulon.base.*;
import de.metux.nebulon.storage.*;
import de.metux.nebulon.util.*;
import java.io.*;

public class nbtest {

	public static void main(String argv[]) throws IOException {
		String filename = "Makefile";
		String testhash = "7062b195a6d9b533690b1e100d74d379";
		Score score = new Score("MD5", testhash);
		FilesystemBlockStore bs = new FilesystemBlockStore("./data", true);
		bs.storeBlock(score, FileIO.loadBinaryFile(filename));

		BlockInfo inf = bs.getBlock(score);
		if (inf == null)
			System.err.println("could not read block");
		if (inf.data == null)
			System.err.println("missing data buffer");

		System.out.write(inf.data);
	}
}
