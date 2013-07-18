package de.metux.nebulon.service;

import de.metux.nebulon.base.ICryptBlockStore;
import de.metux.nebulon.base.CryptScore;
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
public class CryptBlockStoreHandler implements IHandler {

	private static final String logname = "cryptblockstore";

	private ICryptBlockStore cryptblockstore;

	/**
	 * constructor
	 *
	 * @param bs	blockstore instance
	 */
	public CryptBlockStoreHandler(ICryptBlockStore cbs) {
		cryptblockstore = cbs;
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

			byte[] data = cryptblockstore.get(score);

			if (data == null) {
				Log.error(logname, "Block not found: "+q);
				request.replyStatus(IRequest.status_not_found, "File not found");
				request.replyBody("Block not found: "+q+"\n");
				return true;
			}

			request.replyHeader(IRequest.header_content_length, String.valueOf(data.length));
			request.replyHeader("X-Nebulon-Score: ",q);
			request.replyBody(data);
		} catch (IOException e) {
			Log.error(logname, "Failed to parse score: "+q);
			request.replyStatus(IRequest.status_not_found, "File not found");
			request.replyBody("Failed to parse score: "+q+"\n");
			return true;
		} catch (GeneralSecurityException e) {
			Log.error(logname, "Decryption failed: "+q+" "+e);
			request.replyStatus(IRequest.status_not_found, "Decryption failed");
			request.replyBody("Decryption failed: "+q+" "+e+"\n");
			return true;
		}

		return true;
	}
}
