package net.korithekoder.projectpiggyg.command.stupid;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.korithekoder.projectpiggyg.util.discord.UserUtil;
import org.jetbrains.annotations.NotNull;

/**
 * The iconic command for sending anonymous DMs to users with PiggyG.
 */
public class TrollCommandListener extends ListenerAdapter {

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		if (!event.getName().equals("troll")) {
			return;
		}

		User user = event.getOption("user").getAsUser();
		String message = event.getOption("message").getAsString();

		Runnable onSuccess = () -> {
			event.reply("'Ight gang, the troll was sent").setEphemeral(true).queue();
		};
		Runnable onFailure = () -> {
			event.reply("Sorry bruv, but the troll didn't send... :pensive:").setEphemeral(true).queue();
		};

		UserUtil.sendDirectMessage(user, message, onSuccess, onFailure);
	}
}
