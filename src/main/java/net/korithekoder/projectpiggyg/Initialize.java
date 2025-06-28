package net.korithekoder.projectpiggyg;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.korithekoder.projectpiggyg.data.Constants;
import net.korithekoder.projectpiggyg.util.*;
import org.jetbrains.annotations.NotNull;

/**
 * The initialization of PiggyG. This is where
 * components like folders, logging, event listeners and
 * similar are set up and configured.
 */
public final class Initialize {

	// Use "event.getJda()" when you need to access the JDA instance in your event listeners
	private static final JDA CLIENT = JDABuilder.createLight(Constants.PIGGYG_TOKEN, Constants.ALLOWED_GATEWAY_INTENTS)
			.enableCache(CacheFlag.VOICE_STATE)
			.setEventPassthrough(true)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.setChunkingFilter(ChunkingFilter.ALL)
			.build();

	/**
	 * Sets up everything needed for PiggyG to function.
	 */
	public static void init() {
		CLIENT.addEventListener(new ListenerAdapter() {
			@Override
			public void onReady(@NotNull ReadyEvent event) {
				// Insert code to load PiggyG here!
				logGitInfo();
				setupProjectFilesAndFolders();
				configureUtilities();
				logSystemInfo();
				logVersionInfo();
			}
		});
	}

	private static void logGitInfo() {
		GitUtil.RepoInfo repoInfo = GitUtil.getRepoInfo();
		LoggerUtil.log("PIGGYG 2.0", LogType.INFO, false, false);
		LoggerUtil.log("Setting up build", LogType.INFO, true, false);

		// Log current commit
		if (repoInfo.commit() != null) {
			LoggerUtil.log("Git Commit: " + repoInfo.commit(), LogType.INFO, false, false);
		} else {
			LoggerUtil.log("Could not determine current Git commit.", LogType.ERROR, false, false);
		}
		// Log current branch
		if (repoInfo.branch() != null) {
			LoggerUtil.log("Git Branch: " + repoInfo.branch(), LogType.INFO, false, false);
		} else {
			LoggerUtil.log("Could not determine current Git branch.", LogType.ERROR, false, false);
		}
		// Log remote URL
		if (repoInfo.branch() != null) {
			LoggerUtil.log("Git Remote URL: " + repoInfo.remoteUrl(), LogType.INFO, false, false);
		} else {
			LoggerUtil.log("Could not determine Git remote URL.", LogType.ERROR, false, false);
		}
		// Log if the current repo is modified
		if (repoInfo.branch() != null) {
			LoggerUtil.log("Git Modified?: " + repoInfo.isModified(), LogType.INFO, false, false);
		} else {
			LoggerUtil.log("Could not determine if the current repository is modified.", LogType.ERROR, false, false);
		}

		LoggerUtil.log("--------------------------------------------------------------", LogType.INFO, false, false);
	}

	private static void setupProjectFilesAndFolders() {
		PathUtil.createDirectory(Constants.APP_DATA_DIRECTORY, false);
		PathUtil.createDirectory(PathUtil.ofAppData("logs"), false);
	}

	private static void configureUtilities() {
		LoggerUtil.configure();
	}

	private static void logSystemInfo() {
		LoggerUtil.log("Current Platform: " + System.getProperty("os.name"), LogType.INFO, false);
		LoggerUtil.log("Current Platform Version: " + System.getProperty("os.version"), LogType.INFO, false);
		LoggerUtil.log("--------------------------------------------------------------", LogType.INFO, false, false);
	}

	private static void logVersionInfo() {
		LoggerUtil.log("PiggyG Version: " + AppUtil.getAppVersion(), LogType.INFO, false);
		LoggerUtil.log("App Data Directory: " + Constants.APP_DATA_DIRECTORY, LogType.INFO, false);
		LoggerUtil.log("JDA Version: " + JDAInfo.VERSION, LogType.INFO, false);
		LoggerUtil.log("Discord API Version: " + JDAInfo.DISCORD_GATEWAY_VERSION, LogType.INFO, false);
		LoggerUtil.log("Java Version: " + System.getProperty("java.version"), LogType.INFO, false);
		LoggerUtil.log("--------------------------------------------------------------", LogType.INFO, false, false);
	}

	private Initialize() {
	}
}
