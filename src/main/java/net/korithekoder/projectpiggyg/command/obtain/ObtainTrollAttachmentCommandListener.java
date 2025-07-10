package net.korithekoder.projectpiggyg.command.obtain;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;
import net.korithekoder.projectpiggyg.command.CommandListener;
import net.korithekoder.projectpiggyg.data.command.CommandOptionData;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
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

		File trollAttachment = new File(PathUtil.fromGuildFolder(guild.getId(), "trollattachments", attachmentName));

		if (trollAttachment.exists()) {
			event.replyFiles(FileUpload.fromData(trollAttachment, attachmentName)).queue();
		} else {
			event.reply("Sorry dawg, I couldn't find the file with that name...").queue();
		}
	}
}
