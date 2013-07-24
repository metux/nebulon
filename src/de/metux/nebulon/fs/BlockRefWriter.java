package de.metux.nebulon.fs;

import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.base.BlockRef;
import java.lang.StringBuilder;
import java.io.IOException;

/**
 * manages writing blockrefs to the store
 * it writes them as a tree of (unencrypted) blockref-blocks
 * and returns the score of the root block
 *
 * when the blocklist gets too large to fit into one block,
 * automatically greate a parent node (and leave ourselves as
 * leaf node) and pass the (partial) blocklist blocks there.
 */
public class BlockRefWriter {
	IBlockStore blockstore;
	BlockRefWriter parent = null;
	String reftype;

	/* maximum number of blockrefs per tree */
	public static final int max_refs = 128;

	StringBuilder buffer = new StringBuilder(1024);
	public int size = 0;

	/**
	 * Constructor: using blockstore and blockref-type
	 *
	 * @param	bs	IBlockStore interface to the underlying block store
	 * @param	rt	blockref type (used for direct block references
	 */
	public BlockRefWriter(IBlockStore bs, String rt) {
		blockstore = bs;
		reftype = rt;
	}

	/**
	 * add the given score to our parent blockref node
	 * create the parent node if not existing yet
	 *
	 * this usually is the act of finishing a blockref node in our current tree level
	 * and pushes reference to it it to the parent node / upper level
	 *
	 * @param	s	block score to be added to parent node
	 * @throws	java.io.IOException
	 */
	private void addParent(Score s) throws IOException {
		if (parent == null)
			parent = new BlockRefWriter(blockstore, BlockRef.type_blocklist);
		parent.add(s);
	}

	/**
	 * write out the buffered blockrefs to the block store and return its score
	 *
	 * @result	Score object for the written blockref list
	 * @throws	java.io.IOException
	 */
	private Score flushBuffer() throws IOException {
		Score s = blockstore.put(buffer.toString().getBytes());
		size = 0;
		buffer = new StringBuilder(1024);
		return s;
	}

	/**
	 * add a new score to the blockref list, using our instance's default blockref type
	 *
	 * @param	s	Score object to be stored
	 * @throws	java.io.IOException
	 */
	public void add(Score s) throws IOException {

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

	/**
	 * finish up this blocklist writer instance: write out all remaining blockrefs
	 * an return the score to the root of the blocklist tree.
	 *
	 * in case we're not the top-level node (we've got a parent), call the parent's
	 * finish() method and return it's result score
	 *
	 * @throws	java.io.IOException
	 */
	public Score finish() throws IOException {
		/* everything fits into our current level */
		if (parent == null)
			return flushBuffer();

		/* we've got at least one more layer */
		addParent(flushBuffer());
		return parent.finish();
	}
}
