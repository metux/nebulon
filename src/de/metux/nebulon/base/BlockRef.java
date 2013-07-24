package de.metux.nebulon.base;

import java.lang.StringBuilder;

/**
 * Block reference object
 *
 * All objects need to be referenced via (non-encrypted) BlockRef's
 * in order not to be dropped by garbage collector.
 *
 * BlockRefs may also point to other blockref objects, eg. for hierachical
 * structure, very long files (where blockref list would be too long to
 * fit into one block, etc)
 */
public class BlockRef {

	/** BlockRef type: encrypted data block **/
	public static final String type_crypted_data = "CD";

	/** BlockRef type: block list (points to another BlockRef object **/
	public static final String type_blocklist = "BL";

	public String type;
	public Score score;

	/**
	 * Constructor: by type string and score as string representation
	 *
	 * @param	t	blockref type
	 * @param	s	string representation of score
	 */
	public BlockRef(String t, Score s) {
		type = t;
		score = s;
	}

	/**
	 * get string representation of the blockref: <type>+":"+<score-as-string>
	 *
	 * @result		string represention of the blockref
	 */
	public final String toString() {
		return type+":"+score.toString();
	}

	/**
	 * render string representation of a blockref defined by type and score (w/o BlockRef instance)
	 *
	 * @param	t	blockref type
	 * @param	s	string representation of score
	 */
	public static final String toString(String t, Score s) {
		return t+":"+s.toString();
	}

	/**
	 * Parse string representation into new object
	 *
	 * @param	s	string representation of blockref to be parsed
	 * @result		BlockRef object
	 * @throws	MalformedBlockRefException
	 */
	public static final BlockRef parse(String s) throws MalformedBlockRefException {
		if (s == null)
			s = "";

		String s2[] = s.split(":");
		if (s2.length != 3)
			throw new MalformedBlockRefException(s);

		return new BlockRef(s2[0], new Score(s2[1], s2[2]));
	}

	/**
	 * Serialize a list of blocks with same blockref type to a byte array
	 *
	 * @param	scores	list of Score objects
	 * @param	max	max number of objects to use (instead of whole array)
	 * @param	type	blockref type to use
	 */
	public static final byte[] serializeScoreList(Score[] scores, int max, String type) {
		StringBuilder sb = new StringBuilder();
		for (int x=0; x<max; x++) {
			sb.append(type);
			sb.append(":");
			sb.append(scores[x].toString());
			sb.append("\n");
		}
		return sb.toString().getBytes();
	}
}
