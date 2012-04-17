package ti.geoloqi.service;

import org.appcelerator.kroll.annotations.Kroll;

public interface LQServiceContants {
	@Kroll.constant
	String ACTION_AUTH_USER = "ACTION_AUTH_USER";
	@Kroll.constant
	String ACTION_CREATE_ANONYMOUS_USER = "ACTION_CREATE_ANONYMOUS_USER";
	@Kroll.constant
	String ACTION_CREATE_USER = "ACTION_CREATE_USER";
	@Kroll.constant
	String ACTION_DEFAULT = "ACTION_DEFAULT";
	@Kroll.constant
	String ACTION_SEND_C2DM_TOKEN = "ACTION_SEND_C2DM_TOKEN";

	String EXTRA_C2DM_SENDER = "EXTRA_C2DM_SENDER";
	String EXTRA_C2DM_TOKEN = "EXTRA_C2DM_TOKEN";
	String EXTRA_EMAIL = "EXTRA_EMAIL";
	String EXTRA_LOW_BATTERY_TRACKING = "EXTRA_LOW_BATTERY_TRACKING";
	String EXTRA_PASSWORD = "EXTRA_PASSWORD";
	String EXTRA_SDK_ID = "EXTRA_SDK_ID";
	String EXTRA_SDK_SECRET = "EXTRA_SDK_SECRET";
	String EXTRA_USERNAME = "EXTRA_USERNAME";
	
}
