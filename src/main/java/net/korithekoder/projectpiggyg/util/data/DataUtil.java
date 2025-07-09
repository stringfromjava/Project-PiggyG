package net.korithekoder.projectpiggyg.util.data;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for manipulating data in (specifically) variables.
 */
public final class DataUtil {

	/**
	 * Simply builds a string together without having to
	 * concatenate everything with the addition ({@code +}) operator.
	 *
	 * @param strings All strings to be put together.
	 * @return A newly constructed string.
	 */
	public static String buildString(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * Retrieves a field from a {@link JSONObject} and casts it to the specified type.
	 * <p>
	 * If the key does not exist or the value cannot be cast to the specified type,
	 * the provided default value is returned.
	 *
	 * @param <T>          The type of the value to return.
	 * @param jsonObject   The {@link JSONObject} to retrieve the field from.
	 * @param key          The key of the field to retrieve.
	 * @param defaultValue The default value to return if the key is missing or the type is mismatched.
	 * @return The value associated with the key, cast to the specified type, or the default value.
	 */
	public static <T> T getJsonField(JSONObject jsonObject, String key, T defaultValue) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			try {
				return (T) value;
			} catch (ClassCastException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	/**
	 * Generates a new {@link org.json.JSONObject} of multiple
	 * keys with info of what time it currently is. (Meant specifically
	 * for new logs that are made for commands.)
	 *
	 * @return A new {@link org.json.JSONObject} with the current time.
	 */
	public static JSONObject getCurrentTimeJson() {
		LocalDateTime time = LocalDateTime.now();
		return new JSONObject()
				.put("year", Integer.toString(time.getYear()))
				.put("month", Integer.toString(time.getMonthValue()))
				.put("day", Integer.toString(time.getDayOfMonth()))
				.put("hour", Integer.toString(time.getHour()))
				.put("minute", Integer.toString(time.getMinute()))
				.put("second", Integer.toString(time.getSecond()))
				.put("tz", Clock.systemDefaultZone().getZone());  // tz = time zone
	}

	/**
	 * Creates a very basic {@link org.json.JSONObject} with info
	 * about a guild (aka its name and ID).
	 *
	 * @param guild The guild to get info from.
	 * @return A {@link org.json.JSONObject} with the info said as above.
	 */
	public static JSONObject createGuildInfoJson(@NotNull Guild guild) {
		return new JSONObject()
				.put("name", guild.getName())
				.put("id", guild.getId());
	}

	/**
	 * Creates a very basic {@link org.json.JSONObject} with info
	 * about a user (aka their username and ID).
	 *
	 * @param user The user to get info from.
	 * @return A {@link org.json.JSONObject} with the info said as above.
	 */
	public static JSONObject createUserInfoJson(@NotNull User user) {
		return new JSONObject()
				.put("name", user.getName())
				.put("id", user.getId());
	}

	public static JSONObject createMessageJson(Message message) {
		Guild guild = message.getGuild();
		List<Message.Attachment> attachments = message.getAttachments();
		User author = message.getAuthor();
		Channel channel = message.getChannel();
		ChannelType channelType = channel.getType();
		JSONArray attachmentsArray = new JSONArray();

		for (Message.Attachment attachment : attachments) {
			attachmentsArray.put(new JSONObject()
					.put("name", attachment.getFileName())
					.put("url", attachment.getUrl()));
		}

		return new JSONObject()
				.put("message", new JSONObject()
						.put("contents", message.getContentRaw())
						.put("attachments", attachments))
				.put("channel-type", channelType)
				.put("author", createUserInfoJson(author))
				.put("guild", createGuildInfoJson(guild))
				.put("time", getCurrentTimeJson());
	}

	/**
	 * Gets and returns a {@link java.util.List} of all file names
	 * of attachments that are part of a {@link net.dv8tion.jda.api.entities.Message}.
	 *
	 * @param message The message to get the attachment file names from.
	 * @return All attachment filenames contained in a {@link java.util.List}.
	 */
	public static List<String> getAttachmentNamesFromMessage(Message message) {
		ArrayList<String> toReturn = new ArrayList<>();
		for (Message.Attachment attachment : message.getAttachments()) {
			toReturn.add(attachment.getFileName());
		}
		return toReturn;
	}

	private DataUtil() {
	}
}
