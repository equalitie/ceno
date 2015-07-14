package plugins.CENO.Client;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import net.minidev.json.JSONObject;
import plugins.CENO.CENOErrCode;
import plugins.CENO.CENOException;
import plugins.CENO.URLtoUSKTools;
import plugins.CENO.Client.ULPRManager.ULPRStatus;
import freenet.client.FetchException;
import freenet.client.FetchException.FetchExceptionMode;
import freenet.keys.FreenetURI;
import freenet.pluginmanager.PluginHTTPException;
import freenet.support.Base64;
import freenet.support.IllegalBase64Exception;
import freenet.support.Logger;
import freenet.support.api.HTTPRequest;

public class LookupHandler extends AbstractCENOClientHandler {

	public String handleHTTPGet(HTTPRequest request) throws PluginHTTPException {
		boolean clientIsHtml = isClientHtml(request);

		String urlParam = request.getParam("url", "");
		if (urlParam.isEmpty()) {
			if (clientIsHtml) {
				return new CENOException(CENOErrCode.LCS_HANDLER_URL_INVALID).getMessage();
			}
			return returnErrorJSON(new CENOException(CENOErrCode.LCS_HANDLER_URL_INVALID));
		}

		if (!clientIsHtml) {
			try {
				urlParam = Base64.decodeUTF8(urlParam);
			} catch (IllegalBase64Exception e) {
				return returnErrorJSON(new CENOException(CENOErrCode.LCS_HANDLER_URL_INVALID));
			}
		}

		try {
			urlParam = URLtoUSKTools.validateURL(urlParam);
		} catch (MalformedURLException e) {
			if (clientIsHtml) {
				return new CENOException(CENOErrCode.LCS_HANDLER_URL_INVALID).getMessage();
			} else {
				return returnErrorJSON(new CENOException(CENOErrCode.LCS_HANDLER_URL_INVALID));
			}
		}

		FreenetURI calculatedUSK = null;
		try {
			calculatedUSK = URLtoUSKTools.computeUSKfromURL(urlParam, CENOClient.bridgeKey);
		} catch (Exception e) {
			if (clientIsHtml) {
				return new CENOException(CENOErrCode.LCS_HANDLER_URL_INVALID).getMessage();
			} else {
				return returnErrorJSON(new CENOException(CENOErrCode.LCS_HANDLER_URL_INVALID));
			}
		}

		String localFetchResult = localCacheLookup(calculatedUSK);

		if (localFetchResult == null) {
			ULPRStatus urlULPRStatus = ULPRManager.lookupULPR(urlParam);
			RequestSender.requestFromBridge(urlParam);
			if (urlULPRStatus == ULPRStatus.failed) {
				if (clientIsHtml) {
					return printStaticHTMLReplace("Resources/requestedFromBridge.html", "[urlRequested]", urlParam);
				} else {
					JSONObject jsonResponse = new JSONObject();
					jsonResponse.put("complete", true);
					jsonResponse.put("found", false);
					return jsonResponse.toJSONString();
				}
			} else {
				if (clientIsHtml) {
					return printStaticHTMLReplace("Resources/sentULPR.html", "[urlRequested]", urlParam);
				} else {
					JSONObject jsonResponse = new JSONObject();
					jsonResponse.put("complete", false);
					return jsonResponse.toJSONString();
				}
			}
		} else {
			if (clientIsHtml) {
				return localFetchResult;
			} else {
				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("complete", true);
				jsonResponse.put("found", true);
				jsonResponse.put("bundle", localFetchResult);
				return jsonResponse.toJSONString();
			}
		}
	}

	private String localCacheLookup(FreenetURI calculatedUSK) {
		if (calculatedUSK.getSuggestedEdition() < 0) {
			calculatedUSK = calculatedUSK.setSuggestedEdition(0);
		}

		ClientGetSyncCallback getSyncCallback = new ClientGetSyncCallback();
		String fetchResult = null;
		try {
			CENOClient.nodeInterface.localFetchURI(calculatedUSK, getSyncCallback);
			fetchResult = getSyncCallback.getResult(45L, TimeUnit.SECONDS);
		} catch (FetchException e) {
			if (e.getMode() == FetchExceptionMode.PERMANENT_REDIRECT) {
				fetchResult = localCacheLookup(e.newURI);
			} else if (e.isFatal()) {
				Logger.warning(this, "Fatal fetch exception while looking in the local cache for USK: " + calculatedUSK + " Exception: " + e.getMessage());
				//TODO Throw custom CENOException
			} else {
				Logger.error(this, "Unhandled exception while looking in the local cache for USK: " + calculatedUSK + " Exception: " + e.getMessage());
			}
		}
		return fetchResult;
	}

	public String handleHTTPPost(HTTPRequest request)
			throws PluginHTTPException {
		return "LookupHandler: POST request received";
	}

}