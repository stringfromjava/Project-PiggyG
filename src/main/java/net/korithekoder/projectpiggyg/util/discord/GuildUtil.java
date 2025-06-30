package net.korithekoder.projectpiggyg.util.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
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
		// Paths
		String guildId = guild.getId();
		String newGuildPath = PathUtil.fromGuildFolder(guildId);
		String newLogPath = PathUtil.fromGuildFolder(guildId, "logs");
		String newUAPath = PathUtil.fromGuildFolder(guildId, "trollattachments");

		// Create directories
		PathUtil.createPath(newGuildPath);
		PathUtil.createPath(newLogPath);
		PathUtil.createPath(newUAPath);
		// Create files
		File configFile = FileUtil.createFile("config.json", newGuildPath);
		File trollLogsFile = FileUtil.createFile("trolls.json", newLogPath);

		// Write to files with default content
		FileUtil.writeToFile(configFile, generateDefaultConfigJson());
		FileUtil.writeToFile(trollLogsFile, "[]");
	}

	/**
	 * Creates default data for a {@code config.json} file.
	 *
	 * @return A string of the default data.
	 */
	public static String generateDefaultConfigJson() {
		JSONObject config = new JSONObject("{}");
		config.put("disable-trolls-globally", false);
		config.put("blocked-troll-users", List.of());
		return config.toString(2);
	}

	private GuildUtil() {
	}
}
