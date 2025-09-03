package net.stringfromjava.projectpiggyg.util.app;

import net.stringfromjava.projectpiggyg.util.Constants;
import net.stringfromjava.projectpiggyg.util.data.FileUtil;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for displaying logs in the console and in
 * PiggyG's {@code logs} folder.
 */
public final class LoggerUtil {

	private static File logFile;

	/**
	 * Configures the logging system.
	 */
	public static void configure() {
		logFile = FileUtil.createFile(STR."\{getFormattedLogTimes()[1]}.txt", PathUtil.ofAppData("logs"), false);
	}

	/**
	 * Logs info into the console and a text file that PiggyG created at startup.
	 *
	 * @param info The info to log.
	 */
	public static void log(String info) {
		log(info, LogType.INFO, true, false, true);
	}

	/**
	 * Logs info into the console and a text file that PiggyG created at startup.
	 *
	 * @param info The info to log.
	 * @param type The type of log being displayed.
	 */
	public static void log(String info, LogType type) {
		log(info, type, true, false, true);
	}

	/**
	 * Logs info into the console and a text file that PiggyG created at startup.
	 *
	 * @param info        The info to log.
	 * @param type        The type of log being displayed.
	 * @param includeDots Should the new log have {@code ...} at the end of it?
	 */
	public static void log(String info, LogType type, boolean includeDots) {
		log(info, type, includeDots, false, true);
	}

	/**
	 * Logs info into the console and a text file that PiggyG created at startup.
	 *
	 * @param info        The info to log.
	 * @param type        The type of log being displayed.
	 * @param includeDots Should the new log have {@code ...} at the end of it?
	 * @param emphasis    Should the log be underlined?
	 */
	public static void log(String info, LogType type, boolean includeDots, boolean emphasis) {
		log(info, type, includeDots, emphasis, true);
	}

	/**
	 * Logs a typical error message.
	 *
	 * @param info The error message to log.
	 */
	public static void error(String info) {
		log(info, LogType.ERROR, false, true, true);
	}

	/**
	 * Logs a typical error message.
	 *
	 * @param info        The error message to log.
	 * @param writeToFile Should this log be written to the current log file?
	 */
	public static void error(String info, boolean writeToFile) {
		log(info, LogType.ERROR, false, true, writeToFile);
	}

	/**
	 * Logs info into the console and a text file that PiggyG created at startup.
	 *
	 * @param info        The info to log.
	 * @param type        The type of log being displayed.
	 * @param includeDots Should the new log have {@code ...} at the end of it?
	 * @param emphasis    Should the log be underlined?
	 * @param writeToFile Should the new log be written to the current log file?
	 */
	public static void log(String info, LogType type, boolean includeDots, boolean emphasis, boolean writeToFile) {
		String fileLog = constructLog(info, type, includeDots, false, true);
		String consoleLog = constructLog(info, type, includeDots, emphasis, false);
		if (writeToFile) {
			FileUtil.writeToFile(logFile, STR."\{fileLog}\n", true);
		}
		System.out.println(consoleLog);
	}

	/**
	 * Gets the formatted log times for both the file and any log added
	 * inside the log file.
	 *
	 * @return A string array, with the first element being for regular logs, and
	 * the second being for the log file's name.
	 */
	public static String[] getFormattedLogTimes() {
		LocalDateTime now = LocalDateTime.now();
		String logTimeDisplay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		String fileTimeDisplay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
		return new String[]{ logTimeDisplay, fileTimeDisplay };
	}

	/**
	 * Gets and returns a copy of the current log file.
	 *
	 * @return A copy of the current log file that's being used.
	 */
	@Nullable
	public static File getLogFile() {
		return logFile == null ? null : new File(logFile.getPath());
	}

	private static String constructLog(String info, LogType type, boolean includeDots, boolean emphasis, boolean isFileLog) {
		StringBuilder log = new StringBuilder();
		String[] logTimes = getFormattedLogTimes();
		log.append(!isFileLog ? getLogColor(type, emphasis) : "");
		log.append(STR."\{logTimes[0]} ");
		log.append("[PIGGYG] ");
		log.append(STR."[\{type}] ");
		log.append(info);
		log.append(includeDots ? "..." : "");
		log.append(!isFileLog ? Constants.Debug.CONSOLE_TEXT_RESET : "");
		return log.toString();
	}

	private static String getLogColor(LogType type, boolean emphasis) {
		String toReturn = Constants.Debug.CONSOLE_TEXT_BOLD;
		if (emphasis) {
			toReturn += Constants.Debug.CONSOLE_TEXT_UNDERLINE;
		}
		switch (type) {
			case INFO -> toReturn += Constants.Debug.CONSOLE_TEXT_PINK;
			case WARN -> toReturn += Constants.Debug.CONSOLE_TEXT_YELLOW;
			case ERROR -> toReturn += Constants.Debug.CONSOLE_TEXT_RED;
		}
		return toReturn;
	}
}
