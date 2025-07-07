package net.korithekoder.projectpiggyg.command.obtain;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.korithekoder.projectpiggyg.command.PiggyGCommand;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Command for getting a specific attachment that was sent in a troll message.
 */
public class ObtainTrollAttachmentCommandListener extends PiggyGCommand {

	public ObtainTrollAttachmentCommandListener(String name) {
		super(name);
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
