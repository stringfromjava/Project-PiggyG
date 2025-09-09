package net.stringfromjava.projectpiggyg.event.guild;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.stringfromjava.projectpiggyg.util.Constants;
import net.stringfromjava.projectpiggyg.util.app.AppUtil;
import net.stringfromjava.projectpiggyg.util.data.JsonUtil;
import net.stringfromjava.projectpiggyg.util.data.FileUtil;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
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

		String msgPath = PathUtil.ensurePathExists(PathUtil.fromGuildBlobCache(
				guild.getId(),
				Constants.System.GUILD_BLOB_CACHE_CHANNELS_FOLDER_NAME,
				channel.getId(),
				Constants.System.GUILD_BLOB_CACHE_MESSAGES_FOLDER_NAME,
				message.getId()
		), false, false);

		File msgJsonFile = FileUtil.createFile("message.json", msgPath, false);
		FileUtil.writeToFile(msgJsonFile, JsonUtil.createMessageJson(message).toString(2));

		for (Message.Attachment attachment : message.getAttachments()) {
			FileUtil.convertAttachmentToFile(attachment, msgPath, false);
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
