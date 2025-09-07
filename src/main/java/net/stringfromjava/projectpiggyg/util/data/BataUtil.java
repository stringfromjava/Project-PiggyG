package net.stringfromjava.projectpiggyg.util.data;

/**
 * Utility class for handling basic data operations.
 * This can include anything from manipulating data formats,
 * converting data types, and more. (Hence why it's called Bata).
 * <p>
 * I'm so funny trust.
 */
public final class BataUtil {

	/**
	 * Combines two string arrays into one.
	 *
	 * @param base The base array to append to.
	 * @param toAppend Any number of strings to append to the base array. This can
	 *                 either be multiple strings or another array of strings.
	 * @return A new array containing all the strings from both arrays.
	 */
	public static String[] combineArrays(String[] base, String... toAppend) {
		String[] all = new String[base.length + toAppend.length];
		System.arraycopy(base, 0, all, 0, base.length);
		System.arraycopy(toAppend, 0, all, base.length, toAppend.length);
		return all;
	}

	private BataUtil() {
	}
}
