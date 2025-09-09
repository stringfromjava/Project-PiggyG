package net.stringfromjava.projectpiggyg.command.obtain.voice;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;
import net.stringfromjava.projectpiggyg.command.LogObtainerCommandListener;
import net.stringfromjava.projectpiggyg.util.Constants;
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
 * Command for getting logs of users muting/deafening other users.
 */
public class ObtainVoiceChannelActionLogsCommandListener extends LogObtainerCommandListener {

	public ObtainVoiceChannelActionLogsCommandListener(String name) {
		super(name);
		description = "Gets logs from users (typically admins) server muting/deafening other members in voice channels.";
		helpDescription = """
				Gets all logs of users (usually admins) server muting/deafening other users.
				Pretty helpful for catching admins abusing their power! Only users with the
				"Manage server" permission can use this command.
				""";
		options = List.of(
				new CommandOptionData(OptionType.USER, "affected_user", "An optional affected user that was muted/deafened to obtain specific logs from.", false),
				new CommandOptionData(OptionType.USER, "from_user", "An optional inflicting user that muted/deafened another user to obtain specific logs from.", false),
				new CommandOptionData(OptionType.CHANNEL, "voice_channel", "An optional voice channel to obtain specific logs from.", false)
		);
		memberPermissions = DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER);
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		User affectedUser = null;
		User fromUser = null;
		VoiceChannel voiceChannel = null;
		OptionMapping affectedUserOM = event.getOption("affected_user");
		OptionMapping fromUserOM = event.getOption("from_user");
		OptionMapping voiceChannelOM = event.getOption("voice_channel");

		if (affectedUserOM != null) {
			affectedUser = affectedUserOM.getAsUser();
		}
		if (fromUserOM != null) {
			fromUser = fromUserOM.getAsUser();
		}
		if (voiceChannelOM != null) {
			GuildChannelUnion gcu = voiceChannelOM.getAsChannel();
			if (gcu.getType() == ChannelType.VOICE) {
				voiceChannel = gcu.asVoiceChannel();
			} else {
				event.reply("Brother, do you not know what \"VC\" stands for? :sob:\nYou need to put in a voice channel").queue();
				return;
			}
		}

		File voiceActionLogsFile = FileUtil.ensureFileExists(
				PathUtil.fromGuildLogs(
						guild.getId(),
						Constants.System.VOICE_ACTION_LOG_FILE_NAME
				),
				"[]"
		);
		JSONArray voiceActionLogs = new JSONArray(FileUtil.getFileData(voiceActionLogsFile));
		// Filter logs based on provided options
		JSONArray filteredLogs = new JSONArray();
		for (int i = 0; i < voiceActionLogs.length(); i++) {
			JSONObject log = voiceActionLogs.getJSONObject(i);

			boolean matches = true;
			if (affectedUser != null) {
				String affectedId = JsonUtil.getJsonField(log, "affected-id", "");
				if (!affectedUser.getId().equals(affectedId)) {
					matches = false;
				}
			}
			if (fromUser != null) {
				String inflicterId = JsonUtil.getJsonField(log, "inflicter-id", "");
				if (!fromUser.getId().equals(inflicterId)) {
					matches = false;
				}
			}
			if (voiceChannel != null) {
				String channelId = JsonUtil.getJsonField(log, "channel-id", "");
				if (!voiceChannel.getId().equals(channelId)) {
					matches = false;
				}
			}

			if (matches) {
				filteredLogs.put(log);
			}
		}

		File logsFile = generateLogFile(filteredLogs, "voice-action-logs");

		if (logsFile != null && !filteredLogs.isEmpty()) {
			CommandUtil.sendSafeReply(
					null,
					event,
					List.of(FileUpload.fromData(logsFile))
			);
		} else {
			CommandUtil.sendSafeReply(
					"Sorry bro, but there ain't no one who muted/deafened anyone yet...",
					event
			);
		}
	}

	@Override
	public String generateTextLog(JSONObject info) {
		StringBuilder sb = new StringBuilder();
		JSONObject timeSent = JsonUtil.getJsonField(info, "time", new JSONObject());
		JSONObject action = JsonUtil.getJsonField(info, "action", new JSONObject());
		JSONObject channel = JsonUtil.getJsonField(info, "channel", new JSONObject());
		JSONObject affected = JsonUtil.getJsonField(info, "affected", new JSONObject());
		JSONObject inflicter = JsonUtil.getJsonField(info, "inflicter", new JSONObject());

		// Get all necessary attributes of the log
		String affectedUsername = JsonUtil.getJsonField(affected, "name", "Unknown");
		String affectedId = JsonUtil.getJsonField(affected, "id", "Unknown");
		String inflicterUsername = JsonUtil.getJsonField(inflicter, "name", "Unknown");
		String inflicterId = JsonUtil.getJsonField(inflicter, "id", "Unknown");
		String actionType = JsonUtil.getJsonField(action, "type", "Unknown");
		String actionValue = JsonUtil.getJsonField(action, "value", "Unknown");
		String channelName = JsonUtil.getJsonField(channel, "name", "Unknown");
		String channelId = JsonUtil.getJsonField(channel, "id", "Unknown");
		String year = JsonUtil.getJsonField(timeSent, "year", "Unknown");
		String month = JsonUtil.getJsonField(timeSent, "month", "Unknown");
		String day = JsonUtil.getJsonField(timeSent, "day", "Unknown");
		String hour = JsonUtil.getJsonField(timeSent, "hour", "Unknown");
		String minute = JsonUtil.getJsonField(timeSent, "minute", "Unknown");
		String second = JsonUtil.getJsonField(timeSent, "second", "Unknown");
		String timeZone = JsonUtil.getJsonField(timeSent, "tz", "Unknown");

		// Combine all info
		sb.append("-------------------------------------------------------------\n");
		sb.append(STR."[AFFECTED USER] \{affectedUsername} (ID = \{affectedId})\n");
		sb.append(STR."[INFLICTING USER] \{inflicterUsername} (ID = \{inflicterId})\n");
		sb.append(STR."[ACTION TYPE] \{actionType}\n");
		sb.append(STR."[ACTION VALUE] \{getValueFromActionType(actionType, actionValue)}\n");
		sb.append(STR."[CHANNEL] \{channelName} (ID = \{channelId})\n");
		sb.append(STR."[DATE INFLICTED] \{month}/\{day}/\{year}\n");
		sb.append(STR."[TIME INFLICTED] \{hour}:\{minute}:\{second}\n");
		sb.append(STR."[TIMEZONE] \{timeZone}\n");
		sb.append("-------------------------------------------------------------\n");

		return sb.toString();
	}

	private String getValueFromActionType(String type, String value) {
		switch (type) {
			case "SERVER_MUTE" -> {
				return (value.equals("true")) ? "MUTED" : "UNMUTED";
			}
			case "SERVER_DEAFEN" -> {
				return (value.equals("true")) ? "DEAFENED" : "UNDEAFENED";
			}
			default -> {
				return "Unknown";
			}
		}
	}
}
