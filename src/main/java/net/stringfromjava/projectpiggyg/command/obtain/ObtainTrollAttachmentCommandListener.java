package net.stringfromjava.projectpiggyg.command.obtain;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;
import net.stringfromjava.projectpiggyg.command.CommandListener;
import net.stringfromjava.projectpiggyg.util.Constants;
import net.stringfromjava.projectpiggyg.data.command.CommandOptionData;
import net.stringfromjava.projectpiggyg.util.data.PathUtil;
import net.stringfromjava.projectpiggyg.util.discord.CommandUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * Command for getting a specific attachment that was sent in a troll message.
 */
public class ObtainTrollAttachmentCommandListener extends CommandListener {

	public ObtainTrollAttachmentCommandListener(String name) {
		super(name);
		description = "Gets an attachment that was sent in a troll message.";
		helpDescription = """
				Permits you to get a specific attachment that was sent on
				a specific troll message. Only users with the "Manage server"
				permission can use this command.

				__***TIP:*** When using this command, use `/obtaintrolllogs` and then
				find a troll log to get a valid name of a file!__
				""";
		options = List.of(
				new CommandOptionData(OptionType.STRING, "attachment_name", "The file name of the attachment sent.", true)
		);
		memberPermissions = DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER);
	}

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		String attachmentName = event.getOption("attachment_name").getAsString();

		File trollAttachment = new File(PathUtil.fromGuildFolder(
				guild.getId(),
				Constants.System.GUILD_TROLL_ATTACHMENT_FOLDER_NAME,
				attachmentName)
		);

		if (trollAttachment.exists()) {
			CommandUtil.sendSafeReply(
					"",
					event,
					List.of(FileUpload.fromData(trollAttachment, attachmentName))
			);
		} else {
			CommandUtil.sendSafeReply("Sorry dawg, I couldn't find the file with that name...", event);
		}
	}
}
