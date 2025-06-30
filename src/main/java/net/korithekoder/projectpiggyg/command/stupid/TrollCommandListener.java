package net.korithekoder.projectpiggyg.command.stupid;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import net.korithekoder.projectpiggyg.command.PiggyGCommand;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.discord.UserUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * The iconic command for sending anonymous DMs to users with PiggyG.
 */
public class TrollCommandListener extends PiggyGCommand {

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		super.onSlashCommandInteraction(event);

		if (!event.getName().equals("troll")) {
			return;
		}

		Guild guild = event.getGuild();
		User user = event.getOption("user").getAsUser();
		String message = event.getOption("message").getAsString();
		Message.Attachment attachment = null; // Reassign later since this option isn't required
		OptionMapping attachmentOM = event.getOption("attachment");
		FileUpload attachmentAsFileUpload;
		Runnable onSuccess = () -> {
			event.reply("'Ight gang, the troll was sent").setEphemeral(true).queue();
		};
		Runnable onFailure = () -> {
			event.reply("Sorry bruv, but the troll didn't send... :pensive:").setEphemeral(true).queue();
		};

		// Prevent the user from sending a troll to a bot
		if (user.isBot()) {
			event.reply("Pigga you can't send trolls to bots dumbass :unamused:").setEphemeral(true).queue();
			return;
		}

		// Can only use this command on a server!
		if (guild == null) {
			event.reply("Cuh' you really tryna troll yourself right now?\nStop playing yourself :sob::man_facepalming:").queue();
			return;
		}

		if (attachmentOM != null) {
			attachment = attachmentOM.getAsAttachment();
		}

		if (attachment != null) {
			File file = FileUtil.convertAttachmentToFile(attachment, guild.getId());
			if (file != null) {
				attachmentAsFileUpload = FileUpload.fromData(file, attachment.getFileName());
			} else {
				attachmentAsFileUpload = null;
			}
		} else {
			attachmentAsFileUpload = null;
		}

		if (attachmentAsFileUpload == null) {
			UserUtil.sendDirectMessage(user, message, List.of(), onSuccess, onFailure);
		} else {
			UserUtil.sendDirectMessage(user, message, List.of(attachmentAsFileUpload), onSuccess, onFailure);
		}
	}
}
