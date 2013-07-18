package de.metux.nebulon.service;

import de.metux.nebulon.base.IBlockStore;
import de.metux.nebulon.base.Score;
import de.metux.nanoweb.core.IHandler;
import de.metux.nanoweb.core.IRequest;
import de.metux.nanoweb.core.Log;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * simple fileserver handler.
 *
 * just looks for the requested file request path under the
 * root given in constructor
 */
public class BlockStoreHandler implements IHandler {

	private static final String logname = "blockstore";

	private IBlockStore blockstore;

	/**
	 * constructor
	 *
	 * @param bs	blockstore instance
	 */
	public BlockStoreHandler(IBlockStore bs) {
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

		Log.debug("fileserver", "Score: "+q);

		Score score = Score.parse(q);

		if (score == null) {
			Log.error(logname, "Failed to parse score: "+q);
			request.replyStatus(IRequest.status_not_found, "File not found");
			request.replyBody("Failed to parse score: "+q+"\n");
			return true;
		}

		byte[] data = blockstore.get(score);

		if (data == null) {
			Log.error(logname, "Block not found: "+q);
			request.replyStatus(IRequest.status_not_found, "File not found");
			request.replyBody("Block not found: "+q+"\n");
			return true;
		}

		request.replyHeader(IRequest.header_content_length, String.valueOf(data.length));
		request.replyHeader("X-Nebulon-Score: ",q);
		request.replyBody(data);

		return true;
	}
}
