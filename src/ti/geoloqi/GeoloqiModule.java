package ti.geoloqi;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.TiApplication;

import ti.geoloqi.common.MLog;
import ti.geoloqi.proxy.LQSessionProxy;
import ti.geoloqi.proxy.LQTrackerProxy;
import ti.geoloqi.proxy.common.LQBroadcastReceiverImpl;
import ti.geoloqi.service.LQServiceContants;
import ti.geoloqi.service.LQServiceWrapper;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import com.geoloqi.android.sdk.service.LQService;

@Kroll.module(name = "Geoloqi", id = "ti.geoloqi")
public class GeoloqiModule extends KrollModule implements LQServiceContants {
	private static final String LCAT = GeoloqiModule.class.getSimpleName();
	public static boolean DBG = false;
	private LQServiceWrapper wrapper = LQServiceWrapper.getInstance();
	private static GeoloqiModule module;
	private LQBroadcastReceiverImpl locationBroadcastReceiver = new LQBroadcastReceiverImpl();
	private boolean addLocationBroadcastReceiver = true;

	@Kroll.constant
	public static final String ON_VALIDATE = "onValidate";

	@Kroll.constant
	public static final String LOCATION_CHANGED = "LOCATION_CHANGED";

	@Kroll.constant
	public static final String LOCATION_UPLOADED = "LOCATION_UPLOADED";

	@Kroll.constant
	public static final String TRACKER_PROFILE_CHANGED = "TRACKER_PROFILE_CHANGED";

	public GeoloqiModule() {
		super();
		TiConfig.LOGD = DBG;
		module = this;
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		MLog.d(LCAT, "inside onAppCreate");
	}

	@Kroll.method
	public void setDebug(boolean value) {
		TiConfig.LOGD = value;
		DBG = value;
	}

	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);
		MLog.d(LCAT, "onResume");
		Intent intent = new Intent(activity, LQService.class);
		activity.bindService(intent, wrapper.getConnection(), 0);
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

	@Override
	public void onPause(Activity activity) {
		super.onPause(activity);
		MLog.d(LCAT, "onPause");
		if (wrapper.isServiceBound()) {
			activity.unbindService(wrapper.getConnection());
			wrapper.setServiceBound(false);
		}
		if (addLocationBroadcastReceiver) {
			activity.getApplicationContext().unregisterReceiver(
					locationBroadcastReceiver);
			MLog.d(LCAT, "Receiver UnRegistered");
		}
	}

	public static GeoloqiModule getInstance() {
		return module;
	}

	@Kroll.method
	public void startLQService(String action, Object extraParams) {
		try {
			wrapper.startService(getActivity(), action, extraParams);
		} catch (Exception e) {
			MLog.e(LCAT, e.getMessage());
			fireEvent(ON_VALIDATE, e.getMessage());
		}
	}

	@Kroll.method
	public LQSessionProxy getLQSession() {
		return wrapper.getSession();
	}

	@Kroll.method
	public LQTrackerProxy getLQTracker() {
		return LQTrackerProxy.getInstance(getActivity());
	}

	@Kroll.method
	public String getLowBatteryTracking() {
		Boolean value = wrapper.getLowBatteryTracking();
		String retVal = "";
		if (value != null) {
			retVal = value.toString();
		}
		return retVal;
	}

	@Kroll.method
	public void setLowBatteryTracking(boolean enabled) {
		wrapper.setLowBatteryTracking(enabled);
	}

	@Kroll.method
	public void addLocationBroadcastReceiver(boolean value) {
		addLocationBroadcastReceiver = value;
	}
}
