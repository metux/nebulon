package de.metux.nebulon;

import de.metux.nanoweb.mount.MountEntry;
import de.metux.nanoweb.mount.MountingHandler;
import de.metux.nanoweb.example.DummyHandler;
import de.metux.nanoweb.daemon.MiniServer;
import de.metux.nebulon.service.BlockStoreHandler;
import de.metux.nebulon.service.CryptBlockStoreHandler;
import de.metux.nebulon.service.CryptFileHandler;
import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.store.CryptBlockStore;
import de.metux.nebulon.store.FilesystemBlockStore;
import de.metux.nebulon.util.FileIO;
import de.metux.nebulon.fs.CryptFileWriter;
import de.metux.nebulon.fs.CryptFileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class nbtest {

	public static final int http_port = 8080;
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
		FileInputStream in = new FileInputStream("TEST.IN");
		byte buffer[] = new byte[bufsize];
		int sz;
		while ((sz = in.read(buffer))!=-1) {
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

	public static void server() {
		DummyHandler dh = new DummyHandler();
		BlockStoreHandler bsh = new BlockStoreHandler(blockstore);
		CryptBlockStoreHandler cbsh = new CryptBlockStoreHandler(cryptblockstore);
		CryptFileHandler cfh = new CryptFileHandler(blockstore, cryptblockstore);

		MiniServer srv = new MiniServer(
			http_port,
			new MountingHandler(
				dh,
				new MountEntry[] {
					new MountEntry("getblock", bsh),
					new MountEntry("getcryptblock", cbsh),
					new MountEntry("getcryptfile", cfh)
				}
			)
		);
		srv.run();
	}

	public static void main(String argv[]) throws IOException, GeneralSecurityException {
//		testraw();
//		testcrypt();
		test_crypt_file();
		server();
	}
}
