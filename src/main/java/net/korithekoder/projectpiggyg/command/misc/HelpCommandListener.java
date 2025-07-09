package net.korithekoder.projectpiggyg.command.misc;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.korithekoder.projectpiggyg.command.Command;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Command for getting info about another command.
 */
public class HelpCommandListener extends Command {

	private final Map<String, String> commandDescriptions = new HashMap<>();

	public HelpCommandListener(String name) {
		super(name);
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		String command;
		OptionMapping commandOM = event.getOption("command");
		List<net.dv8tion.jda.api.interactions.commands.Command> commands = event.getJDA().retrieveCommands().complete();

		// Set up the more descriptive pieces of
		// info for each command
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
			for (net.dv8tion.jda.api.interactions.commands.Command cmd : commands) {
				toSend.append(STR."`/\{cmd.getName()} ");
				cmd.getOptions().forEach(option -> toSend.append(STR."<\{option.getName()}> "));
				toSend.append("`\n")
						.append(STR."\t\{cmd.getDescription()}\n")
						.append("------------------------------------------------------------\n");
			}
			event.reply(toSend.toString()).queue();
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
						toSend.append(commandDescriptions.get(cmd.getName()));
						event.reply(toSend.toString()).queue();
						commandFound.set(true);
					});
			// Stop the command if it was found and sent
			if (commandFound.get()) {
				return;
			}
			// Reply saying the command wasn't found if it doesn't exist
			event.reply("Sorry fam', but the command given wasn't found :pensive:").queue();
		}
	}

	private void assignDescriptions() {
		// For any new commands, put a help (*better) description here!
		commandDescriptions
				.put("help", """
						...
						""");
		commandDescriptions
				.put("troll", """
						Allows you to send an anonymous DM with PiggyG to anyone
						on the server that either hasn't blocked trolls (or hasn't blocked
						PiggyG entirely :broken_heart:).
						""");
		commandDescriptions
				.put("obtaintrollattachment", """
						Permits you to get a specific attachment that was sent on
						a specific troll message. Only users with the "Manage server"
						permission can use this command.
						
						__***TIP:*** When using this command, use `/obtaintrolllogs` and then
						find a troll log to get a valid name of a file!__
						""");
		commandDescriptions
				.put("obtaintrolllogs", """
						Sends a `.txt` file with every troll command sent.
						This includes helpful info such as what time it was sent,
						what time zone it was sent from, the author's/receiver's username
						and ID, and much more. Only users with the "Manage server"
						permission can use this command.
						""");
		commandDescriptions
				.put("obtainvoicechannellogs", """
						Gets all logs of people joining and leaving voice channels.
						You can also optionally put in a user an a channel to
						filter the logs as well. Only users with the "Manage server"
						permission can use this command.
						""");
		commandDescriptions
				.put("obtainvoicechannelactionlogs", """
						Gets all logs of users (usually admins) server muting/deafening other users.
						Pretty helpful for catching admins abusing their power! Only users with the
						"Manage server" permission can use this command.
						""");
	}
}
