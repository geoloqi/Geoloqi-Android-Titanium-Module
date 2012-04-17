package ti.geoloqi.proxy.common;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;

import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;

import com.geoloqi.android.sdk.LQException;
import com.geoloqi.android.sdk.LQSession;
import com.geoloqi.android.sdk.LQSession.OnRunApiRequestListener;

public class OnRunApiRequestListenerImpl implements OnRunApiRequestListener {

	private KrollProxy proxy;
	private static final String LCAT = OnRunApiRequestListenerImpl.class
			.getSimpleName();

	public OnRunApiRequestListenerImpl(final KrollProxy proxy) {
		this.proxy = proxy;
	}

	@Override
	public void onComplete(LQSession session, HttpResponse response,
			StatusLine status) {
		MLog.d(LCAT, "onComplete");
		MLog.d(LCAT, String.valueOf(status.getStatusCode()));
		KrollDict kd = MUtils.processHttpResponse(response);
		proxy.fireEvent(ProxyConstants.ON_COMPLETE, kd);
	}

	@Override
	public void onFailure(LQSession session, LQException e) {
		MLog.d(LCAT, "onFailure");
		MLog.d(LCAT, e.getMessage());
		e.printStackTrace();
		proxy.fireEvent(ProxyConstants.ON_FAILURE, e.getMessage());
	}

	@Override
	public void onSuccess(LQSession session, HttpResponse response) {
		MLog.d(LCAT, "onSuccess");
		MLog.d(LCAT, String.valueOf(response.getStatusLine().getStatusCode()));
		KrollDict kd = MUtils.processHttpResponse(response);
		proxy.fireEvent(ProxyConstants.ON_SUCCESS, kd);
	}

}
