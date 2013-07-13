package de.metux.nebulon;

import de.metux.nebulon.base.*;
import de.metux.nebulon.storage.*;
import java.io.*;

public class nbtest {

	public static byte[] loadBinaryFile (String name) {
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(name));
			byte[] theBytes = new byte[dis.available()];
			dis.read(theBytes, 0, dis.available());
			dis.close();
			return theBytes;
		} catch (IOException ex) {
		}
		return null;
	}

	public static void main(String argv[]) {
		String filename = "Makefile";
		String testhash = "7062b195a6d9b533690b1e100d74d379";
		Score score = new Score("MD5", testhash);
		FilesystemBlockStore bs = new FilesystemBlockStore("./data");
		bs.storeBlock(score, loadBinaryFile(filename));
	}
}
