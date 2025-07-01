package net.korithekoder.projectpiggyg.util.data;

import org.json.JSONObject;

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

	private DataUtil() {
	}
}
