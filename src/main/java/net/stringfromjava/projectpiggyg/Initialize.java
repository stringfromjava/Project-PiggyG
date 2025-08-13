package net.stringfromjava.projectpiggyg;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.stringfromjava.projectpiggyg.command.misc.HelpCommandListener;
import net.stringfromjava.projectpiggyg.command.obtain.*;
import net.stringfromjava.projectpiggyg.command.stupid.TrollCommandListener;
import net.stringfromjava.projectpiggyg.data.Constants;
import net.stringfromjava.projectpiggyg.event.guild.JoinLeaveGuildEventListener;
import net.stringfromjava.projectpiggyg.event.guild.MessageCacheGuildEventListener;
import net.stringfromjava.projectpiggyg.event.guild.VoiceChannelGuildEventListener;
import net.stringfromjava.projectpiggyg.util.app.AppUtil;
import net.stringfromjava.projectpiggyg.util.app.LogType;
import net.stringfromjava.projectpiggyg.util.app.LoggerUtil;
import net.stringfromjava.projectpiggyg.util.data.JsonUtil;
import net.stringfromjava.projectpiggyg.util.data.FileUtil;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import net.stringfromjava.projectpiggyg.util.discord.GuildUtil;
import net.stringfromjava.projectpiggyg.util.discord.CommandUtil;
import net.stringfromjava.projectpiggyg.util.git.GitUtil;
import net.stringfromjava.projectpiggyg.util.sys.SystemUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The initialization of PiggyG. This is where
 * components like folders, logging, event listeners and
 * similar are set up and configured.
 */
public final class Initialize {

	/**
	 * Has PiggyG been fully initialized?
	 */
	public static boolean initialized = false;

	// Use "event.getJDA()" when you need to access the JDA instance in your event listeners
	private static final JDA client = JDABuilder.createLight(Constants.Discord.PIGGYG_TOKEN, Constants.Discord.ALLOWED_GATEWAY_INTENTS)
			.enableCache(CacheFlag.VOICE_STATE)
			.setEventPassthrough(true)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.setChunkingFilter(ChunkingFilter.ALL)
			.build();

	private static final String logSeparationLine = "==========================================================================================";
	private static final String logSeparationSubLine = "..........................................................................................";

	/**
	 * Sets up everything needed for PiggyG to function.
	 * <p>
	 * <u><b><i>IMPORTANT:</i></b> This should be only called <b>ONCE</b>.</u>
	 */
	public static void init() {
		client.addEventListener(new ListenerAdapter() {
			@Override
			public void onReady(@NotNull ReadyEvent event) {
				LoggerUtil.log("### PIGGYG 2.0 ###", LogType.INFO, false, false, false);
				LoggerUtil.log("Initializing setup process", LogType.INFO, true, false, false);
				displaySeparator(false, false);
				//
				// Insert code to load PiggyG here!
				// ==========================================
				setupProjectFilesAndFolders();
				configureUtilities();
				logGitInfo();
				logSystemInfo();
				logVersionInfo();
				removeOldLogFiles();
				checkForMissingGuildFolders();
				cacheGuildMessages();
				registerEventListeners();
				uploadCommands(); // This part of the setup should ALWAYS be the last!!
			}

			@Override
			public void onException(@NotNull ExceptionEvent event) {
				LoggerUtil.error(STR."Failed to initialize PiggyG, got this error: '\{event.getCause().getMessage()}'", false);
				System.exit(0);
			}
		});
	}

	private static void setupProjectFilesAndFolders() {
		PathUtil.createPath(PathUtil.ofAppData(), false);
		PathUtil.createPath(PathUtil.ofAppData("logs"), false);
		PathUtil.createPath(PathUtil.ofAppData("guilds"), false);
	}

	private static void configureUtilities() {
		LoggerUtil.configure();
		AppUtil.configure();
	}

	private static void logGitInfo() {
		GitUtil.RepoInfo repoInfo = GitUtil.getRepoInfo();
		// Log current commit
		if (repoInfo.commit() != null) {
			LoggerUtil.log(STR."Git Commit: \{repoInfo.commit()}", LogType.INFO, false);
		} else {
			LoggerUtil.log("Could not determine current Git commit.", LogType.ERROR, false);
		}
		// Log current branch
		if (repoInfo.branch() != null) {
			LoggerUtil.log(STR."Git Branch: \{repoInfo.branch()}", LogType.INFO, false);
		} else {
			LoggerUtil.log("Could not determine current Git branch.", LogType.ERROR, false);
		}
		// Log remote URL
		if (repoInfo.remoteUrl() != null) {
			LoggerUtil.log(STR."Git Remote URL: \{repoInfo.remoteUrl()}", LogType.INFO, false);
		} else {
			LoggerUtil.log("Could not determine Git remote URL.", LogType.ERROR, false);
		}
		// Log if the current repo is modified
		LoggerUtil.log(STR."Git Modified?: \{repoInfo.isModified()}", LogType.INFO, false);
		displaySeparator();
	}

	private static void logSystemInfo() {
		LoggerUtil.log(STR."Current Platform: \{SystemUtil.getPlatformType()}", LogType.INFO, false);
		LoggerUtil.log(STR."Current Platform Version: \{System.getProperty("os.version")}", LogType.INFO, false);
		LoggerUtil.log(STR."App Data Directory: \{Constants.System.APP_DATA_DIRECTORY}", LogType.INFO, false);
		displaySeparator();
	}

	private static void logVersionInfo() {
		LoggerUtil.log(STR."PiggyG Version: \{AppUtil.getAppVersion()}", LogType.INFO, false);
		LoggerUtil.log(STR."JDA API Version: \{JDAInfo.VERSION}", LogType.INFO, false);
		LoggerUtil.log(STR."Discord API Version: \{JDAInfo.DISCORD_GATEWAY_VERSION}", LogType.INFO, false);
		LoggerUtil.log(STR."Java Version: \{System.getProperty("java.version")}", LogType.INFO, false);
		displaySeparator();
	}

	private static void removeOldLogFiles() {
		File logsFolder = Paths.get(PathUtil.ofAppData("logs")).toFile();
		// If the logs folder doesn't exist, then
		// skip the operation to avoid null errors
		if (!PathUtil.doesPathExist(logsFolder.getPath())) {
			return;
		}

		List<File> logFiles = new ArrayList<>(Arrays.stream(logsFolder.listFiles()).sorted().toList());

		// Sort from oldest to newest
		Collections.reverse(logFiles);

		// If the number of log files is over the set limit, then
		// delete all old ones to save memory
		int staleFilesFound = 0;
		if (logFiles.size() > Constants.System.MAX_LOG_FILES_ALLOWED - 1) {
			for (int i = Constants.System.MAX_LOG_FILES_ALLOWED - 1; i < logFiles.size(); i++) {
				boolean deleted = FileUtil.deleteFile(logFiles.get(i).getPath());
				if (deleted) {
					staleFilesFound++;
				}
			}
		}
		LoggerUtil.log(STR."Deleted \{staleFilesFound} stale log file\{staleFilesFound != 1 ? "s" : ""}.", LogType.INFO, false);
		displaySeparator();
	}

	private static void checkForMissingGuildFolders() {
		int missingGuildFoldersFound = 0;
		for (Guild guild : client.getGuilds()) {
			String guildFolder = PathUtil.fromGuildFolder(guild.getId());
			if (!PathUtil.doesPathExist(guildFolder)) {
				LoggerUtil.log(
						STR."Folder for guild '\{guild.getName()} (ID = \{guild.getId()}) is missing!",
						LogType.WARN,
						false
				);
				GuildUtil.createNewGuildFolder(guild);
				displaySeparator(true);
				missingGuildFoldersFound++;
			}
		}

		if (missingGuildFoldersFound == 0) {
			LoggerUtil.log(STR."No missing guild folders were detected!", LogType.INFO, false);
		} else {
			LoggerUtil.log(JsonUtil.buildString(
					"Created missing folders for ",
					STR."\{missingGuildFoldersFound} guild",
					STR."\{(missingGuildFoldersFound != 1) ? "s" : ""}."
			), LogType.INFO, false, true);
		}

		displaySeparator();
	}

	private static void cacheGuildMessages() {
		boolean msgLoggingAllowed = AppUtil.conditionalEnabled("MESSAGE_LOGGING_ALLOWED");
		if (!msgLoggingAllowed) {
			LoggerUtil.log(STR."Conditional 'MESSAGE_LOGGING_ALLOWED' is disabled! Skipping message caching");
			displaySeparator();
			// Remove all blob cache folders from each guild folder
			// to save and conserve memory
			for (Guild guild : client.getGuilds()) {
				String blobCachePath = PathUtil.fromGuildFolder(guild.getId(), "blobcache");
				PathUtil.deleteFolder(blobCachePath);
			}
			return;
		}

		for (Guild guild : client.getGuilds()) {
			GuildUtil.cacheGuildMessages(guild);
			displaySeparator(true);
		}
		LoggerUtil.log("Finished caching messages for all guilds.", LogType.INFO, false);
		displaySeparator();
	}

	private static void registerEventListeners() {
		// Guild events
		client.addEventListener(new JoinLeaveGuildEventListener());
		client.addEventListener(new MessageCacheGuildEventListener());
		client.addEventListener(new VoiceChannelGuildEventListener());
	}

	private static void uploadCommands() {
		client.updateCommands().addCommands(
				//
				// Add any new commands here!
				// ========================================
				CommandUtil.createCommandData(
						client,
						new HelpCommandListener("help")
				),
				CommandUtil.createCommandData(
						client,
						new TrollCommandListener("troll")
				),
				CommandUtil.createCommandData(
						client,
						new ObtainTrollLogsCommandListener("obtaintrolllogs")
				),
				CommandUtil.createCommandData(
						client,
						new ObtainTrollAttachmentCommandListener("obtaintrollattachment")
				),
				CommandUtil.createCommandData(
						client,
						new ObtainVoiceChannelLogs("obtainvoicechannellogs")
				),
				CommandUtil.createCommandData(
						client,
						new ObtainVoiceChannelActionLogsCommandListener("obtainvoicechannelactionlogs")
				),
				CommandUtil.createCommandData(
						client,
						new ObtainDeletedMessagesCommandListener("obtaindeletedmessages")
				)
		).queue(
				success -> {
					LoggerUtil.log("All commands have been uploaded.", LogType.INFO, false);
					displaySeparator();
					LoggerUtil.log("PiggyG's setup is now complete.", LogType.INFO, false);
					initialized = true;
					displaySeparator();
				}
		);
	}

	//
	// DO NOT WORRY ABOUT THESE FUNCTIONS, THEY ARE
	// NOT VERY SIGNIFICANT TO PIGGYG'S SETUP.
	// IGNORE EVERYTHING BEYOND THIS POINT
	// =========================================================================

	private static void displaySeparator() {
		displaySeparator(false, true);
	}

	private static void displaySeparator(boolean isSubSeperator) {
		displaySeparator(isSubSeperator, true);
	}

	private static void displaySeparator(boolean isSubSeperator, boolean writeToFile) {
		LoggerUtil.log(
				(!isSubSeperator) ? logSeparationLine : logSeparationSubLine,
				LogType.INFO,
				false,
				false,
				writeToFile
		);
	}

	private Initialize() {
	}
}
