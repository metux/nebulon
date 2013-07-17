package de.metux.nebulon.fs;

import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.base.BlockRef;
//import de.metux.nebulon.util.Log;
import java.lang.StringBuilder;
import java.io.IOException;

/**
 * manages writing blockrefs to the store
 * it writes them as a tree of (unencrypted) blockref-blocks
 * and returns the score of the root block
 */
public class BlockRefWriter {
	IBlockStore blockstore;
	BlockRefWriter parent = null;
	String reftype;

	/* maximum number of blockrefs per tree */
	public static final int max_refs = 128;

	StringBuilder buffer = new StringBuilder(1024);
	public int size = 0;

	public BlockRefWriter(IBlockStore bs, String rt) {
		blockstore = bs;
		reftype = rt;
	}

	private void addParent(Score s) throws IOException {
		if (parent == null)
			parent = new BlockRefWriter(blockstore, BlockRef.type_blocklist);
		parent.add(s);
	}

	private Score flushBuffer() throws IOException {
		Score s = blockstore.put(buffer.toString().getBytes());
		size = 0;
		buffer = new StringBuilder(1024);
		return s;
	}

	public void add(Score s) throws IOException {
//		Log.debug("BlockRefWriter::add() score="+s.toString());

		/** our list is full - write out and push to the next hierachy level */
		if (size == max_refs-1) {
			addParent(flushBuffer());
			size = 0;
		}

		buffer.append(reftype);
		buffer.append(":");
		s.print(buffer);
		buffer.append("\n");
		size++;
	}

	public Score finish() throws IOException {
		/* everything fits into our current level */
		if (parent == null)
			return flushBuffer();

		/* we've got at least one more layer */
		addParent(flushBuffer());
		return parent.finish();
	}
}
