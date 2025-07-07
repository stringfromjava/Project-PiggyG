package net.korithekoder.projectpiggyg.util.data;

import org.json.JSONObject;

import java.time.Clock;
import java.time.LocalDateTime;

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
	 * @param <T>         The type of the value to return.
	 * @param jsonObject  The {@link JSONObject} to retrieve the field from.
	 * @param key         The key of the field to retrieve.
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
	public static JSONObject createCommandLogTime() {
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

	private DataUtil() {
	}
}
