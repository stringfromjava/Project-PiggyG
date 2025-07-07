package net.korithekoder.projectpiggyg.command.obtain;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import net.korithekoder.projectpiggyg.command.ILogObtainer;
import net.korithekoder.projectpiggyg.command.PiggyGCommand;
import net.korithekoder.projectpiggyg.command.PiggyGLogObtainerCommand;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import net.korithekoder.projectpiggyg.util.data.DataUtil;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Command for getting all the troll logs sent on a server.
 */
public class ObtainTrollLogsCommandListener extends PiggyGLogObtainerCommand implements ILogObtainer {

	public ObtainTrollLogsCommandListener(String name) {
		super(name);
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
				PathUtil.fromGuildFolder(guild.getId(), "logs", "troll.json"),
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
			String senderId = DataUtil.getJsonField(l, "sender-id", "");
			// Only add the log if the wanted user's ID
			// equals the sender's ID in the log
			if (fromUser == null || senderId.equals(fromUser.getId())) {
				filteredLogs.put(l);
			}
		}

		// Create a temporary text file to send with the logs
		File logFile = generateLogFile(filteredLogs, "troll-logs");
		if (logFile != null) {
			event.replyFiles(FileUpload.fromData(logFile, logFile.getName())).queue();
		} else {
			event.reply("Sorry bruv, but I couldn't get the troll logs... :pensive:").queue();
		}
	}

	@Override
	public String generateTextLog(JSONObject info) {
		StringBuilder sb = new StringBuilder();
		JSONObject timeSent = DataUtil.getJsonField(info, "time-sent", new JSONObject());

		// Get all necessary attributes of the log
		String senderUsername = DataUtil.getJsonField(info, "sender-username", "Unknown");
		String senderId = DataUtil.getJsonField(info, "sender-id", "Unknown");
		String receiverUsername = DataUtil.getJsonField(info, "receiver-username", "Unknown");
		String receiverId = DataUtil.getJsonField(info, "receiver-id", "Unknown");
		String message = DataUtil.getJsonField(info, "message", "Unknown");
		String attachmentName = DataUtil.getJsonField(info, "attachment-name", "One wasn't sent");
		String attachmentUrl = DataUtil.getJsonField(info, "attachment-url", "One wasn't sent");
		String yearSent = DataUtil.getJsonField(timeSent, "year", "Unknown");
		String monthSent = DataUtil.getJsonField(timeSent, "month", "Unknown");
		String daySent = DataUtil.getJsonField(timeSent, "day", "Unknown");
		String hourSent = DataUtil.getJsonField(timeSent, "hour", "Unknown");
		String minuteSent = DataUtil.getJsonField(timeSent, "minute", "Unknown");
		String secondSent = DataUtil.getJsonField(timeSent, "second", "Unknown");
		String timeZone = DataUtil.getJsonField(timeSent, "tz", "Unknown");

		// Combine all info
		sb.append("-------------------------------------------------------------\n");
		sb.append(STR."[SENDER USERNAME] \{senderUsername}\n");
		sb.append(STR."[SENDER ID] \{senderId}\n");
		sb.append(STR."[RECEIVER USERNAME] \{receiverUsername}\n");
		sb.append(STR."[RECEIVER ID] \{receiverId}\n");
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
