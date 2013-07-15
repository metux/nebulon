package de.metux.nebulon.fs;

import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.CryptKey;
import de.metux.nebulon.base.BlockRef;
import de.metux.nebulon.util.FileIO;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.StringBuffer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class CryptFileReader {

	ICryptBlockStore cryptblockstore;
	IBlockStore blockstore;
	CryptKey rootkey;
	ArrayList<CryptKey> keys = new ArrayList<CryptKey>();
	ArrayList<Score> scores = new ArrayList<Score>();
	int count;

	void load_blockref_entry(BlockRef ref) throws IOException {
		if (ref.type.equals(BlockRef.type_blocklist)) {
			load_blockref_list(ref.score);
		} else if (ref.type.equals(BlockRef.type_crypted_data)) {
			scores.add(ref.score);
		} else {
			throw new IOException("unknown blockref type: "+ref.type);
		}
	}

	void load_blockref_list(Score score) throws IOException {
		byte[] data = blockstore.get(score);
		if (data == null)
			throw new IOException("cannot read blockref: "+score.toString());

		BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
		String line;
		while ((line = r.readLine())!=null)
			load_blockref_entry(BlockRef.parse(line));
	}

	void load_key_list(Score score) throws IOException, GeneralSecurityException {
		System.err.println("keylist ref: "+score.toString());
		byte[] data = cryptblockstore.get(new CryptScore(score,rootkey));
		if (data == null)
			throw new IOException("cannot read keylist: "+score.toString());

		BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
		String line;
		while ((line = r.readLine())!=null)
			keys.add(CryptKey.parse(line));
	}

	void hdrValue(String name, String value) throws IOException, GeneralSecurityException {
		if (name.equals("Class")) {
			if (!value.equals("cryptfile/1"))
				throw new IOException("unsupported file type: \""+value+"\"");
		} else if (name.equals("BlockRefList")) {
			load_blockref_list(Score.parse(value));
		} else if (name.equals("KeyList")) {
			load_key_list(Score.parse(value));
		} else {
			throw new IOException("unsupported header: "+name);
		}
	}

	void hdrLine(String line) throws IOException, GeneralSecurityException {
		int pos = line.indexOf(":");
		if (pos < 1)
			throw new IOException("broken hdr line: \""+line+"\"");

		/** looks like we got a valid header line */
		String hdr = line.substring(0,pos);
		pos++;
		int len = line.length();
		while ((pos<len) && ((line.charAt(pos)==' ')||(line.charAt(pos)=='\t')))
			pos++;

		if (pos>=len)
			throw new IOException("broken hdr line: \""+line+"\"");

		hdrValue(hdr, line.substring(pos));
	}

	public CryptFileReader(IBlockStore bs, ICryptBlockStore cbs, CryptScore cs) throws IOException, GeneralSecurityException {
		cryptblockstore = cbs;
		blockstore = bs;
		rootkey = cs.getKey();

		byte[] header = bs.get(cs.getScore());
		if (header == null)
			throw new IOException("cannot find file header: "+cs.getScore().toString());

		BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(header)));
		String line;
		while ((line = r.readLine()) != null)
			hdrLine(line);

		if (scores.size() != keys.size())
			throw new IOException("key count mismatch: dblocks="+scores.size()+" keys="+keys.size());
	}

	public byte[] read() throws IOException, GeneralSecurityException {
		if (count == scores.size())
			return null;

		byte[] data = cryptblockstore.get(new CryptScore(scores.get(count),keys.get(count)));
		count++;
		return data;
	}
}
