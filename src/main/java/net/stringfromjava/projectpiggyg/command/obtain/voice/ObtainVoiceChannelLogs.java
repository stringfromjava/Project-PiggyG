package net.stringfromjava.projectpiggyg.command.obtain.voice;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.stringfromjava.projectpiggyg.command.CommandListener;
import net.stringfromjava.projectpiggyg.data.command.CommandOptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Command for getting voice channel logs.
 */
public class ObtainVoiceChannelLogs extends CommandListener {

	public ObtainVoiceChannelLogs(String name) {
		super(name);
		description = "Gets logs from people interacting with and in voice channels.";
		helpDescription = """
				Gets all logs of people joining and leaving voice channels.
				You can also optionally put in a user an a channel to
				filter the logs as well. Only users with the "Manage server"
				permission can use this command.
				""";
		options = List.of(
				new CommandOptionData(OptionType.USER, "user", "An optional user to obtain specific logs from.", false),
				new CommandOptionData(OptionType.CHANNEL, "voice_channel", "An optional voice channel to obtain specific logs from.", false)
		);
		memberPermissions = DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER);
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		// TODO: Work on this later lol
	}
}
