package net.stringfromjava.projectpiggyg.util.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import net.stringfromjava.projectpiggyg.command.CommandListener;
import net.stringfromjava.projectpiggyg.data.command.CommandOptionData;
import net.stringfromjava.projectpiggyg.util.app.LoggerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for handling Discord commands for PiggyG.
 */
public final class CommandUtil {

	private static Map<String, String> helpDescriptions = new HashMap<>();

	/**
	 * Creates a command to register onto PiggyG with simplicity.
	 *
	 * @param jda             The JDA object to register the command's event listener with.
	 * @param commandListener The {@link net.stringfromjava.projectpiggyg.command.CommandListener} to
	 *                        register the command with. Note that fields such as {@code name} and {@code description}
	 *                        that are a part of the listener will also be what
	 *                        the user will see on Discord when the command registers.
	 */
	public static CommandData createCommandData(@NotNull JDA jda, @NotNull CommandListener commandListener) {
		List<CommandOptionData> options = (List<CommandOptionData>) commandListener.getOptions();
		CommandData command = Commands.slash(commandListener.getName(), commandListener.getDescription())
				.addOptions(options.stream()
						.map(option -> new OptionData(
								option.optionType(),
								option.name(),
								option.description(),
								option.required()
						))
						.toList())
				.setDefaultPermissions(commandListener.getMemberPermissions());
		jda.addEventListener(commandListener);
		helpDescriptions.put(commandListener.getName(), commandListener.getHelpDescription());
		LoggerUtil.log(STR."Creating new command '/\{command.getName()}'");
		return command;
	}

	/**
	 * A safe way to send a reply in a command without errors being raised.
	 *
	 * @param message The message to send.
	 * @param event   The {@link net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent} to send a reply with.
	 */
	public static void sendSafeReply(String message, SlashCommandInteractionEvent event) {
		sendSafeReply(message, event, null, null, null);
	}

	/**
	 * A safe way to send a reply in a command without errors being raised.
	 *
	 * @param message The message to send.
	 * @param event   The {@link net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent} to send a reply with.
	 * @param files   Optional files to send with the reply.
	 */
	public static void sendSafeReply(String message, SlashCommandInteractionEvent event, Collection<FileUpload> files) {
		sendSafeReply(message, event, files, null, null);
	}

	/**
	 * A safe way to send a reply in a command without errors being raised.
	 *
	 * @param message   The message to send.
	 * @param event     The {@link net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent} to send a reply with.
	 * @param files     Optional files to send with the reply.
	 * @param onSuccess Callback function to be triggered when the message was successfully sent.
	 */
	public static void sendSafeReply(String message, SlashCommandInteractionEvent event, Collection<FileUpload> files, Runnable onSuccess) {
		sendSafeReply(message, event, files, onSuccess, null);
	}

	/**
	 * A safe way to send a reply in a command without errors being raised.
	 *
	 * @param message   The message to send.
	 * @param event     The {@link net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent} to send a reply with.
	 * @param files     Optional files to send with the reply.
	 * @param onSuccess Callback function to be triggered when the message was successfully sent.
	 * @param onFailure Callback function to be triggered when the message failed to send.
	 */
	public static void sendSafeReply(@Nullable String message, @NotNull SlashCommandInteractionEvent event, @Nullable Collection<FileUpload> files, @Nullable Runnable onSuccess, @Nullable Runnable onFailure) {
		event.reply((message != null) ? message : "")
				.addFiles((files != null) ? files : List.of())
				.submit()
				.orTimeout(30, TimeUnit.SECONDS)
				.whenComplete((msg, exception) -> {
					if (exception == null) {
						if (onSuccess != null) {
							onSuccess.run();
						}
					} else {
						if (onFailure != null) {
							onFailure.run();
						}
						String errorMsg = STR."Failed to reply to message, got this message: \{exception.getMessage()}";
						LoggerUtil.error(errorMsg);
						throw new RuntimeException(errorMsg);
					}
				});
	}

	/**
	 * Gets the help description of a command when
	 * the {@code /help} command is used.
	 *
	 * @param command The command to get a help description from.
	 * @return The help description of the command passed down.
	 * @throws RuntimeException If the command passed down isn't registered.
	 */
	@Nullable
	public static String getHelpDescription(String command) {
		if (helpDescriptions.containsKey(command)) {
			return helpDescriptions.get(command);
		} else {
			throw new RuntimeException(STR."There's no command registered under the name '/\{command}'!");
		}
	}

	private CommandUtil() {
	}
}
