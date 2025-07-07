package net.korithekoder.projectpiggyg.command.stupid;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import net.korithekoder.projectpiggyg.command.PiggyGCommand;
import net.korithekoder.projectpiggyg.util.data.DataUtil;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import net.korithekoder.projectpiggyg.util.discord.UserUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * The iconic command for sending anonymous DMs to users with PiggyG.
 */
public class TrollCommandListener extends PiggyGCommand {

	public TrollCommandListener(String name) {
		super(name);
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		User user = event.getOption("user").getAsUser();
		String message = event.getOption("message").getAsString();
		Message.Attachment attachment = null; // Reassign later since this option isn't required
		File attachmentAsFile = null;
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

		// Can only use this command on a guild!
		if (guild == null) {
			event.reply("Cuh' you really tryna troll yourself right now?\nStop playing yourself :sob::man_facepalming:").queue();
			return;
		}

		if (attachmentOM != null) {
			attachment = attachmentOM.getAsAttachment();
		}

		if (attachment != null) {
			attachmentAsFile = FileUtil.convertAttachmentToFile(attachment, guild.getId());
			if (attachmentAsFile != null) {
				attachmentAsFileUpload = FileUpload.fromData(attachmentAsFile, attachment.getFileName());
			} else {
				attachmentAsFileUpload = null;
			}
		} else {
			attachmentAsFileUpload = null;
		}

		// Send the troll message
		UserUtil.sendDirectMessage(
				user,
				message,
				(attachmentAsFileUpload != null) ? List.of(attachmentAsFileUpload) : null,
				onSuccess,
				onFailure
		);

		// Log the info
		File trollLogsFile = FileUtil.ensureFileExists(
				PathUtil.fromGuildFolder(guild.getId(), "logs", "trolls.json"),
				"[]"
		);
		JSONArray trollLogs = new JSONArray(FileUtil.getFileData(trollLogsFile));
		JSONObject newLog = new JSONObject("{}");
		newLog.put("sender-username", event.getUser().getName())
				.put("sender-id", event.getUser().getId())
				.put("receiver-username", user.getName())
				.put("receiver-id", user.getId())
				.put("time-sent", DataUtil.createCommandLogTime())
				.put("attachment-name", (attachmentAsFile != null) ? attachmentAsFile.getName() : "null")
				.put("attachment-url", (attachment != null) ? attachment.getUrl() : "null")
				.put("message", message);
		trollLogs.put(newLog);
		FileUtil.writeToFile(trollLogsFile, trollLogs.toString(2));
	}
}
