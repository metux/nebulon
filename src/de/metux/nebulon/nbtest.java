package de.metux.nebulon;

import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.store.CryptBlockStore;
import de.metux.nebulon.store.FilesystemBlockStore;
import de.metux.nebulon.util.FileIO;
import de.metux.nebulon.fs.CryptFileWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class nbtest {

	public static final String filename = "Makefile";

	private static IBlockStore _bs = null;
	private static ICryptBlockStore _cbs = null;

	public static IBlockStore getBS() {
		if (_bs == null)
			_bs = new FilesystemBlockStore("./data", true);
		return _bs;
	}

	public static ICryptBlockStore getCBS() {
		if (_cbs == null)
			_cbs = new CryptBlockStore(getBS());
		return _cbs;
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

	public static void test_crypt_file() throws IOException, GeneralSecurityException {
		final int bufsize = 4096;

		IBlockStore bs = getBS();
		ICryptBlockStore cbs = getCBS();
		CryptFileWriter cfw = new CryptFileWriter(bs, cbs);
		FileInputStream in = new FileInputStream("nbtest");
		byte buffer[] = new byte[bufsize];
		int sz;
		while ((sz = in.read(buffer))!=-1) {
			System.err.println("Got "+sz+" bytes ... writing to cbs");
			if (sz != bufsize) {
				byte[] newbuf = new byte[sz];
				System.arraycopy(buffer, 0, newbuf, 0, sz);
				cfw.write(newbuf);
			} else {
				cfw.write(buffer);
			}
		}

		CryptScore sc = cfw.finish();
		System.err.println("Cryptfile: "+sc.toString());
	}

	public static void testcrypt() throws IOException, GeneralSecurityException {
		ICryptBlockStore cbs = getCBS();
		CryptScore score = cbs.put(FileIO.loadBinaryFile(filename));
		dump(score.toString(), cbs.get(score));
	}

	public static void main(String argv[]) throws IOException, GeneralSecurityException {
//		testraw();
		testcrypt();
		test_crypt_file();
	}
}
