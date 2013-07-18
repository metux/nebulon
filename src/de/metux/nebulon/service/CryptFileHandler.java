package de.metux.nebulon.service;

import de.metux.nebulon.base.CryptScore;
import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.MalformedCryptScoreException;
import de.metux.nebulon.fs.CryptFileReader;
import de.metux.nanoweb.core.IHandler;
import de.metux.nanoweb.core.IRequest;
import de.metux.nanoweb.core.Log;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;

/**
 * simple fileserver handler.
 *
 * just looks for the requested file request path under the
 * root given in constructor
 */
public class CryptFileHandler implements IHandler {

	private static final String logname = "cryptblockstore";

	private ICryptBlockStore cryptblockstore;
	private IBlockStore blockstore;

	/**
	 * constructor
	 *
	 * @param bs	blockstore instance
	 */
	public CryptFileHandler(IBlockStore bs, ICryptBlockStore cbs) {
		cryptblockstore = cbs;
		blockstore = bs;
	}

	/**
	 * handle the request
	 *
	 * @param request	the request to be handled
	 * @result		returns true when request had been handled
	 */
	public boolean handle(IRequest request)
	throws IOException {
		String q = URLDecoder.decode(request.getPath(),"UTF-8");
		while (q.startsWith("/"))
			q = q.substring(1);

		Log.debug(logname, "Score: "+q);

		try {
			CryptScore score = CryptScore.parse(q);
			CryptFileReader reader = new CryptFileReader(blockstore, cryptblockstore, score);
			byte[] data;
			Log.debug(logname, "Got encrypted file: "+q);
			request.replyHeader("X-Nebulon-Score: ",q);
			request.replyStatus(IRequest.status_not_found, "OK");
			while ((data = reader.read()) != null) {
				request.replyBody(data);
			}
		} catch (MalformedCryptScoreException e) {
			Log.warning(logname, "cryptscore parsing failed: "+q+" ",e);
			request.replyStatus(IRequest.status_not_found, "Decryption failed");
			request.replyBody("cryptscore parsing failed: "+q+" "+e+"\n");
			return true;
		} catch (GeneralSecurityException e) {
			Log.warning(logname, "Decryption failed: "+q+" ",e);
			request.replyStatus(IRequest.status_not_found, "Decryption failed");
			request.replyBody("Decryption failed: "+q+" "+e+"\n");
			return true;
		} catch (IOException e) {
			Log.warning(logname, "Decryption failed: "+q+" ",e);
			request.replyStatus(IRequest.status_not_found, "Decryption failed");
			request.replyBody("Decryption failed: "+q+" "+e+"\n");
			return true;
		}

		return true;
	}
}
