package net.korithekoder.projectpiggyg.util.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.FileUpload;
import net.korithekoder.projectpiggyg.util.app.AppUtil;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import net.korithekoder.projectpiggyg.util.data.DataUtil;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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
		String newLogPath = PathUtil.fromGuildFolder(guildId, "logs");
		String newTAPath = PathUtil.fromGuildFolder(guildId, "trollattachments");
		String newBlobCachePath = PathUtil.fromGuildFolder(guildId, "blobcache");
		String newBlobCacheMessagesPath = PathUtil.fromGuildFolder(guildId, "blobcache", "messages");
		String newBlobCacheMessagesAttachmentsPath = PathUtil.fromGuildFolder(guildId, "blobcache", "messages", "attachments");

		// Create directories
		PathUtil.createPath(newGuildPath);
		PathUtil.createPath(newLogPath);
		PathUtil.createPath(newTAPath);
		PathUtil.createPath(newBlobCachePath);
		PathUtil.createPath(newBlobCacheMessagesPath);
		PathUtil.createPath(newBlobCacheMessagesAttachmentsPath);
		// Create files
		File configFile = FileUtil.createFile("config.json", newGuildPath);
		File trollLogsFile = FileUtil.createFile("troll.json", newLogPath);
		File voiceLogsFile = FileUtil.createFile("voice.json", newLogPath);
		File voiceActionLogsFile = FileUtil.createFile("voice-action.json", newLogPath);
		File deletedMessageLogsFile = FileUtil.createFile("deleted-message.json", newLogPath);

		// Write to files with default content
		FileUtil.writeToFile(configFile, generateDefaultConfigJson());
		FileUtil.writeToFile(trollLogsFile, "[]");
		FileUtil.writeToFile(voiceLogsFile, "[]");
		FileUtil.writeToFile(voiceActionLogsFile, "[]");
		FileUtil.writeToFile(deletedMessageLogsFile, "[]");
	}

	/**
	 * A safe way to send a reply in a command without errors being raised.
	 *
	 * @param message The message to send.
	 * @param event   The event to send a reply to.
	 */
	public static void sendSafeCommandReply(String message, SlashCommandInteractionEvent event) {
		sendSafeCommandReply(message, event, null, null, null);
	}

	/**
	 * A safe way to send a reply in a command without errors being raised.
	 *
	 * @param message The message to send.
	 * @param event   The event to send a reply to.
	 * @param files   Optional files to send with the reply.
	 */
	public static void sendSafeCommandReply(String message, SlashCommandInteractionEvent event, Collection<FileUpload> files) {
		sendSafeCommandReply(message, event, files, null, null);
	}

	/**
	 * A safe way to send a reply in a command without errors being raised.
	 *
	 * @param message   The message to send.
	 * @param event     The event to send a reply to.
	 * @param files     Optional files to send with the reply.
	 * @param onSuccess Callback function to be triggered when the message was successfully sent.
	 */
	public static void sendSafeCommandReply(String message, SlashCommandInteractionEvent event, Collection<FileUpload> files, Runnable onSuccess) {
		sendSafeCommandReply(message, event, files, onSuccess, null);
	}

	/**
	 * A safe way to send a reply in a command without errors being raised.
	 *
	 * @param message   The message to send.
	 * @param event     The event to send a reply to.
	 * @param files     Optional files to send with the reply.
	 * @param onSuccess Callback function to be triggered when the message was successfully sent.
	 * @param onFailure Callback function to be triggered when the message failed to send.
	 */
	public static void sendSafeCommandReply(@Nullable String message, @NotNull SlashCommandInteractionEvent event, @Nullable Collection<FileUpload> files, @Nullable Runnable onSuccess, @Nullable Runnable onFailure) {
		event.reply((message != null) ? message : "")
				.addFiles((files != null) ? files : List.of())
				.queue(
						success -> {
							if (onSuccess != null) {
								onSuccess.run();
							}
						},
						failure -> {
							if (onFailure != null) {
								onFailure.run();
							}
							// 10062 = Interaction has expired
							if (failure instanceof ErrorResponseException err && err.getErrorCode() == 10062) {
								// The command timed out (not sent in 3 seconds)
								LoggerUtil.log("Command reply timed out! (interaction expired)", LogType.WARN, false);
							} else {
								String errorMsg = STR."Failed to reply to message, got this message: \{failure.getMessage()}";
								LoggerUtil.error(errorMsg);
								throw new RuntimeException(errorMsg);
							}
						}
				);
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
			boolean msgLoggingAllowed = AppUtil.isConditionalEnabled("MESSAGE_LOGGING_ALLOWED");
			if (!msgLoggingAllowed) {
				LoggerUtil.log(STR."Conditional 'MESSAGE_LOGGING_ALLOWED' is disabled! Skipping message caching");
				return;
			}
		}

		String blobCachePath = PathUtil.fromGuildBlobCache(guild.getId());
		PathUtil.ensurePathExists(blobCachePath);

		PathUtil.ensurePathExists(
				PathUtil.fromGuildFolder(guild.getId(), "blobcache", "messages", "attachments"),
				false
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
			JSONArray messages = new JSONArray();

			// Loop through the message history of the current channel
			for (Message message : GuildUtil.getFullMessageHistory(messageChannel)) {
				messages.put(DataUtil.createMessageJson(message));
				// Store all the attachments that are contained in a message
				for (Message.Attachment attachment : message.getAttachments()) {
					String path = PathUtil.fromGuildBlobCache(guild.getId(), "messages", "attachments", message.getId());
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
