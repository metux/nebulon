package de.metux.nebulon.base;

public class Score {

	public String keytype;
	public String key;

	public Score(String kt, String k) {
		keytype = kt;
		key = k;
	}

	public String toString() {
		return ((keytype==null)?"":keytype)+":"+((key==null)?"":key);
	}
}
