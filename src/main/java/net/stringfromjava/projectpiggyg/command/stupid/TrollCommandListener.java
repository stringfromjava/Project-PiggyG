package net.stringfromjava.projectpiggyg.command.stupid;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;
import net.stringfromjava.projectpiggyg.command.CommandListener;
import net.stringfromjava.projectpiggyg.util.Constants;
import net.stringfromjava.projectpiggyg.data.command.CommandOptionData;
import net.stringfromjava.projectpiggyg.util.data.JsonUtil;
import net.stringfromjava.projectpiggyg.util.data.FileUtil;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import net.stringfromjava.projectpiggyg.util.discord.CommandUtil;
import net.stringfromjava.projectpiggyg.util.discord.UserUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * The iconic command for sending anonymous DMs to users with PiggyG.
 */
public class TrollCommandListener extends CommandListener {

	public TrollCommandListener(String name) {
		super(name);
		description = "Send an anonymous DM to a user on the server.";
		helpDescription = """
				Allows you to send an anonymous DM with PiggyG to anyone
				on the server that either hasn't blocked trolls (or hasn't blocked
				PiggyG entirely :broken_heart:).
				""";
		options = List.of(
				new CommandOptionData(OptionType.USER, "user", "The user to troll.", true),
				new CommandOptionData(OptionType.STRING, "message", "The message to send.", true),
				new CommandOptionData(OptionType.ATTACHMENT, "attachment", "An optional attachment to send with your stupid message.", false)
		);
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		User receiver = event.getOption("user").getAsUser();
		String message = event.getOption("message").getAsString();
		Message.Attachment attachment = null; // Reassign later since this option isn't required
		File attachmentAsFile = null;
		OptionMapping attachmentOM = event.getOption("attachment");
		FileUpload attachmentAsFileUpload;
		Runnable onSuccess = () -> CommandUtil.sendSafeReply(
				"'Ight gang, the troll was sent",
				event,
				null,
				true
		);
		Runnable onFailure = () -> CommandUtil.sendSafeReply(
				"Sorry bruv, but the troll didn't send... :pensive:",
				event,
				null,
				true
		);

		// Prevent the user from sending a troll to a bot
		if (receiver.isBot()) {
			event.reply("Pigga you can't send trolls to bots dumbass :unamused:").setEphemeral(true).queue();
			return;
		}

		if (attachmentOM != null) {
			attachment = attachmentOM.getAsAttachment();
		}

		if (attachment != null) {
			String taPath = PathUtil.fromGuildFolder(guild.getId(), "trollattachments");
			attachmentAsFile = FileUtil.convertAttachmentToFile(attachment, taPath);
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
				receiver,
				message,
				(attachmentAsFileUpload != null) ? List.of(attachmentAsFileUpload) : null,
				onSuccess,
				onFailure
		);

		File trollLogsFile = FileUtil.ensureFileExists(
				PathUtil.fromGuildLogs(guild.getId(), Constants.System.TROLL_LOG_FILE_NAME),
				"[]"
		);
		JSONArray trollLogs = new JSONArray(FileUtil.getFileData(trollLogsFile));
		JSONObject newLog = new JSONObject();
		newLog.put("author", JsonUtil.createUserInfoJson(event.getUser()))
				.put("receiver", JsonUtil.createUserInfoJson(receiver))
				.put("time", JsonUtil.getCurrentTimeJson())
				.put("attachment", new JSONObject()
						.put("name", (attachmentAsFile != null) ? attachmentAsFile.getName() : "null")
						.put("url", (attachment != null) ? attachment.getUrl() : "null"))
				.put("message", message);
		trollLogs.put(newLog);
		FileUtil.writeToFile(trollLogsFile, trollLogs.toString(2));
	}
}
