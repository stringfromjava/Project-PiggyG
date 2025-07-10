package net.korithekoder.projectpiggyg.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.utils.FileUpload;
import net.korithekoder.projectpiggyg.data.command.CommandOptionData;
import net.korithekoder.projectpiggyg.util.app.LogType;
import net.korithekoder.projectpiggyg.util.app.LoggerUtil;
import net.korithekoder.projectpiggyg.util.data.DataUtil;
import net.korithekoder.projectpiggyg.util.data.FileUtil;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import net.korithekoder.projectpiggyg.util.discord.GuildUtil;
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
 * Simple class for new commands to extend to.
 * All this really does is make every command do checks when things
 * don't exist or to prevent errors.
 * <p>
 * TIP: It's recommended for your subclasses to call {@code super(name)}
 * and then set the other attributes in your subclass's constructor accordingly.
 * <p>
 * When you make a new command, make sure to do two things:
 * <p>
 * 1. Register its event listener in {@link net.korithekoder.projectpiggyg.Initialize}.
 * <p>
 * 2. Add to the command uploads also in {@link net.korithekoder.projectpiggyg.Initialize}.
 * <p>
 * <b>!! IMPORTANT !!</b>: Make sure to call the super method(s) when extending
 * to this class, or otherwise this class will be useless!
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

	@Override
	public final void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		// If the triggered command's name doesn't
		// equal this command's name, then ignore this method
		if (!name.equals(event.getName())) {
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
			if (tempFile != null) {
				event.reply("Pigga you can't use this command in our fucking DMs...")
						.addFiles(FileUpload.fromData(tempFile)).queue();
			} else {
				event.reply("Pigga you can't use this command in our fucking DMs...").queue();
			}
			return;
		}

		try {
			// Check if the guild's folder exists
			// (if the command was run on one)
			if (guild != null) {
				Path guildPath = Paths.get(PathUtil.fromGuildFolder(guild.getId()));
				if (!guildPath.toFile().exists()) {
					LoggerUtil.log(
							STR."Guild folder for guild '\{guild.getName()}' (ID = \{guild.getId()} doesn't exist! Creating guild directory",
							LogType.WARN,
							true
					);
					GuildUtil.createNewGuildFolder(guild);
				}
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
			String errorMsg = DataUtil.buildString(
					"# Sorry gang, but one of my command's event listeners (unfortunately) had an error. :sob:\n",
					"\t__Please report this error (and the log file) to my official repository.__\n",
					"\t**[OFFICIAL GITHUB REPO]:** https://github.com/korithekoder/Project-PiggyG\n",
					STR."\t**[COMMAND CLASS NAME]:** *\{element.getClassName()}*\n",
					STR."\t**[LINE]:** *\{element.getLineNumber()}*\n",
					STR."\t**[ERROR MESSAGE]:** *\{e.getMessage()}*"
			);

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
