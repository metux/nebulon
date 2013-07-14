package de.metux.nebulon.base;

import de.metux.nebulon.util.FileIO;

/**
 * this class represents a pointer to an encrypted block, including key
 */
public class CryptScore {

	public Score score;	/* score of the encrypted block */
	public String cipher;	/* the cipher used for encryption */
	public byte[] key;	/* encryption key (as hex) */

	public String toString() {
		return score.toString()+":"+cipher+":"+FileIO.byteArray2Hex(key);
	}
}
