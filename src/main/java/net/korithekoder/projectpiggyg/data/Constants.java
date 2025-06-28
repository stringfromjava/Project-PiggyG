package net.korithekoder.projectpiggyg.data;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.korithekoder.projectpiggyg.util.PathUtil;
import net.korithekoder.projectpiggyg.util.PlatformType;
import net.korithekoder.projectpiggyg.util.SystemUtil;

import java.util.ArrayList;

/**
 * Class containing constants used throughout PiggyG.
 * This class is final and cannot be instantiated.
 */
public final class Constants {

	/**
	 * The "password" for the bot to connect to the Discord API.
	 */
	public static final String PIGGYG_TOKEN = Dotenv.load().get("TOKEN");

	/**
	 * The allowed gateway intents for PiggyG.
	 */
	public static final ArrayList<GatewayIntent> ALLOWED_GATEWAY_INTENTS = new ArrayList<>() {
		{
			add(GatewayIntent.GUILD_MESSAGES);
			add(GatewayIntent.MESSAGE_CONTENT);
			add(GatewayIntent.GUILD_MEMBERS);
			add(GatewayIntent.GUILD_VOICE_STATES);
		}
	};

	/**
	 * The slash character for file pathways based on what OS PiggyG is running on.
	 */
	public static final char OS_SLASH = (SystemUtil.getPlatformType() == PlatformType.WINDOWS) ? '\\' : '/';

	/**
	 * The full directory to the project's app data folder for PiggyG.
	 */
	public static final String APP_DATA_DIRECTORY = PathUtil.getUserHomePath() + OS_SLASH + "PiggyG";

	/**
	 * ANSI code for resetting the color of text in the console.
	 */
	public static final String CONSOLE_TEXT_RESET = "\u001B[0m";

	/**
	 * The color black for text in the console.
	 */
	public static final String CONSOLE_TEXT_BLACK = "\u001B[30m";

	/**
	 * The color red for text in the console.
	 */
	public static final String CONSOLE_TEXT_RED = "\u001B[31m";

	/**
	 * The color pink for text in the console.
	 */
	public static final String CONSOLE_TEXT_PINK = "\u001b[38;5;219m";

	/**
	 * The color green for text in the console.
	 */
	public static final String CONSOLE_TEXT_GREEN = "\u001B[32m";

	/**
	 * The color yellow for text in the console.
	 */
	public static final String CONSOLE_TEXT_YELLOW = "\u001B[33m";

	/**
	 * The color blue for text in the console.
	 */
	public static final String CONSOLE_TEXT_BLUE = "\u001B[34m";

	/**
	 * The color purple for text in the console.
	 */
	public static final String CONSOLE_TEXT_PURPLE = "\u001B[35m";

	/**
	 * The color cyan for text in the console.
	 */
	public static final String CONSOLE_TEXT_CYAN = "\u001B[36m";

	/**
	 * The color white for text in the console.
	 */
	public static final String CONSOLE_TEXT_WHITE = "\u001B[37m";

	/**
	 * Makes text in the console bold.
	 */

	public static final String CONSOLE_TEXT_BOLD = "\033[0;1m";

	/**
	 * Makes text in the console underlined.
	 */
	public static final String CONSOLE_TEXT_UNDERLINE = "\u001B[4m";

	private Constants() {
	}
}
