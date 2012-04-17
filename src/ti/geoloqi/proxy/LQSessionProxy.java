package ti.geoloqi.proxy;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.json.JSONArray;
import org.json.JSONObject;

import ti.geoloqi.GeoloqiModule;
import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;
import ti.geoloqi.proxy.common.OnRunApiRequestListenerImpl;
import ti.geoloqi.proxy.common.ProxyConstants;

import com.geoloqi.android.sdk.LQSession;

@Kroll.proxy(creatableInModule = GeoloqiModule.class)
public class LQSessionProxy extends KrollProxy {
	private static final String LCAT = LQSessionProxy.class.getSimpleName();
	private String KEY;
	private String SECRET;
	private String LQ_C2DM_SENDER = "";
	private LQSession session;

	public LQSessionProxy() {
		super();
		MLog.d(LCAT, "Inside LQSessionProxy");
	}

	public LQSessionProxy(LQSession session) {
		super();
		this.session = session;
		MLog.d(LCAT, "Inside LQSessionProxy(LQSession session)");
	}

	private void createLQSession() {
		session = new LQSession(this.getActivity(), KEY, SECRET, LQ_C2DM_SENDER);
	}

	public LQSession getSession() {
		return this.session;
	}

	@Override
	public void handleCreationDict(KrollDict dict) {
		MLog.d(LCAT, "Inside handleCreationDict");
		super.handleCreationDict(dict);
		if (dict.containsKey(ProxyConstants.KEY)) {
			KEY = dict.getString(ProxyConstants.KEY);
		}
		if (dict.containsKey(ProxyConstants.SECRET)) {
			SECRET = dict.getString(ProxyConstants.SECRET);
		}
		createLQSession();
	}

	@Kroll.method
	public boolean restoreSavedSession() {
		MLog.d(LCAT, "Inside restoreSavedSession");
		boolean retVal = false;
		if (!isSessionNull()) {
			retVal = session.restoreSavedSession();
		}
		return retVal;
	}

	@Kroll.method
	public boolean isPushEnabled() {
		MLog.d(LCAT, "Inside isPushEnabled");
		boolean retVal = false;
		if (!isSessionNull()) {
			retVal = session.isPushEnabled();
		}
		return retVal;
	}

	@Kroll.method
	public void setPushEnabled(boolean enable) {
		MLog.d(LCAT, "Inside setPushEnabled");
		if (!isSessionNull()) {
			session.setPushEnabled(enable);
		}
	}

	@Kroll.method
	public String getUSerName() {
		MLog.d(LCAT, "Inside getUSerName");
		String retVal = "";
		if (!isSessionNull()) {
			retVal = session.getUsername();
		}
		return retVal;
	}

	@Kroll.method
	public boolean isAnonymous() {
		MLog.d(LCAT, "Inside isAnonymous");
		boolean isAnonumus = false;
		if (!isSessionNull()) {
			isAnonumus = session.isAnonymous();
		}
		return isAnonumus;
	}

	@Kroll.method
	public void createAnonymousUserAccount() throws Exception {
		MLog.d(LCAT, "Inside createAnonymousUserAccount");
		if (!isSessionNull()) {
			session.createAnonymousUserAccount(new OnRunApiRequestListenerImpl(
					this));
		}
	}

	@Kroll.method
	public void createAccountWithUsername(String userName, String email)
			throws Exception {
		MLog.d(LCAT, "Inside createAccountWithUsername");
		if (!isSessionNull()) {
			session.createAccountWithUsername(userName, email,
					new OnRunApiRequestListenerImpl(this));
		}
	}

	@Kroll.method
	public void authenticateUser(String userName, String password)
			throws Exception {
		MLog.d(LCAT, "Inside authenticateUser");
		if (!isSessionNull()) {
			session.authenticateUser(userName, password,
					new OnRunApiRequestListenerImpl(this));
		}
	}

	@Kroll.method
	public void registerDeviceToken(String token) throws Exception {
		MLog.d(LCAT, "Inside registerDeviceToken");
		if (!isSessionNull()) {
			session.sendC2dmToken(token, new OnRunApiRequestListenerImpl(this));
		}
	}

	@Kroll.method
	public void runGetRequest(String path) throws Exception {
		MLog.d(LCAT, "Inside runGetRequest");
		if (!isSessionNull()) {
			session.runGetRequest(path, new OnRunApiRequestListenerImpl(this));
		}
	}

	@Kroll.method
	public void runPostRequestWithJSONObject(String path, Object json)
			throws Exception {
		MLog.d(LCAT, "Inside runPostRequestWithJSONObject");
		if (!isSessionNull()) {
			JSONObject jsonObject = MUtils.convertToJSON(json);
			if (jsonObject != null) {
				session.runPostRequest(path, jsonObject,
						new OnRunApiRequestListenerImpl(this));
			}
		}
	}

	@Kroll.method
	public void runPostRequestWithJSONArray(String path, Object json)
			throws Exception {
		MLog.d(LCAT, "Inside runPostRequestWithJSONArray");
		if (!isSessionNull()) {
			JSONArray jsonObjectArray = MUtils.convertToJSONArray(json);
			if (jsonObjectArray != null) {
				session.runPostRequest(path, jsonObjectArray,
						new OnRunApiRequestListenerImpl(this));
			}
		}
	}

	@Kroll.method
	public void runAPIRequest(String api_name, String httpVerb,
			Object extraParameter) throws Exception {
		MLog.d(LCAT, "Inside runAPIRequest");
		if (httpVerb.equals(ProxyConstants.REQUESTTYPE_GET)) {
			if (!isSessionNull()) {
				session.runGetRequest(api_name,
						new OnRunApiRequestListenerImpl(this));
			}
		} else if (httpVerb.equals(ProxyConstants.REQUESTTYPE_POST)) {
			if (!isSessionNull()) {
				JSONObject jsonObject = MUtils.convertToJSON(extraParameter);
				if (jsonObject != null) {
					session.runPostRequest(api_name, jsonObject,
							new OnRunApiRequestListenerImpl(this));
				}
			}
		}

	}

	@Kroll.method
	public String formatTimeStamp(long time) throws Exception {
		MLog.d(LCAT, "Inside formatTimeStamp");
		return LQSession.formatTimestamp(time);
	}

	private boolean isSessionNull() {
		if (session == null) {
			GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_VALIDATE,
					ProxyConstants.NULL_SESSION);
			return true;
		}

		return false;
	}

}
