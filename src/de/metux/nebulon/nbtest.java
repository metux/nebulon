package de.metux.nebulon;

import javax.crypto.*;
import de.metux.nebulon.base.*;
import de.metux.nebulon.storage.*;
import de.metux.nebulon.util.*;
import java.io.*;
import java.security.*;

public class nbtest {

	public static void testraw() throws IOException {
		String filename = "Makefile";
		BlockStore bs = new BlockStore(new FilesystemBlockStore("./data", true));

		Score score = bs.storeBlock(FileIO.loadBinaryFile(filename));

		System.out.println("SCORE: "+score);

		byte[] data = bs.getBlock(score);
		if (data == null)
			System.err.println("could not read block");

		System.out.write(data);
		System.out.println(score.key.length);
	}

	public static void testcrypt() throws IOException {
		String filename = "Makefile";
		BlockStore bs = new BlockStore(new FilesystemBlockStore("./data", true));
		CryptBlockStore cbs = new CryptBlockStore(bs);

		CryptScore score = cbs.put(FileIO.loadBinaryFile(filename));

		System.out.println("SCORE: "+score);

		byte[] data = cbs.get(score);
		if (data == null)
			System.err.println("could not read block");

		System.out.write(data);
		System.out.println(score.key.length);
	}

	public static void main(String argv[]) throws IOException {
//		testraw();
		testcrypt();
	}
}
