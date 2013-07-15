package de.metux.nebulon.fs;

import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.CryptKey;
import de.metux.nebulon.base.BlockRef;
import de.metux.nebulon.util.FileIO;
import java.io.IOException;
import java.lang.StringBuffer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class CryptFileWriter {

	ICryptBlockStore cryptblockstore;
	IBlockStore blockstore;
	BlockRefWriter brw;
	ArrayList<CryptKey> keylist = new ArrayList<CryptKey>();

	public CryptFileWriter(IBlockStore bs, ICryptBlockStore cbs) {
		cryptblockstore = cbs;
		blockstore = bs;
		brw = new BlockRefWriter(bs, BlockRef.type_crypted_data);
	}

	private byte[] serializeKeyList() {
		StringBuffer sb = new StringBuffer();
		for (CryptKey cs : keylist) {
			cs.print(sb);
			sb.append("\n");
		}
		return sb.toString().getBytes();
	}

	public CryptScore finish() throws IOException, GeneralSecurityException {
		/** write out the key list **/
		CryptScore cryptscore = cryptblockstore.put(serializeKeyList());
		Score blockrefs = brw.finish();
		StringBuffer sb = new StringBuffer();
		sb.append("Class: cryptfile/1\n");
		sb.append("BlockRefList: ");
		blockrefs.print(sb);
		sb.append("\nKeyList: ");
		cryptscore.getScore().print(sb);
		sb.append("\n");
		System.err.println(sb.toString());
		return new CryptScore(
			blockstore.put(sb.toString().getBytes()),
			cryptscore.getKey()
		);
	}

	public void write(byte[] b) throws IOException, GeneralSecurityException {
		CryptScore score = cryptblockstore.put(b);
		brw.add(score.getScore());
		keylist.add(score.getKey());
	}
}
