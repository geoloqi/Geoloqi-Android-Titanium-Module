package ti.geoloqi.proxy.common;

import org.appcelerator.kroll.KrollDict;

import ti.geoloqi.GeoloqiModule;
import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;
import ti.geoloqi.proxy.LQTrackerProxy;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.geoloqi.android.sdk.LQTracker.LQTrackerProfile;
import com.geoloqi.android.sdk.receiver.LQBroadcastReceiver;

/**
 * This class is an implementation of LQBroadcastReceiver of geoloqi
 * 
 * @see com.geoloqi.android.sdk.receiver.LQBroadcastReceiver
 */
public class LQBroadcastReceiverImpl extends LQBroadcastReceiver {

	public static final String LCAT = LQBroadcastReceiverImpl.class.getSimpleName();
	private static final String OLD_PROFILE = "OLD_PROFILE";
	private static final String NEW_PROFILE = "NEW_PROFILE";
	private static final String LOCATION_ATTRIB = "location";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.geoloqi.android.sdk.receiver.LQBroadcastReceiver#onLocationChanged
	 * (android.content.Context, android.location.Location)
	 */
	@Override
	public void onLocationChanged(Context context, Location location) {
		MLog.d(LCAT, "in onLocationChanged");
		KrollDict locationKD = new KrollDict(1);
		locationKD.put(LOCATION_ATTRIB, MUtils.locationToDictionary(location));
		GeoloqiModule.getInstance().fireEvent(GeoloqiModule.LOCATION_CHANGED, locationKD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.geoloqi.android.sdk.receiver.LQBroadcastReceiver#onLocationUploaded
	 * (android.content.Context, int)
	 */
	@Override
	public void onLocationUploaded(Context context, int numberOfLocationsUploaded) {
		MLog.d(LCAT, "in onLocationUploaded, Number of Locations Uploaded: " + numberOfLocationsUploaded);
		GeoloqiModule.getInstance().fireEvent(GeoloqiModule.LOCATION_UPLOADED, numberOfLocationsUploaded);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.geoloqi.android.sdk.receiver.LQBroadcastReceiver#onTrackerProfileChanged
	 * (android.content.Context,
	 * com.geoloqi.android.sdk.LQTracker.LQTrackerProfile,
	 * com.geoloqi.android.sdk.LQTracker.LQTrackerProfile)
	 */
	@Override
	public void onTrackerProfileChanged(Context context, LQTrackerProfile oldProfile, LQTrackerProfile newProfile) {
		MLog.d(LCAT, "in onTrackerProfileChanged.");
		KrollDict profileKD = new KrollDict(2);
		profileKD.put(OLD_PROFILE, LQTrackerProxy.profileToString(oldProfile));
		profileKD.put(NEW_PROFILE, LQTrackerProxy.profileToString(newProfile));
		GeoloqiModule.getInstance().fireEvent(GeoloqiModule.TRACKER_PROFILE_CHANGED, profileKD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.geoloqi.android.sdk.receiver.LQBroadcastReceiver#onPushMessageReceived
	 * (android.content.Context, android.os.Bundle)
	 */
	@Override
	public void onPushMessageReceived(Context context, Bundle data) {
		MLog.d(LCAT, "in onPushMessageReceived.");
		GeoloqiModule.getInstance().fireEvent(GeoloqiModule.PUSH_MESSAGE_RECEIVED, data);
	}
}
