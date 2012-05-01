package ti.geoloqi;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.TiApplication;

import ti.geoloqi.common.MLog;
import ti.geoloqi.proxy.LQSessionProxy;
import ti.geoloqi.proxy.LQTrackerProxy;
import ti.geoloqi.proxy.common.LQBroadcastReceiverImpl;
import ti.geoloqi.service.LQServiceWrapper;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import com.geoloqi.android.sdk.service.LQService;

/**
 * This is a root module class of Geoloqi Titanium Android Module
 */
@Kroll.module(name = "Geoloqi", id = "ti.geoloqi")
public class GeoloqiModule extends KrollModule {
	private static final String LCAT = GeoloqiModule.class.getSimpleName();
	public static boolean debug = false;
	private LQServiceWrapper serviceWrapper = LQServiceWrapper.getInstance();
	private static GeoloqiModule module;
	private LQBroadcastReceiverImpl locationBroadcastReceiver = new LQBroadcastReceiverImpl();
	private boolean addLocationBroadcastReceiver = true;

	// Service Action Constants
	@Kroll.constant
	public static final String ACTION_AUTH_USER = "ACTION_AUTH_USER";
	@Kroll.constant
	public static final String ACTION_CREATE_ANONYMOUS_USER = "ACTION_CREATE_ANONYMOUS_USER";
	@Kroll.constant
	public static final String ACTION_CREATE_USER = "ACTION_CREATE_USER";
	@Kroll.constant
	public static final String ACTION_DEFAULT = "ACTION_DEFAULT";
	@Kroll.constant
	public static final String ACTION_SEND_C2DM_TOKEN = "ACTION_SEND_C2DM_TOKEN";

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
	public static final String ON_SERVICE_CONNECTED = "onServiceConnected";
	@Kroll.constant
	public static final String ON_SERVICE_DISCONNECTED = "onServiceDisconnected";

	/**
	 * Class Constructor
	 */
	public GeoloqiModule() {
		super();
		TiConfig.LOGD = debug;
		module = this;
	}

	/**
	 * AppCreate event provided by Kroll
	 * 
	 * @param TiApplication
	 */
	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		MLog.d(LCAT, "inside onAppCreate");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.appcelerator.kroll.KrollModule#onResume(android.app.Activity)
	 */
	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);
		MLog.d(LCAT, "in onResume, activity is: " + activity);
		Intent intent = new Intent(activity, LQService.class);
		activity.bindService(intent, serviceWrapper.getConnection(), 0);
		MLog.d(LCAT, "Attempting to bind to service....");
		if (addLocationBroadcastReceiver) {
			final IntentFilter filter = new IntentFilter();
			filter.addAction(LQBroadcastReceiverImpl.ACTION_TRACKER_PROFILE_CHANGED);
			filter.addAction(LQBroadcastReceiverImpl.ACTION_LOCATION_CHANGED);
			filter.addAction(LQBroadcastReceiverImpl.ACTION_LOCATION_UPLOADED);
			activity.getApplicationContext().registerReceiver(
					locationBroadcastReceiver, filter);
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
		if (serviceWrapper.isServiceBound()) {
			activity.unbindService(serviceWrapper.getConnection());
			serviceWrapper.setServiceBound(false);
		}
		if (addLocationBroadcastReceiver) {
			activity.getApplicationContext().unregisterReceiver(
					locationBroadcastReceiver);
			MLog.d(LCAT, "Receiver UnRegistered");
		}
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

	/**
	 * This method exposes functionality on module object to start the geoloqi
	 * tracking service.
	 * 
	 * @param action One of the action defined by the geoloqi sdk
	 * @param extraParams JSON object containing extra intent constants defined
	 * by the geoloqi sdk
	 */
	@Kroll.method
	public void startLQService(String action, Object extraParams) {
		MLog.d(LCAT, "in startLQService");
		serviceWrapper.startService(getActivity(), action, extraParams);
	}

	/**
	 * This method exposes functionality on module object to return the
	 * LQSessionProxy object.
	 * 
	 * @return LQSessionProxy
	 */
	@Kroll.method
	public LQSessionProxy getLQSession() {
		MLog.d(LCAT, "in getLQSession");
		return serviceWrapper.getSession();
	}

	/**
	 * This method exposes functionality on module object to return the
	 * LQTrackerProxy object.
	 * 
	 * @return LQTrackerProxy
	 */
	@Kroll.method
	public LQTrackerProxy getLQTracker() {
		MLog.d(LCAT, "in getLQTracker");
		return LQTrackerProxy.getInstance(getActivity());
	}

	/**
	 * This method exposes functionality on module object to get the current
	 * value of the low battery tracking preference.
	 * 
	 * @return String Battery value.
	 */
	@Kroll.method
	public String getLowBatteryTracking() {
		MLog.d(LCAT, "in getLowBatteryTracking");
		Boolean value = serviceWrapper.getLowBatteryTracking();
		String retVal = "";
		if (value != null) {
			retVal = value.toString();
		}
		return retVal;
	}

	/**
	 * This method exposes functionality on module object to set the low battery
	 * tracking preference.
	 * 
	 * @param boolean Value to indicate whether the low battery tracking
	 * preference is enabled/disabled
	 */
	@Kroll.method
	public void setLowBatteryTracking(boolean enabled) {
		MLog.d(LCAT, "in setLowBatteryTracking, value is: " + enabled);
		serviceWrapper.setLowBatteryTracking(enabled);
	}

	/**
	 * This method exposes functionality on module object to add broadcase
	 * receiver for location tracker
	 * 
	 * @param boolean Value to set whether to receive broadcast events or not.
	 */
	@Kroll.method
	public void addLocationBroadcastReceiver(boolean value) {
		MLog.d(LCAT, "in addLocationBroadcastReceiver, value is: " + value);
		addLocationBroadcastReceiver = value;
	}
}
