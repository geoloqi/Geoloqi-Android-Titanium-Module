package ti.geoloqi.proxy.common;

import org.appcelerator.kroll.annotations.Kroll;

public interface ProxyConstants {

	// Tracker Constants
	String TRACKERPROFILE_OFF = "OFF";
	String TRACKERPROFILE_PASSIVE = "PASSIVE";
	String TRACKERPROFILE_REALTIME = "REAL_TIME";
	String TRACKERPROFILE_LOGGING = "LOGGING";
	String TRACKSTATUS_LIVE = "LIVE";
	String TRACKSTATUS_QUEUEING = "QUEUEING";
	String TRACKSTATUS_NOT_TRACKING = "NOT_TRACKING";

	// Session Proxy Constants
	String KEY = "KEY";
	String SECRET = "SECRET";
	String NULL_SESSION = "Session is NULL";
	String REQUESTTYPE_GET = "GET";
	String REQUESTTYPE_POST = "POST";
	@Kroll.constant
	String ON_SUCCESS = "onSuccess";
	@Kroll.constant
	String ON_COMPLETE = "onComplete";
	@Kroll.constant
	String ON_FAILURE = "onFailure";

	// LQSession Fields
	@Kroll.constant
	String ISO_8601_DATE = "yyyy-MM-dd'T'HH:mm:ssZ";
	@Kroll.constant
	String EXTRA_USERNAME = "username";
	@Kroll.constant
	String EXTRA_PASSWORD = "password";
	@Kroll.constant
	String EXTRA_EMAIL = "email";
	@Kroll.constant
	String EXTRA_ACCESS_TOKEN = "access_token";
	@Kroll.constant
	String EXTRA_IS_ANONYMOUS = "is_anonymous";
	@Kroll.constant
	String EXTRA_ERROR = "error";
	@Kroll.constant
	String EXTRA_ERROR_DESCRIPTION = "error_description";
	@Kroll.constant
	String EXTRA_GRANT_TYPE = "grant_type";
	@Kroll.constant
	String DEVICE_ID_FIELD_NAME = "device_id";
	@Kroll.constant
	String MAC_ADDRESS_FIELD_NAME = "mac";
	@Kroll.constant
	String C2DM_ID_FIELD_NAME = "c2dm_token";
	@Kroll.constant
	String API_URL_BASE = "https://api.geoloqi.com/1/";
	@Kroll.constant
	String API_PATH_USER_CREATE_ANON = "user/create_anon";
	@Kroll.constant
	String API_PATH_USER_CREATE = "user/create";
	@Kroll.constant
	String API_PATH_LOCATION_UPDATE = "location/update";
	@Kroll.constant
	String API_PATH_SET_C2DM_TOKEN = "account/set_c2dm_token";
	@Kroll.constant
	String API_PATH_OAUTH_TOKEN = "oauth/token";
}
