package net.korithekoder.projectpiggyg;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.korithekoder.projectpiggyg.data.Constants;
import net.korithekoder.projectpiggyg.util.*;

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
		setupProjectFilesAndFolders();
		configureUtilities();
		logSystemInfo();
	}

	private static void setupProjectFilesAndFolders() {
		PathUtil.createDirectory(Constants.PROJECT_DIRECTORY, false);
		PathUtil.createDirectory(PathUtil.ofAppData("logs"), false);
	}

	private static void configureUtilities() {
		LoggerUtil.configure();
	}

	private static void logSystemInfo() {
		LoggerUtil.log("Current Platform: " + SystemUtil.getPlatformType(), LogType.INFO, false);
	}

	private Initialize() {
	}
}
