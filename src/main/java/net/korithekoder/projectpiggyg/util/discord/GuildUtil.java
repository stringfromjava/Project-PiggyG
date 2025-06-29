package net.korithekoder.projectpiggyg.util.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.korithekoder.projectpiggyg.data.Constants;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import org.apache.commons.collections4.bag.CollectionBag;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Utility class for handling components specific to
 * Discord guilds (or servers, as called by normal discordians).
 */
public final class GuildUtil {

	/**
	 * Creates a brand-new folder with many files and folders inside.
	 *
	 * @param guild The new guild as a {@link net.dv8tion.jda.api.entities.Guild} object.
	 */
	public static void createNewServerFolder(Guild guild) {
		LoggerUtil.log(
				STR."PiggyG joined a new server. (NAME: \{guild.getName()}, ID: \{guild.getId()})",
				LogType.INFO,
				false
		);
		// Paths
		String newServerPath = PathUtil.constructPath(Constants.APP_DATA_DIRECTORY, "servers", guild.getId());
		String newLogPath = PathUtil.constructPath(Constants.APP_DATA_DIRECTORY, "servers", guild.getId(), "logs");

		// Create directories
		PathUtil.createDirectory(newServerPath);
		PathUtil.createDirectory(newLogPath);
		// Create files
		File configFile = FileUtil.createFile("config.json", newServerPath);
		File trollLogsFile = FileUtil.createFile("trolls.json", newLogPath);

		// Write to files with default content
		FileUtil.writeToFile(configFile, generateDefaultConfigJson());
		FileUtil.writeToFile(trollLogsFile, "[]");

//		System.out.println(FileUtil.getFileData(configFile));
	}

	/**
	 * Creates default data for a {@code config.json} file.
	 *
	 * @return A string of the default data.
	 */
	public static String generateDefaultConfigJson() {
		JSONObject config = new JSONObject("{}");
		config.put("disabletrollsglobally", false);
		config.put("blockedtrollusers", List.of());
		return config.toString(2);
	}

	private GuildUtil() {
	}
}
