package de.metux.nebulon.base;

public interface ICryptBlockStore {

	/* get a data block -- including payload */
	public byte[] get(CryptScore score);

	/* store a block with associated data, including score/key generation */
	public CryptScore put(byte[] content);

	/* delete a block */
	public boolean delete(CryptScore k);
}
