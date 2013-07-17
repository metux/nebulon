package de.metux.nebulon.fs;

import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.CryptKey;
import de.metux.nebulon.base.BlockRef;
import de.metux.nebulon.util.Log;
import java.io.IOException;
import java.lang.StringBuilder;
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
		StringBuilder sb = new StringBuilder();
		for (CryptKey cs : keylist) {
			cs.print(sb);
			sb.append("\n");
		}
		return sb.toString().getBytes();
	}

	public CryptScore finish() throws IOException, GeneralSecurityException {
		CryptScore cryptscore = cryptblockstore.put(serializeKeyList());
		Score blockrefs = brw.finish();
		StringBuilder sb = new StringBuilder();
		sb.append("Class: cryptfile/1\n");
		sb.append("BlockRefList: ");
		blockrefs.print(sb);
		sb.append("\nKeyList: ");
		cryptscore.getScore().print(sb);
		sb.append("\n");
		return new CryptScore(
			blockstore.put(sb.toString().getBytes()),
			cryptscore.getKey()
		);
	}

	public void write(byte[] b) throws IOException, GeneralSecurityException {
		CryptScore score = cryptblockstore.put(b);
//		Log.debug("CryptFileWriter::write() score="+score.getScore().toString());
		brw.add(score.getScore());
		keylist.add(score.getKey());
	}
}
