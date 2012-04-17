package ti.geoloqi.proxy;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.json.JSONException;

import ti.geoloqi.GeoloqiModule;
import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;
import android.location.Location;

import com.geoloqi.android.sdk.data.LQLocation;

@Kroll.proxy(creatableInModule = GeoloqiModule.class)
public class LQLocationProxy extends KrollProxy {
	public static final String LCAT = LQLocationProxy.class.getSimpleName();
	private LQLocation lqlocation;
	private GeoloqiModule module = GeoloqiModule.getInstance();

	@Kroll.constant
	public static final String MINIMUM_ACCURACY = "MINIMUM_ACCURACY";

	// inline parameters
	public static final String PROP_PROVIDER = "location";

	// properties location variables
	private Location location;

	public LQLocationProxy() {
		super();
	}

	@Override
	public void handleCreationDict(KrollDict dict) {
		super.handleCreationDict(dict);
		MLog.d(LCAT, "in handleCreationDict, dict: " + dict);
		if (dict.containsKeyAndNotNull(PROP_PROVIDER)) {
			location = MUtils.parseLocation(dict.get(PROP_PROVIDER));
		}

		createLQLocation();
	}

	private void createLQLocation() {
		if (location != null) {
			lqlocation = new LQLocation(location);
		} else {
			module.fireEvent(GeoloqiModule.ON_VALIDATE, "Location not provided.");
		}
	}

	private LQLocation getLQLocation() {
		if (lqlocation == null) {
			module.fireEvent(GeoloqiModule.ON_VALIDATE, "LQLocation not available.");
		}

		return lqlocation;
	}

	@Kroll.method
	public String getBattery() {
		MLog.d(LCAT, "in getBattery()");
		String retVal = "";
		if (getLQLocation() != null) {
			retVal = String.valueOf(lqlocation.getBattery());
		}

		return retVal;
	}

	@Kroll.method
	public String isAccurate() {
		MLog.d(LCAT, "in isAccurate()");
		String retVal = "";
		if (getLQLocation() != null) {
			retVal = String.valueOf(lqlocation.isAccurate());
		}

		return retVal;
	}

	@Kroll.method
	public void setBattery(String battery) {
		MLog.d(LCAT, "in setBattery(), battery: " + battery);
		if (getLQLocation() != null) {
			try {
				lqlocation.setBattery(Integer.parseInt(battery));
			} catch (NumberFormatException e) {
				MLog.e(LCAT, "setBattery-->" + e.getMessage());
				module.fireEvent(GeoloqiModule.ON_VALIDATE, "Invalid value provided for battery parameter.");
			}
		}
	}

	@Kroll.method
	public String toJson() {
		MLog.d(LCAT, "in toJson()");
		String retVal = "";
		if (getLQLocation() != null) {
			try {
				retVal = lqlocation.toJson().toString();
			} catch (JSONException e) {
				MLog.e(LCAT, "toJson-->" + e.getMessage());
				module.fireEvent(GeoloqiModule.ON_VALIDATE, "Unable to convert to JSON.");
			}
		}

		return retVal;
	}

	@Kroll.method
	public String getSystemBatteryLevel() {
		MLog.d(LCAT, "in getSystemBatteryLevel()");
		String retVal = "";
		if (getLQLocation() != null) {
			retVal = String.valueOf(lqlocation.getSystemBatteryLevel(getActivity().getApplicationContext()));
		}

		return retVal;
	}

}
