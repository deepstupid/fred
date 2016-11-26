package freenet.support.compress;

import freenet.client.InsertException;
import freenet.client.async.ClientContext;
import freenet.client.async.ClientPutState;

public interface CompressJob {
	void tryCompress(ClientContext context) throws InsertException;
	void onFailure(InsertException e, ClientPutState c, ClientContext context);
}
