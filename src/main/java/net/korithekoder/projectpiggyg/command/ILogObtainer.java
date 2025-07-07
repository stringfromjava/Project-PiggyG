package net.korithekoder.projectpiggyg.command;

import org.json.JSONObject;

/**
 * Interface for commands that specifically get logs.
 */
public interface ILogObtainer {

	/**
	 * Generates a new log that will being displayed inside a text file.
	 *
	 * @param info A {@link org.json.JSONObject} which contains all the log info.
	 * @return A new {@code String} with all info in the way it was formatted inside the method.
	 */
	String generateTextLog(JSONObject info);
}
