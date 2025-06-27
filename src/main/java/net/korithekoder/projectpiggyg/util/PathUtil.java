package net.korithekoder.projectpiggyg.util;

import net.korithekoder.projectpiggyg.data.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for creating, managing, shortening, and manipulating file directories.
 */
public final class PathUtil {

	/**
	 * Creates a directory at the specified path if it does not already exist.
	 *
	 * @param path The path of the directory to create.
	 * @throws RuntimeException If an I/O error occurs while creating the directory.
	 */
	public static void createDirectory(String path) {
		createDirectory(path, true);
	}

	/**
	 * Creates a directory at the specified path if it does not already exist.
	 *
	 * @param path    The path of the directory to create.
	 * @param logInfo Should the new log when creating the directory
	 *                be written to the current log file?
	 * @throws RuntimeException If an I/O error occurs while creating the directory.
	 */
	public static void createDirectory(String path, boolean logInfo) {
		Path dirPath = Paths.get(path);
		try {
			if (!Files.exists(dirPath)) {
				if (logInfo) {
					LoggerUtil.log("Creating new directory '" + path + "'", LogType.INFO, true);
				}
				Files.createDirectories(dirPath);
			}
		} catch (IOException e) {
			if (logInfo) {
				LoggerUtil.log("Failed to create directory: " + path + "!", LogType.ERROR, false);
			} else {
				throw new RuntimeException("Failed to create directory: " + path + "!");
			}
		}
	}

	/**
	 * Gets the full pathway to the project folder for PiggyG.
	 *
	 * @return The directory to PiggyG's project folder.
	 */
	public static String getUserHomePath() {
		String userHome = System.getProperty("user.home");
		PlatformType platformType = SystemUtil.getPlatformType();
		switch (platformType) {
			case WINDOWS -> {
				return System.getenv("APPDATA");
			}
			case MACOS -> {
				return Paths.get(userHome, "Library", "Application Support").toString();
			}
			case LINUX -> {
				return Paths.get(userHome, ".config").toString();
			}
			default -> throw new RuntimeException("PiggyG doesn't support the platform \"" + platformType + "\"!");
		}
	}

	/**
	 * Gets a pathway to either a file or folder from PiggyG's
	 * folder in the user's app data directory.
	 *
	 * @param toAppend Strings that will be added alongside of the project's folder's path.
	 * @return The path with the appended strings with it.
	 */
	public static String ofAppData(String... toAppend) {
		StringBuilder toReturn = new StringBuilder(Constants.PROJECT_DIRECTORY);
		for (String path : toAppend) {
			toReturn.append(Constants.OS_SLASH);
			toReturn.append(path);
		}
		return toReturn.toString();
	}

	private PathUtil() {
	}
}
