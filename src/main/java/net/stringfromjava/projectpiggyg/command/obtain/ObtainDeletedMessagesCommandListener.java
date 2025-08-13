package net.stringfromjava.projectpiggyg.command.obtain;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;
import net.stringfromjava.projectpiggyg.command.LogObtainerCommandListener;
import net.stringfromjava.projectpiggyg.data.Constants;
import net.stringfromjava.projectpiggyg.data.command.CommandOptionData;
import net.stringfromjava.projectpiggyg.util.data.JsonUtil;
import net.stringfromjava.projectpiggyg.util.data.FileUtil;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import net.stringfromjava.projectpiggyg.util.discord.CommandUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Command for getting deleted messages.
 */
public class ObtainDeletedMessagesCommandListener extends LogObtainerCommandListener {

	private JDA client;
	private Channel channel;

	public ObtainDeletedMessagesCommandListener(String name) {
		super(name);
		description = "Get logged messages that were deleted.";
		helpDescription = """
				Get messages that were deleted by any user.
				You can input a number for how many deleted messages to go back
				in the history! Only people with the "Manage server" permission can
				use this command.
				""";
		options = List.of(
				new CommandOptionData(OptionType.CHANNEL, "channel", "The channel to obtain logs from.", true),
				new CommandOptionData(OptionType.INTEGER, "amount", "The amount of deleted messages to go back on. (0 = All the messages)", false)
		);
		memberPermissions = DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER);
		requiredConditional = Constants.Conditionals.MESSAGE_LOGGING_ALLOWED;
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		client = event.getJDA();
		int amount = 0;
		channel = event.getOption("channel").getAsChannel();
		OptionMapping amountOM = event.getOption("amount");
		Guild guild = event.getGuild();
		File deletedMessageIdsFile = FileUtil.ensureFileExists(
				PathUtil.fromGuildLogs(
						guild.getId(),
						Constants.System.DELETED_MESSAGE_LOG_FILE_NAME
				),
				"[]"
		);
		File cachedMessagesFile = FileUtil.ensureFileExists(
				PathUtil.fromGuildBlobCache(
						guild.getId(),
						Constants.System.GUILD_BLOB_CACHE_MESSAGES_FOLDER_NAME,
						STR."\{channel.getId()}.json"
				),
				"[]"
		);
		JSONArray cachedMessages = new JSONArray(FileUtil.getFileData(cachedMessagesFile));
		JSONArray deletedMessageIds = new JSONArray(FileUtil.getFileData(deletedMessageIdsFile));
		JSONArray obtained = new JSONArray();

		if (!(channel instanceof GuildMessageChannel)) {
			CommandUtil.sendSafeCommandReply(
					"Brother, you need to put in a channel where you can __*SEND MESSAGES*__, not whatever bullshit you tried to feed me :sob::pray:",
					event
			);
		}

		if (amountOM != null) {
			amount = amountOM.getAsInt();
		}
		if (amount == 0) {
			amount = deletedMessageIds.length();
		}

		if (amount < 0 || amount > deletedMessageIds.length()) {
			CommandUtil.sendSafeCommandReply(
					"Bruh, you can't go back that far in the history :man_facepalming:",
					event
			);
			return;
		}

		for (int i = 0; i < amount; i++) {
			if (!(deletedMessageIds.get(i) instanceof String deletedMsgId)) {
				continue;
			}
			JSONObject log = getCachedMessageById(cachedMessages, deletedMsgId);
			if (log != null) {
				obtained.put(log);
			}
		}

		File toSend = generateLogFile(obtained, "deleted-messages");

		if (!obtained.isEmpty()) {
			CommandUtil.sendSafeCommandReply(null, event, List.of(FileUpload.fromData(toSend)));
		} else {
			CommandUtil.sendSafeCommandReply("Hmmm, seems like no one deleted any messages yet...", event);
		}
	}

	@Override
	public String generateTextLog(JSONObject info) {
		StringBuilder sb = new StringBuilder();
		JSONObject timeSent = JsonUtil.getJsonField(info, "time", new JSONObject());
		JSONObject author = JsonUtil.getJsonField(info, "author", new JSONObject());
		JSONObject message = JsonUtil.getJsonField(info, "message", new JSONObject());
		JSONArray attachments = JsonUtil.getJsonField(message, "attachments", new JSONArray());

		// Get all necessary attributes of the log
		String authorId = JsonUtil.getJsonField(author, "id", "Unknown");
		String messageContents = JsonUtil.getJsonField(message, "contents", "<No message was sent.>");
		String messageId = JsonUtil.getJsonField(message, "id", "Unknown");
		String yearSent = JsonUtil.getJsonField(timeSent, "year", "Unknown");
		String monthSent = JsonUtil.getJsonField(timeSent, "month", "Unknown");
		String daySent = JsonUtil.getJsonField(timeSent, "day", "Unknown");
		String hourSent = JsonUtil.getJsonField(timeSent, "hour", "Unknown");
		String minuteSent = JsonUtil.getJsonField(timeSent, "minute", "Unknown");
		String secondSent = JsonUtil.getJsonField(timeSent, "second", "Unknown");

		String authorUsername = client.getUserById(authorId).getName();
		StringBuilder attachmentsDisplay = new StringBuilder();

		for (Object attachment : attachments.toList()) {
			if (!(attachment instanceof String a)) {
				continue;
			}
			attachmentsDisplay.append("\"").append(a).append("\"");
			int idx = attachments.toList().indexOf(a);
			if (idx != attachments.length() - 1) {
				attachmentsDisplay.append(",");
			}
		}

		// Combine all info
		sb.append("-------------------------------------------------------------\n");
		sb.append(STR."[AUTHOR] \{authorUsername} (ID = \{authorId})\n");
		sb.append(STR."[MESSAGE] \"\{messageContents}\" (ID = \{messageId})\n");
		sb.append(STR."[ATTACHMENTS] \{!attachmentsDisplay.toString().isEmpty() ? attachmentsDisplay.toString() : "None were sent"}\n");
		sb.append(STR."[CHANNEL] \{channel.getName()} (ID = \{channel.getId()}, TYPE = \{channel.getType().toString()})\n");
		sb.append(STR."[DATE SENT] \{monthSent}/\{daySent}/\{yearSent}\n");
		sb.append(STR."[TIME SENT] \{hourSent}:\{minuteSent}:\{secondSent}\n");
		sb.append("-------------------------------------------------------------\n");

		return sb.toString();
	}

	@Nullable
	private JSONObject getCachedMessageById(JSONArray logs, String msgId) {
		for (Object log : logs) {
			if (!(log instanceof JSONObject l)) {
				continue;
			}

			JSONObject msg = JsonUtil.getJsonField(l, "message", new JSONObject());
			String id = JsonUtil.getJsonField(msg, "id", "");
			if (msgId.equals(id)) {
				return l;
			}
		}
		return null;
	}
}
