package net.korithekoder.projectpiggyg.command.obtain;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.korithekoder.projectpiggyg.command.PiggyGCommand;
import org.jetbrains.annotations.NotNull;

/**
 * Command for getting voice channel logs.
 */
public class ObtainVoiceChannelLogs extends PiggyGCommand {

	public ObtainVoiceChannelLogs(String name) {
		super(name);
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		// TODO: Work on this later lol
	}
}
