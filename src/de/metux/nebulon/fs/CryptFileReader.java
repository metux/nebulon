package de.metux.nebulon.fs;

import de.metux.nebulon.base.BlockRef;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.CryptKey;
import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.base.MalformedBlockRefException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

/**
 * Read an encrypted file of type: cryptfile/1
 *
 * All data blocks are written out to a CryptBlockStore, references to the blocks are collected
 * as BlockList tree (using BlockRefWriter), their encryption keys are stored in one large
 * encrypted keylist block. Finally, an CryptFileHeader block is written, pointing to the
 * blocklist root and keylist block.
 *
 * The returned CryptScore has special semantics: score field is pointing to the CryptFileBlock,
 * while key field holds the encryption key of the encrypted keylist block
 */
public class CryptFileReader {

	ICryptBlockStore cryptblockstore;
	IBlockStore blockstore;
	CryptKey rootkey;
	ArrayList<CryptKey> keys = new ArrayList<CryptKey>();
	ArrayList<Score> scores = new ArrayList<Score>();
	int count;

	/**
	 * load an BlockRef entry
	 *
	 * @param	ref	BlockRef object to be loaded
	 * @throws	java.io.IOException
	 */
	void load_blockref_entry(BlockRef ref) throws IOException {
		if (ref.type.equals(BlockRef.type_blocklist)) {
			load_blockref_list(ref.score);
		} else if (ref.type.equals(BlockRef.type_crypted_data)) {
			scores.add(ref.score);
		} else {
			throw new MalformedBlockRefException("unknown blockref type: "+ref.type);
		}
	}

	/**
	 * load an blocklist block by given score
	 *
	 * @param	score	score of the blocklist block to load
	 * @throws	java.io.IOException
	 */
	void load_blockref_list(Score score) throws IOException {
		byte[] data = blockstore.get(score);
		if (data == null)
			throw new FileNotFoundException("blockref: "+score.toString());

		BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
		String line;
		while ((line = r.readLine())!=null)
			load_blockref_entry(BlockRef.parse(line));
	}

	/**
	 * load keylist block from given score and decrypt it using key from our global CryptScore
	 *
	 * @param	score	score of the keylist block to be loaded
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
	void load_key_list(Score score) throws IOException, GeneralSecurityException {
		byte[] data = cryptblockstore.get(new CryptScore(score,rootkey));
		if (data == null)
			throw new FileNotFoundException("keylist: "+score.toString());

		BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
		String line;
		while ((line = r.readLine())!=null)
			keys.add(CryptKey.parse(line));
	}

	/**
	 * process an header entry
	 *
	 * @param	name	header name
	 * @param	value	header value
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
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

	/**
	 * process/parse header line
	 *
	 * @param	line	header line
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
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

	/**
	 * Constructor: using BlockStore, CryptBlockScore, CryptScore
	 *
	 * @param	bs	blockstore to read unencrypted from
	 * @param	cbs	encrypted blockstore to read encrypted data from
	 * @param	cs	CryptScore to be used: score field points to the file header,
	 *			while key field is the encryption key of the keylist block
	 * @throws	java.io.IOException, java.security.GeneralSecurityException
	 */
	public CryptFileReader(IBlockStore bs, ICryptBlockStore cbs, CryptScore cs) throws IOException, GeneralSecurityException {
		cryptblockstore = cbs;
		blockstore = bs;
		rootkey = cs.getKey();

		byte[] header = bs.get(cs.getScore());
		if (header == null)
			throw new FileNotFoundException("crypt file header: "+cs.getScore().toString());

		BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(header)));
		String line;
		while ((line = r.readLine()) != null)
			hdrLine(line);

		if (scores.size() != keys.size())
			throw new IOException("key count mismatch: dblocks="+scores.size()+" keys="+keys.size());
	}

	/**
	 * read an decrypt the next data block, until no more blocks present
	 *
	 * @result	decrypted data block
	 * @throws	IOException, GeneralSecurityException
	 */
	public byte[] read() throws IOException, GeneralSecurityException {
		if (count == scores.size())
			return null;

		byte[] data = cryptblockstore.get(new CryptScore(scores.get(count),keys.get(count)));
		count++;
		return data;
	}
}
