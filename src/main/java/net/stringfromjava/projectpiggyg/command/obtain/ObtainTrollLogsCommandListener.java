package net.stringfromjava.projectpiggyg.command.obtain;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;
import net.stringfromjava.projectpiggyg.command.ILogObtainer;
import net.stringfromjava.projectpiggyg.command.LogObtainerCommandListener;
import net.stringfromjava.projectpiggyg.data.Constants;
import net.stringfromjava.projectpiggyg.data.command.CommandOptionData;
import net.stringfromjava.projectpiggyg.util.data.JsonUtil;
import net.stringfromjava.projectpiggyg.util.data.FileUtil;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import net.stringfromjava.projectpiggyg.util.discord.CommandUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Command for getting all the troll logs sent on a server.
 */
public class ObtainTrollLogsCommandListener extends LogObtainerCommandListener implements ILogObtainer {

	public ObtainTrollLogsCommandListener(String name) {
		super(name);
		description = "Obtain all logs of the different troll messages sent.";
		helpDescription = """
				Sends a `.txt` file with every troll command sent.
				This includes helpful info such as what time it was sent,
				what time zone it was sent from, the author's/receiver's username
				and ID, and much more. Only users with the "Manage server"
				permission can use this command.
				""";
		memberPermissions = DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER);
		options = List.of(
				new CommandOptionData(OptionType.USER, "from_user", "An optional user to filter the logs.", false)
		);
		memberPermissions = DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER);
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		User fromUser = null;
		OptionMapping fromUserOM = event.getOption("from_user");

		if (fromUserOM != null) {
			fromUser = fromUserOM.getAsUser();
		}

		File trollLogsFile = FileUtil.ensureFileExists(
				PathUtil.fromGuildLogs(guild.getId(), Constants.System.TROLL_LOG_FILE_NAME),
				"[]"
		);
		JSONArray trollLogs = new JSONArray(FileUtil.getFileData(trollLogsFile));

		if (trollLogs.isEmpty()) {
			event.reply("Hmm, seems like no one has sent any trolls yet...").queue();
			return;
		}

		if (fromUser != null && fromUser.isBot()) {
			event.reply("Bro...\n***STOP FUCKING USING COMMANDS ON BOTS, YOU CAN'T MESS WITH MY HOME BOYS LIKE THAT*** :rage:").queue();
			return;
		}

		// Filter the logs if a user was provided
		JSONArray filteredLogs = new JSONArray();
		for (Object log : trollLogs) {
			if (!(log instanceof JSONObject)) {
				continue;
			}
			JSONObject l = (JSONObject) log;
			String senderId = JsonUtil.getJsonField(l, "sender-id", "");
			// Only add the log if the wanted user's ID
			// equals the sender's ID in the log
			if (fromUser == null || senderId.equals(fromUser.getId())) {
				filteredLogs.put(l);
			}
		}

		// Create a temporary text file to send with the logs
		File logFile = generateLogFile(filteredLogs, "troll-logs");
		if (logFile != null) {
			CommandUtil.sendSafeCommandReply(
					"",
					event,
					List.of(FileUpload.fromData(logFile, logFile.getName()))
			);
		} else {
			CommandUtil.sendSafeCommandReply("Sorry bruv, but I couldn't get the troll logs... :pensive:", event);
		}
	}

	@Override
	public String generateTextLog(JSONObject info) {
		StringBuilder sb = new StringBuilder();
		JSONObject timeSent = JsonUtil.getJsonField(info, "time", new JSONObject());
		JSONObject author = JsonUtil.getJsonField(info, "author", new JSONObject());
		JSONObject receiver = JsonUtil.getJsonField(info, "receiver", new JSONObject());
		JSONObject attachment = JsonUtil.getJsonField(info, "attachment", new JSONObject());

		// Get all necessary attributes of the log
		String authorUsername = JsonUtil.getJsonField(author, "name", "Unknown");
		String authorId = JsonUtil.getJsonField(author, "id", "Unknown");
		String receiverUsername = JsonUtil.getJsonField(receiver, "name", "Unknown");
		String receiverId = JsonUtil.getJsonField(receiver, "id", "Unknown");
		String message = JsonUtil.getJsonField(info, "message", "Unknown");
		String attachmentName = JsonUtil.getJsonField(attachment, "name", "One wasn't sent");
		String attachmentUrl = JsonUtil.getJsonField(attachment, "url", "One wasn't sent");
		String yearSent = JsonUtil.getJsonField(timeSent, "year", "Unknown");
		String monthSent = JsonUtil.getJsonField(timeSent, "month", "Unknown");
		String daySent = JsonUtil.getJsonField(timeSent, "day", "Unknown");
		String hourSent = JsonUtil.getJsonField(timeSent, "hour", "Unknown");
		String minuteSent = JsonUtil.getJsonField(timeSent, "minute", "Unknown");
		String secondSent = JsonUtil.getJsonField(timeSent, "second", "Unknown");
		String timeZone = JsonUtil.getJsonField(timeSent, "tz", "Unknown");

		// Combine all info
		sb.append("-------------------------------------------------------------\n");
		sb.append(STR."[AUTHOR] \{authorUsername} (ID = \{authorId})\n");
		sb.append(STR."[RECEIVER] \{receiverUsername} (ID = \{receiverId})\n");
		sb.append(STR."[MESSAGE SENT] \{message}\n");
		sb.append(STR."[ATTACHMENT NAME] \{!attachmentName.equals("null") ? STR."\"\{attachmentName}\"" : "One wasn't sent"}\n");
		sb.append(STR."[ATTACHMENT URL] \{!attachmentUrl.equals("null") ? attachmentUrl : "One wasn't sent"}\n");
		sb.append(STR."[DATE SENT] \{monthSent}/\{daySent}/\{yearSent}\n");
		sb.append(STR."[TIME SENT] \{hourSent}:\{minuteSent}:\{secondSent}\n");
		sb.append(STR."[TIMEZONE] \{timeZone}\n");
		sb.append("-------------------------------------------------------------\n");

		return sb.toString();
	}
}
