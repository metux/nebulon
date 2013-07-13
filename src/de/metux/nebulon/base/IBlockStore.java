package de.metux.nebulon.base;

public interface IBlockStore {

	/* get a data block -- including payload */
	public BlockInfo getBlock(Score k);

	/* store a block with associated data, including score/key generation */
	public Score storeBlock(byte[] content);

	/* delete a block */
	public boolean deleteBlock(Score k);
}
