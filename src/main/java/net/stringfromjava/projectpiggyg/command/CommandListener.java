package net.stringfromjava.projectpiggyg.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.utils.FileUpload;
import net.stringfromjava.projectpiggyg.Initialize;
import net.stringfromjava.projectpiggyg.data.command.CommandOptionData;
import net.stringfromjava.projectpiggyg.util.app.AppUtil;
import net.stringfromjava.projectpiggyg.util.app.LogType;
import net.stringfromjava.projectpiggyg.util.app.LoggerUtil;
import net.stringfromjava.projectpiggyg.util.data.FileUtil;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import net.stringfromjava.projectpiggyg.util.discord.CommandUtil;
import net.stringfromjava.projectpiggyg.util.discord.GuildUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;

/**
 * Simple boilerplate class for new commands to extend to.
 * All this really does is make every command do checks when things
 * don't exist or to prevent errors.
 * <p>
 * TIP: It's recommended for your subclasses to call {@code super(name)}
 * and then set the other attributes in your subclass's constructor accordingly.
 * <p>
 * When you make a new command, make sure to add it to the command uploads
 * in {@link net.stringfromjava.projectpiggyg.Initialize}.
 */
public abstract class CommandListener extends ListenerAdapter {

	/**
	 * The name of {@code this} command that will be used when uploaded to Discord.
	 */
	protected String name;

	/**
	 * The description of {@code this} command that will be used when uploaded to Discord.
	 */
	protected String description;

	/**
	 * The description of {@code this} command that's displayed when the
	 * user uses the {@code /help} command.
	 */
	protected String helpDescription;

	/**
	 * Can {@code this} command only be used on a guild?
	 */
	protected boolean isGuildCommand;

	/**
	 * The permissions for {@code this} command. This will determine who and
	 * which members on a guild can use {@code this} command.
	 */
	protected DefaultMemberPermissions memberPermissions;

	/**
	 * The conditional that is required to run {@code this} command.
	 * {@code null} or an empty string will allow the command to be used
	 * regardless what conditional(s) are enabled.
	 */
	protected String requiredConditional;

	/**
	 * The options that are used with {@code this} command.
	 */
	protected Collection<CommandOptionData> options;

	/**
	 * @param name The name of {@code this} command.
	 */
	public CommandListener(String name) {
		this.name = name;
		description = "<No description was set by the developer for this command.>";
		helpDescription = description;
		isGuildCommand = true;
		memberPermissions = DefaultMemberPermissions.ENABLED;
		options = List.of();
		requiredConditional = null;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getHelpDescription() {
		return helpDescription;
	}

	public boolean isGuildCommand() {
		return isGuildCommand;
	}

	public DefaultMemberPermissions getMemberPermissions() {
		return memberPermissions;
	}

	public Collection<CommandOptionData> getOptions() {
		return options;
	}

	public String getRequiredConditional() {
		return requiredConditional;
	}

	@Override
	public final void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		// If the triggered command's name doesn't
		// equal this command's name, then ignore this method
		if (!name.equals(event.getName())) {
			return;
		}

		if (!Initialize.initialized) {
			CommandUtil.sendSafeReply(
					"Hold your damn horses pigga, I'm not done setting up yet! :angry:",
					event
			);
			return;
		}

		// Get the dumbass reaction image to send
		// if the command being used is in DMs and
		// is a guild-only command
		Guild guild = event.getGuild();
		InputStream input = getClass()
				.getClassLoader()
				.getResourceAsStream(PathUtil.constructPath("reactions", "non-guild-command.jpg"));
		File tempFile = null;
		try {
			tempFile = File.createTempFile("non-guild-command", ".jpg");
			if (input != null) {
				Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else {
				LoggerUtil.log(
						STR."Could not get reaction image for a non-guild command, did you delete it?",
						LogType.ERROR,
						false
				);
			}
		} catch (IOException e) {
			LoggerUtil.log(
					STR."Could not get reaction image for a non-guild command, got this error: '\{e.getMessage()}'",
					LogType.ERROR,
					false
			);
		}

		// Ensure check safety of running this command inside a guild
		if (guild == null && isGuildCommand) {
			CommandUtil.sendSafeReply(
					"Pigga you can't use this command in our fucking DMs...",
					event,
					(tempFile != null) ? List.of(FileUpload.fromData(tempFile)) : List.of()
			);
			return;
		}

		//
 		// Add your checks before running a command here!
		// ============================================================
		try {
			// Check if the guild's folder exists
			// (if the command was run on one)
			if (guild != null) {
				Path guildPath = Paths.get(PathUtil.fromGuildFolder(guild.getId()));
				if (!guildPath.toFile().exists()) {
					LoggerUtil.log(
							STR."Guild folder for guild '\{guild.getName()}' (ID = \{guild.getId()} is missing! Creating guild directory",
							LogType.WARN,
							true
					);
					GuildUtil.createNewGuildFolder(guild);
				}
			}

			// Ensure PiggyG's role is the highest on the guild
			// (if the command is a guild command)
			if (guild != null && isGuildCommand) {
				Member self = guild.getSelfMember();
				Role highestBotRole = self.getRoles().isEmpty() ? null : self.getRoles().getFirst();
				if (highestBotRole == null) {
					CommandUtil.sendSafeReply(
							"Pigga...\nDid you really remove all my roles? :rage:",
							event
					);
					return;
				}
				int maxPosition = guild.getRoles().stream() // Get the highest position of all roles in the guild
						.filter(role -> !role.isManaged()) // Ignore integration-managed roles
						.mapToInt(Role::getPosition)
						.max()
						.orElse(-1);
				if (!(highestBotRole.getPosition() >= maxPosition)) {
					CommandUtil.sendSafeReply(
							"Hey bruv, you need to put my role to be the highest in order for me to function correctly!",
							event
					);
					return;
				}
			}

			// Check if the required conditional is enabled
			boolean isEnabled = requiredConditional != null && (requiredConditional.isEmpty() || !AppUtil.conditionalEnabled(requiredConditional));
			if (isEnabled) {
				CommandUtil.sendSafeReply("Sorry bruv, but the developer hosting me disabled this command... :unamused:", event);
				return;
			}

			// Run the command :sparkles:
			// (fucking finally lol)
			onSlashCommandUsed(event);
		} catch (Exception e) {
			LoggerUtil.error(STR."Command '\{name}' failed to execute, got this error: '\{e.getMessage()}'");
			StackTraceElement element = e.getStackTrace()[0];
			File currentLogFile = LoggerUtil.getLogFile();
			File logsToSend = FileUtil.ensureFileExists(
					PathUtil.ofAppData(
							"logs",
							(currentLogFile != null) ? currentLogFile.getName() : LoggerUtil.getFormattedLogTimes()[1]
					)
			);
			// Error message for the log file and the reply
			String errorMsg = STR."""
					# Sorry gang, but one of my command's event listeners (unfortunately) had an error. :sob:
					\t__Please report this error (and the log file) to my official repository.__
					\t**[OFFICIAL GITHUB REPO]:** https://github.com/stringfromjava/Project-PiggyG
					\t**[COMMAND CLASS NAME]:** *\{element.getClassName()}*
					\t**[LINE]:** *\{element.getLineNumber()}*
					\t**[ERROR MESSAGE]:** *\{e.getMessage()}*
					""";

			// Log all info
			LoggerUtil.log(errorMsg, LogType.ERROR, false);
			try {
				event.reply(errorMsg)
						.addFiles(FileUpload.fromData(logsToSend, logsToSend.getName()))
						.queue();
			} catch (Exception ex) {
				LoggerUtil.error(
						STR."Failed to reply with the error message (yes really), got this error: '\{e.getMessage()}",
						false // False because the log file PiggyG tried to obtain didn't exist
				);
			}
		}
	}

	/**
	 * The method for every command to override (instead of {@code onSlashCommandInteraction()}).
	 *
	 * @param event The {@link net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent}
	 *              that gets passed down after {@code onSlashCommandInteraction()} is triggered.
	 */
	protected abstract void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event);
}
