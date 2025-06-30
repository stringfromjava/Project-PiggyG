package net.korithekoder.projectpiggyg.util.data;

/**
 * Utility class for manipulating data in (specifically) variables.
 */
public final class DataUtil {

	/**
	 * Simply builds a string together without having to
	 * concatenate everything with the plus ({@code +}) operator.
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

	private DataUtil() {
	}
}
