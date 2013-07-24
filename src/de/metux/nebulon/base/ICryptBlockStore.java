package de.metux.nebulon.base;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Interface: crypt block store
 *
 * An ICryptBlockStore object stores encrypted data blocks
 * By default, the cleartext's hash value is used as encryption key, in order to allow deduplication
 */
public interface ICryptBlockStore {

	/**
	 * retrieve and decrypt an crypted block
	 *
	 * @param	score	CryptScore pointing to the crypted object and encryption key
	 * @result		unencrypted data block
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
	public byte[] get(CryptScore score) throws IOException, GeneralSecurityException;

	/**
	 * encrypt an data block using its (SHA-256) hash code as encryption key and stores it
	 * into the underlying blockstore
	 *
	 * @param	content	byte array holding the data block
	 * @result		CryptScore object holding the encrypted block's score and encryption key
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
	public CryptScore put(byte[] content) throws IOException, GeneralSecurityException;

	/**
	 * delete an encrypted block from the underlying block store
	 *
	 * @param	k	CryptScore object pointing to the encrypted object
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
	public boolean delete(CryptScore k) throws IOException, java.security.GeneralSecurityException;
}
