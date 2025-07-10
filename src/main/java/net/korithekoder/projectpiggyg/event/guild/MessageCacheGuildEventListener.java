package net.korithekoder.projectpiggyg.event.guild;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.korithekoder.projectpiggyg.util.app.AppUtil;
import net.korithekoder.projectpiggyg.util.data.DataUtil;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.File;

/**
 * Event listener class for logging edited and deleted messages on a guild.
 */
public class MessageCacheGuildEventListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!AppUtil.isConditionalEnabled("MESSAGE_LOGGING_ALLOWED")) {
			return;
		}
		Message message = event.getMessage();
		Guild guild = event.getGuild();
		Channel channel = event.getChannel();
		File logsFile = FileUtil.ensureFileExists(
				PathUtil.fromGuildBlobCache(
						guild.getId(),
						"messages",
						STR."\{channel.getId()}.json"
				),
				"[]"
		);
		JSONArray messages = new JSONArray(FileUtil.getFileData(logsFile));

		messages.put(DataUtil.createMessageJson(message));
		FileUtil.writeToFile(logsFile, messages.toString(2));
	}

	@Override
	public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
		// TODO: Do this later
	}

	@Override
	public void onMessageDelete(@NotNull MessageDeleteEvent event) {
		if (!AppUtil.isConditionalEnabled("MESSAGE_LOGGING_ALLOWED")) {
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
