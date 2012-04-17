package ti.geoloqi.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.util.TiConvert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

final public class MUtils {
	public static final String LCAT = MUtils.class.getSimpleName();
	private static final String DEFAULT_RESPONSE_FORMAT = "UTF-8";

	private MUtils() {}

	public static KrollDict processHttpResponse(HttpResponse response) {
		return processHttpResponse(response, DEFAULT_RESPONSE_FORMAT);
	}

	// This method converts the httpResponse to json object and return the kroll
	// dict fro the titanium user
	public static KrollDict processHttpResponse(HttpResponse response, String format) {
		KrollDict kd = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), format));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line);
			}
			String strResponse = builder.toString();
			MLog.d(LCAT, strResponse);

			try {
				kd = new KrollDict();
				kd.put("response", new KrollDict(new JSONObject(strResponse)));
			} catch (Exception e) {
				kd = new KrollDict();
				MLog.e(LCAT, "processHttpResponse-->" + e.toString());
			}
		} catch (IllegalStateException e) {
			MLog.e(LCAT, e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			MLog.e(LCAT, e.toString());
			e.printStackTrace();
		}

		return kd;
	}

	public static Location parseLocation(Object location) {
		Location loc = null;
		try {
			JSONObject jsonObj = new JSONObject(String.valueOf(location));

			MLog.d(LCAT, "jsonObj=" + jsonObj);

			JSONObject coords = null;
			JSONObject provider = null;
			if (jsonObj != null && jsonObj.has("coords")) {
				coords = jsonObj.getJSONObject("coords");
			}

			if (jsonObj != null && jsonObj.has("provider")) {
				provider = jsonObj.getJSONObject("provider");
			}

			MLog.d(LCAT, "coords=" + coords);
			MLog.d(LCAT, "provider=" + provider);

			String prov = "";
			float acc = 0, speed = 0;
			double lat = 0, longt = 0, alt = 0;

			if (provider != null && provider.has("name")) {
				prov = String.valueOf(provider.get("name"));
			}
			if (coords != null) {
				if (coords.has("accuracy")) {
					acc = Float.parseFloat(String.valueOf(coords.get("accuracy")));
				}
				if (coords.has("altitude")) {
					alt = Double.parseDouble(String.valueOf(coords.get("altitude")));
				}
				if (coords.has("latitude")) {
					lat = Double.parseDouble(String.valueOf(coords.get("latitude")));
				}
				if (coords.has("longitude")) {
					longt = Double.parseDouble(String.valueOf(coords.get("longitude")));
				}
				if (coords.has("speed")) {
					speed = Float.parseFloat(String.valueOf(coords.get("speed")));
				}
			}
			if (!(provider == null && coords == null)) {
				loc = new Location(prov);
				loc.setAccuracy(acc);
				loc.setAltitude(alt);
				loc.setLatitude(lat);
				loc.setLongitude(longt);
				loc.setSpeed(speed);

				MLog.d(LCAT, "Location created=" + loc);
			} else {
				MLog.e(LCAT, "Blank location provided");
			}
		} catch (JSONException je) {
			MLog.e(LCAT, "JSONException occured, unable to create Location");
			je.printStackTrace();
		} catch (Exception e) {
			MLog.e(LCAT, "Exception occured, unable to create Location");
			e.printStackTrace();
		}

		return loc;
	}

	public static KrollDict locationToDictionary(Location loc) {
		KrollDict kd = new KrollDict(2);
		KrollDict kdCoord = new KrollDict(5);
		KrollDict kdProvider = new KrollDict(1);

		if (loc != null) {
			kdProvider.put("name", loc.getProvider());
			kdCoord.put("accuracy", loc.getAccuracy());
			kdCoord.put("altitude", loc.getAltitude());
			kdCoord.put("latitude", loc.getLatitude());
			kdCoord.put("longitude", loc.getLongitude());
			kdCoord.put("speed", loc.getSpeed());
			kd.put("coords", kdCoord);
			kd.put("provider", kdProvider);
		}
		return kd;
	}

	public static JSONObject convertToJSON(Object object) {
		JSONObject json = null;
		try {
			json = TiConvert.toJSON((HashMap<String, Object>) object);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	public static JSONArray convertToJSONArray(Object object) {
		JSONArray json = null;
		try {
			if (object instanceof Object[]) {
				json = TiConvert.toJSONArray((Object[]) object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

}
