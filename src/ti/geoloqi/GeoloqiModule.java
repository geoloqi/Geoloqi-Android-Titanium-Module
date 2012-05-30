package ti.geoloqi;

import java.util.HashMap;
import java.util.Map;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.TiApplication;

import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;
import ti.geoloqi.proxy.LQSessionProxy;
import ti.geoloqi.proxy.LQTrackerProxy;
import ti.geoloqi.proxy.common.LQBroadcastReceiverImpl;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.geoloqi.android.sdk.LQSession;
import com.geoloqi.android.sdk.LQSharedPreferences;
import com.geoloqi.android.sdk.service.LQService;
import com.geoloqi.android.sdk.service.LQService.LQBinder;

/**
 * This is a root module class of Geoloqi Titanium Android Module
 */
@Kroll.module(name = "Geoloqi", id = "ti.geoloqi")
public class GeoloqiModule extends KrollModule {
	private static final String LCAT = GeoloqiModule.class.getSimpleName();
	// Event Constants
	@Kroll.constant
	public static final String ON_VALIDATE = "onValidate";
	@Kroll.constant
	public static final String LOCATION_CHANGED = "onLocationChanged";
	@Kroll.constant
	public static final String LOCATION_UPLOADED = "onLocationUploaded";
	@Kroll.constant
	public static final String TRACKER_PROFILE_CHANGED = "onTrackerProfileChanged";
	@Kroll.constant
	public static final String PUSH_MESSAGE_RECEIVED = "onPushMessageReceived";

	// Class constants
	private final String USERNAME = "username";
	private final String EMAIL = "email";
	private final String ON_SUCCESS = "onSuccess";
	private final String ON_FAILURE = "onFailure";
	private final String CLIENT_ID = "clientId";
	private final String CLIENT_SECRET = "clientSecret";
	private final String TRACKING_PROFILE = "trackingProfile";
	private final String LOW_BATTERY_TRACKING = "lowBatteryTracking";
	private final String VIBRATE = "vibrate";
	private final String PUSH_EMAIL = "pushAccount";
	private final String PUSH_ICON = "pushIcon";
	private final String DEFAULT_TRACKING_PROFILE = "OFF";

	// Module instance
	private static GeoloqiModule module;
	private boolean moduleInitialized = false;

	// LQService variables
	private LQService mService;
	private boolean mBound;
	private KrollFunction onSuccessCallback;
	private KrollFunction onFailureCallback;
	private String initTrackerProfile = DEFAULT_TRACKING_PROFILE;

	// Handler
	private Handler handler = new Handler();

	// Broadcast receiver instance
	private LQBroadcastReceiverImpl locationBroadcastReceiver = new LQBroadcastReceiverImpl();
	private boolean addLocationBroadcastReceiver = true;
	
	// Debug variable
	public static boolean debug = false;

	// Session and Tracker proxies
	private LQSessionProxy session;
	private LQTrackerProxy tracker;

	/**
	 * Class Constructor
	 */
	public GeoloqiModule() {
		super();
		TiConfig.LOGD = debug;
		module = this;
	}

	/**
	 * This method returns the instance of the GeoloqiModule class internally
	 * used by module classes
	 * 
	 * @return GeoloqiModule object
	 */
	public static GeoloqiModule getInstance() {
		return module;
	}

	/*** Module Lifecycle methods ***/
	/**
	 * AppCreate event provided by Kroll
	 * 
	 * @param TiApplication
	 */
	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		MLog.d(LCAT, "inside onAppCreate");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.appcelerator.kroll.KrollModule#onResume(android.app.Activity)
	 */
	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);
		// Service Binding
		MLog.d(LCAT, "in onResume, activity is: " + activity);
		
		// Hack!
		session = new LQSessionProxy(new LQSession(activity));
		
		Intent intent = new Intent(activity, LQService.class);
		activity.bindService(intent, mConnection, 0);
		MLog.d(LCAT, "Attempting to bind to service....");

		// Registering Broadcast receiver
		if (addLocationBroadcastReceiver) {
			activity.getApplicationContext().registerReceiver(locationBroadcastReceiver,
					LQBroadcastReceiverImpl.getDefaultIntentFilter());
			MLog.d(LCAT, "Receiver Registered");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.appcelerator.kroll.KrollModule#onPause(android.app.Activity)
	 */
	@Override
	public void onPause(Activity activity) {
		super.onPause(activity);
		MLog.d(LCAT, "in onPause, activity is: " + activity);

		// Service unbinding
		if (mBound) {
			activity.unbindService(mConnection);
			mBound = false;
		}

		// Unregister Broadcast receiver
		if (addLocationBroadcastReceiver) {
			activity.getApplicationContext().unregisterReceiver(
					locationBroadcastReceiver);
			MLog.d(LCAT, "Receiver UnRegistered");
		}
	}

	/**** Module Exposed Methods ****/
	@Kroll.getProperty
	public KrollProxy getSession() {
		return session;
	}

	@Kroll.getProperty
	public KrollProxy getTracker() {
		return tracker;
	}

	/**
	 * This method is used to turn on/off debugging info.
	 * 
	 * @param value
	 */
	@Kroll.method
	public void setDebug(boolean value) {
		MLog.d(LCAT, "inside onAppCreate, value is: " + value);
		TiConfig.LOGD = value;
		debug = value;
	}

	/**
	 * This method is to initialize module
	 * 
	 * @param params
	 *            Parameters
	 * @param callback
	 *            Callback methods to be called
	 */
	@SuppressWarnings("unchecked")
	@Kroll.method
	public void init(Object params, Object callback) {
		MLog.d(LCAT, "in init");
		// Start service
		KrollDict paramsMap = new KrollDict((HashMap<String, Object>) params);
		Map<String, KrollFunction> callbackMap = null;

		String clientId = null, clientSecret = null, account = null, icon = null;
		boolean lowBatteryTracking = false;
		boolean vibrate = false;

		HashMap<String, String> mapExtras = new HashMap<String, String>(4);

		try {
			if (!MUtils.checkCallbackObject(callback)) {
				fireEvent(ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.SRV_CALLBACK_NA_CODE, GeoloqiValidations.SRV_CALLBACK_NA_DESC));
				return;
			} else {
				callbackMap = (HashMap<String, KrollFunction>) callback;
				if (callbackMap.containsKey(ON_SUCCESS)) {
					onSuccessCallback = callbackMap.get(ON_SUCCESS);
				}
				if (callbackMap.containsKey(ON_FAILURE)) {
					onFailureCallback = callbackMap.get(ON_FAILURE);
				}
			}
			// if no parameters are received
			if (paramsMap == null || paramsMap.isEmpty()) {
				onFailureCallback.call(krollObject, MUtils.generateErrorObject(GeoloqiValidations.SRV_INIT_PARAMS_EMPTY_CODE, GeoloqiValidations.SRV_INIT_PARAMS_EMPTY_DESC));
				return;
			}

			// check all parameters
			if (paramsMap.containsKey(CLIENT_ID)) {
				clientId = paramsMap.getString(CLIENT_ID);
				mapExtras.put(CLIENT_ID, clientId);
			} else {
				onFailureCallback.call(krollObject, MUtils.generateErrorObject(GeoloqiValidations.SRV_CLIENTID_NA_CODE, GeoloqiValidations.SRV_CLIENTID_NA_DESC));
				return;
			}
			if (paramsMap.containsKey(CLIENT_SECRET)) {
				clientSecret = paramsMap.getString(CLIENT_SECRET);
				mapExtras.put(CLIENT_SECRET, clientSecret);
			} else {
				onFailureCallback.call(krollObject, MUtils.generateErrorObject(GeoloqiValidations.SRV_CLIENTSECRET_NA_CODE, GeoloqiValidations.SRV_CLIENTSECRET_NA_DESC));
				return;
			}
			if (paramsMap.containsKey(TRACKING_PROFILE)) {
				initTrackerProfile = paramsMap.getString(TRACKING_PROFILE);
			}
			if (paramsMap.containsKey(LOW_BATTERY_TRACKING)) {
				lowBatteryTracking = paramsMap.getBoolean(LOW_BATTERY_TRACKING);
				mapExtras.put(LOW_BATTERY_TRACKING, String.valueOf(lowBatteryTracking));
			}
			if (paramsMap.containsKey(VIBRATE)) {
				vibrate = paramsMap.getBoolean(VIBRATE);
				mapExtras.put(VIBRATE, String.valueOf(vibrate));
			}
			if (paramsMap.containsKey(PUSH_EMAIL)) {
				account = paramsMap.getString(PUSH_EMAIL);
				mapExtras.put(PUSH_EMAIL, account);
			}
			if (paramsMap.containsKey(PUSH_ICON)) {
				icon = paramsMap.getString(PUSH_ICON);
				mapExtras.put(PUSH_ICON, icon);
			}

			// set profile in tracker
			tracker = LQTrackerProxy.getInstance(getActivity());

			// start service
			startService(LQService.ACTION_DEFAULT, mapExtras);
		} catch (Exception e) {
			onFailureCallback.call(this.krollObject, MUtils.generateErrorObject(GeoloqiValidations.SRV_INIT_FAILED_CODE, GeoloqiValidations.SRV_INIT_FAILED_DESC));
			e.printStackTrace();
		}
	}

	/*** Session Methods ***/
	/**
	 * Perform an asynchronous HttpRequest to create a new user account and
	 * update the session with the new account credentials.
	 * 
	 * @param params
	 *            JSON object containing username and password
	 * @param callbackMap
	 *            JSON object containing callback
	 */
	@Kroll.method
	public void createUser(Object params, Object callback) throws Exception {
		MLog.d(LCAT, "Inside createAccountWithUsername");
		Map<String, String> mapParams = (HashMap<String, String>) params;
		String username = null, email = null;
		if (mapParams.containsKey(USERNAME)) {
			username = mapParams.get(USERNAME);
		}
		if (mapParams.containsKey(EMAIL)) {
			email = mapParams.get(EMAIL);
		}
		if (!isSessionNull()) {
			session.createUserAccount(username, email, callback, handler, getActivity());
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
	 */
	@Kroll.method
	public void createAnonymousUser(Object object, Object callback) {
		MLog.d(LCAT, "Inside createAnonymousUserAccount");
		if (!isSessionNull()) {
			session.createAnonymousUserAccount(callback, handler, getActivity());
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
	 */
	@Kroll.method
	public void authenticateUser(String userName, String password, Object callback) {
		MLog.d(LCAT, "Inside Module authenticateUser");
		if (!isSessionNull()) {
			HashMap<String, KrollFunction> callbackMap = (HashMap<String, KrollFunction>) callback;
			session.requestSession(userName, password, callback, handler, getActivity());
		}
	}

	/**
	 * Check session nullability
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

	/******** Service Methods *********/
	/**
	 * This method exposes functionality on module object to get the current
	 * value of the low battery tracking preference.
	 * 
	 * @return boolean Preference value.
	 */
	@Kroll.method
	public boolean isLowBatteryTrackingEnabled() {
		MLog.d(LCAT, "in isLowBatteryTrackingEnabled");
		return LQSharedPreferences.isLowBatteryTrackingEnabled(getActivity());
	}

	/**
	 * This method exposes functionality on module object to enable low battery
	 * tracking
	 */
	@Kroll.method
	public void enableLowBatteryTracking() {
		MLog.d(LCAT, "in enableLowBatteryTracking");
		LQSharedPreferences.enableLowBatteryTracking(getActivity());
	}

	/**
	 * This method exposes functionality on module object to disable low battery
	 * tracking
	 */
	@Kroll.method
	public void disableLowBatteryTracking() {
		MLog.d(LCAT, "in disableLowBatteryTracking");
		LQSharedPreferences.disableLowBatteryTracking(getActivity());
	}

	// method to check service availability,
	// if null then throws validation error
	private boolean checkService() {
		if (mService == null) {
			fireEvent(ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.SRV_SERVICE_NA_CODE, GeoloqiValidations.SRV_SERVICE_NA_CODE));
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Starts geoloqi tracking service
	 * 
	 * @param activity
	 *            current activity
	 * @param action
	 * @see com.geoloqi.android.sdk.service.LQService
	 * @param extraParams
	 *            JSON Object
	 */
	private void startService(String action, Map<String, String> mapExtras) {
		Activity activity = getActivity();

		Intent intent = new Intent(activity, LQService.class);
		intent.setAction(action);

		if (mapExtras.containsKey(CLIENT_ID)) {
			LQSharedPreferences.setClientId(activity, mapExtras.get(CLIENT_ID));
		}
		if (mapExtras.containsKey(CLIENT_SECRET)) {
			LQSharedPreferences.setClientSecret(activity, mapExtras.get(CLIENT_SECRET));
		}
		if (mapExtras.containsKey(LOW_BATTERY_TRACKING)) {
			if (Boolean.getBoolean(mapExtras.get(LOW_BATTERY_TRACKING))) {
				LQSharedPreferences.enableLowBatteryTracking(activity);
			} else {
				LQSharedPreferences.disableLowBatteryTracking(activity);
			}
		}
		if (mapExtras.containsKey(VIBRATE)) {
			if (Boolean.getBoolean(mapExtras.get(VIBRATE))) {
				LQSharedPreferences.enableVibration(activity);
			} else {
				LQSharedPreferences.disableVibration(activity);
			}
		}
		if (mapExtras.containsKey(PUSH_EMAIL)) {
			LQSharedPreferences.setPushAccount(activity, mapExtras.get(PUSH_EMAIL));
		}
		if (mapExtras.containsKey(PUSH_ICON)) {
			LQSharedPreferences.setPushIcon(activity, mapExtras.get(PUSH_ICON));
		}

		activity.startService(intent);

		MLog.d(LCAT, "Starting Service");
	}

	// Set session and tracker and call onSuccessCallback for init method
	private void setInitValues(LQSession pSession) {
		if (pSession != null) {
			session = new LQSessionProxy(pSession);
			tracker.setProfile(initTrackerProfile);
			moduleInitialized = true;
			onSuccessCallback.call(krollObject, new HashMap<String, String>(1));
		} else {
			onFailureCallback.call(krollObject, new HashMap<String, String>(1));
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MLog.d(LCAT, "onServiceConnected");
			try {
				LQBinder binder = (LQBinder) service;
				mService = binder.getService();
				mBound = true;

				if (!moduleInitialized) {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							setInitValues(mService.getSession());
						}
					}, 5000);
				}

				// Display the current tracker profile
				MLog.d(LCAT, "onServiceConnected->" + mService.getTracker().getProfile().toString());
			} catch (ClassCastException e) {
				MLog.e(LCAT, e.toString());
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			MLog.e(LCAT, "onServiceDisconnected");
			mBound = false;
		}
	};
}
