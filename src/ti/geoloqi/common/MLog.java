package ti.geoloqi.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.appcelerator.kroll.common.Log;

import ti.geoloqi.GeoloqiModule;

final public class MLog {
	private static final String TIMESTAMP_FORMAT = "dd/MM/yyyy HH:mm:ss";

	private MLog() {}

	public static void d(String tag, String message) {
		if (GeoloqiModule.DBG) {
			Log.d(processTag(tag), message);
		}
	}

	public static void e(String tag, String message) {
		Log.e(processTag(tag), message);
	}

	public static void w(String tag, String message) {
		Log.w(processTag(tag), message);
	}

	private static String processTag(String tag) {
		SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);

		String strDate = formatter.format(new Date());

		return tag + "-" + strDate;
	}
}
