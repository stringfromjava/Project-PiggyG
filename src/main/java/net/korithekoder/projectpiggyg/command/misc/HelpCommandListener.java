package net.korithekoder.projectpiggyg.command.misc;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.korithekoder.projectpiggyg.command.PiggyGCommand;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Command for getting info about another command.
 */
public class HelpCommandListener extends PiggyGCommand {

	private final Map<String, String> commandDescriptions = new HashMap<>();

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		if (!event.getName().equals("help")) {
			return;
		}

		String command;
		OptionMapping commandOM = event.getOption("command");
		List<Command> commands = event.getJDA().retrieveCommands().complete();
		assignDescriptions();

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
			for (Command cmd : commands) {
				toSend.append(STR."`/\{cmd.getName()} ");
				cmd.getOptions().forEach(option -> toSend.append(STR."<\{option.getName()}> "));
				toSend.append("`\n")
						.append(STR."\t\{cmd.getDescription()}\n")
						.append("------------------------------------------------------------\n");
			}
			event.reply(toSend.toString()).queue();
		} else {
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
						toSend.append(commandDescriptions.get(cmd.getName()));
						event.reply(toSend.toString()).queue();
						commandFound.set(true);
					});
			// Reply saying the command wasn't found if it doesn't exist
			if (commandFound.get()) {
				return;
			}
			event.reply("Sorry fam', but the command given wasn't found :pensive:").queue();
		}
	}

	private void assignDescriptions() {
		// For any new commands, put a help (*better) description here!
		commandDescriptions
				.put("troll", STR."""
						Allows you to send an anonymous DM with PiggyG to anyone
						on the server that either hasn't blocked trolls (or hasn't blocked
						PiggyG entirely :broken_heart:).
						""");
		commandDescriptions
				.put("obtaintrollattachment", STR."""
						Permits you to get a specific attachment that was sent on
						a specific troll message. Only users with the "Manage server"
						permission can use this command.
						
						__***TIP:*** When using this command, use `obtaintrolllogs` and then
						find a troll log to get a valid name of a file!__
						""");
		commandDescriptions
				.put("obtaintrolllogs", STR."""
						Sends a `.txt` file with every troll command sent.
						This includes helpful info such as what time it was sent,
						what time zone it was sent from, the sender's/receiver's username
						and ID, and much more. Only users with the "Manage server"
						permission can use this command.
						""");
	}
}
