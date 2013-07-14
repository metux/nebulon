package de.metux.nebulon.base;

import java.io.IOException;

public interface IBlockStore {

	public byte[] get(Score score) throws IOException;
	public Score put(byte[] data) throws IOException;
	public boolean delete(Score score) throws IOException;
}
