package net.stringfromjava.projectpiggyg.event.guild;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.stringfromjava.projectpiggyg.data.Constants;
import net.stringfromjava.projectpiggyg.util.app.LogType;
import net.stringfromjava.projectpiggyg.util.app.LoggerUtil;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import net.stringfromjava.projectpiggyg.util.discord.GuildUtil;
import net.stringfromjava.projectpiggyg.util.discord.UserUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

/**
 * Events when PiggyG joins/leaves a guild.
 */
public class JoinLeaveGuildEventListener extends ListenerAdapter {

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event) {
		Guild newGuild = event.getGuild();
		User guildOwner = newGuild.getOwner().getUser();
		GuildUtil.createNewGuildFolder(newGuild);
		UserUtil.sendDirectMessage(guildOwner, Constants.Discord.NEW_GUILD_DM_MESSAGE);
		LoggerUtil.log(
				STR."PiggyG joined a new guild. (NAME: '\{newGuild.getName()}', ID: '\{newGuild.getId()}')",
				LogType.INFO,
				false
		);
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event) {
		Guild guild = event.getGuild();
		String guildFolder = PathUtil.ofAppData("guilds", guild.getId());
		PathUtil.deleteFolder(Paths.get(guildFolder));
	}
}
