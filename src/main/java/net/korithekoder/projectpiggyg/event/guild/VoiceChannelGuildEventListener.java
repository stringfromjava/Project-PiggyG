package net.korithekoder.projectpiggyg.event.guild;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.korithekoder.projectpiggyg.util.data.DataUtil;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * Event listeners for voice channels on guilds.
 */
public class VoiceChannelGuildEventListener extends ListenerAdapter {

	@Override
	public void onGuildVoiceGuildMute(@NotNull net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent event) {
		User affectedUser = event.getMember().getUser();
		Guild guild = event.getGuild();
		File voiceActionLogsFile = getGuildLogFile(guild.getId(), "voice-action");
		JSONArray voiceActionLogs = new JSONArray(FileUtil.getFileData(voiceActionLogsFile));

		event.getGuild().retrieveAuditLogs()
				.type(ActionType.MEMBER_UPDATE)
				.queue(logs -> {
					// Loop through all audit logs until the one about
					// the current guild mute is found
					User mutedBy = null;
					boolean isMuted = false;
					VoiceChannel channel = event.getVoiceState().getChannel().asVoiceChannel();
					for (AuditLogEntry entry : logs) {
						AuditLogChange muteChange = entry.getChangeByKey("mute");
						boolean isTarget = entry.getTargetId().equals(affectedUser.getId());

						if (isTarget) {
							mutedBy = entry.getUser();
							isMuted = muteChange != null && Boolean.TRUE.equals(muteChange.getNewValue());
							break;
						}
					}

					// Create the new log
					voiceActionLogs.put(generateVoiceChannelActionLog(
							VoiceActionType.SERVER_MUTE,
							affectedUser,
							mutedBy,
							channel,
							Boolean.toString(isMuted)
					));
					FileUtil.writeToFile(voiceActionLogsFile, voiceActionLogs.toString(2));
				});
	}

	@Override
	public void onGuildVoiceGuildDeafen(@NotNull GuildVoiceGuildDeafenEvent event) {
		User affectedUser = event.getMember().getUser();
		Guild guild = event.getGuild();
		File voiceActionLogsFile = getGuildLogFile(guild.getId(), "voice-action");
		JSONArray voiceActionLogs = new JSONArray(FileUtil.getFileData(voiceActionLogsFile));

		event.getGuild().retrieveAuditLogs()
				.type(ActionType.MEMBER_UPDATE)
				.queue(logs -> {
					// Loop through all audit logs until the one about
					// the current guild deafen is found
					User deafenedBy = null;
					boolean isDeafened = false;
					VoiceChannel channel = event.getVoiceState().getChannel().asVoiceChannel();
					for (AuditLogEntry entry : logs) {
						AuditLogChange deafChange = entry.getChangeByKey("deaf");
						boolean isTarget = entry.getTargetId().equals(affectedUser.getId());

						if (isTarget) {
							deafenedBy = entry.getUser();
							isDeafened = deafChange != null && Boolean.TRUE.equals(deafChange.getNewValue());
							break;
						}
					}

					// Create the new log
					voiceActionLogs.put(generateVoiceChannelActionLog(
							VoiceActionType.SERVER_DEAFEN,
							affectedUser,
							deafenedBy,
							channel,
							Boolean.toString(isDeafened)
					));
					FileUtil.writeToFile(voiceActionLogsFile, voiceActionLogs.toString(2));
				});
	}

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
		AudioChannelUnion channelUnionJoined = event.getChannelJoined();
		AudioChannelUnion channelUnionLeft = event.getChannelLeft();
		VoiceChannel channelJoined = (channelUnionJoined != null) ? channelUnionJoined.asVoiceChannel() : null;
		VoiceChannel channelLeft = (channelUnionLeft != null) ? channelUnionLeft.asVoiceChannel() : null;
		Guild guild = event.getGuild();
		User user = event.getMember().getUser();

		File voiceLogsFile = getGuildLogFile(guild.getId(), "voice");
		JSONArray logs = new JSONArray(FileUtil.getFileData(voiceLogsFile));
		JSONObject logInfo = new JSONObject();

		logInfo.put("member", DataUtil.createUserInfoJson(user))
				.put("joined", new JSONObject()
						.put("name", (channelJoined != null) ? channelJoined.getName() : "No voice channel joined")
						.put("id", (channelJoined != null) ? channelJoined.getId() : "No voice channel joined"))
				.put("left", new JSONObject()
						.put("name", (channelLeft != null) ? channelLeft.getName() : "No voice channel left")
						.put("id", (channelLeft != null) ? channelLeft.getId() : "No voice channel left"))
				.put("time", DataUtil.getCurrentTimeJson());

		logs.put(logInfo);
		FileUtil.writeToFile(voiceLogsFile, logs.toString(2));
	}

	private File getGuildLogFile(String guildId, String name) {
		return FileUtil.ensureFileExists(
				PathUtil.fromGuildFolder(
						guildId,
						"logs",
						STR."\{name}.json"
				),
				"[]"
		);
	}

	private JSONObject generateVoiceChannelActionLog(VoiceActionType voiceActionType, User to, User from, VoiceChannel channel, String value) {
		return new JSONObject()
				.put("action", new JSONObject()
						.put("type", voiceActionType)
						.put("value", value))
				.put("affected", DataUtil.createUserInfoJson(to))
				.put("inflicter", DataUtil.createUserInfoJson(from))
				.put("channel", new JSONObject()
						.put("name", channel.getName())
						.put("id", channel.getId()))
				.put("time", DataUtil.getCurrentTimeJson());
	}
}
