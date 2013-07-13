package de.metux.nebulon.base;

/**
 * basic blockstore interface.
 * just dumb store, without any key checks
 */
public interface IBasicBlockStore {

	/* get a data block -- including payload */
	public BlockInfo getBlock(Score k);

	/* get block metadata -- w/o payload */
	public BlockInfo statBlock(Score k);

	/* store a block with associated data - no key generation/checking */
	public boolean storeBlock(Score k, byte[] content);

	/* delete a block */
	public boolean deleteBlock(Score k);
}
