package net.korithekoder.projectpiggyg.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import net.korithekoder.projectpiggyg.util.discord.GuildUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple class for new commands to extend to.
 * All this really does is make every command do checks when things
 * don't exist.
 */
public class PiggyGCommand extends ListenerAdapter {

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();

		// Check if the guild's folder exists
		// (if the command was run on one)
		if (guild != null) {
			Path guildPath = Paths.get(PathUtil.fromGuildFolder(guild.getId()));
			if (!guildPath.toFile().exists()) {
				LoggerUtil.log(
						STR."Guild folder for guild \{guild.getName()} (ID = \{guild.getId()} doesn't exist! Creating guild directory",
						LogType.WARN,
						true
				);
				GuildUtil.createNewServerFolder(guild);
			}
		}
	}
}
