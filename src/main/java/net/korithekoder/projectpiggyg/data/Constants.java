package net.korithekoder.projectpiggyg.data;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.requests.GatewayIntent;

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

	private Constants() {}
}
