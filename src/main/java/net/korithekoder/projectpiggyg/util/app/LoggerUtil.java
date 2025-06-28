package net.korithekoder.projectpiggyg.util.app;

import net.korithekoder.projectpiggyg.data.Constants;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;

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
		logFile = FileUtil.createFile(getFormattedLogTimes()[1] + ".txt", PathUtil.ofAppData("logs"), false);
	}

	/**
	 * Logs info into the console and a text file that PiggyG created at startup.
	 *
	 * @param info The info to log.
	 */
	public static void log(String info) {
		log(info, LogType.INFO, true, true);
	}

	/**
	 * Logs info into the console and a text file that PiggyG created at startup.
	 *
	 * @param info The info to log.
	 * @param type The type of log being displayed.
	 */
	public static void log(String info, LogType type) {
		log(info, type, true, true);
	}

	/**
	 * Logs info into the console and a text file that PiggyG created at startup.
	 *
	 * @param info        The info to log.
	 * @param type        The type of log being displayed.
	 * @param includeDots Should the new log have {@code ...} at the end of it?
	 */
	public static void log(String info, LogType type, boolean includeDots) {
		log(info, type, includeDots, true);
	}

	/**
	 * Logs info into the console and a text file that PiggyG created at startup.
	 *
	 * @param info        The info to log.
	 * @param type        The type of log being displayed.
	 * @param includeDots Should the new log have {@code ...} at the end of it?
	 * @param writeToFile Should the new log be written to the current log file?
	 */
	public static void log(String info, LogType type, boolean includeDots, boolean writeToFile) {
		String fileLog = constructLog(info, type, includeDots, true);
		String consoleLog = constructLog(info, type, includeDots, false);
		if (writeToFile) {
			FileUtil.writeToFile(logFile, fileLog + "\n", true);
		}
		System.out.println(consoleLog);
	}

	private static String constructLog(String info, LogType type, boolean includeDots, boolean isFileLog) {
		StringBuilder log = new StringBuilder();
		String[] logTimes = getFormattedLogTimes();
		log.append(!isFileLog ? getLogColor(type) : "");
		log.append(logTimes[0] + " ");
		log.append("[PIGGYG] ");
		log.append("[" + type + "] ");
		log.append(info);
		log.append(includeDots ? "..." : "");
		log.append(!isFileLog ? Constants.CONSOLE_TEXT_RESET : "");
		return log.toString();
	}

	private static String[] getFormattedLogTimes() {
		LocalDateTime now = LocalDateTime.now();
		String logTimeDisplay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		String fileTimeDisplay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
		return new String[]{ logTimeDisplay, fileTimeDisplay };
	}

	private static String getLogColor(LogType type) {
		String toReturn = Constants.CONSOLE_TEXT_BOLD;
		switch (type) {
			case INFO -> toReturn += Constants.CONSOLE_TEXT_PINK;
			case WARN -> toReturn += Constants.CONSOLE_TEXT_YELLOW;
			case ERROR -> toReturn += Constants.CONSOLE_TEXT_UNDERLINE + Constants.CONSOLE_TEXT_RED;
		}
		return toReturn;
	}
}
