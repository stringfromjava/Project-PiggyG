package net.korithekoder.projectpiggyg;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.korithekoder.projectpiggyg.data.Constants;

/**
 * The entry point for the PiggyG Discord bot.
 * This class does not need to be modified. This is only used to
 * initialize PiggyG and ensures he logs into the Discord API.
 */
public class Main {

	// Use "event.getJda()" to get the JDA instance in event listeners
	private static final JDA JDA_INSTANCE = JDABuilder.createLight(Constants.PIGGYG_TOKEN, Constants.ALLOWED_GATEWAY_INTENTS)
			.setEventPassthrough(true)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.setChunkingFilter(ChunkingFilter.ALL).enableCache(CacheFlag.VOICE_STATE).build();

	/**
	 * The main method that starts it all.
	 * @param args The arguments passed down when the program is run through
	 *             the command line.
	 */
	public static void main(String[] args) {}
}