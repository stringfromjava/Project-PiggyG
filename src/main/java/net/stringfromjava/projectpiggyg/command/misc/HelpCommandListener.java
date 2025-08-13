package net.stringfromjava.projectpiggyg.command.misc;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.stringfromjava.projectpiggyg.command.CommandListener;
import net.stringfromjava.projectpiggyg.data.command.CommandOptionData;
import net.stringfromjava.projectpiggyg.util.discord.CommandUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Command for getting more in-depth info about PiggyG's commands.
 */
public class HelpCommandListener extends CommandListener {

	public HelpCommandListener(String name) {
		super(name);
		description = "Get more info about my commands.";
		helpDescription = """
				Get more helpful info for either all commands or a specific command.
				
				TIP: You can put in a specific command to get more details about it!
				""";
		options = List.of(
				new CommandOptionData(OptionType.STRING, "command", "Specific command to get more info of.", false)
		);
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		String command;
		OptionMapping commandOM = event.getOption("command");
		List<Command> commands = event.getJDA().retrieveCommands().complete();

		if (commandOM != null) {
			command = commandOM.getAsString();
		} else {
			command = null;
		}

		// List every command with their basic description if
		// a specific command wasn't provided
		StringBuilder toSend = new StringBuilder();
		AtomicBoolean commandFound = new AtomicBoolean(false);
		if (command == null) {
			for (net.dv8tion.jda.api.interactions.commands.Command cmd : commands) {
				toSend.append(STR."`/\{cmd.getName()} ");
				cmd.getOptions().forEach(option -> toSend.append(STR."<\{option.getName()}> "));
				toSend.append("`\n")
						.append(STR."\t\{cmd.getDescription()}\n")
						.append("------------------------------------------------------------\n");
			}
			CommandUtil.sendSafeCommandReply(toSend.toString(), event);
		} else {
			// Get the wanted command with more descriptive info
			commands.stream()
					.filter(cmd -> cmd.getName().equals(command))
					.findFirst()
					.ifPresent(cmd -> {
						toSend.append(STR."# `/\{cmd.getName()}`\n");
						cmd.getOptions().forEach(option ->
								toSend.append(STR."\t`<name: \{option.getName()}")
										.append(STR." | required: \{option.isRequired()}")
										.append(STR." | type: \{option.getType()}>`")
										.append(STR." - \{option.getDescription()}\n\n")
						);
						toSend.append("## Description\n");
						toSend.append(CommandUtil.getHelpDescription(cmd.getName()));
						event.reply(toSend.toString()).queue();
						commandFound.set(true);
					});
			// Stop the command if it was found and sent
			if (commandFound.get()) {
				return;
			}
			CommandUtil.sendSafeCommandReply("Sorry fam', but the command given wasn't found :pensive:", event);
		}
	}
}
