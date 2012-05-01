package ti.geoloqi.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ti.geoloqi.GeoloqiModule;
import ti.geoloqi.GeoloqiValidations;
import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;
import ti.geoloqi.proxy.LQSessionProxy;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.geoloqi.android.sdk.service.LQService;
import com.geoloqi.android.sdk.service.LQService.LQBinder;

/**
 * This is a singleton class which wrapps methods of Geoloqi LQService service.
 */
public class LQServiceWrapper {
	private static final String LCAT = LQServiceWrapper.class.getSimpleName();
	private static LQServiceWrapper service = new LQServiceWrapper();
	private Map<String, String> hmServiceFields = null;
	private LQService mService;
	private boolean mBound;

	private final String EXTRA_C2DM_SENDER = "EXTRA_C2DM_SENDER";
	private final String EXTRA_C2DM_TOKEN = "EXTRA_C2DM_TOKEN";
	private final String EXTRA_EMAIL = "EXTRA_EMAIL";
	private final String EXTRA_LOW_BATTERY_TRACKING = "EXTRA_LOW_BATTERY_TRACKING";
	private final String EXTRA_PASSWORD = "EXTRA_PASSWORD";
	private final String EXTRA_SDK_ID = "EXTRA_SDK_ID";
	private final String EXTRA_SDK_SECRET = "EXTRA_SDK_SECRET";
	private final String EXTRA_USERNAME = "EXTRA_USERNAME";

	/**
	 * Private Class Constructor
	 */
	private LQServiceWrapper() {
		prepareServiceFields();
	}

	/**
	 * check whether the tracking service is bound or not.
	 * 
	 * @return boolean value indicating whether the service is bound or not
	 */
	public boolean isServiceBound() {
		return mBound;
	}

	/**
	 * set whether to bound the service or not
	 * 
	 * @param bound boolean value indicating the service bound
	 */
	public void setServiceBound(boolean bound) {
		mBound = bound;
	}

	/**
	 * return the service connection with respect to the service
	 * 
	 * @return ServiceConnection
	 */
	public ServiceConnection getConnection() {
		return mConnection;
	}

	/**
	 * Returns the class instance
	 * 
	 * @return LQServiceWrapper
	 */
	public static LQServiceWrapper getInstance() {
		return service;
	}

	/**
	 * Creates a hashmap with the key value pairs that are required by the
	 * geoloqi tracking service
	 */
	private void prepareServiceFields() {
		hmServiceFields = new HashMap<String, String>(13);
		// Actions
		hmServiceFields.put(GeoloqiModule.ACTION_AUTH_USER, LQService.ACTION_AUTH_USER);
		hmServiceFields.put(GeoloqiModule.ACTION_CREATE_ANONYMOUS_USER, LQService.ACTION_CREATE_ANONYMOUS_USER);
		hmServiceFields.put(GeoloqiModule.ACTION_CREATE_USER, LQService.ACTION_CREATE_USER);
		hmServiceFields.put(GeoloqiModule.ACTION_DEFAULT, LQService.ACTION_DEFAULT);
		hmServiceFields.put(GeoloqiModule.ACTION_SEND_C2DM_TOKEN, LQService.ACTION_SEND_C2DM_TOKEN);
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

	/**
	 * check whether the service is running or not
	 * 
	 * @return boolean value indicating the status of the service.
	 */
	private boolean checkService() {
		boolean retVal = true;
		if (mService == null) {
			GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.SRV_SERVICE_NA_CODE, GeoloqiValidations.SRV_SERVICE_NA_DESC));
			retVal = false;
		}
		return retVal;
	}

	/**
	 * Return LQSession proxy object
	 * 
	 * @return LQSessionProxy
	 */
	public LQSessionProxy getSession() {
		LQSessionProxy sproxy = null;
		if (checkService()) {
			sproxy = new LQSessionProxy(mService.getSession());
		}
		return sproxy;
	}

	/**
	 * Get the current value of the low battery tracking preference.
	 * 
	 * @return Battery value.
	 */
	public Boolean getLowBatteryTracking() {
		Boolean retVal = null;
		if (checkService()) {
			retVal = mService.getLowBatteryTracking();
		}
		return retVal;
	}

	/**
	 * Set the low battery tracking preference
	 * 
	 * @param enabled boolean value to indicate whether the low battery tracking
	 * preference is allowed or not.
	 */
	public void setLowBatteryTracking(boolean enabled) {
		if (checkService()) {
			mService.setLowBatteryTracking(enabled);
		}
	}

	/**
	 * Starts geoloqi tracking service
	 * 
	 * @param activity current activity
	 * @param action @see com.geoloqi.android.sdk.service.LQService
	 * @param extraParams JSON Object
	 */
	public void startService(Activity activity, String action, Object extraParams) {
		Map<String, Object> map = (HashMap<String, Object>) extraParams;
		Iterator<String> keys = null;
		String key = null;
		String glqAction = null;

		if (hmServiceFields.containsKey(action)) {
			glqAction = hmServiceFields.get(action);
		} else {
			GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.SRV_ACTION_NA_CODE, GeoloqiValidations.SRV_ACTION_NA_DESC));
		}

		if (map != null) {
			keys = map.keySet().iterator();
			while (keys.hasNext()) {
				key = keys.next();
				if (!hmServiceFields.containsKey(key)) {
					GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.SRV_UND_EXTRA_FLDS_CODE, GeoloqiValidations.SRV_UND_EXTRA_FLDS_DESC + key));
				}
			}
		} else {
			GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.SRV_INV_EXTRA_FLDS_CODE, GeoloqiValidations.SRV_INV_EXTRA_FLDS_DESC));
		}

		// Bind and start the geoloqi service

		if (activity != null) {
			Intent intent = new Intent(activity, LQService.class);
			intent.setAction(glqAction);
			keys = map.keySet().iterator();
			while (keys.hasNext()) {
				key = keys.next();
				MLog.d(LCAT, "Extra Key: " + hmServiceFields.get(key) + ", Extra Value: " + map.get(key));
				if (EXTRA_LOW_BATTERY_TRACKING.equals(key)) {
					intent.putExtra(hmServiceFields.get(key), (Boolean) map.get(key));
				} else {
					intent.putExtra(hmServiceFields.get(key), (String) map.get(key));
				}
			}

			MLog.d(LCAT, "Intent Prepared");
			activity.startService(intent);
			MLog.d(LCAT, "Starting Service");
		} else {
			GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.SRV_ACTIVITY_NULL_CODE, GeoloqiValidations.SRV_ACTIVITY_NULL_DESC));
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
				GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_SERVICE_CONNECTED, null);
			} catch (ClassCastException e) {
				MLog.e(LCAT, e.toString());
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			MLog.e(LCAT, "onServiceDisconnected");
			mBound = false;
			GeoloqiModule.getInstance().fireEvent(GeoloqiModule.ON_SERVICE_DISCONNECTED, null);
		}
	};
}
