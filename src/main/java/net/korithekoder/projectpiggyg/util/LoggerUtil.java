package net.korithekoder.projectpiggyg.util;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for displaying logs in the console and in
 * PiggyG's {@code logs} folder.
 */
public final class LoggerUtil {

	private static String logTimeDisplay;
	private static String fileTimeDisplay;

	private static File logFile;

	/**
	 * Configures the logging system.
	 */
	public static void configure() {
		LocalDateTime now = LocalDateTime.now();
		logTimeDisplay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		fileTimeDisplay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));

		logFile = FileUtil.createFile(fileTimeDisplay + ".txt", PathUtil.ofAppData("logs"), false);
	}

	/**
	 * Logs info into a text file that PiggyG created at startup.
	 *
	 * @param info The info to log.
	 */
	public static void log(String info) {
		log(info, LogType.INFO, true, true);
	}

	/**
	 * Logs info into a text file that PiggyG created at startup.
	 *
	 * @param info The info to log.
	 * @param type The type of log being displayed.
	 */
	public static void log(String info, LogType type) {
		log(info, type, true, true);
	}

	/**
	 * Logs info into a text file that PiggyG created at startup.
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
		String log = constructLog(info, type, includeDots);
		if (writeToFile) {
			FileUtil.writeToFile(logFile, log + "\n");
		}
		System.out.println(log);
	}

	private static String constructLog(String info, LogType type, boolean includeDots) {
		StringBuilder log = new StringBuilder();
		log.append(logTimeDisplay + " ");
		log.append("[PIGGYG] ");
		log.append("[" + type + "] ");
		log.append(info);
		log.append(includeDots ? "..." : "");
		return log.toString();
	}
}
