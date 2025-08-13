package net.stringfromjava.projectpiggyg.event.guild;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.stringfromjava.projectpiggyg.data.Constants;
import net.stringfromjava.projectpiggyg.util.app.AppUtil;
import net.stringfromjava.projectpiggyg.util.data.JsonUtil;
import net.stringfromjava.projectpiggyg.util.data.FileUtil;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import net.stringfromjava.projectpiggyg.util.discord.GuildUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.File;

/**
 * Event listener class for logging edited and deleted messages on a guild.
 */
public class MessageCacheGuildEventListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!AppUtil.conditionalEnabled("MESSAGE_LOGGING_ALLOWED")) {
			return;
		}

		Message message = event.getMessage();
		Guild guild = event.getGuild();
		Channel channel = event.getChannel();

		String blobCachePath = PathUtil.fromGuildBlobCache(guild.getId());
		if (!PathUtil.doesPathExist(blobCachePath)) {
			PathUtil.createPath(blobCachePath);
			GuildUtil.cacheGuildMessages(guild);
		}

		File logsFile = FileUtil.ensureFileExists(
				PathUtil.fromGuildBlobCache(
						guild.getId(),
						Constants.System.GUILD_BLOB_CACHE_MESSAGES_FOLDER_NAME,
						STR."\{channel.getId()}.json"
				),
				"[]"
		);
		JSONArray messages = new JSONArray(FileUtil.getFileData(logsFile));

		messages.put(JsonUtil.createMessageJson(message));
		FileUtil.writeToFile(logsFile, messages.toString(2));

		// Store all the attachments that are contained in the message
		for (Message.Attachment attachment : message.getAttachments()) {
			String path = PathUtil.createPath(
					PathUtil.fromGuildBlobCache(
							guild.getId(),
							Constants.System.GUILD_BLOB_CACHE_MESSAGES_FOLDER_NAME,
							Constants.System.GUILD_BLOB_CACHE_MESSAGES_ATTACHMENT_FOLDER_NAME,
							message.getId()
					)
			);
			FileUtil.convertAttachmentToFile(attachment, path, false);
		}
	}

	@Override
	public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
		// TODO: Do this later
	}

	@Override
	public void onMessageDelete(@NotNull MessageDeleteEvent event) {
		if (!AppUtil.conditionalEnabled("MESSAGE_LOGGING_ALLOWED")) {
			return;
		}
		Guild guild = event.getGuild();
		File deletedMessageLogFile = FileUtil.ensureFileExists(
				PathUtil.fromGuildLogs(guild.getId(), "deleted-message.json")
		);
		JSONArray deletedMessages = new JSONArray(FileUtil.getFileData(deletedMessageLogFile));
		deletedMessages.put(event.getMessageId());
		FileUtil.writeToFile(deletedMessageLogFile, deletedMessages.toString(2));
	}
}
