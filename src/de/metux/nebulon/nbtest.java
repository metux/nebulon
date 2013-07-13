package de.metux.nebulon;

import de.metux.nebulon.base.*;
import de.metux.nebulon.storage.*;
import de.metux.nebulon.util.*;
import java.io.*;

public class nbtest {

	public static void main(String argv[]) throws IOException {
		String filename = "Makefile";
		BlockStore bs = new BlockStore(new FilesystemBlockStore("./data", true));
		Score score = bs.storeBlock(FileIO.loadBinaryFile(filename));

		System.out.println("SCORE: "+score);

		BlockInfo inf = bs.getBlock(score);
		if (inf == null)
			System.err.println("could not read block");
		if (inf.data == null)
			System.err.println("missing data buffer");

		System.out.write(inf.data);
	}
}
