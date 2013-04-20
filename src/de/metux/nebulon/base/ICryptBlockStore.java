package de.metux.nebulon.base;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface ICryptBlockStore {

	public byte[] get(CryptScore score) throws IOException, GeneralSecurityException;
	public CryptScore put(byte[] content) throws IOException, java.security.GeneralSecurityException;
	public boolean delete(CryptScore k) throws IOException, java.security.GeneralSecurityException;
}
