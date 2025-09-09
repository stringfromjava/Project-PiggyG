package net.stringfromjava.projectpiggyg.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.stringfromjava.projectpiggyg.data.command.CommandOptionData;
import net.stringfromjava.projectpiggyg.util.discord.CommandUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Explain what your command does here!
 */
public class ExampleCommandListener extends CommandListener {

	public ExampleCommandListener(String name) {
		// Super constructor call for just
		// the name of the command
		super(name);
		// The more simple description that the user sees on Discord
		description = "A test command for displaying as an example.";
		// The description the user sees when they use the /help
		// command on this specific command
		helpDescription = """
				This is a testing example command, being displayed with the `/help` command
				with more helpful info!
				""";
		// All the options that can be taken in for this command.
		// !! IMPORTANT !!: Do NOT use the add() method with "options", as the
		// "options" attribute is immutable and requires a new reassignment with List.of()!
		options = List.of(
				new CommandOptionData(OptionType.STRING, "option_1", "This option is required and needs a string.", true),
				new CommandOptionData(OptionType.USER, "option_2", "This option isn't required and needs a user.", false)
		);
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		//
		// This is where you put your code for
		// your command when it gets ran!
		// =================================================

		// Get the required option straightforwardly, since it's required, and
		// we know for sure it won't return null!
		String option1 = event.getOption("option_1").getAsString();

		// Get the non-required option as an OptionMapping object
		// since there's a chance it could be null (meaning the user
		// didn't put pass an input)!
		OptionMapping option2OM = event.getOption("option_2");
		// The object to assign and use if option2OM isn't null
		User option2 = null;

		if (option2OM != null) {
			option2 = option2OM.getAsUser();
		}

		// This is the recommended way to send a reply after the command is
		// done being used and is ready to tell the user the output!
		CommandUtil.sendSafeReply(
				// Mentions the said user (if it isn't null, of course)
				// with whatever message the user using the command passed down
				STR."\{(option2 != null) ? STR."<@\{option2.getId()}?" : ""}> \{option1}",
				event
		);
	}
}
