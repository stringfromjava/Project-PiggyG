package net.stringfromjava.projectpiggyg.command;

import net.stringfromjava.projectpiggyg.util.app.LogType;
import net.stringfromjava.projectpiggyg.util.app.LoggerUtil;
import net.stringfromjava.projectpiggyg.util.data.FileUtil;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Abstract class for commands specifically for getting logs.
 */
public abstract class LogObtainerCommandListener extends CommandListener implements ILogObtainer {

	public LogObtainerCommandListener(String name) {
		super(name);
		isGuildCommand = true; // Must always be a guild command
	}

	/**
	 * Creates a temporary log file meant to be sent to the user
	 * when they trigger {@code this} log command.
	 * <p>
	 * NOTE: It is designed for you to override {@code generateTextLog()}, as that's how you will
	 * handle what it looks like in the said log file.
	 *
	 * @param logs A {@link org.json.JSONArray} with all logs. Note that each log needs to
	 *             be a {@link org.json.JSONObject}, anything else will simply be ignored.
	 * @param fileName The name of the new to-be-sent log file. (This DOES NOT include the extension.)
	 * @return The new log file. If it fails to create, then {@code null} is returned instead.
	 */
	@Nullable
	protected final File generateLogFile(JSONArray logs, String fileName) {
		// Create a temporary text file to send with the logs
		try {
			File toReturn = Files.createTempFile(fileName, ".txt").toFile();
			StringBuilder fileInfo = new StringBuilder();

			// Generate all logs into the temporary text file
			for (Object info : logs) {
				// Only allow JSON objects since that's what a log is anyway
				if (!(info instanceof JSONObject infoJson)) {
					continue;
				}
				fileInfo.append(STR."\{generateTextLog(infoJson)}\n");
			}

			FileUtil.writeToFile(toReturn, fileInfo.toString());
			return toReturn;
		} catch (IOException e) {
			LoggerUtil.log(
					STR."Failed to generate the needed log file, got this error: '\{e.getMessage()}'",
					LogType.ERROR,
					false
			);
			return null;
		}
	}
}
