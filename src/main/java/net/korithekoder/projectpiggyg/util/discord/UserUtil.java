package net.korithekoder.projectpiggyg.util.discord;

import net.dv8tion.jda.api.entities.User;
import net.korithekoder.projectpiggyg.util.data.DataUtil;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;

/**
 * Utility class for carrying out processes
 * with specifically Discord users.
 */
public final class UserUtil {

	/**
	 * Makes PiggyG send a direct message (DM) to a specific user.
	 *
	 * @param user    The user to send a DM to (as a {@link net.dv8tion.jda.api.entities.User} object).
	 * @param message The message to send the user.
	 */
	public static void sendDirectMessage(User user, String message) {
		sendDirectMessage(user, message, null, null);
	}

	/**
	 * Makes PiggyG send a direct message (DM) to a specific user.
	 *
	 * @param user    The user to send a DM to (as a {@link net.dv8tion.jda.api.entities.User} object).
	 * @param message The message to send the user.
	 */
	public static void sendDirectMessage(User user, String message, Runnable onSuccess) {
		sendDirectMessage(user, message, onSuccess, null);
	}

	/**
	 * Makes PiggyG send a direct message (DM) to a specific user.
	 *
	 * @param user    The user to send a DM to (as a {@link net.dv8tion.jda.api.entities.User} object).
	 * @param message The message to send the user.
	 */
	public static void sendDirectMessage(User user, String message, Runnable onSuccess, Runnable onFailure) {
		user.openPrivateChannel()
				.flatMap(privateChannel -> privateChannel.sendMessage(message))
				.queue(
						success -> {
							if (onSuccess != null) {
								onSuccess.run();
							}
						},
						failure -> {
							if (onFailure != null) {
								onFailure.run();
							}
							String failMsg = DataUtil.buildString(
									"Could not send DM to user ",
									user.getName(),
									", they either blocked PiggyG (so sad) or have their DMs closed!"
							);
							LoggerUtil.log(failMsg, LogType.WARN, false);
						}
				);
	}

	private UserUtil() {
	}
}
