package net.korithekoder.projectpiggyg.util.data;

import net.korithekoder.projectpiggyg.data.Constants;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import net.korithekoder.projectpiggyg.util.sys.PlatformType;
import net.korithekoder.projectpiggyg.util.sys.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

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
	public static void createPath(String path) {
		createPath(path, true);
	}

	/**
	 * Creates a directory at the specified path if it does not already exist.
	 *
	 * @param path    The path of the directory to create.
	 * @param logInfo Should the new log when creating the directory
	 *                be written to the current log file?
	 * @throws RuntimeException If an I/O error occurs while creating the directory.
	 */
	public static void createPath(String path, boolean logInfo) {
		Path dirPath = Paths.get(path);
		try {
			if (!Files.exists(dirPath)) {
				if (logInfo) {
					LoggerUtil.log(STR."Creating new directory '\{path}'", LogType.INFO, true);
				}
				Files.createDirectories(dirPath);
			}
		} catch (IOException e) {
			if (logInfo) {
				LoggerUtil.log(STR."Failed to create directory: \{path}!", LogType.ERROR, false);
			} else {
				throw new RuntimeException(STR."Failed to create directory: \{path}!");
			}
		}
	}

	/**
	 * Deletes a folder and it's contents by using recursion.
	 *
	 * @param path The folder to delete.
	 */
	public static void deletePath(Path path) {
		final int maxRetries = 10;
		final int retryDelayMs = 1000;  // 1 second
		int attempt = 0;
		while (true) {
			// Attempt to delete the given directory
			try {
				// Delete all files/folders inside the given folder.
				if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
					try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
						for (Path entry : entries) {
							deletePath(entry);
						}
					}
				}
				// Delete the folder after everything inside it has been deleted
				Files.delete(path);
				break;
			} catch (IOException e) {
				// Failed to delete directory, try again
				attempt++;
				// Give up if too many attempts were made
				if (attempt >= maxRetries) {
					LoggerUtil.log(
							DataUtil.buildString(
									"Failed to delete directory '",
									path.toString(),
									"'!",
									"Error Message: ",
									e.getMessage()
							),
							LogType.WARN,
							false
					);
					break;
				}
				// Make a thread to add a small delay
				// between attempts on deleting the given directory
				try {
					Thread.sleep(retryDelayMs);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
	}

	/**
	 * Gets the folder path to a specific guild.
	 *
	 * @param guildId The ID of the guild. This is also the name of the folder.
	 * @return The path to the guild folder.
	 */
	public static String fromGuildFolder(String guildId, String... toAppend) {
		StringBuilder toReturn = new StringBuilder(constructPath(Constants.APP_DATA_DIRECTORY, "guilds", guildId));
		for (String folder : toAppend) {
			toReturn.append(folder);
			toReturn.append(Constants.OS_FILE_SLASH);
		}
		String s = toReturn.toString();
		return s.substring(0, s.length() - 1);
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
			default -> throw new RuntimeException(STR."PiggyG doesn't support the platform \"\{platformType}\"!");
		}
	}

	/**
	 * Gets a pathway to either a file or folder from PiggyG's
	 * folder in the user's app data directory.
	 *
	 * @param toAppend Strings that will be added alongside the project's folder's path.
	 * @return The path with the appended strings with it.
	 */
	public static String ofAppData(String... toAppend) {
		StringBuilder toReturn = new StringBuilder(Constants.APP_DATA_DIRECTORY);
		for (String path : toAppend) {
			toReturn.append(Constants.OS_FILE_SLASH);
			toReturn.append(path);
		}
		return toReturn.toString();
	}

	/**
	 * Constructs a directory with ease using the current
	 * OS's file slash character without having to do
	 * {@code "folder" + Constants.OS_FILE_SLASH + "folder"} many times.
	 *
	 * @param folders The different folder(s) to construct together.
	 * @return A pathway with the different folder(s) passed down with the
	 * correct OS file slash.
	 */
	public static String constructPath(String... folders) {
		StringBuilder path = new StringBuilder();
		for (String folder : folders) {
			path.append(folder);
			path.append(Constants.OS_FILE_SLASH);
		}
		return path.toString();
	}

	/**
	 * Ensures a directory exists; if it doesn't, then
	 * it automatically creates it.
	 *
	 * @param path The path to ensure existence of.
	 */
	public static void ensurePathExists(String path) {
		File toCheck = Paths.get(path).toFile();
		if (!toCheck.exists()) {
			LoggerUtil.log(
					STR."Directory '\{path}' is missing!",
					LogType.WARN,
					false
			);
			createPath(path);
		}
	}

	private PathUtil() {
	}
}
