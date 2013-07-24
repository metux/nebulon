package de.metux.nebulon.store;

import de.metux.nebulon.base.CryptIntegrityException;
import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.CryptKey;
import de.metux.nebulon.base.Defaults;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.crypt.Crypt;
import de.metux.nebulon.util.FileIO;
import de.metux.nebulon.util.Log;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Simple ICryptBlockStore implementation, ontop of an IBlockStore instance
 */
public class CryptBlockStore implements ICryptBlockStore {

	private IBlockStore blockstore;

	/**
	 * Constructor: using given blockstore
	 */
	public CryptBlockStore(IBlockStore bs) {
		blockstore = bs;
	}

	/**
	 * delete an encrypted block, pointed to by CryptScore object
	 *
	 * @param	score	CryptScore pointing to the object
	 * @result		true if object was present and had been deleted
	 * @throws	java.io.IOException
	 */
	public boolean delete(CryptScore score) throws IOException {
		return blockstore.delete(score.getScore());
	}

	/**
	 * store block of data as encrypted block and return its CryptScore
	 *
	 * @param	data	byte array with the data to be stored
	 * @result		CryptScore of the stored object
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
	public CryptScore put(byte[] data) throws IOException, GeneralSecurityException {
		return put(data, Defaults.crypt_ciphertype);
	}

	/**
	 * store block of data as encrypted block store using given ciphertype
	 *
	 * @param	data		byte array with the data to be stored
	 * @param	ciphertype	ciphertype to be used for encryption
	 * @result		CryptScore of the stored object
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
	public CryptScore put(byte[] data, String ciphertype) throws IOException, GeneralSecurityException {

		byte[] key = Score.computeKey(data);

		if (Defaults.cryptstore_debug)
			Log.debug("CryptScore::put() innerkey="+FileIO.byteArray2Hex(key));

		long start = 0;

		if (Defaults.cryptstore_timing)
			start = System.nanoTime();

		byte[] crypted = Crypt.encrypt(ciphertype, key, data);

		if (Defaults.cryptstore_timing)
			Log.debug("CryptScore::put() encryption time: "+(System.nanoTime()-start));

		return new CryptScore(blockstore.put(crypted),ciphertype,key);
	}

	/**
	 * read and decrypt a block
	 *
	 * @param	score	CryptScore of the encrypted object to be loaded
	 * @result	byte array of the encrypted data - null if failed
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
	public byte[] get(CryptScore score) throws IOException, GeneralSecurityException {
		byte[] crypted = blockstore.get(score.getScore());
		if (crypted == null) {
			Log.info("CryptBlockStore::get() crypted block not found: "+score.getScore());
			return null;
		}

		CryptKey k = score.getKey();

		long start = 0;

		if (Defaults.cryptstore_timing)
			start = System.nanoTime();

		byte[] cleartext = Crypt.decrypt(k.cipher, k.key, crypted);

		if (Defaults.cryptstore_timing)
			Log.debug("CryptScore::put() decryption time: "+(System.nanoTime()-start));

		/* integrity check */
		byte[] confirm_key = Score.computeKey(cleartext);
		if (!Arrays.equals(k.key,confirm_key))
			throw new CryptIntegrityException(confirm_key, k.key);

		return cleartext;
	}
}
