package de.metux.nebulon.fs;

import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.CryptKey;
import de.metux.nebulon.base.BlockRef;
import java.io.IOException;
import java.lang.StringBuilder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

/**
 * Write an encrypted file of type: cryptfile/1
 *
 * All data blocks are written out to a CryptBlockStore, references to the blocks are collected
 * as BlockList tree (using BlockRefWriter), their encryption keys are stored in one large
 * encrypted keylist block. Finally, an CryptFileHeader block is written, pointing to the
 * blocklist root and keylist block.
 *
 * The returned CryptScore has special semantics: score field is pointing to the CryptFileBlock,
 * while key field holds the encryption key of the encrypted keylist block
 */
public class CryptFileWriter {

	ICryptBlockStore cryptblockstore;
	IBlockStore blockstore;
	BlockRefWriter brw;
	ArrayList<CryptKey> keylist = new ArrayList<CryptKey>();

	/**
	 * Constructor: using IBlockStore (unencrypted blocks) and ICryptScore (encrypted blocks)
	 *
	 * @param	bs	blockstore for the unencrypted blocks
	 * @param	cbs	cryptblockstore for the encrypted blocks
	 */
	public CryptFileWriter(IBlockStore bs, ICryptBlockStore cbs) {
		cryptblockstore = cbs;
		blockstore = bs;
		brw = new BlockRefWriter(bs, BlockRef.type_crypted_data);
	}

	/**
	 * serialize the keylist (of written encrypted data blocks) to a binary crypt keylist block
	 *
	 * @result		byte array holding the serialized keylist
	 */
	private byte[] serializeKeyList() {
		StringBuilder sb = new StringBuilder();
		for (CryptKey cs : keylist) {
			cs.print(sb);
			sb.append("\n");
		}
		return sb.toString().getBytes();
	}

	/**
	 * Finish the crypted file, write out the keylist and file header block and return CryptScore
	 * The CryptScore has special semantics: score points to the file header block, while
	 * key is the encryption key of the encrypted keylist block
	 *
	 * @result	CryptScore object for the written CryptFile
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
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

	/**
	 * write a data block to the crypted file
	 *
	 * @param	b	byte array holding the data block
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
	public void write(byte[] b) throws IOException, GeneralSecurityException {
		CryptScore score = cryptblockstore.put(b);
		brw.add(score.getScore());
		keylist.add(score.getKey());
	}
}
