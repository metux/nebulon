package de.metux.nebulon.base;

import de.metux.nebulon.util.Log;
import java.lang.StringBuilder;

public class BlockRef {

	public static final String type_crypted_data = "CD";
	public static final String type_blocklist = "BL";

	public String type;
	public Score score;

	public BlockRef(String t, Score s) {
		type = t;
		score = s;
	}

	public String toString() {
		return type+":"+score.toString();
	}

	public static String toString(String t, Score s) {
		return t+":"+s.toString();
	}

	public static final BlockRef parse(String s) {
		if (s == null)
			s = "";

		String s2[] = s.split(":");
		if (s2.length == 3)
			return new BlockRef(s2[0], new Score(s2[1], s2[2]));

		Log.err("BlockRef::parse() failed to parse \""+s+"\"");
		return null;
	}

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
