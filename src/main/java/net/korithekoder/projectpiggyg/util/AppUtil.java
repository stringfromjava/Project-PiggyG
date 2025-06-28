package net.korithekoder.projectpiggyg.util;

import net.korithekoder.projectpiggyg.Initialize;

/**
 * Utility class for either obtaining or manipulating
 * information in the application.
 */
public final class AppUtil {

	/**
	 * Gets the app's current version that it is running on.
	 *
	 * @return The current version. Note that if it is being run on an
	 * IDE, or it isn't running from a packaged {@code .jar} file, then
	 * it will return {@code DEV} instead.
	 */
	public static String getAppVersion() {
		String version = Initialize.class.getPackage().getImplementationVersion();
		if (version == null) {
			version = "DEV"; // Fallback if not running from a packaged .jar
		}
		return version;
	}

	private AppUtil() {}
}
