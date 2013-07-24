package de.metux.nebulon.base;

import de.metux.nebulon.util.FileIO;
import de.metux.nebulon.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Score -- Nebulon's primary object key
 *
 * A score consists of an keytype (eg. SHA-256) and an hash value
 * All objects are identified by their content hash value
 */
public class Score {

	public String keytype;
	public byte[] key;

	public Score(String kt, byte[] k) {
		keytype = kt;
		key = k;
	}

	/**
	 * Constructor using keytype and key (hashcode) in hex
	 *
	 * @param	kt	key type / hash function (eg. SHA-256)
	 * @param	k	hash value as HEX string
	 */
	public Score(String kt, String k) {
		keytype = kt;
		key = FileIO.hexStringToByteArray(k);
	}

	/**
	 * Returns a string representation
	 *
	 * @result		string representation <keytype>+":"<hash-as-hex>
	 */
	public String toString() {
		return ((keytype==null)?"":keytype)+":"+((key==null)?"":FileIO.byteArray2Hex(key));
	}

	/**
	 * Compute a key value using default hash function (SHA-256)
	 *
	 * @param	data	byte array of data to compute hash from
	 * @result		result as byte array
	 */
	public static final byte[] computeKey(byte[] data) {
		return computeKey(Defaults.score_keytype, data);
	}

	/**
	 * Compute a key value using given keytype / hash function
	 *
	 * @param	keytype	the key type to use
	 * @param	data	byte array of data to compute hash from
	 * @result		result as byte array - null if failed
	 */
	public static byte[] computeKey(String keytype, byte[] data) {
		try {
			byte[] key = MessageDigest.getInstance(keytype).digest(data);
			return key;
		} catch (NoSuchAlgorithmException e) {
			Log.err("Score::computeKey() No such algorithm: "+keytype, e);
			return null;
		}
	}

	/**
	 * Compute a score for given data block, using given keytype
	 *
	 * @param	keytype	the key type / hash function to use
	 * @param	data	byte array of data to compute score from
	 * @result		Score object
	 */
	public static Score compute(String keytype, byte[] data) {
		try {
			return new Score(keytype, MessageDigest.getInstance(keytype).digest(data));
		} catch (NoSuchAlgorithmException e) {
			Log.err("Score::compute() No such algorithm: "+keytype, e);
			return null;
		}
	}

	/**
	 * Compute a score for given data block, using default keytype (SHA-256)
	 *
	 * @param	data	byte array of data to compute score from
	 * @result		Score object
	 */
	public static Score compute(byte[] data) throws MalformedScoreException {
		return compute(Defaults.score_keytype, data);
	}

	/**
	 * Parse score string representation into new Score object
	 *
	 * @param	s	string representation to parse
	 * @result		Score object
	 * @throws	MailformedScoreException - if string representation cannot be parsed
	 */
	public static final Score parse(String s) throws MalformedScoreException {
		if (s == null)
			s = "";

		String[] s2 = s.split(":");
		if (s2.length != 2)
			throw new MalformedScoreException(s);

		return new Score(s2[0], s2[1]);
	}

	/**
	 * Write string representation into StringBuilder
	 *
	 * @param	sb	StringBuffer to write to
	 */
	public void print(StringBuilder sb) {
		sb.append(keytype);
		sb.append(":");
		sb.append(FileIO.byteArray2Hex(key));
	}
}
