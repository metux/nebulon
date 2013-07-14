package de.metux.nebulon.storage;

import de.metux.nebulon.base.*;
import java.security.*;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;

public class CryptBlockStore implements ICryptBlockStore {

	private static final String ciphertype = "Blowfish/ECB/PKCS5Padding";

	IBlockStore blockstore;

	public CryptBlockStore(IBlockStore bs) {
		blockstore = bs;
	}

	public boolean delete(CryptScore score) {
		return blockstore.deleteBlock(score.score);
	}

	public byte[] encrypt(byte[] key, byte[] data) {
		try {
			SecretKeySpec ks = new SecretKeySpec(key, ciphertype);
			Cipher cipher = Cipher.getInstance(ciphertype);
			cipher.init(Cipher.ENCRYPT_MODE, ks);
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("encrypt: No such algorithm: "+ciphertype+" ==> "+e);
			return null;
		} catch (NoSuchPaddingException e) {
			System.err.println("encrypt: padding exception ==> "+e);
			return null;
		} catch (InvalidKeyException e) {
			System.err.println("encrypt: invalid key exception ==> "+e);
			return null;
		} catch (IllegalBlockSizeException e) {
			System.err.println("encrypt: illegal block size exception ==> "+e);
			return null;
		} catch (BadPaddingException e) {
			System.err.println("encrypt: illegal block size exception ==> "+e);
			return null;
		}
	}

	public byte[] decrypt(byte[] key, byte[] data) {
		try {
			SecretKeySpec ks = new SecretKeySpec(key, ciphertype);
			Cipher cipher = Cipher.getInstance(ciphertype);
			cipher.init(Cipher.DECRYPT_MODE, ks);
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("decrypt: No such algorithm: "+ciphertype+" ==> "+e);
			return null;
		} catch (NoSuchPaddingException e) {
			System.err.println("decrypt: padding exception ==> "+e);
			return null;
		} catch (InvalidKeyException e) {
			System.err.println("decrypt: invalid key exception ==> "+e);
			return null;
		} catch (IllegalBlockSizeException e) {
			System.err.println("decrypt: illegal block size exception ==> "+e);
			return null;
		} catch (BadPaddingException e) {
			System.err.println("decrypt: illegal block size exception ==> "+e);
			return null;
		}
	}

	public CryptScore put(byte[] data) {
		CryptScore cs = new CryptScore();
		cs.cipher = ciphertype;
		cs.key = Score.getSHA256Score(data).key; // the inner score is our cipher key
		cs.score = blockstore.storeBlock(encrypt(cs.key, data));
		return cs;
	}

	public byte[] get(CryptScore score) {
		byte[] crypted = blockstore.getBlock(score.score);
		return decrypt(score.key, crypted);
	}
}
