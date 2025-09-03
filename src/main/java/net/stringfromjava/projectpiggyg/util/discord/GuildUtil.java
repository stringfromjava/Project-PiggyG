package net.stringfromjava.projectpiggyg.util.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.stringfromjava.projectpiggyg.util.Constants;
import net.stringfromjava.projectpiggyg.util.app.AppUtil;
import net.stringfromjava.projectpiggyg.util.app.LoggerUtil;
import net.stringfromjava.projectpiggyg.util.data.JsonUtil;
import net.stringfromjava.projectpiggyg.util.data.FileUtil;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling components specific to
 * Discord guilds (or servers, as called by normal discordians).
 */
public final class GuildUtil {

	/**
	 * Creates a brand-new folder with many files and folders inside for a new guild.
	 *
	 * @param guild The new guild as a {@link net.dv8tion.jda.api.entities.Guild} object.
	 */
	public static void createNewGuildFolder(Guild guild) {
		// Paths
		String guildId = guild.getId();
		String newGuildPath = PathUtil.fromGuildFolder(guildId);
		String newLogPath = PathUtil.fromGuildFolder(guildId, Constants.System.GUILD_LOG_FOLDER_NAME);
		String newTAPath = PathUtil.fromGuildFolder(guildId, Constants.System.GUILD_TROLL_ATTACHMENT_FOLDER_NAME);
		String newBlobCachePath = PathUtil.fromGuildFolder(guildId, Constants.System.GUILD_BLOB_CACHE_MESSAGES_FOLDER_NAME);
		String newBlobCacheMessagesPath = PathUtil.fromGuildFolder(
				guildId,
				Constants.System.GUILD_BLOB_CACHE_MESSAGES_FOLDER_NAME,
				Constants.System.GUILD_BLOB_CACHE_MESSAGES_ATTACHMENT_FOLDER_NAME
		);

		// Create directories
		PathUtil.createPath(newGuildPath);
		PathUtil.createPath(newLogPath);
		PathUtil.createPath(newTAPath);
		PathUtil.createPath(newBlobCachePath);
		PathUtil.createPath(newBlobCacheMessagesPath);
		// Create files
		File configFile = FileUtil.createFile(Constants.System.GUILD_CONFIG_FILE_NAME, newGuildPath);
		File trollLogsFile = FileUtil.createFile(Constants.System.TROLL_LOG_FILE_NAME, newLogPath);
		File voiceLogsFile = FileUtil.createFile(Constants.System.VOICE_JOINS_LEAVES_LOG_FILE_NAME, newLogPath);
		File voiceActionLogsFile = FileUtil.createFile(Constants.System.VOICE_ACTION_LOG_FILE_NAME, newLogPath);
		File deletedMessageLogsFile = FileUtil.createFile(Constants.System.DELETED_MESSAGE_LOG_FILE_NAME, newLogPath);

		// Write to files with default content
		FileUtil.writeToFile(configFile, generateDefaultConfigJson());
		FileUtil.writeToFile(trollLogsFile, "[]");
		FileUtil.writeToFile(voiceLogsFile, "[]");
		FileUtil.writeToFile(voiceActionLogsFile, "[]");
		FileUtil.writeToFile(deletedMessageLogsFile, "[]");

		cacheGuildMessages(guild);
	}

	/**
	 * Gets a {@link net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel}'s
	 * full message history. Due to Discord only allowing 100 messages to be collected at a time,
	 * this function gets a channel's history in batches of 100.
	 *
	 * @param channel The {@link net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel} to
	 *                get the message history from.
	 * @return The entire message history stored in a {@link java.util.List}.
	 */
	@NotNull
	public static List<Message> getFullMessageHistory(@NotNull GuildMessageChannel channel) {
		List<Message> messages = new ArrayList<>();
		MessageHistory history = channel.getHistory();
		Message lastMessage = null;

		while (true) {
			List<Message> batch;
			if (lastMessage == null) {
				batch = history.retrievePast(100).complete();
			} else {
				batch = history.getHistoryAfter(channel, lastMessage.getId()).complete().getRetrievedHistory();
			}
			if (batch.isEmpty()) break;
			messages.addAll(batch);
			lastMessage = batch.get(batch.size() - 1);
			if (batch.size() < 100) break;
		}
		return messages;
	}

	public static void cacheGuildMessages(Guild guild) {
		cacheGuildMessages(guild, true);
	}

	public static void cacheGuildMessages(Guild guild, boolean logInfo) {
		if (logInfo) {
			LoggerUtil.log(STR."Caching messages for guild '\{guild.getName()}' (ID = \{guild.getId()})");
			boolean msgLoggingAllowed = AppUtil.conditionalEnabled("MESSAGE_LOGGING_ALLOWED");
			if (!msgLoggingAllowed) {
				LoggerUtil.log(STR."Message logging is disabled! Skipping message caching");
				return;
			}
		}

		PathUtil.ensurePathExists(
				PathUtil.fromGuildBlobCache(guild.getId())
		);

		PathUtil.ensurePathExists(
				PathUtil.fromGuildBlobCache(
						guild.getId(),
						Constants.System.GUILD_BLOB_CACHE_MESSAGES_FOLDER_NAME,
						Constants.System.GUILD_BLOB_CACHE_MESSAGES_ATTACHMENT_FOLDER_NAME
				)
		);

		// Loop through all channels and store their messages
		for (GuildChannel channel : guild.getChannels()) {
			if (!(channel instanceof GuildMessageChannel messageChannel)) {
				continue;
			}
			File channelJsonFile = FileUtil.ensureFileExists(
					PathUtil.fromGuildBlobCache(guild.getId(), "messages", STR."\{messageChannel.getId()}.json"),
					"[]",
					false
			);
			JSONArray messages = new JSONArray(FileUtil.getFileData(channelJsonFile));

			// Loop through the message history of the current channel
			for (Message message : GuildUtil.getFullMessageHistory(messageChannel)) {
				JSONObject messageInfo = JsonUtil.createMessageJson(message);
				boolean containsMessage = false;
				// Make sure the message isn't already cached
				for (Object log : messages) {
					if (!(log instanceof JSONObject l)) {
						continue;
					}

					JSONObject msg = JsonUtil.getJsonField(l, "message", new JSONObject());
					String msgId = JsonUtil.getJsonField(msg, "id", "");

					if (msgId.equals(messageInfo.getJSONObject("message").get("id"))) {
						containsMessage = true;
						break;
					}
				}
				if (!containsMessage) {
					messages.put(messageInfo);
				}
				// Store all the attachments that are contained in a message
				for (Message.Attachment attachment : message.getAttachments()) {
					String path = PathUtil.ensurePathExists(
							PathUtil.fromGuildBlobCache(guild.getId(), "messages", "attachments", message.getId())
					);
					FileUtil.convertAttachmentToFile(attachment, path, false);
				}
			}

			FileUtil.writeToFile(channelJsonFile, messages.toString(2));
		}
	}

	/**
	 * Creates default data for a {@code config.json} file.
	 *
	 * @return A string of the default data.
	 */
	public static String generateDefaultConfigJson() {
		JSONObject config = new JSONObject();
		config.put("disable-trolls-globally", false);
		config.put("blocked-troll-users", List.of());
		return config.toString(2);
	}

	private GuildUtil() {
	}
}
