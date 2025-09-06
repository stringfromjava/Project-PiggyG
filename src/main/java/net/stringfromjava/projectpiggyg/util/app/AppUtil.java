package net.stringfromjava.projectpiggyg.util.app;

import net.stringfromjava.projectpiggyg.util.sys.RuntimeUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * Utility class for either collecting or manipulating
 * information in the application, including info in the {@code pom.xml}
 * file, the {@code config.properties} file, etc.
 */
public final class AppUtil {

	private static Properties configProperties;
	private static ArrayList<String> conditionals;

	public static void configure() {
		// Load the config file
		configProperties = new Properties();
		attemptPropertiesLoad(configProperties, "/config.properties");
		// Load and configure all conditionals
		conditionals = new ArrayList<>() {
			{
				String[] conditionals = Arrays.stream(configProperties.getProperty("conditionals", "").split(","))
						.map(String::trim)
						.toArray(String[]::new);
				addAll(Arrays.asList(conditionals));
			}
		};
	}

	/**
	 * Gets the app's current version that it is running on.
	 *
	 * @return The current version. Note that if it is being run on an
	 * IDE, or it isn't running from a packaged {@code .jar} file, then
	 * it will return {@code DEV} instead.
	 */
	public static String getAppVersion() {
		String version = AppUtil.class.getPackage().getImplementationVersion();
		if (!RuntimeUtil.isRunningFromJar() || version == null) {
			// Fallback if not running from a packaged .jar, usually this gets
			// returned instead if PiggyG is running in an IDE
			version = "DEV";
		}
		return version;
	}

	/**
	 * Returns if a conditional is enabled.
	 *
	 * @param id The ID of the conditional.
	 * @return If it is enabled.
	 */
	public static boolean conditionalEnabled(String id) {
		return conditionals.contains(id);
	}

	private static void attemptPropertiesLoad(Properties properties, String resourcePath) {
		try (InputStream in = AppUtil.class.getResourceAsStream(resourcePath)) {
			properties.load(in);
		} catch (Exception e) {
			String errorMsg = String.format(
					"\"%s\" file was not found. It is required in order to run PiggyG, did you delete it?",
					resourcePath
			);
			LoggerUtil.error(errorMsg);
			System.exit(0);
		}
	}

	private AppUtil() {
	}
}
