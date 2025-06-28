package net.korithekoder.projectpiggyg.util;

import net.korithekoder.projectpiggyg.Initialize;

/**
 * Utility class for either obtaining or handling things
 * from/with specifically the user's computer system.
 */
public final class SystemUtil {

	/**
	 * Gets the platform type that PiggyG is currently being run on.
	 *
	 * @return The platform...
	 */
	public static PlatformType getPlatformType() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return PlatformType.WINDOWS;
		} else if (os.contains("mac")) {
			return PlatformType.MACOS;
		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			return PlatformType.LINUX;
		} else {
			throw new RuntimeException("PiggyG doesn't support the current platform!");
		}
	}

	private SystemUtil() {
	}
}
