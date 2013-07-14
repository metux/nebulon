package de.metux.nebulon.storage;

import de.metux.nebulon.base.BlockInfo;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.IBasicBlockStore;
import de.metux.nebulon.base.Score;

public class BlockStore implements IBlockStore {

	private IBasicBlockStore store;

	public BlockStore(IBasicBlockStore sbs) {
		store = sbs;
	}

	private static final Score genScore(byte[] data) {
		return Score.getSHA256Score(data);
	}

	/* get a data block -- including payload */
	public byte[] getBlock(Score k) {
		BlockInfo inf = store.getBlock(k);
		if (inf == null)
			return null;
		else
			return inf.data;
	}

	/* store a block with associated data, including score/key generation */
	public Score storeBlock(byte[] content) {
		Score s = genScore(content);
		return ((store.storeBlock(s, content)) ? s : null);
	}

	/* delete a block */
	public boolean deleteBlock(Score k) {
		return store.deleteBlock(k);
	}
}
