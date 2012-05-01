package ti.geoloqi.proxy;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.json.JSONException;

import ti.geoloqi.GeoloqiModule;
import ti.geoloqi.GeoloqiValidations;
import ti.geoloqi.common.MLog;
import ti.geoloqi.common.MUtils;
import android.location.Location;

import com.geoloqi.android.sdk.data.LQLocation;

/**
 * This class is a wrapper around the LQLocation class of geoloqi
 * 
 * @see com.geoloqi.android.sdk.data.LQLocation
 */
@Kroll.proxy(creatableInModule = GeoloqiModule.class)
public class LQLocationProxy extends KrollProxy {
	public static final String LCAT = LQLocationProxy.class.getSimpleName();
	private LQLocation lqlocation;
	private GeoloqiModule module = GeoloqiModule.getInstance();

	@Kroll.constant
	public static final String MINIMUM_ACCURACY = "MINIMUM_ACCURACY";

	// inline parameters
	private final String PROP_PROVIDER = "location";

	// properties location variables
	private Location location;

	/**
	 * Class Constructor
	 */
	public LQLocationProxy() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.appcelerator.kroll.KrollProxy#handleCreationDict(org.appcelerator
	 * .kroll.KrollDict)
	 */
	@Override
	public void handleCreationDict(KrollDict dict) {
		super.handleCreationDict(dict);
		MLog.d(LCAT, "in handleCreationDict, dict: " + dict);
		if (dict.containsKeyAndNotNull(PROP_PROVIDER)) {
			location = MUtils.parseLocation(dict.get(PROP_PROVIDER));
		}

		createLQLocation();
	}

	/**
	 * Creates LQLocation class instance.
	 */
	private void createLQLocation() {
		if (location != null) {
			lqlocation = new LQLocation(location);
		} else {
			module.fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.LOC_LOCATION_NP_CODE, GeoloqiValidations.LOC_LOCATION_NP_DESC));
		}
	}

	/**
	 * Gets the current location.
	 * 
	 * @return LQLocation
	 */
	private LQLocation getLQLocation() {
		if (lqlocation == null) {
			module.fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.LOC_LQLOCATION_NA_CODE, GeoloqiValidations.LOC_LQLOCATION_NA_DESC));
		}

		return lqlocation;
	}

	/**
	 * Get the battery level when this fix was recorded.
	 * 
	 * @return battery level
	 */
	@Kroll.method
	public String getBattery() {
		MLog.d(LCAT, "in getBattery()");
		String retVal = "";
		if (getLQLocation() != null) {
			retVal = String.valueOf(lqlocation.getBattery());
		}

		return retVal;
	}

	/**
	 * Determine if the Location is minimally accurate.
	 * 
	 * @return Location Accuracy
	 */
	@Kroll.method
	public String isAccurate() {
		MLog.d(LCAT, "in isAccurate()");
		String retVal = "";
		if (getLQLocation() != null) {
			retVal = String.valueOf(lqlocation.isAccurate());
		}

		return retVal;
	}

	/**
	 * Set the battery level when this fix was recorded.
	 * 
	 * @param battery level
	 */
	@Kroll.method
	public void setBattery(String battery) {
		MLog.d(LCAT, "in setBattery(), battery: " + battery);
		if (getLQLocation() != null) {
			try {
				lqlocation.setBattery(Integer.parseInt(battery));
			} catch (NumberFormatException e) {
				MLog.e(LCAT, "setBattery-->" + e.getMessage());
				module.fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.LOC_INV_BATT_PARAM_CODE, GeoloqiValidations.LOC_INV_BATT_PARAM_DESC));
			}
		}
	}

	/**
	 * Convert the Location to a JSONObject suitable for passing to the Geoloqi.
	 * API.
	 * 
	 * @return JSONObject
	 */
	@Kroll.method
	public String toJson() {
		MLog.d(LCAT, "in toJson()");
		String retVal = "";
		if (getLQLocation() != null) {
			try {
				retVal = lqlocation.toJson().toString();
			} catch (JSONException e) {
				MLog.e(LCAT, "toJson-->" + e.getMessage());
				module.fireEvent(GeoloqiModule.ON_VALIDATE, MUtils.generateErrorObject(GeoloqiValidations.LOC_UN_CONV_JSON_CODE, GeoloqiValidations.LOC_UN_CONV_JSON_DESC));
			}
		}

		return retVal;
	}

	/**
	 * Gets the battery level of the system.
	 * 
	 * @return
	 */
	@Kroll.method
	public String getSystemBatteryLevel() {
		MLog.d(LCAT, "in getSystemBatteryLevel()");
		String retVal = "";
		if (getLQLocation() != null) {
			retVal = String.valueOf(LQLocation.getSystemBatteryLevel(getActivity().getApplicationContext()));
		}

		return retVal;
	}

}
