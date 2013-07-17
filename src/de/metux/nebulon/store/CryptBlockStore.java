package de.metux.nebulon.store;

import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.crypt.Crypt;
import de.metux.nebulon.util.Encoder;
import de.metux.nebulon.util.FileIO;
import de.metux.nebulon.util.Log;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class CryptBlockStore implements ICryptBlockStore {

	private static final String default_ciphertype = "Blowfish/ECB/PKCS5Padding";

	private IBlockStore blockstore;
	private boolean gzip = false;

	public CryptBlockStore(IBlockStore bs) {
		blockstore = bs;
	}

	public boolean delete(CryptScore score) throws IOException {
		return blockstore.delete(score.getScore());
	}

	public CryptScore put(byte[] data) throws IOException, GeneralSecurityException {
		return put(data, default_ciphertype);
	}

	public CryptScore put(byte[] data, String ciphertype) throws IOException, GeneralSecurityException {

		if (gzip)
			data = Encoder.encode_gzip(data);

		byte[] key = Score.computeKey(data);

		Log.debug("CryptScore::put() innerkey="+FileIO.byteArray2Hex(key));

		long start = System.nanoTime();
		byte[] crypted = Crypt.encrypt(ciphertype, key, data);
		long end = System.nanoTime();

		Log.debug("CryptScore::put() encryption time: "+(end-start));

		return new CryptScore(
			blockstore.put(crypted),
			(gzip ? ciphertype+"/GZip" : ciphertype),
			key
		);
	}

	public byte[] get(CryptScore score) throws IOException, GeneralSecurityException {
		byte[] crypted = blockstore.get(score.getScore());
		if (crypted == null) {
			Log.info("CryptBlockStore::get() crypted block not found: "+score.getScore());
			return null;
		}

		String ct = score.getKey().cipher;
		if (ct.endsWith("/GZip")) {
			return Encoder.decode_gzip(
				Crypt.decrypt(
					ct.substring(0, ct.length()-5),
					score.getKey().key,
					crypted
			));
		} else {
			return Crypt.decrypt(ct, score.getKey().key, crypted);
		}
	}
}
