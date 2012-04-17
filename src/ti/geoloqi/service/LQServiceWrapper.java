package ti.geoloqi.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ti.geoloqi.GeoloqiModule;
import ti.geoloqi.common.MLog;
import ti.geoloqi.proxy.LQSessionProxy;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.geoloqi.android.sdk.service.LQService;
import com.geoloqi.android.sdk.service.LQService.LQBinder;

public class LQServiceWrapper implements LQServiceContants {
	private static final String LCAT = LQServiceWrapper.class.getSimpleName();
	private static LQServiceWrapper service = new LQServiceWrapper();
	private HashMap<String, String> hmServiceFields = null;
	private LQService mService;
	private boolean mBound;

	private LQServiceWrapper() {
		prepareServiceFields();
	}

	public boolean isServiceBound() {
		return mBound;
	}

	public void setServiceBound(boolean bound) {
		mBound = bound;
	}

	public ServiceConnection getConnection() {
		return mConnection;
	}

	public static LQServiceWrapper getInstance() {
		return service;
	}

	private void prepareServiceFields() {
		hmServiceFields = new HashMap<String, String>(13);
		// Actions
		hmServiceFields.put(ACTION_AUTH_USER, LQService.ACTION_AUTH_USER);
		hmServiceFields.put(ACTION_CREATE_ANONYMOUS_USER, LQService.ACTION_CREATE_ANONYMOUS_USER);
		hmServiceFields.put(ACTION_CREATE_USER, LQService.ACTION_CREATE_USER);
		hmServiceFields.put(ACTION_DEFAULT, LQService.ACTION_DEFAULT);
		hmServiceFields.put(ACTION_SEND_C2DM_TOKEN, LQService.ACTION_SEND_C2DM_TOKEN);
		// Extras
		hmServiceFields.put(EXTRA_C2DM_SENDER, LQService.EXTRA_C2DM_SENDER);
		hmServiceFields.put(EXTRA_C2DM_TOKEN, LQService.EXTRA_C2DM_TOKEN);
		hmServiceFields.put(EXTRA_EMAIL, LQService.EXTRA_EMAIL);
		hmServiceFields.put(EXTRA_LOW_BATTERY_TRACKING, LQService.EXTRA_LOW_BATTERY_TRACKING);
		hmServiceFields.put(EXTRA_PASSWORD, LQService.EXTRA_PASSWORD);
		hmServiceFields.put(EXTRA_SDK_ID, LQService.EXTRA_SDK_ID);
		hmServiceFields.put(EXTRA_SDK_SECRET, LQService.EXTRA_SDK_SECRET);
		hmServiceFields.put(EXTRA_USERNAME, LQService.EXTRA_USERNAME);
	}

	private boolean checkService() {
		boolean retVal = true;
		if (mService == null) {
			GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_VALIDATE, "Service not available.");
			retVal = false;
		}
		return retVal;
	}

	public LQSessionProxy getSession() {
		LQSessionProxy sproxy = null;
		if (checkService()) {
			sproxy = new LQSessionProxy(mService.getSession());
		}
		return sproxy;
	}

	public Boolean getLowBatteryTracking() {
		Boolean retVal = null;
		if (checkService()) {
			retVal = mService.getLowBatteryTracking();
		}
		return retVal;
	}

	public void setLowBatteryTracking(boolean enabled) {
		if (checkService()) {
			mService.setLowBatteryTracking(enabled);
		}
	}

	public void startService(Activity activity, String action, Object extraParams) throws Exception {
		Map<String, String> map = (HashMap<String, String>) extraParams;
		Iterator<String> keys = null;
		String key = null;

		if (hmServiceFields.containsKey(action)) {
			action = hmServiceFields.get(action);
		} else {
			throw new Exception("Undefined Action provided");
		}

		if (map != null) {
			keys = map.keySet().iterator();
			while (keys.hasNext()) {
				key = keys.next();
				if (!hmServiceFields.containsKey(key)) {
					throw new Exception("Undefined Extra Field provided, field: " + key);
				}
			}
		} else {
			throw new Exception("Invalid Extra Fields provided");
		}

		// Bind and start the geoloqi service

		if (activity != null) {
			Intent intent = new Intent(activity, LQService.class);
			intent.setAction(action);
			keys = map.keySet().iterator();
			while (keys.hasNext()) {
				key = keys.next();
				MLog.d(LCAT, "key: " + key + ", hmServiceFields.get(key): " + hmServiceFields.get(key) + ", value: " + map.get(key));
				intent.putExtra(hmServiceFields.get(key), map.get(key));
			}

			MLog.d(LCAT, "Intent Prepared");
			activity.startService(intent);
			MLog.d(LCAT, "Starting Service");
		} else {
			throw new Exception("Activity is null");
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
