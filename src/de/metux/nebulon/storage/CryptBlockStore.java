package de.metux.nebulon.storage;

import de.metux.nebulon.base.*;
import java.security.*;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;

public class CryptBlockStore implements ICryptBlockStore {

	private static final String default_ciphertype = "Blowfish/ECB/PKCS5Padding";

	IBlockStore blockstore;

	public CryptBlockStore(IBlockStore bs) {
		blockstore = bs;
	}

	public boolean delete(CryptScore score) {
		return blockstore.deleteBlock(score.score);
	}

	public CryptScore put(byte[] data) {
		return put(data, default_ciphertype);
	}

	public CryptScore put(byte[] data, String ciphertype) {
		CryptScore cs = new CryptScore();
		cs.cipher = ciphertype;
		cs.key = Score.getSHA256Score(data).key; // the inner score is our cipher key

		try {
			SecretKeySpec ks = new SecretKeySpec(cs.key, ciphertype);
			Cipher cipher = Cipher.getInstance(ciphertype);
			cipher.init(Cipher.ENCRYPT_MODE, ks);
			cs.score = blockstore.storeBlock(cipher.doFinal(data));
			return cs;
		} catch (NoSuchAlgorithmException e) {
			Log.err("CryptStore::put() No such algorithm: "+ciphertype, e);
			return null;
		} catch (NoSuchPaddingException e) {
			Log.err("CryptStore::put() padding exception", e);
			return null;
		} catch (InvalidKeyException e) {
			Log.err("CryptStore::put() invalid key exception", e);
			return null;
		} catch (IllegalBlockSizeException e) {
			Log.err("CryptStore::put() illegal block size exception", e);
			return null;
		} catch (BadPaddingException e) {
			Log.err("CryptStore::put(): illegal block size exception", e);
			return null;
		}
	}

	public byte[] get(CryptScore score) {
		byte[] crypted = blockstore.getBlock(score.score);
		if (crypted == null) {
			Log.info("CryptBlockStore::get() crypted block not found: "+score.score);
			return null;
		}

		try {
			SecretKeySpec ks = new SecretKeySpec(score.key, score.cipher);
			Cipher cipher = Cipher.getInstance(score.cipher);
			cipher.init(Cipher.DECRYPT_MODE, ks);
			return cipher.doFinal(crypted);
		} catch (NoSuchAlgorithmException e) {
			Log.info("CryptBlockStore::get() No such algorithm: "+score.cipher, e);
			return null;
		} catch (NoSuchPaddingException e) {
			Log.info("CryptBlockStore::get() padding exception", e);
			return null;
		} catch (InvalidKeyException e) {
			Log.info("CryptBlockStore::get() invalid key exception", e);
			return null;
		} catch (IllegalBlockSizeException e) {
			Log.info("CryptBlockStore::get() illegal block size exception", e);
			return null;
		} catch (BadPaddingException e) {
			Log.info("CryptBlockStore::get() illegal block size exception", e);
			return null;
		}
	}
}
