package net.korithekoder.projectpiggyg.event.guild;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.korithekoder.projectpiggyg.util.data.DataUtil;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.File;

public class MessageGuildEventListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		Message message = event.getMessage();
		Guild guild = event.getGuild();
		Channel channel = event.getChannel();
		File logsFile = FileUtil.ensureFileExists(
				PathUtil.fromBlobCache(
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
		// TODO: Do this later
	}
}
