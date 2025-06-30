package net.korithekoder.projectpiggyg;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.korithekoder.projectpiggyg.command.stupid.TrollCommandListener;
import net.korithekoder.projectpiggyg.data.Constants;
import net.korithekoder.projectpiggyg.event.guild.JoinLeaveGuildEventListener;
import net.korithekoder.projectpiggyg.util.app.AppUtil;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import net.korithekoder.projectpiggyg.util.git.GitUtil;
import net.korithekoder.projectpiggyg.util.sys.SystemUtil;
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

	// Use "event.getJda()" when you need to access the JDA instance in your event listeners
	private static final JDA client = JDABuilder.createLight(Constants.PIGGYG_TOKEN, Constants.ALLOWED_GATEWAY_INTENTS)
			.enableCache(CacheFlag.VOICE_STATE)
			.setEventPassthrough(true)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.setChunkingFilter(ChunkingFilter.ALL)
			.build();

	private static final String logSeparationLine = "-----------------------------------------------------------------";

	/**
	 * Sets up everything needed for PiggyG to function.
	 */
	public static void init() {
		client.addEventListener(new ListenerAdapter() {
			@Override
			public void onReady(@NotNull ReadyEvent event) {
				// Insert code to load PiggyG here!
				removeOldLogFiles();
				logGitInfo();
				setupProjectFilesAndFolders();
				configureUtilities();
				logSystemInfo();
				logVersionInfo();
				registerEventListeners();
				uploadCommands();
			}
		});
	}

	private static void removeOldLogFiles() {
		File logsFolder = Paths.get(PathUtil.ofAppData("logs")).toFile();
		// If the logs folder doesn't exist, then
		// skip the operation to avoid null errors
		if (!logsFolder.isDirectory()) {
			return;
		}
		// Get all log files
		List<File> logFiles = new ArrayList<>(Arrays.stream(logsFolder.listFiles()).sorted().toList());

		// Sort from oldest to newest
		Collections.reverse(logFiles);

		// If the number of log files is over the set limit, then
		// delete all old ones to save memory
		if (logFiles.size() > Constants.MAX_LOG_FILES_ALLOWED - 1) {
			for (int i = Constants.MAX_LOG_FILES_ALLOWED - 1; i < logFiles.size(); i++) {
				File f = logFiles.get(i);
				f.delete();
			}
		}
	}

	private static void logGitInfo() {
		GitUtil.RepoInfo repoInfo = GitUtil.getRepoInfo();
		LoggerUtil.log("### PIGGYG 2.0 ###", LogType.INFO, false, false);
		LoggerUtil.log("Setting up build", LogType.INFO, true, false);

		// Log current commit
		if (repoInfo.commit() != null) {
			LoggerUtil.log(STR."Git Commit: \{repoInfo.commit()}", LogType.INFO, false, false);
		} else {
			LoggerUtil.log("Could not determine current Git commit.", LogType.ERROR, false, false);
		}
		// Log current branch
		if (repoInfo.branch() != null) {
			LoggerUtil.log(STR."Git Branch: \{repoInfo.branch()}", LogType.INFO, false, false);
		} else {
			LoggerUtil.log("Could not determine current Git branch.", LogType.ERROR, false, false);
		}
		// Log remote URL
		if (repoInfo.remoteUrl() != null) {
			LoggerUtil.log(STR."Git Remote URL: \{repoInfo.remoteUrl()}", LogType.INFO, false, false);
		} else {
			LoggerUtil.log("Could not determine Git remote URL.", LogType.ERROR, false, false);
		}
		// Log if the current repo is modified
		LoggerUtil.log(STR."Git Modified?: \{repoInfo.isModified()}", LogType.INFO, false, false);
		LoggerUtil.log(logSeparationLine, LogType.INFO, false, false);
	}

	private static void setupProjectFilesAndFolders() {
		PathUtil.createPath(Constants.APP_DATA_DIRECTORY, false);
		PathUtil.createPath(PathUtil.ofAppData("logs"), false);
		PathUtil.createPath(PathUtil.ofAppData("guilds"), false);
	}

	private static void configureUtilities() {
		LoggerUtil.configure();
	}

	private static void logSystemInfo() {
		LoggerUtil.log(STR."Current Platform: \{SystemUtil.getPlatformType()}", LogType.INFO, false);
		LoggerUtil.log(STR."Current Platform Version: \{System.getProperty("os.version")}", LogType.INFO, false);
		LoggerUtil.log(STR."App Data Directory: \{Constants.APP_DATA_DIRECTORY}", LogType.INFO, false);
		LoggerUtil.log(logSeparationLine, LogType.INFO, false);
	}

	private static void logVersionInfo() {
		LoggerUtil.log(STR."PiggyG Version: \{AppUtil.getAppVersion()}", LogType.INFO, false);
		LoggerUtil.log(STR."JDA API Version: \{JDAInfo.VERSION}", LogType.INFO, false);
		LoggerUtil.log(STR."Discord API Version: \{JDAInfo.DISCORD_GATEWAY_VERSION}", LogType.INFO, false);
		LoggerUtil.log(STR."Java Version: \{System.getProperty("java.version")}", LogType.INFO, false);
		LoggerUtil.log(logSeparationLine, LogType.INFO, false);
	}

	private static void registerEventListeners() {
		// Normal events
		client.addEventListener(new JoinLeaveGuildEventListener());
		// Command events
		client.addEventListener(new TrollCommandListener());
	}

	private static void uploadCommands() {
		client.updateCommands()
				.addCommands(
						// Troll command
						Commands.slash("troll", "Send an anonymous DM to a user on the server.")
								.addOption(OptionType.USER, "user", "The user to troll.", true)
								.addOption(OptionType.STRING, "message", "The message to send.", true)
								.addOption(OptionType.ATTACHMENT, "attachment", "An optional attachment to send with your dumb message.")
				)
				.queue(
						success -> {
							LoggerUtil.log("All commands were successfully uploaded!", LogType.INFO, false);
							LoggerUtil.log(logSeparationLine, LogType.INFO, false);
						},
						error -> {
							LoggerUtil.log(STR."Failed to upload commands: \{error.getMessage()}", LogType.ERROR, false);
							LoggerUtil.log(logSeparationLine, LogType.INFO, false);
						}
				);
	}

	private Initialize() {
	}
}
