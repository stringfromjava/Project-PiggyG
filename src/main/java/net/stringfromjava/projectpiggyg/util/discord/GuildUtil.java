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

	public static void createNewGuildFolder(Guild guild) {
		createNewGuildFolder(guild, true);
	}

	/**
	 * Creates a brand-new folder with many files and folders inside for a new guild.
	 *
	 * @param guild The new guild as a {@link net.dv8tion.jda.api.entities.Guild} object.
	 * @param cacheMessages Whether to cache all messages in the guild upon creation.
	 */
	public static void createNewGuildFolder(Guild guild, boolean cacheMessages) {
		// Paths
		String guildId = guild.getId();
		String newGuildPath = PathUtil.fromGuildFolder(guildId);
		String newLogPath = PathUtil.fromGuildFolder(guildId, Constants.System.GUILD_LOG_FOLDER_NAME);
		String newTAPath = PathUtil.fromGuildFolder(guildId, Constants.System.GUILD_TROLL_ATTACHMENT_FOLDER_NAME);
		String newBlobCachePath = PathUtil.fromGuildFolder(guildId, Constants.System.GUILD_BLOB_CACHE_FOLDER_NAME);

		// Create directories
		PathUtil.createPath(newGuildPath);
		PathUtil.createPath(newLogPath);
		PathUtil.createPath(newTAPath);
		PathUtil.createPath(newBlobCachePath);

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

		if (cacheMessages) {
			cacheGuildMessages(guild);
		}
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

		// Loop through all channels and store their messages
		for (GuildChannel channel : guild.getChannels()) {
			if (!(channel instanceof GuildMessageChannel messageChannel)) {
				continue;
			}
			String channelPath = PathUtil.ensurePathExists(
					PathUtil.fromGuildBlobCache(
							guild.getId(),
							Constants.System.GUILD_BLOB_CACHE_CHANNELS_FOLDER_NAME,
							messageChannel.getId()
					),
					false
			);
			// Loop through the message history of the current channel
			for (Message message : GuildUtil.getFullMessageHistory(messageChannel)) {
				String messagePath = PathUtil.ensurePathExists(
						PathUtil.constructPath(channelPath, Constants.System.GUILD_BLOB_CACHE_MESSAGES_FOLDER_NAME, message.getId()),
						false
				);
				// Create the .json file for the basic data of the message
				FileUtil.ensureFileExists(
						PathUtil.constructPath(messagePath, "message.json"),
						JsonUtil.createMessageJson(message).toString(2),
						false
				);
				// Store all the attachments that are contained in a message
				for (Message.Attachment attachment : message.getAttachments()) {
					FileUtil.convertAttachmentToFile(attachment, messagePath, false);
				}
			}
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
