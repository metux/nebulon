package de.metux.nebulon.base;

/**
 * this class represents a pointer to an encrypted block, including key
 */
public final class CryptScore {

	Score score;	/* score of the encrypted block */
	CryptKey key;

	public String toString() {
		return score.toString()+":"+key.toString();
	}

	public CryptScore(Score s, String c, byte[] k) {
		score = s;
		key = new CryptKey(c, k);
	}

	public CryptScore(Score s, String c, String k) {
		score = s;
		key = new CryptKey(c, k);
	}

	public CryptScore(Score s, CryptKey k) {
		score = s;
		key = k;
	}

	public static CryptScore parse(String s) throws MalformedCryptScoreException {
		if (s == null)
			s = "";

		String[] s2 = s.split(":");

		if (s2.length != 4)
			throw new MalformedCryptScoreException(s);

		return new CryptScore(new Score(s2[0], s2[1]), new CryptKey(s2[2], s2[3]));
	}

	public CryptKey getKey() {
		return key;
	}

	public Score getScore() {
		return score;
	}
}
