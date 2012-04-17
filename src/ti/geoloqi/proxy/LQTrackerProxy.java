package ti.geoloqi.proxy;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import ti.geoloqi.common.MLog;
import ti.geoloqi.proxy.common.ProxyConstants;
import android.app.Activity;

import com.geoloqi.android.sdk.LQTracker;
import com.geoloqi.android.sdk.LQTracker.LQTrackerProfile;

@Kroll.proxy
public class LQTrackerProxy extends KrollProxy implements ProxyConstants {

	private static final String LCAT = LQTrackerProxy.class.getSimpleName();
	private LQTracker tracker;
	private LQSessionProxy sessionProxy;
	private static LQTrackerProxy mInstance = null;

	private LQTrackerProxy(Activity activity) {
		super();
		tracker = LQTracker.getInstance(activity.getApplicationContext());
		MLog.d(LCAT, "LQTracker() created");
	}

	public static LQTrackerProxy getInstance(Activity activity) {
		if (mInstance == null) {
			try {
				mInstance = new LQTrackerProxy(activity);
			} catch (Exception e) {
				MLog.e(LCAT, "Exception while creating LQTrackerProxy is: "
						+ e.getMessage());
				e.printStackTrace();
			}
		}

		return mInstance;
	}

	@Kroll.method
	public void setSession(LQSessionProxy session) {
		if (session != null) {
			tracker.setSession(session.getSession());
			sessionProxy = session;
		}
	}

	@Kroll.method
	public LQSessionProxy getSession() {
		return sessionProxy;
	}

	@Kroll.method
	public String getProfile() {

		return profileToString(tracker.getProfile());

	}

	@Kroll.method
	public boolean setProfile(String profile) {

		LQTracker.LQTrackerProfile tProfile = stringToProfile(profile);

		if (tProfile != null) {
			return tracker.setProfile(tProfile);
		}
		return false;
	}

	@Kroll.method
	public boolean canSwitchToProfile(String profile) {
		LQTracker.LQTrackerProfile tProfile = stringToProfile(profile);

		if (tProfile != null) {
			return tracker.canSwitchToProfile(tProfile);
		}
		return false;
	}

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

	@Kroll.method
	public String getStatus() {
		switch (tracker.getStatus()) {
		case LIVE:
			return TRACKSTATUS_LIVE;
		case QUEUEING:
			return TRACKSTATUS_QUEUEING;
		default:
			return TRACKSTATUS_NOT_TRACKING;
		}
	}

	@Kroll.method
	public void configureForCurrentProfile() {
		MLog.d(LCAT, "LQTrackerProxy().configureForCurrentProfile()");
		tracker.configureForCurrentProfile();
	}

	@Kroll.method
	public void uploadLocationQueue() {
		tracker.uploadLocationQueue();
	}

	@Kroll.method
	public void uploadLocationQueueIfNecessary() {
		tracker.uploadLocationQueueIfNecessary();
	}

}
