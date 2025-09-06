package net.stringfromjava.projectpiggyg.util.sys;

import net.stringfromjava.projectpiggyg.util.app.LoggerUtil;

/**
 * Utility class for obtaining information about
 * the current runtime environment.
 */
public final class RuntimeUtil {

	private static boolean ansiCodesAllowed = true;

	/**
	 * Gets whether the application is running from a JAR file.
	 *
	 * @return If it is running from a JAR file.
	 */
	public static boolean isRunningFromJar() {
		String path = LoggerUtil.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath();
		return path.endsWith(".jar");
	}

	/**
	 * Gets whether the application is running in an IDE.
	 *
	 * @return If it is running in an IDE.
	 */
	public static boolean isRunningInIDE() {
		// IntelliJ
		if (System.getProperty("idea.launcher.port") != null) {
			return true;
		}
		// Eclipse
		if (System.getProperty("eclipse.application") != null) {
			return true;
		}
		// Check code source path
		String path = LoggerUtil.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath();
		return path.contains("out/production") || path.contains("bin/");
	}

	/**
	 * Detects the current runtime environment.
	 *
	 * @return The detected environment.
	 */
	public static RunEnvironment detectEnvironment() {
		if (isRunningInIDE()) {
			return RunEnvironment.IDE;
		} else if (isRunningFromJar()) {
			return RunEnvironment.JAR;
		} else {
			return RunEnvironment.CLASSPATH;
		}
	}

	/**
	 * Returns a boolean whether ANSI codes are allowed in the console.
	 *
	 * @return If ANSI codes are allowed.
	 */
	public static boolean ansiCodesAllowed() {
		return ansiCodesAllowed;
	}

	/**
	 * Toggle whether ANSI codes are allowed in the console.
	 *
	 * @param allow If they are allowed.
	 */
	public static void allowAnsiCodes(boolean allow) {
		ansiCodesAllowed = allow;
	}

	private RuntimeUtil() {
	}
}
