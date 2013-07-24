package de.metux.nebulon.base;

import java.io.IOException;

/**
 * Interface: simple block store
 *
 * An IBlockStore object stores raw data blocks, identified by their hash code
 */
public interface IBlockStore {

	/**
	 * retrieve a data block from blockstore
	 *
	 * @param	score	Score pointing to the object
	 * @result		byte array holding the fetched data
	 * @throws	java.io.IOException
	 */
	public byte[] get(Score score) throws IOException;

	/**
	 * put a data block to block store, using default hash function (SHA-256) for score computation
	 *
	 * @param	data	byte array holding the data to be stored
	 * @result		Score object pointing to the stored object
	 * @throws	java.io.IOException
	 */
	public Score put(byte[] data) throws IOException;

	/**
	 * delete object from block store, pointed by score
	 *
	 * @param	score	Score pointing to the object to be deleted
	 * @throws	java.io.IOException
	 */
	public boolean delete(Score score) throws IOException;
}
