package de.metux.nebulon.base;

import de.metux.nebulon.util.FileIO;
import de.metux.nebulon.util.Log;

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

	public CryptScore(Score s, String c, byte[] k) {
		score = s;
		cipher = c;
		key = k;
	}

	public CryptScore(Score s, String c, String k) {
		score = s;
		cipher = c;
		key = FileIO.hexStringToByteArray(k);
	}

	public CryptScore(Score s, CryptKey k) {
		score = s;
		cipher = k.cipher;
		key = k.key;
	}

	public CryptScore parse(String s) {
		if (s == null)
			s = "";

		String[] s2 = s.split(":");

		if (s2.length == 4) {
			Log.err("CryptScore::parse(): unable to parse cryptscore \""+s+"\"");
			return null;
		}

		return new CryptScore(new Score(s2[0], s2[1]), s2[2], s2[3]);
	}

	public CryptKey getKey() {
		return new CryptKey(cipher, key);
	}

	public Score getScore() {
		return score;
	}
}
