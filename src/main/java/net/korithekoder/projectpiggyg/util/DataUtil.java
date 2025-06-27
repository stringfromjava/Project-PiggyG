package net.korithekoder.projectpiggyg.util;

/**
 * Utility class for manipulating data in variables.
 */
public final class DataUtil {

	/**
	 * Splits the "class" word at the beginning when {@code class}
	 * is used at the end of a class reference.
	 *
	 * @param clazz The class (which can be obtained with using the keyword
	 *              {@code class} at the end of a class).
	 * @return Only the package name.
	 */
	public static String getClassPackage(Class<?> clazz) {
		return clazz.toString().split(" ")[1];
	}

	private DataUtil() {
	}
}
