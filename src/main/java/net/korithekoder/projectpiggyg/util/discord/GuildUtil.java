package net.korithekoder.projectpiggyg.util.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.korithekoder.projectpiggyg.data.Constants;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;

/**
 * Utility class for handling components specific to
 * Discord guilds (or servers, as called by normal discordians).
 */
public final class GuildUtil {

	public static void createNewServerFolder(Guild guild) {
		LoggerUtil.log(
				"PiggyG joined a new server. (NAME: " + guild.getName() + ", ID: " + guild.getId() + ")",
				LogType.INFO,
				false
		);
		String newServerPath = PathUtil.constructPath(Constants.APP_DATA_DIRECTORY, "servers", guild.getId());
		PathUtil.createDirectory(newServerPath);
	}

	private GuildUtil() {
	}
}
