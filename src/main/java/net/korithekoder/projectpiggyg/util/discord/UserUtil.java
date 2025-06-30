package net.korithekoder.projectpiggyg.util.discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.FileUpload;
import net.korithekoder.projectpiggyg.util.data.DataUtil;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		sendDirectMessage(user, message, List.of(), null, null);
	}

	/**
	 * Makes PiggyG send a direct message (DM) to a specific user.
	 *
	 * @param user    The user to send a DM to (as a {@link net.dv8tion.jda.api.entities.User} object).
	 * @param message The message to send the user.
	 */
	public static void sendDirectMessage(User user, String message, @NotNull Collection<FileUpload> files) {
		sendDirectMessage(user, message, files, null, null);
	}

	/**
	 * Makes PiggyG send a direct message (DM) to a specific user.
	 *
	 * @param user      The user to send a DM to (as a {@link net.dv8tion.jda.api.entities.User} object).
	 * @param message   The message to send the user.
	 * @param onSuccess Callback function that gets called when the message is sent.
	 */
	public static void sendDirectMessage(User user, String message, @NotNull Collection<FileUpload> files, Runnable onSuccess) {
		sendDirectMessage(user, message, files, onSuccess, null);
	}

	/**
	 * Makes PiggyG send a direct message (DM) to a specific user.
	 *
	 * @param user      The user to send a DM to (as a {@link net.dv8tion.jda.api.entities.User} object).
	 * @param message   The message to send the user.
	 * @param files     A list of {@link import net.dv8tion.jda.api.utils.FileUpload} objects.
	 * @param onSuccess Callback function that gets called when the message is sent.
	 * @param onFailure Callback function that gets called when the message failed (or cannot) send.
	 */
	public static void sendDirectMessage(User user, String message, @NotNull Collection<FileUpload> files, Runnable onSuccess, Runnable onFailure) {
		user.openPrivateChannel()
				.flatMap(privateChannel -> {
					return privateChannel.sendMessage(message)
							.addFiles(files);
				})
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
