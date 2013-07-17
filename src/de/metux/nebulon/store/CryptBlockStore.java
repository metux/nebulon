package de.metux.nebulon.store;

import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nebulon.util.Encoder;
import de.metux.nebulon.util.FileIO;
import de.metux.nebulon.util.Log;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CryptBlockStore implements ICryptBlockStore {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private static final String default_ciphertype = "Blowfish/ECB/PKCS5Padding";

	private IBlockStore blockstore;
	private boolean gzip = true;

	public CryptBlockStore(IBlockStore bs) {
		blockstore = bs;
	}

	public boolean delete(CryptScore score) throws IOException {
		return blockstore.delete(score.getScore());
	}

	public CryptScore put(byte[] data) throws IOException, GeneralSecurityException {
		return put(data, default_ciphertype);
	}

	public CryptScore put(byte[] data, String ciphertype) throws IOException, GeneralSecurityException {

		byte[] key = Score.computeKey(data);

		Log.debug("CryptScore::put() innerkey="+FileIO.byteArray2Hex(key));

		long start = System.nanoTime();
		Cipher cipher = Cipher.getInstance(ciphertype);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ciphertype));
		byte[] crypted = cipher.doFinal(gzip ? Encoder.encode_gzip(data) : data);
		long end = System.nanoTime();

		Log.debug("CryptScore::put() encryption time: "+(end-start));

		return new CryptScore(
			blockstore.put(crypted),
			(gzip ? ciphertype+"/GZip" : ciphertype),
			key
		);
	}

	public byte[] get(CryptScore score) throws IOException, GeneralSecurityException {
		byte[] crypted = blockstore.get(score.getScore());
		if (crypted == null) {
			Log.info("CryptBlockStore::get() crypted block not found: "+score.getScore());
			return null;
		}

		String ct = score.getKey().cipher;
		if (ct.endsWith("/GZip")) {
			Cipher cipher = Cipher.getInstance(ct.substring(0, ct.length()-5));
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(score.getKey().key, score.getKey().cipher));
			return Encoder.decode_gzip(cipher.doFinal(crypted));
		} else {
			Cipher cipher = Cipher.getInstance(score.getKey().cipher);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(score.getKey().key, score.getKey().cipher));
			return cipher.doFinal(crypted);
		}
	}
}
