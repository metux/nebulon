package de.metux.nebulon.base;

/**
 * this class represents a pointer to an encrypted block, including key
 *
 * It is also used by other components (eg. CryptFileStore) with different semantics
 */
public final class CryptScore {

	/** score of the encrypted block **/
	Score score;

	/** encryption key **/
	CryptKey key;

	/** compute a string representation: <score-as-string>+":"+<key-as-string> **/
	public String toString() {
		return score.toString()+":"+key.toString();
	}

	/**
	 * Constructor: using score, cipertype and binary key
	 *
	 * @param	s	Score object
	 * @param	c	cipher type name
	 * @param	k	encryption key as byte array
	 */
	public CryptScore(Score s, String c, byte[] k) {
		score = s;
		key = new CryptKey(c, k);
	}

	/**
	 * Constructor: using score as string, ciphertype and binary key
	 *
	 * @param	s	score as string representation
	 * @param	c	keytype
	 * @param	k	key as hex string representation
	 */
	public CryptScore(Score s, String c, String k) {
		score = s;
		key = new CryptKey(c, k);
	}

	/**
	 * Constructor: using Score and CryptKey object
	 *
	 * @param	s	score object
	 * @param	k	cryptkey object
	 */
	public CryptScore(Score s, CryptKey k) {
		score = s;
		key = k;
	}

	/**
	 * Parse crypt score object from string representation
	 *
	 * @param	s	crypt score as string representation
	 * @result		CryptScore object
	 */
	public static CryptScore parse(String s) throws MalformedCryptScoreException {
		if (s == null)
			s = "";

		String[] s2 = s.split(":");

		if (s2.length != 4)
			throw new MalformedCryptScoreException(s);

		return new CryptScore(new Score(s2[0], s2[1]), new CryptKey(s2[2], s2[3]));
	}

	/**
	 * Retrieve encryption key and ciphertype as CryptKey object
	 *
	 * @result		CryptKey object holding encryption key and ciphertype
	 */
	public CryptKey getKey() {
		return key;
	}

	/**
	 * Retrieve score object
	 *
	 * @result		Score object
	 */
	public Score getScore() {
		return score;
	}
}
