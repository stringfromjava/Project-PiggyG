package net.korithekoder.projectpiggyg.util.data;

import net.korithekoder.projectpiggyg.data.Constants;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class for handling files on the user's computer.
 */
public final class FileUtil {

	/**
	 * Creates a file at the specified pathway.
	 *
	 * @param name The name of the file. Some examples might be
	 *             {@code my-text-file.txt} or {@code my-json-file.json}.
	 * @param path The path to create the new file.
	 * @return The new file instance.
	 */
	@NotNull
	public static File createFile(String name, String path) {
		return createFile(name, path, true);
	}

	/**
	 * Creates a file at the specified pathway.
	 *
	 * @param name    The name of the file. Some examples might be
	 *                {@code my-text-file.txt} or {@code my-json-file.json}.
	 * @param path    The path to create the new file.
	 * @param logInfo Should PiggyG log that a new file was created?
	 * @return The new file instance.
	 */
	@NotNull
	public static File createFile(String name, String path, boolean logInfo) {
		String newPath = path + Constants.OS_FILE_SLASH + name;
		File newFile = new File(newPath);

		// Create the new file
		if (!newFile.exists()) {
			try {
				if (logInfo) {
					LoggerUtil.log("Creating new file in '" + newPath + "'");
				}
				newFile.createNewFile();
			} catch (IOException e) {
				if (logInfo) {
					LoggerUtil.log("File '" + name + "' could not be created in '" + path + "'!", LogType.ERROR, false);
				} else {
					throw new RuntimeException("File '" + name + "' could not be created in '" + path + "'!");
				}
			}
		}

		return newFile;
	}

	/**
	 * Writes to a file with simplicity and ease.
	 *
	 * @param file     The file object to write to.
	 * @param contents The data to write to the file with.
	 */
	public static void writeToFile(File file, String contents) {
		writeToFile(file, contents, false);
	}

	/**
	 * Writes to a file with simplicity and ease.
	 *
	 * @param file     The file object to write to.
	 * @param contents The data to write to the file with.
	 * @param append   Should the contents be added or overwritten?
	 */
	public static void writeToFile(File file, String contents, boolean append) {
		if (file == null) {
			LoggerUtil.log(
					"Tried to write to file, but it was null!",
					LogType.ERROR,
					false,
					false
			);
			return;
		}

		if (!file.exists()) {
			LoggerUtil.log(
					"Given file '" + file.getName() + "' doesn't exist! Creating new file",
					LogType.WARN,
					true,
					false
			);
		}

		try {
			FileWriter writer = new FileWriter(file, append);
			writer.write(contents);
			writer.close();
		} catch (IOException e) {
			LoggerUtil.log(
					"Failed to write new contents to file, got this error message: " + e.getMessage(),
					LogType.ERROR,
					false
			);
		}
	}

	private FileUtil() {
	}
}
