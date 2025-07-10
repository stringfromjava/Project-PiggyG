package net.korithekoder.projectpiggyg.util.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.korithekoder.projectpiggyg.command.CommandListener;
import net.korithekoder.projectpiggyg.data.command.CommandOptionData;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for handling Discord commands for PiggyG.
 */
public final class CommandUtil {

	private static Map<String, String> helpDescriptions = new HashMap<>();

	/**
	 * Creates a command to register onto PiggyG with simplicity.
	 *
	 * @param jda             The JDA object to register the command's event listener with.
	 * @param commandListener The {@link net.korithekoder.projectpiggyg.command.CommandListener} to
	 *                        register the command with. Note that the {@code name} and {@code description}
	 *                        fields that are a part of the listener will also be the name and description that
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
