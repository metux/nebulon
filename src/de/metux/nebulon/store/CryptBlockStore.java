package de.metux.nebulon.store;

import de.metux.nebulon.base.CryptIntegrityException;
import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.CryptKey;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.crypt.Crypt;
import de.metux.nebulon.util.FileIO;
import de.metux.nebulon.util.Log;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class CryptBlockStore implements ICryptBlockStore {

	private IBlockStore blockstore;

	public CryptBlockStore(IBlockStore bs) {
		blockstore = bs;
	}

	public boolean delete(CryptScore score) throws IOException {
		return blockstore.delete(score.getScore());
	}

	public CryptScore put(byte[] data) throws IOException, GeneralSecurityException {
		return put(data, Crypt.default_ciphertype);
	}

	public CryptScore put(byte[] data, String ciphertype) throws IOException, GeneralSecurityException {

		byte[] key = Score.computeKey(data);

		Log.debug("CryptScore::put() innerkey="+FileIO.byteArray2Hex(key));

		long start = System.nanoTime();
		byte[] crypted = Crypt.encrypt(ciphertype, key, data);
		long end = System.nanoTime();

		Log.debug("CryptScore::put() encryption time: "+(end-start));

		return new CryptScore(blockstore.put(crypted),ciphertype,key);
	}

	public byte[] get(CryptScore score) throws IOException, GeneralSecurityException {
		byte[] crypted = blockstore.get(score.getScore());
		if (crypted == null) {
			Log.info("CryptBlockStore::get() crypted block not found: "+score.getScore());
			return null;
		}

		CryptKey k = score.getKey();
		byte[] cleartext = Crypt.decrypt(k.cipher, k.key, crypted);

		/* integrity check */
		byte[] confirm_key = Score.computeKey(cleartext);
		if (!Arrays.equals(k.key,confirm_key))
			throw new CryptIntegrityException(confirm_key, k.key);

		return cleartext;
	}
}
