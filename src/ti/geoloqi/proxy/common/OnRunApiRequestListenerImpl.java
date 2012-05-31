package ti.geoloqi.proxy.common;

import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollObject;
import org.json.JSONObject;

import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;

import com.geoloqi.android.sdk.LQException;
import com.geoloqi.android.sdk.LQSession;
import com.geoloqi.android.sdk.LQSession.OnRunApiRequestListener;

/**
 * This class is an implementation of OnRunApiRequestListener of geoloqi
 * 
 * @see com.geoloqi.android.sdk.LQSession.OnRunApiRequestListener
 */
public class OnRunApiRequestListenerImpl implements OnRunApiRequestListener {

	private KrollObject krollObject;
	private final String LCAT = OnRunApiRequestListenerImpl.class.getSimpleName();
	private final String ON_SUCCESS = "onSuccess";
	private final String ON_COMPLETE = "onComplete";
	private final String ON_FAILURE = "onFailure";

	private KrollFunction onComplete = null, onFailure = null,
			onSuccess = null;

	/**
	 * Class Constructor
	 * 
	 * @param krollObject
	 *            Object on which event call is to be executed
	 * @param callbackMap
	 *            Map containing key as event name and value as a callback
	 *            function to be called on the event
	 */
	public OnRunApiRequestListenerImpl(KrollObject krollObject, Map<String, KrollFunction> callbackMap) {
		this.krollObject = krollObject;
		if (callbackMap != null) {
			if (callbackMap.containsKey(ON_COMPLETE)) {
				onComplete = callbackMap.get(ON_COMPLETE);
			}
			if (callbackMap.containsKey(ON_FAILURE)) {
				onFailure = callbackMap.get(ON_FAILURE);
			}
			if (callbackMap.containsKey(ON_SUCCESS)) {
				onSuccess = callbackMap.get(ON_SUCCESS);
			}
		} else {
			MLog.e(LCAT, "callbackMap is null for: " + krollObject);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.geoloqi.android.sdk.LQSession.OnRunApiRequestListener#onComplete(
	 * com.geoloqi.android.sdk.LQSession, org.apache.http.HttpResponse,
	 * org.apache.http.StatusLine)
	 */
	@Override
	public void onComplete(LQSession session, JSONObject json,
			Header[] headers, StatusLine status) {
		MLog.d(LCAT, "onComplete, status code: " + String.valueOf(status.getStatusCode()));

		KrollDict kd = MUtils.processHttpResponse(json, headers, status);

		if (onComplete != null) {
			onComplete.call(krollObject, kd);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.geoloqi.android.sdk.LQSession.OnRunApiRequestListener#onFailure(com
	 * .geoloqi.android.sdk.LQSession, com.geoloqi.android.sdk.LQException)
	 */
	@Override
	public void onFailure(LQSession session, LQException e) {
		MLog.d(LCAT, "onFailure, message is: " + e.getMessage());

		if (onFailure != null) {
			onFailure.call(krollObject, MUtils.generateErrorObject("", e.getMessage()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.geoloqi.android.sdk.LQSession.OnRunApiRequestListener#onSuccess(com
	 * .geoloqi.android.sdk.LQSession, org.apache.http.HttpResponse)
	 */
	@Override
	public void onSuccess(LQSession session, JSONObject json,
			Header[] headers) {
		MLog.d(LCAT, "onSuccess");
		
		KrollDict kd = MUtils.processHttpResponse(json, headers, null);

		if (onSuccess != null) {
			onSuccess.call(krollObject, kd);
		}
	}
}
