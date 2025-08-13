package net.stringfromjava.projectpiggyg.data;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import net.stringfromjava.projectpiggyg.util.sys.PlatformType;
import net.stringfromjava.projectpiggyg.util.sys.SystemUtil;

import java.util.ArrayList;

/**
 * Class containing constants used throughout PiggyG.
 * This class is final and cannot be instantiated.
 */
public final class Constants {

	/**
	 * Constants that are used specifically on (or for) Discord.
	 */
	public static final class Discord {

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
		 * The message that gets sent to the guild owner of a new
		 * guild that PiggyG joins.
		 */
		public static final String NEW_GUILD_DM_MESSAGE = """
				## Thanks for adding me to yo' server gang.
				
				Before anything serious happens though, 'ima need to
				give you the breakdown of how things run by me:
				
				- Everything that happens on yo' server doesn't ever
				  slip by when I'm around. When something happens, I remember and log it.
				  If you wanna obtain a log, then use the corresponding command
				  to get the specific kind of log.
				- Get stuck on something? I gotchu gang, just use `/help <command>`
				  to get some helpful info about one of my commands.
				""";

		private Discord() {
		}
	}

	/**
	 * Constants that have to do with the local system PiggyG is running on.
	 */
	public static final class System {

		/**
		 * The slash character for directories based on the OS PiggyG is running on.
		 */
		public static final char OS_PATH_SEPERATOR = (SystemUtil.getPlatformType() == PlatformType.WINDOWS) ? '\\' : '/';

		/**
		 * The full directory to the project's app data folder for PiggyG.
		 */
		public static final String APP_DATA_DIRECTORY = STR."\{PathUtil.getUserHomePath()}\{OS_PATH_SEPERATOR}.piggyg";

		/**
		 * The maximum number of log files that can be stored in
		 * the {@code logs} folder.
		 */
		public static final int MAX_LOG_FILES_ALLOWED = 15;

		/**
		 * The name of the folder that contains all the logs
		 * for each guild.
		 */
		public static final String GUILD_LOG_FOLDER_NAME = "logs";

		/**
		 * The name of the folder that contains all the
		 * troll attachments sent inside each guild.
		 */
		public static final String GUILD_TROLL_ATTACHMENT_FOLDER_NAME = "trollattachments";

		/**
		 * The name of the folder that contains all the
		 * cached data for each guild.
		 */
		public static final String GUILD_BLOB_CACHE_FOLDER_NAME = "blobcache";

		/**
		 * The name of the folder that contains all the
		 * cached messages for edit/delete history inside each guild.
		 */
		public static final String GUILD_BLOB_CACHE_MESSAGES_FOLDER_NAME = "messages";

		/**
		 * The name of the folder that contains all the
		 * cached message attachments inside each guild.
		 */
		public static final String GUILD_BLOB_CACHE_MESSAGES_ATTACHMENT_FOLDER_NAME = "attachments";

		/**
		 * The file name for what the troll command log
		 * file is called.
		 */
		public static final String GUILD_CONFIG_FILE_NAME = "config.json";

		/**
		 * The file name for what the troll command log
		 * file is called.
		 */
		public static final String TROLL_LOG_FILE_NAME = "troll.json";

		/**
		 * The file name for what the guild voice channel
		 * joins/leaves logs file is called.
		 */
		public static final String VOICE_JOINS_LEAVES_LOG_FILE_NAME = "voice.json";

		/**
		 * The file name for what the guild mute/deafen logs file
		 * for guilds is called.
		 */
		public static final String VOICE_ACTION_LOG_FILE_NAME = "voice-action.json";

		/**
		 * The file name for what the deleted message logs file
		 * for guilds is called.
		 */
		public static final String DELETED_MESSAGE_LOG_FILE_NAME = "deleted-message.json";

		private System() {
		}
	}

	public static final class Conditionals {

		/**
		 * ID for the conditional {@code MESSAGE_LOGGING_ALLOWED}, determining if
		 * message edit history and deletion history are allowed.
		 * This will also determine if message caching is allowed, too.
		 * <u><i><b>NOTE:</b> If message logging was enabled before, but gets disabled
		 * after, then all cached memory will be deleted to conserve memory!</i></u>
		 */
		public static final String MESSAGE_LOGGING_ALLOWED = "MESSAGE_LOGGING_ALLOWED";

		private Conditionals() {
		}
	}

	/**
	 * Constants for things specifically relating to either
	 * debugging PiggyG or the IDE it is running in (if it is, of course).
	 */
	public static final class Debug {

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

		private Debug() {
		}
	}

	private Constants() {
	}
}
