package ti.geoloqi.proxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.json.JSONArray;
import org.json.JSONObject;

import ti.geoloqi.GeoloqiModule;
import ti.geoloqi.GeoloqiValidations;
import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;
import ti.geoloqi.proxy.common.OnRunApiRequestListenerImpl;

import com.geoloqi.android.sdk.LQSession;

/**
 * Wrapper around the geoloqi LQSession class which is useful for making GeoLoqi
 * Web Service APIs calls.
 * 
 * @see com.geoloqi.android.sdk.LQSession
 */
@Kroll.proxy(creatableInModule = GeoloqiModule.class)
public class LQSessionProxy extends KrollProxy {
	private static final String LCAT = LQSessionProxy.class.getSimpleName();
	private String key;
	private String secret;
	private String lqC2DMSender = "";
	private LQSession session;
	// Session Proxy Constants
	private final String API_KEY = "apiKey";
	private final String API_SECRET = "apiSecret";
	private final String REQUESTTYPE_GET = "GET";
	private final String REQUESTTYPE_POST = "POST";

	// Kroll Proxy constants
	@Kroll.constant
	public static final String ISO_8601_DATE = LQSession.ISO_8601_DATE;
	@Kroll.constant
	public static final String EXTRA_USERNAME = LQSession.EXTRA_USERNAME;
	@Kroll.constant
	public static final String EXTRA_PASSWORD = LQSession.EXTRA_PASSWORD;
	@Kroll.constant
	public static final String EXTRA_EMAIL = LQSession.EXTRA_EMAIL;
	@Kroll.constant
	public static final String EXTRA_ACCESS_TOKEN = LQSession.EXTRA_ACCESS_TOKEN;
	@Kroll.constant
	public static final String EXTRA_IS_ANONYMOUS = LQSession.EXTRA_IS_ANONYMOUS;
	@Kroll.constant
	public static final String EXTRA_ERROR = LQSession.EXTRA_ERROR;
	@Kroll.constant
	public static final String EXTRA_ERROR_DESCRIPTION = LQSession.EXTRA_ERROR_DESCRIPTION;
	@Kroll.constant
	public static final String EXTRA_GRANT_TYPE = LQSession.EXTRA_GRANT_TYPE;
	@Kroll.constant
	public static final String DEVICE_ID_FIELD_NAME = LQSession.DEVICE_ID_FIELD_NAME;
	@Kroll.constant
	public static final String MAC_ADDRESS_FIELD_NAME = LQSession.MAC_ADDRESS_FIELD_NAME;
	@Kroll.constant
	public static final String C2DM_ID_FIELD_NAME = LQSession.C2DM_ID_FIELD_NAME;
	@Kroll.constant
	public static final String API_URL_BASE = LQSession.API_URL_BASE;
	@Kroll.constant
	public static final String API_PATH_USER_CREATE_ANON = LQSession.API_PATH_USER_CREATE_ANON;
	@Kroll.constant
	public static final String API_PATH_USER_CREATE = LQSession.API_PATH_USER_CREATE;
	@Kroll.constant
	public static final String API_PATH_LOCATION_UPDATE = LQSession.API_PATH_LOCATION_UPDATE;
	@Kroll.constant
	public static final String API_PATH_SET_C2DM_TOKEN = LQSession.API_PATH_SET_C2DM_TOKEN;
	@Kroll.constant
	public static final String API_PATH_OAUTH_TOKEN = LQSession.API_PATH_OAUTH_TOKEN;

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
	 * Creates new LQSession object
	 */
	private void createLQSession() {
		session = new LQSession(this.getActivity(), key, secret, lqC2DMSender);
	}

	/**
	 * This method checks if the callback object provided is of type
	 * HashMap<String,KrollFunction> or not
	 * 
	 * @param callback Object passed by developer
	 * @return true if valid object is received else false
	 */
	private boolean checkCallbackObject(Object callback) {
		boolean status = false;
		if (callback != null && callback instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) callback;
			Iterator<?> itcallback = map.keySet().iterator();
			Object key = null, value = null;
			int counter = map.size();
			if (map.size() == counter) {
				while (itcallback.hasNext()) {
					key = itcallback.next();
					value = map.get(key);
					if (value instanceof KrollFunction) {
						counter--;
					}
				}
				if (counter == 0) {
					status = true;
				}
			}
		}

		if (!status) {
			GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.SES_INVALID_CALLBK_CODE, GeoloqiValidations.SES_INVALID_CALLBK_DESC));
		}
		return status;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.appcelerator.kroll.KrollProxy#handleCreationDict(org.appcelerator
	 * .kroll.KrollDict)
	 */
	@Override
	public void handleCreationDict(KrollDict dict) {
		MLog.d(LCAT, "Inside handleCreationDict");
		super.handleCreationDict(dict);
		if (dict.containsKey(API_KEY)) {
			key = dict.getString(API_KEY);
		}
		if (dict.containsKey(API_SECRET)) {
			secret = dict.getString(API_SECRET);
		}
		createLQSession();
	}

	/**
	 * Restore the saved user session.
	 * 
	 * @return whether the restore operation is successful or not.
	 */
	@Kroll.method
	public boolean restoreSavedSession() {
		MLog.d(LCAT, "Inside restoreSavedSession");
		boolean retVal = false;
		if (!isSessionNull()) {
			retVal = session.restoreSavedSession();
		}
		return retVal;
	}

	/**
	 * Determine if the device has been registered for C2DM.
	 * 
	 * @return C2DM enabled or not.
	 */
	@Kroll.method
	public boolean isPushEnabled() {
		MLog.d(LCAT, "Inside isPushEnabled");
		boolean retVal = false;
		if (!isSessionNull()) {
			retVal = session.isPushEnabled();
		}
		return retVal;
	}

	/**
	 * Register or unregister for C2DM.
	 * 
	 * @param register or unregister for C2DM
	 */
	@Kroll.method
	public void setPushEnabled(boolean enable) {
		MLog.d(LCAT, "Inside setPushEnabled");
		if (!isSessionNull()) {
			session.setPushEnabled(enable);
		}
	}

	/**
	 * Get the username of the account associated with this session.
	 * 
	 * @return username
	 */
	@Kroll.method
	public String getUSerName() {
		MLog.d(LCAT, "Inside getUSerName");
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
	 * Perform an asynchronous HttpRequest to create a new anonymous user
	 * account.
	 * 
	 * @param JSON object
	 * @param callbackMap JSON object containing callback.
	 */
	@Kroll.method
	public void createAnonymousUserAccount(JSONObject object, Object callback) {
		MLog.d(LCAT, "Inside createAnonymousUserAccount");
		if (checkCallbackObject(callback) && !isSessionNull()) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			session.createAnonymousUserAccount(new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap));
		}
	}

	/**
	 * Perform an asynchronous HttpRequest to create a new user account and
	 * update the session with the new account credentials.
	 * 
	 * @param userName for the account
	 * @param email for the account
	 * @param callbackMap JSON object containing callback
	 */
	@Kroll.method
	public void createAccountWithUsername(String userName, String email, Object callback) throws Exception {
		MLog.d(LCAT, "Inside createAccountWithUsername");
		if (checkCallbackObject(callback) && !isSessionNull()) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			session.createAccountWithUsername(userName, email, new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap));
		}
	}

	/**
	 * Perform an asynchronous request to exchange a user's username and
	 * password for an OAuth access token.
	 * 
	 * @param userName account username
	 * @param password account password
	 * @param callbackMap JSON object containing callback
	 */
	@Kroll.method
	public void authenticateUser(String userName, String password, Object callback) {
		MLog.d(LCAT, "Inside authenticateUser");
		if (checkCallbackObject(callback) && !isSessionNull()) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			session.authenticateUser(userName, password, new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap));
		}
	}

	/**
	 * Perform an asynchronous request to send the device's C2DM registration
	 * token to the server.
	 * 
	 * @param token C2DM registration token
	 * @param callbackMap JSON object containing callback
	 */
	@Kroll.method
	public void registerDeviceToken(String token, Object callback) {
		MLog.d(LCAT, "Inside registerDeviceToken");
		if (checkCallbackObject(callback) && !isSessionNull()) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			session.sendC2dmToken(token, new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap));
		}
	}

	/**
	 * Send a GET request to the Geoloqi API.
	 * 
	 * @param path API path
	 * @param callbackMap JSON object containing callback
	 */
	@Kroll.method
	public void runGetRequest(String path, Object callback) {
		MLog.d(LCAT, "Inside runGetRequest");
		if (checkCallbackObject(callback) && !isSessionNull()) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			session.runGetRequest(path, new OnRunApiRequestListenerImpl(this.getKrollObject(), callbackMap));
		}
	}

	/**
	 * Send a POST request to the Geoloqi API
	 * 
	 * @param path API path
	 * @param json object containing post parameters
	 * @param callbackMap JSON object containing callback
	 */
	@Kroll.method
	public void runPostRequestWithJSONObject(String path, Object json, Object callback) {
		MLog.d(LCAT, "Inside runPostRequestWithJSONObject");
		if (checkCallbackObject(callback) && !isSessionNull()) {
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
	 * @param path API path
	 * @param json array of object containing post parameters
	 * @param callbackMap JSON object containing callback
	 */
	@Kroll.method
	public void runPostRequestWithJSONArray(String path, Object json, Object callback) {
		MLog.d(LCAT, "Inside runPostRequestWithJSONArray");
		if (checkCallbackObject(callback) && !isSessionNull()) {
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
	 * @param api_name name of the API
	 * @param httpVerb type of the request(GET or POST)
	 * @param extraParameter JSON object containing data for making the request
	 * @param callbackMap JSON object containing callback
	 */
	@Kroll.method
	public void runAPIRequest(String api_name, String httpVerb, Object extraParameter, Object callback) {
		MLog.d(LCAT, "Inside runAPIRequest");
		if (checkCallbackObject(callback) && !isSessionNull()) {
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
	 * @param time Unix timestamp
	 * @return ISO 8601 date String
	 */
	@Kroll.method
	public String formatTimeStamp(long time) {
		MLog.d(LCAT, "Inside formatTimeStamp");
		return LQSession.formatTimestamp(time);
	}
}
