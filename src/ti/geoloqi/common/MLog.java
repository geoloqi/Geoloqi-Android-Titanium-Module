package ti.geoloqi.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.appcelerator.kroll.common.Log;

import ti.geoloqi.GeoloqiModule;

/**
 * This is a singleton utility class to solve common concerns for logging in
 * Android
 * 
 */
final public class MLog {
	private static final String TIMESTAMP_FORMAT = "dd/MM/yyyy HH:mm:ss";

	/**
	 * Private Class Constructor
	 */
	private MLog() {}

	/**
	 * Logs the debug info.
	 * 
	 * @param tag
	 *            Tag name
	 * @param message
	 *            Message value
	 */
	public static void d(String tag, String message) {
		if (GeoloqiModule.debug) {
			Log.d(processTag(tag), message);
		}
	}

	/**
	 * Logs the error info.
	 * 
	 * @param tag
	 *            Tag name
	 * @param message
	 *            Message value
	 */
	public static void e(String tag, String message) {
		Log.e(processTag(tag), message);
	}

	/**
	 * Logs the warning.
	 * 
	 * @param tag
	 *            Tag name
	 * @param message
	 *            Message value
	 */
	public static void w(String tag, String message) {
		Log.w(processTag(tag), message);
	}

	/**
	 * Add date part to the tag
	 * 
	 * @param tag
	 *            Tag name
	 * @return formatted tag
	 */
	private static String processTag(String tag) {
		SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);

		String strDate = formatter.format(new Date());

		return tag + "-" + strDate;
	}
}
