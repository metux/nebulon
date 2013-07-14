package de.metux.nebulon;

import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.store.CryptBlockStore;
import de.metux.nebulon.store.FilesystemBlockStore;
import de.metux.nebulon.util.FileIO;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class nbtest {

	public static final String filename = "Makefile";

	public static IBlockStore getBS() {
		return new FilesystemBlockStore("./data", true);
	}

	public static ICryptBlockStore getCBS() {
		return new CryptBlockStore(getBS());
	}

	public static final void dump(String score, byte[] data) throws IOException {
		if (data == null) {
			System.err.println("Could not read block: "+score);
		} else {
			System.out.println("Score: "+score);
			System.out.write(data);
		}
	}

	public static void testraw() throws IOException {
		IBlockStore bs = getBS();
		Score score = bs.put(FileIO.loadBinaryFile(filename));
		dump(score.toString(), bs.get(score));
	}

	public static void testcrypt() throws IOException, GeneralSecurityException {
		ICryptBlockStore cbs = new CryptBlockStore(new FilesystemBlockStore("./data", true));
		CryptScore score = cbs.put(FileIO.loadBinaryFile(filename));
		dump(score.toString(), cbs.get(score));
	}

	public static void main(String argv[]) throws IOException, GeneralSecurityException {
//		testraw();
		testcrypt();
	}
}
