package de.metux.nebulon.store;

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
		return blockstore.delete(score.getScore());
	}

	public CryptScore put(byte[] data) throws IOException, GeneralSecurityException {
		return put(data, default_ciphertype);
	}

	public CryptScore put(byte[] data, String ciphertype) throws IOException, GeneralSecurityException {

		byte[] key = Score.computeKey(data);

		Cipher cipher = Cipher.getInstance(ciphertype);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ciphertype));

		return new CryptScore(blockstore.put(cipher.doFinal(data)), ciphertype, key);
	}

	public byte[] get(CryptScore score) throws IOException, GeneralSecurityException {
		byte[] crypted = blockstore.get(score.getScore());
		if (crypted == null) {
			Log.info("CryptBlockStore::get() crypted block not found: "+score.getScore());
			return null;
		}

		Cipher cipher = Cipher.getInstance(score.getKey().cipher);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(score.getKey().key, score.getKey().cipher));
		return cipher.doFinal(crypted);
	}
}
