package ti.geoloqi.proxy;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import ti.geoloqi.common.MLog;
import android.app.Activity;

import com.geoloqi.android.sdk.LQTracker;
import com.geoloqi.android.sdk.LQTracker.LQTrackerProfile;

/**
 * Wrapper around the geoloqi LQTracker class that is responsible for queueing
 * location updates.
 * 
 * @see com.geoloqi.android.sdk.LQTracker
 */
@Kroll.proxy
public class LQTrackerProxy extends KrollProxy {

	private static final String LCAT = LQTrackerProxy.class.getSimpleName();
	private LQTracker tracker;
	private LQSessionProxy sessionProxy;
	private static LQTrackerProxy mInstance = null;

	// Tracker Constants
	private static final String TRACKERPROFILE_OFF = "OFF";
	private static final String TRACKERPROFILE_PASSIVE = "PASSIVE";
	private static final String TRACKERPROFILE_REALTIME = "REALTIME";
	private static final String TRACKERPROFILE_LOGGING = "LOGGING";
	private static final String TRACKSTATUS_LIVE = "LIVE";
	private static final String TRACKSTATUS_QUEUEING = "QUEUEING";
	private static final String TRACKSTATUS_NOT_TRACKING = "NOTTRACKING";

	/**
	 * Class Constructor
	 * 
	 * @param activity
	 */
	private LQTrackerProxy(Activity activity) {
		super();
		tracker = LQTracker.getInstance(activity.getApplicationContext());
		MLog.d(LCAT, "LQTrackerProxy created");
	}

	/**
	 * Singleton implementation to return the LQTrackerProxy class object.
	 * 
	 * @param activity
	 * @return LQTrackerProxy
	 */
	public static LQTrackerProxy getInstance(Activity activity) {
		if (mInstance == null) {
			try {
				mInstance = new LQTrackerProxy(activity);
			} catch (Exception e) {
				MLog.e(LCAT, "Exception while creating LQTrackerProxy is: " + e.getMessage());
				e.printStackTrace();
			}
		}

		return mInstance;
	}

	/**
	 * Set the tracker session.
	 * 
	 * @param session LQSession proxy object
	 */
	@Kroll.method
	public void setSession(LQSessionProxy session) {
		MLog.d(LCAT, "in setSession");
		if (session != null) {
			tracker.setSession(session.getSession());
			sessionProxy = session;
		}
	}

	/**
	 * Get the current tracker session.
	 * 
	 * @return LQSession proxy object
	 */
	@Kroll.method
	public LQSessionProxy getSession() {
		MLog.d(LCAT, "in getSession");
		return sessionProxy;
	}

	/**
	 * Get the current tracker profile.
	 * 
	 * @return tracker profile
	 */
	@Kroll.method
	public String getProfile() {
		MLog.d(LCAT, "in getProfile");
		return profileToString(tracker.getProfile());

	}

	/**
	 * Request the tracker to switch to a different tracking profile.
	 * 
	 * @param profile name of the profile
	 * @return boolean value to signify whether the profile has been changed or
	 * not.
	 */
	@Kroll.method
	public boolean setProfile(String profile) {
		MLog.d(LCAT, "in setProfile");
		LQTracker.LQTrackerProfile tProfile = stringToProfile(profile);

		if (tProfile != null) {
			return tracker.setProfile(tProfile);
		}
		return false;
	}

	/**
	 * Determine if the tracker can successfully switch to the given profile
	 * from its current state
	 * 
	 * @param profile name of the profile
	 * @return boolean value to signify whether profile can be switched or not
	 */
	@Kroll.method
	public boolean canSwitchToProfile(String profile) {
		MLog.d(LCAT, "in canSwitchToProfile");
		LQTracker.LQTrackerProfile tProfile = stringToProfile(profile);

		if (tProfile != null) {
			return tracker.canSwitchToProfile(tProfile);
		}
		return false;
	}

	/**
	 * maps the string value of the profile to one of the values in the
	 * LQTrackerProfile.
	 * 
	 * @param profile string profile value
	 * @return LQTrackerProfile type
	 */
	public static LQTracker.LQTrackerProfile stringToProfile(String profile) {
		LQTracker.LQTrackerProfile tProfile = null;
		if (TRACKERPROFILE_OFF.equals(profile))
			tProfile = LQTrackerProfile.OFF;
		else if (TRACKERPROFILE_PASSIVE.equals(profile))
			tProfile = LQTrackerProfile.PASSIVE;
		else if (TRACKERPROFILE_REALTIME.equals(profile))
			tProfile = LQTrackerProfile.REAL_TIME;
		else if (TRACKERPROFILE_LOGGING.equals(profile))
			tProfile = LQTrackerProfile.LOGGING;
		return tProfile;
	}

	/**
	 * convert the LQTrackerProfile value to string
	 * 
	 * @param profile LQTrackerProfile
	 * @return profile value in string
	 */
	public static String profileToString(LQTracker.LQTrackerProfile profile) {
		switch (profile) {
			case OFF:
				return TRACKERPROFILE_OFF;
			case PASSIVE:
				return TRACKERPROFILE_PASSIVE;
			case REAL_TIME:
				return TRACKERPROFILE_REALTIME;
			case LOGGING:
				return TRACKERPROFILE_LOGGING;

		}
		return null;
	}

	/**
	 * Get the current tracker status.
	 * 
	 * @return tracker status
	 */
	@Kroll.method
	public String getStatus() {
		MLog.d(LCAT, "in getStatus");
		switch (tracker.getStatus()) {
			case LIVE:
				return TRACKSTATUS_LIVE;
			case QUEUEING:
				return TRACKSTATUS_QUEUEING;
			default:
				return TRACKSTATUS_NOT_TRACKING;
		}
	}

	/**
	 * Configure the tracker for the current tracking profile.
	 */
	@Kroll.method
	public void configureForCurrentProfile() {
		MLog.d(LCAT, "in configureForCurrentProfile");
		tracker.configureForCurrentProfile();
	}

	/**
	 * Immediately flush all unsent fixes to the server.
	 */
	@Kroll.method
	public void uploadLocationQueue() {
		MLog.d(LCAT, "in uploadLocationQueue");
		tracker.uploadLocationQueue();
	}

	/**
	 * Determine if we should upload our queued location data.
	 */
	@Kroll.method
	public void uploadLocationQueueIfNecessary() {
		MLog.d(LCAT, "in uploadLocationQueueIfNecessary");
		tracker.uploadLocationQueueIfNecessary();
	}

}
