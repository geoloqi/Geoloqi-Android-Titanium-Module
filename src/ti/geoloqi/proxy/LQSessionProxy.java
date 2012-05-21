package ti.geoloqi.proxy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ti.geoloqi.GeoloqiModule;
import ti.geoloqi.GeoloqiValidations;
import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;
import ti.geoloqi.proxy.common.OnRunApiRequestListenerImpl;
import android.content.Context;
import android.os.Handler;

import com.geoloqi.android.sdk.LQSession;

/**
 * Wrapper around the geoloqi LQSession class which is useful for making GeoLoqi
 * Web Service APIs calls.
 * 
 * @see com.geoloqi.android.sdk.LQSession
 */
@Kroll.proxy
public class LQSessionProxy extends KrollProxy {
	private static final String LCAT = LQSessionProxy.class.getSimpleName();
	private LQSession session;
	
	// Session Proxy Constants
	private final String REQUESTTYPE_GET = "GET";
	private final String REQUESTTYPE_POST = "POST";

	/**
	 * Class Constructor
	 */
	public LQSessionProxy() {
		super();
		MLog.d(LCAT, "Inside LQSessionProxy");
	}

	/**
	 * Overloaded Class Constructor
	 * 
	 * @param LQSession
	 */
	public LQSessionProxy(LQSession session) {
		super();
		this.session = session;
		MLog.d(LCAT, "Inside LQSessionProxy(LQSession session)");
	}

	/**
	 * This method checks if the callback object provided is of type
	 * HashMap<String,KrollFunction> or not
	 * 
	 * @param callback
	 *            Object passed by developer
	 * @return true if valid object is received else false
	 */
	private static boolean checkCallback(Object callback) {
		if (!MUtils.checkCallbackObject(callback)) {
			GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.SES_INVALID_CALLBK_CODE, GeoloqiValidations.SES_INVALID_CALLBK_DESC));
			return false;
		}
		return true;
	}

	/**
	 * Check session nullablity
	 * 
	 * @return session null or not
	 */
	private boolean isSessionNull() {
		if (session == null) {
			GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.SES_SESSION_NA_CODE, GeoloqiValidations.SES_SESSION_NA_DESC));
			return true;
		}

		return false;
	}

	/**
	 * Gets current session
	 * 
	 * @return LQSession
	 */
	public LQSession getSession() {
		return this.session;
	}

	/**
	 * Perform an asynchronous HttpRequest to create a new user account and
	 * update the session with the new account credentials.
	 * 
	 * @param userName
	 *            for the account
	 * @param email
	 *            for the account
	 * @param callbackMap
	 *            JSON object containing callback
	 * @param handler
	 *            Handler to run the callback
	 * @param context
	 *            app context
	 */
	public void createUserAccount(String userName, String email, Object callback,
			Handler handler, Context context) throws Exception {
		MLog.d(LCAT, "Inside Session Proxy createAccountWithUsername");
		if (checkCallback(callback)) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			session.createUserAccount(userName, email,
					new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap), handler, context);
		}
	}

	/**
	 * Perform an asynchronous HttpRequest to create a new anonymous user
	 * account.
	 * 
	 * @param JSON
	 *            object
	 * @param callbackMap
	 *            JSON object containing callback.
	 * @param handler
	 *            Handler to run the callback
	 * @param context
	 *            app context
	 */
	public void createAnonymousUserAccount(Object callback, Handler handler,
			Context context) {
		MLog.d(LCAT, "Inside Session Proxy createAnonymousUserAccount");
		if (checkCallback(callback)) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			session.createAnonymousUserAccount(
					new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap), handler, context);
		}
	}

	/**
	 * Perform an asynchronous request to exchange a user's username and
	 * password for an OAuth access token.
	 * 
	 * @param userName
	 *            account username
	 * @param password
	 *            account password
	 * @param callbackMap
	 *            JSON object containing callback
	 * @param handler
	 *            Handler to run the callback
	 * @param context
	 *            app context
	 */
	public void requestSession(String userName, String password, Object callback,
			Handler handler, Context context) {
		MLog.d(LCAT, "Inside Session Proxy authenticateUser");
		if (checkCallback(callback)) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			session.requestSession(userName, password,
					new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap), handler, context);
		}
	}

	/**
	 * Get the access token of the account associated with this session.
	 * 
	 * @return token
	 */
	@Kroll.method
	public String getAccessToken() {
		MLog.d(LCAT, "Inside getAccessToken");
		String retVal = "";
		if (!isSessionNull()) {
			retVal = session.getAccessToken();
		}
		return retVal;
	}

	/**
	 * Get the user id of the account associated with this session.
	 * 
	 * @return user id
	 */
	@Kroll.method
	public String getUserId() {
		MLog.d(LCAT, "Inside getUserId");
		String retVal = "";
		if (!isSessionNull()) {
			retVal = session.getUserId();
		}
		return retVal;
	}

	/**
	 * Get the username of the account associated with this session.
	 * 
	 * @return username
	 */
	@Kroll.method
	public String getUsername() {
		MLog.d(LCAT, "Inside getUsername");
		String retVal = "";
		if (!isSessionNull()) {
			retVal = session.getUsername();
		}
		return retVal;
	}

	/**
	 * Determine if the session is currently configured with an anonymous user.
	 * 
	 * @return user anonymous or not.
	 */
	@Kroll.method
	public boolean isAnonymous() {
		MLog.d(LCAT, "Inside isAnonymous");
		boolean isAnonumus = false;
		if (!isSessionNull()) {
			isAnonumus = session.isAnonymous();
		}
		return isAnonumus;
	}

	/**
	 * Send a GET request to the Geoloqi API.
	 * 
	 * @param path
	 *            API path
	 * @param callbackMap
	 *            JSON object containing callback
	 * @throws JSONException
	 * @throws UnsupportedEncodingException
	 */
	@Kroll.method
	public void getRequest(String path, Object params, Object callback) throws JSONException, UnsupportedEncodingException {
		MLog.d(LCAT, "Inside runGetRequest");
		if (checkCallback(callback) && !isSessionNull()) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			String key, value;
			Map<String, String> mapParams = (HashMap<String, String>) params;
			Iterator<String> itParams = mapParams.keySet().iterator();

			String paramName = null, paramValue = null;
			StringBuilder queryString = new StringBuilder(100);

			queryString.append(path).append("?");

			while (itParams.hasNext()) {
				paramName = itParams.next();
				paramValue = mapParams.get(paramName);

				queryString.append(paramName).append("=").append(URLEncoder.encode(paramValue, "UTF-8"));
			}

			MLog.d(LCAT, "get request PATH =" + queryString.toString());

			session.runGetRequest(queryString.toString(), new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap));
		}
	}

	/**
	 * Send a POST request to the Geoloqi API.
	 * 
	 * @param path
	 *            API path
	 * @param params
	 *            parameters in form of JSONObject or JSONArray
	 * @param callbackMap
	 *            JSON object containing callback
	 */
	@SuppressWarnings("rawtypes")
	@Kroll.method
	public void postRequest(String path, Object params, Object callback) {
		MLog.d(LCAT, "Inside post");

		if (params instanceof HashMap) {
			runPostRequestWithJSONObject(path, params, callback);
		} else if (params instanceof Object[]) {
			runPostRequestWithJSONArray(path, params, callback);
		} else {
			MLog.e(LCAT, "in postRequest, Invalid params object, params : " + params);
		}

	}

	/**
	 * Send a POST request to the Geoloqi API
	 * 
	 * @param path
	 *            API path
	 * @param json
	 *            object containing post parameters
	 * @param callbackMap
	 *            JSON object containing callback
	 */
	private void runPostRequestWithJSONObject(String path, Object json, Object callback) {
		MLog.d(LCAT, "Inside runPostRequestWithJSONObject");
		if (checkCallback(callback) && !isSessionNull()) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			JSONObject jsonObject = MUtils.convertToJSON(json);
			if (jsonObject != null) {
				session.runPostRequest(path, jsonObject, new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap));
			}
		}
	}

	/**
	 * Send a POST request to the Geoloqi API
	 * 
	 * @param path
	 *            API path
	 * @param json
	 *            array of object containing post parameters
	 * @param callbackMap
	 *            JSON object containing callback
	 */
	private void runPostRequestWithJSONArray(String path, Object json, Object callback) {
		MLog.d(LCAT, "Inside runPostRequestWithJSONArray");
		if (checkCallback(callback) && !isSessionNull()) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			JSONArray jsonObjectArray = MUtils.convertToJSONArray(json);
			if (jsonObjectArray != null) {
				session.runPostRequest(path, jsonObjectArray, new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap));
			}
		}
	}

	/**
	 * Run HttpRequest.
	 * 
	 * @param httpVerb
	 *            type of the request(GET or POST)
	 * @param api_name
	 *            name of the API
	 * @param extraParameter
	 *            JSON object containing data for making the request
	 * @param callbackMap
	 *            JSON object containing callback
	 */
	@Kroll.method
	public void apiRequest(String httpVerb, String api_name, Object extraParameter, Object callback) {
		MLog.d(LCAT, "Inside runAPIRequest");
		if (checkCallback(callback) && !isSessionNull()) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;

			if (httpVerb.equals(REQUESTTYPE_GET)) {
				if (!isSessionNull()) {
					session.runGetRequest(api_name, new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap));
				}
			} else if (httpVerb.equals(REQUESTTYPE_POST)) {
				if (!isSessionNull()) {
					JSONObject jsonObject = MUtils.convertToJSON(extraParameter);
					if (jsonObject != null) {
						session.runPostRequest(api_name, jsonObject, new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap));
					}
				}
			}
		}
	}

	/**
	 * Format a Unix timestamp as an ISO 8601 date String.
	 * 
	 * @param time
	 *            Unix timestamp
	 * @return ISO 8601 date String
	 */
	public String formatTimeStamp(long time) {
		MLog.d(LCAT, "Inside formatTimeStamp");
		return LQSession.formatTimestamp(time);
	}

}
