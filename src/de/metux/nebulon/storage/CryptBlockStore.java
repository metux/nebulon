package de.metux.nebulon.storage;

import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.util.Log;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptBlockStore implements ICryptBlockStore {

	private static final String default_ciphertype = "Blowfish/ECB/PKCS5Padding";

	IBlockStore blockstore;

	public CryptBlockStore(IBlockStore bs) {
		blockstore = bs;
	}

	public boolean delete(CryptScore score) throws IOException {
		return blockstore.delete(score.score);
	}

	public CryptScore put(byte[] data) throws IOException, GeneralSecurityException {
		return put(data, default_ciphertype);
	}

	public CryptScore put(byte[] data, String ciphertype) throws IOException, GeneralSecurityException {
		CryptScore cs = new CryptScore();
		cs.cipher = ciphertype;
		cs.key = Score.compute(data).key; // the inner score is our cipher key

		SecretKeySpec ks = new SecretKeySpec(cs.key, ciphertype);
		Cipher cipher = Cipher.getInstance(ciphertype);
		cipher.init(Cipher.ENCRYPT_MODE, ks);
		cs.score = blockstore.put(cipher.doFinal(data));
		return cs;
	}

	public byte[] get(CryptScore score) throws IOException, GeneralSecurityException {
		byte[] crypted = blockstore.get(score.score);
		if (crypted == null) {
			Log.info("CryptBlockStore::get() crypted block not found: "+score.score);
			return null;
		}

		SecretKeySpec ks = new SecretKeySpec(score.key, score.cipher);
		Cipher cipher = Cipher.getInstance(score.cipher);
		cipher.init(Cipher.DECRYPT_MODE, ks);
		return cipher.doFinal(crypted);
	}
}
