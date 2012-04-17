package ti.geoloqi.proxy.common;

import org.appcelerator.kroll.KrollDict;

import ti.geoloqi.GeoloqiModule;
import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;
import ti.geoloqi.proxy.LQTrackerProxy;
import android.content.Context;
import android.location.Location;

import com.geoloqi.android.sdk.LQTracker.LQTrackerProfile;
import com.geoloqi.android.sdk.receiver.LQBroadcastReceiver;

public class LQBroadcastReceiverImpl extends LQBroadcastReceiver {

	public static final String TAG = LQBroadcastReceiverImpl.class.getName();
	private static final String OLD_PROFILE = "OLD_PROFILE";
	private static final String NEW_PROFILE = "NEW_PROFILE";

	@Override
	public void onLocationChanged(Context context, Location location) {
		MLog.d(TAG, "onLocationChanged. module=");
		KrollDict locationKD = MUtils.locationToDictionary(location);
		GeoloqiModule.getInstance().fireEvent(GeoloqiModule.LOCATION_CHANGED,
				locationKD);
	}

	@Override
	public void onLocationUploaded(Context context,
			int numberOfLocationsUploaded) {

		MLog.d(TAG, "onLocationUploaded.");
		MLog.d(TAG, "Number of Locations Uploaded=" + numberOfLocationsUploaded);

		GeoloqiModule.getInstance().fireEvent(GeoloqiModule.LOCATION_UPLOADED,
				numberOfLocationsUploaded);
	}

	@Override
	public void onTrackerProfileChanged(Context context,
			LQTrackerProfile oldProfile, LQTrackerProfile newProfile) {
		MLog.d(TAG, "onTrackerProfileChanged.");
		KrollDict profileKD = new KrollDict(2);
		profileKD.put(OLD_PROFILE, LQTrackerProxy.profileToString(oldProfile));
		profileKD.put(NEW_PROFILE, LQTrackerProxy.profileToString(newProfile));
		GeoloqiModule.getInstance().fireEvent(
				GeoloqiModule.TRACKER_PROFILE_CHANGED, profileKD);
	}

}
