package plugins.CENO.FreenetInterface;

import java.io.IOException;
import java.util.HashMap;

import plugins.CENO.FreenetInterface.ConnectionOverview.NodeConnections;
import freenet.client.FetchException;
import freenet.client.FetchResult;
import freenet.client.InsertBlock;
import freenet.client.InsertException;
import freenet.client.async.ClientGetCallback;
import freenet.client.async.ClientGetter;
import freenet.client.async.ClientPutCallback;
import freenet.client.async.USKCallback;
import freenet.keys.FreenetURI;
import freenet.keys.USK;
import freenet.node.RequestClient;
import freenet.support.api.Bucket;

public interface FreenetInterface {

	FetchResult fetchURI(FreenetURI uri) throws FetchException;
	ClientGetter localFetchURI(FreenetURI uri, ClientGetCallback callback) throws FetchException;
	ClientGetter fetchULPR(FreenetURI uri, ClientGetCallback callback) throws FetchException;
	boolean subscribeToUSK(USK origUSK, USKCallback cb);
	FreenetURI[] generateKeyPair();
	RequestClient getRequestClient();
	FreenetURI insertBlock(InsertBlock insert, boolean getCHKOnly, String filenameHint) throws InsertException;
	boolean insertFreesite(FreenetURI insertURI, String docName, String content, ClientPutCallback cb) throws IOException, InsertException;
	FreenetURI insertBundleManifest(FreenetURI insertURI, String content, String defaultName, ClientPutCallback cb) throws IOException, InsertException;
	Bucket makeBucket(int length) throws IOException;
	FreenetURI insertManifest(FreenetURI insertURI, HashMap<String, Object> bucketsByName, String defaultName, short priorityClass) throws InsertException;
	NodeConnections getConnections();
	boolean copyAccprops(String freemailAccount);
	boolean sendFreemail(String freemailFrom, String freemailTo[], String subject, String content, String password);
	String[] getUnreadMailsSubject(String freemail, String password, String inboxFolder, boolean shouldDelete);
	String[] getMailsContentFrom(String freemail, String freemailFrom, String password, String mailFolder);
	boolean setRandomNextMsgNumber(String freemailAccount, String freemailTo);
	boolean clearOutboxLog(String freemailAccount, String identityFrom);
	boolean clearOutboxMessages(String freemailAccount, String freemailTo);
}
