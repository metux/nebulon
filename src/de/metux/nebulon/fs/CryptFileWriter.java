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
	ArrayList<CryptScore> keylist = new ArrayList<CryptScore>();

	public CryptFileWriter(IBlockStore bs, ICryptBlockStore cbs) {
		cryptblockstore = cbs;
		blockstore = bs;
		brw = new BlockRefWriter(bs, BlockRef.type_blocklist);
	}

	private byte[] serializeKeyList() {
		StringBuffer sb = new StringBuffer();
		for (CryptScore cs : keylist) {
			sb.append(cs.cipher);
			sb.append(":");
			sb.append(FileIO.byteArray2Hex(cs.key));
			sb.append("\n");
		}
		return sb.toString().getBytes();
	}

	public CryptScore finish() throws IOException, GeneralSecurityException {
		/** write out the key list **/
		CryptScore cryptscore = cryptblockstore.put(serializeKeyList());
		Score blockrefs = brw.finish();
		StringBuffer sb = new StringBuffer();
		sb.append("Content-Type: nebulon/cryptfile-1\n");
		sb.append("BlockRefList: ");
		sb.append(blockrefs.toString());
		sb.append("\nKeyList: ");
		sb.append(cryptscore.score.toString());
		sb.append("\n");
		System.err.println(sb.toString());
		Score header_score = blockstore.put(sb.toString().getBytes());
		CryptKey keylist_key = cryptscore.getKey();
		return new CryptScore(header_score, keylist_key);
	}

	public void write(byte[] b) throws IOException, GeneralSecurityException {
		CryptScore score = cryptblockstore.put(b);
		brw.add(score.score);
		keylist.add(score);
	}
}
