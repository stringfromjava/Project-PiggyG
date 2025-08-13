package net.stringfromjava.projectpiggyg.data.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * Record simply for holding info about an option for a new command.
 *
 * @param optionType  The type of value that {@code this} option takes in.
 * @param name        The name of the option.
 * @param description The description of the option.
 * @param required    Is this option required?
 */
public record CommandOptionData(OptionType optionType, String name, String description, boolean required) {
}
