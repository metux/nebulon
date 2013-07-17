package de.metux.nebulon;

import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.store.CryptBlockStore;
import de.metux.nebulon.store.FilesystemBlockStore;
import de.metux.nebulon.util.FileIO;
import de.metux.nebulon.fs.CryptFileWriter;
import de.metux.nebulon.fs.CryptFileReader;
//import de.metux.nebulon.crypt.Crypt;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class nbtest {

	public static final String filename = "Makefile";

	private static IBlockStore blockstore = null;
	private static ICryptBlockStore cryptblockstore = null;

	static {
		blockstore = new FilesystemBlockStore("./data", false);
		cryptblockstore = new CryptBlockStore(blockstore);
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
		Score score = blockstore.put(FileIO.loadBinaryFile(filename));
		dump(score.toString(), blockstore.get(score));
	}

	public static void test_crypt_file() throws IOException, GeneralSecurityException {
		final int bufsize = 4096;

		CryptFileWriter cfw = new CryptFileWriter(blockstore, cryptblockstore);
		FileInputStream in = new FileInputStream("nbtest");
		byte buffer[] = new byte[bufsize];
		int sz;
		while ((sz = in.read(buffer))!=-1) {
//			System.err.println("Got "+sz+" bytes ... writing to cbs");
			if (sz != bufsize) {
				byte[] newbuf = new byte[sz];
				System.arraycopy(buffer, 0, newbuf, 0, sz);
				cfw.write(newbuf);
			} else {
				cfw.write(buffer);
			}
//			System.err.println("Write done");
		}

		CryptScore sc = cfw.finish();
		System.err.println("Cryptfile: "+sc.toString());

		/** now try to load the crypt file **/
		CryptFileReader cfr = new CryptFileReader(blockstore, cryptblockstore, sc);
		FileOutputStream out = new FileOutputStream("TEST.OUT");
		byte[] buf;
		while ((buf=cfr.read())!=null) {
			out.write(buf);
		}
		out.flush();
		out.close();
	}

	public static void testcrypt() throws IOException, GeneralSecurityException {
		CryptScore score = cryptblockstore.put(FileIO.loadBinaryFile(filename));
		dump(score.toString(), cryptblockstore.get(score));
	}

/*
	public static void testcni() {
//		byte[] input = new String("Hello world foo blah").getBytes();
		byte[] input = new byte[16];
		input[0] = 1;
		input[1] = 2;
		input[2] = 3;
		input[3] = 4;
		byte[] result = OpenSSL.AES_encrypt(input);
		if (result == null)
			System.err.println("testcni: result array is null");
//		else
//			System.err.println("testcni: got non-null");
//			System.err.println("testcni: result array size: "+result.length);
	}
*/

	public static void main(String argv[]) throws IOException, GeneralSecurityException {
//		testraw();
//		testcrypt();
		test_crypt_file();
//		testcni();
	}
}
