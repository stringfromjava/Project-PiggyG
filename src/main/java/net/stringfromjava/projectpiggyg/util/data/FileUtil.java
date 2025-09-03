package net.stringfromjava.projectpiggyg.util.data;

import net.dv8tion.jda.api.entities.Message;
import net.stringfromjava.projectpiggyg.util.Constants;
import net.stringfromjava.projectpiggyg.util.app.LogType;
import net.stringfromjava.projectpiggyg.util.app.LoggerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;

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
	public static File createFile(@NotNull String name, @NotNull String path, boolean logInfo) {
		String newPath = path + Constants.System.OS_PATH_SEPERATOR + name;
		File newFile = new File(newPath);

		// Create the new file
		if (!newFile.exists()) {
			try {
				if (logInfo) {
					LoggerUtil.log(STR."Creating new file in '\{newPath}'");
				}
				newFile.createNewFile();
			} catch (IOException e) {
				if (logInfo) {
					LoggerUtil.log(
							STR."File '\{name}' could not be created in '\{path}', got this error message: '\{e.getMessage()}'",
							LogType.ERROR,
							false
					);
				} else {
					throw new RuntimeException(STR."File '\{name}' could not be created in '\{path}'!");
				}
			}
		}

		return newFile;
	}

	/**
	 * Attempts to delete a file and logs if successful.
	 *
	 * @param path The path of the file to delete.
	 * @return If the deletion was successful.
	 */
	public static boolean deleteFile(String path) {
		File toDelete = ensureFileExists(path);
		boolean deleted = toDelete.delete();
		if (deleted) {
			LoggerUtil.log(STR."Successfully deleted file in path '\{path}'.", LogType.INFO, false);
		} else {
			LoggerUtil.log(STR."Failed to delete file in path '\{path}'.", LogType.WARN, false);
		}
		return deleted;
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
					"Tried to write to file, but it was null.",
					LogType.ERROR,
					false,
					false
			);
			return;
		}

		if (!file.exists()) {
			LoggerUtil.log(
					STR."Given file '\{file.getName()}' doesn't exist! Creating new file",
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
					STR."Failed to write new contents to file, got this error message: \{e.getMessage()}",
					LogType.ERROR,
					false
			);
		}
	}

	/**
	 * Gets the contents of a file.
	 *
	 * @param file The file to collect data from.
	 * @return All data from the file as a string.
	 */
	public static String getFileData(File file) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append(System.lineSeparator());
			}
		} catch (IOException e) {
			LoggerUtil.log(
					STR."Could not obtain data from file '\{file.getName()}'.",
					LogType.ERROR,
					false
			);
			return "";
		}
		return sb.toString();
	}

	/**
	 * Ensures the existence of a file; if it does NOT exist, then
	 * a new file (with the same name) will be created.
	 *
	 * @param filePath The path to the file.
	 * @return The file instance.
	 */
	public static File ensureFileExists(String filePath) {
		return ensureFileExists(filePath, "", true);
	}

	/**
	 * Ensures the existence of a file; if it does NOT exist, then
	 * a new file (with the same name) will be created.
	 *
	 * @param filePath The path to the file.
	 * @param contents Data to add to the file (if it doesn't exist).
	 * @return The file instance.
	 */
	public static File ensureFileExists(String filePath, String contents) {
		return ensureFileExists(filePath, contents, true);
	}

	/**
	 * Ensures the existence of a file; if it does NOT exist, then
	 * a new file (with the same name) will be created.
	 *
	 * @param filePath              The path to the file.
	 * @param contents              Data to add to the file (if it doesn't exist).
	 * @param logNonExistentWarning Should PiggyG log when the file doesn't exist?
	 * @return The file instance.
	 */
	public static File ensureFileExists(String filePath, String contents, boolean logNonExistentWarning) {
		File file = Paths.get(filePath).toFile();
		if (!file.exists()) {
			if (logNonExistentWarning) {
				LoggerUtil.log(
						STR."File '\{file.getName()}' in '\{filePath}' is missing!",
						LogType.WARN,
						false
				);
			}
			createFile(file.getName(), PathUtil.ensurePathExists(PathUtil.getFilePath(file)));
			writeToFile(file, contents);
		}
		return file;
	}

	/**
	 * Converts a {@link net.dv8tion.jda.api.entities.Message.Attachment}
	 * to a {@link java.io.File}.
	 *
	 * @param attachment The attachment to convert.
	 * @param path       The path that the converted file will be stored in.
	 * @return The attachment as a {@link java.io.File} object.
	 */
	@Nullable
	public static File convertAttachmentToFile(Message.Attachment attachment, String path) {
		return convertAttachmentToFile(attachment, path, true);
	}

	/**
	 * Converts a {@link net.dv8tion.jda.api.entities.Message.Attachment}
	 * to a {@link java.io.File}.
	 *
	 * @param attachment            The attachment to convert.
	 * @param path                  The path that the converted file will be stored in.
	 * @param logNonExistentWarning Should PiggyG log a warning when the specified path doesn't exist?
	 * @return The attachment as a {@link java.io.File} object.
	 */
	@Nullable
	public static File convertAttachmentToFile(Message.Attachment attachment, String path, boolean logNonExistentWarning) {
		try (InputStream in = new URL(attachment.getUrl()).openStream()) {
			// Make sure the passed path exists
			PathUtil.ensurePathExists(path, logNonExistentWarning);
			// Copy the data from the attachment to the file
			File file = new File(PathUtil.constructPath(path, attachment.getFileName()));
			try (OutputStream out = new FileOutputStream(file)) {
				byte[] buffer = new byte[8192];
				int len;
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				return file;
			}
		} catch (IOException e) {
			LoggerUtil.log(
					STR."Failed to convert an attachment to a file, got this error message: '\{e.getMessage()}'",
					LogType.ERROR,
					false
			);
			return null;
		}
	}

	/**
	 * Used for removing the file extension from a file's name.
	 *
	 * @param path The directory of the file to obtain.
	 * @return The filename (without the extension).
	 */
	public static String getNameWithoutExtension(String path) {
		int dotIndex = path.lastIndexOf('.');
		return (dotIndex == -1) ? path : path.substring(0, dotIndex);
	}

	private FileUtil() {
	}
}
