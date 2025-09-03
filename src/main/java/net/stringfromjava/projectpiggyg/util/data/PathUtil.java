package net.stringfromjava.projectpiggyg.util.data;

import net.stringfromjava.projectpiggyg.util.Constants;
import net.stringfromjava.projectpiggyg.util.app.LogType;
import net.stringfromjava.projectpiggyg.util.app.LoggerUtil;
import net.stringfromjava.projectpiggyg.util.sys.PlatformType;
import net.stringfromjava.projectpiggyg.util.sys.SystemUtil;
import org.jetbrains.annotations.NotNull;

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
	@NotNull
	public static String createPath(String path) {
		return createPath(path, true);
	}

	/**
	 * Creates a directory at the specified path if it does not already exist.
	 *
	 * @param path    The path of the directory to create.
	 * @param logInfo Should the new log when creating the directory
	 *                be written to the current log file?
	 * @throws RuntimeException If an I/O error occurs while creating the directory.
	 */
	@NotNull
	public static String createPath(String path, boolean logInfo) {
		Path dirPath = Paths.get(path);
		try {
			if (!Files.exists(dirPath)) {
				if (logInfo) {
					LoggerUtil.log(STR."Creating new directory '\{path}'");
				}
				Files.createDirectories(dirPath);
			}
		} catch (IOException e) {
			if (logInfo) {
				LoggerUtil.error(STR."Failed to create directory: \{path}, got this error message: '\{e.getMessage()}'");
			} else {
				throw new RuntimeException(STR."Failed to create directory: \{path}!");
			}
		}
		return path;
	}

	/**
	 * Deletes a folder and it's contents by using recursion.
	 *
	 * @param path The folder to delete as a string.
	 */
	public static void deleteFolder(String path) {
		deleteFolder(Paths.get(path));
	}

	/**
	 * Deletes a folder and it's contents by using recursion.
	 *
	 * @param path The folder to delete as a {@link java.nio.file.Path}.
	 */
	public static void deleteFolder(Path path) {
		final int maxRetries = 10;
		final int retryDelayMs = 1000;  // 1 second
		int attempt = 0;
		while (true) {
			// Attempt to delete the given directory
			try {
				// Delete all files/folders inside the given folder
				if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
					try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
						for (Path entry : entries) {
							deleteFolder(entry);
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
							STR."Failed to delete directory '\{path.toString()}! Error Message: \{e.getMessage()}",
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
	 * Gets the file or folder path of a specific guild.
	 *
	 * @param guildId  The ID of the guild. This is also the name of the folder.
	 * @param toAppend Any other folder to add on to the path.
	 * @return The path to the guild folder.
	 */
	public static String fromGuildFolder(String guildId, String... toAppend) {
		StringBuilder toReturn = new StringBuilder(constructPath(Constants.System.APP_DATA_DIRECTORY, "guilds", guildId));
		for (String path : toAppend) {
			toReturn.append(path);
			toReturn.append(Constants.System.OS_PATH_SEPERATOR);
		}
		String s = toReturn.toString();
		return s.substring(0, s.length() - 1);
	}

	/**
	 * Gets a path to a folder or file inside the {@code blobcache} folder.
	 *
	 * @param guildId  The ID of the guild.
	 * @param toAppend Any other folder to add on to the path.
	 * @return The path to the {@code blobcache} folder (with the extra folders for
	 * the path provided).
	 */
	public static String fromGuildBlobCache(String guildId, String... toAppend) {
		StringBuilder toReturn = new StringBuilder(constructPath(
				Constants.System.APP_DATA_DIRECTORY,
				"guilds",
				guildId,
				Constants.System.GUILD_BLOB_CACHE_FOLDER_NAME
		));
		for (String path : toAppend) {
			toReturn.append(path);
			toReturn.append(Constants.System.OS_PATH_SEPERATOR);
		}
		String s = toReturn.toString();
		return s.substring(0, s.length() - 1);
	}

	/**
	 * Gets the path to the logs folder in a guild folder.
	 *
	 * @param guildId The ID of the guild.
	 * @return The path to the logs folder in a guild folder.
	 */
	public static String fromGuildLogs(String guildId, String... toAppend) {
		StringBuilder toReturn = new StringBuilder(constructPath(
				Constants.System.APP_DATA_DIRECTORY,
				"guilds",
				guildId,
				Constants.System.GUILD_LOG_FOLDER_NAME
		));
		for (String path : toAppend) {
			toReturn.append(path);
			toReturn.append(Constants.System.OS_PATH_SEPERATOR);
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
		StringBuilder toReturn = new StringBuilder(Constants.System.APP_DATA_DIRECTORY);
		for (String path : toAppend) {
			toReturn.append(Constants.System.OS_PATH_SEPERATOR);
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
			path.append(Constants.System.OS_PATH_SEPERATOR);
		}
		return path.toString();
	}

	/**
	 * Ensures a directory exists; if it doesn't, then
	 * it automatically creates it.
	 *
	 * @param path The path to ensure existence of.
	 * @return The path checked (if it needs to be used).
	 */
	public static String ensurePathExists(String path) {
		return ensurePathExists(path, true);
	}

	/**
	 * Ensures a directory exists; if it doesn't, then
	 * it automatically creates it.
	 *
	 * @param path                  The path to ensure existence of.
	 * @param logNonExistentWarning Should PiggyG log a warning when the path doesn't exist?
	 * @return The path checked (if it needs to be used).
	 */
	public static String ensurePathExists(String path, boolean logNonExistentWarning) {
		if (!doesPathExist(path)) {
			if (logNonExistentWarning) {
				LoggerUtil.log(
						STR."Directory '\{path}' is missing!",
						LogType.WARN,
						false
				);
			}
			createPath(path);
		}
		return path;
	}

	/**
	 * Checks if a directory exists. If the path passed down
	 * is a file path and not a folder, then {@code false} is
	 * passed down instead.
	 *
	 * @param path The path to check.
	 * @return If the path exists.
	 */
	public static boolean doesPathExist(@NotNull String path) {
		Path p = Path.of(path);
		return (Files.exists(p) && Files.isDirectory(p));
	}

	/**
	 * Gets the path to the file provided (without the
	 * name of the file included).
	 *
	 * @param file The file object to get the path from.
	 * @return The modified path.
	 */
	public static String getFilePath(File file) {
		String path = file.getPath();
		StringBuilder sb = new StringBuilder();
		char ops = Constants.System.OS_PATH_SEPERATOR;
		String[] brokenDownPath = path.split(STR."\{ops == '\\' ? "\\\\" : ops}");
		for (int i = 0; i < brokenDownPath.length - 1; i++) {
			sb.append(brokenDownPath[i]);
			sb.append(ops);
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}

	private PathUtil() {
	}
}
