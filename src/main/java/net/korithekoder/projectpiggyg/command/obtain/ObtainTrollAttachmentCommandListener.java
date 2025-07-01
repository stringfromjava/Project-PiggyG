package net.korithekoder.projectpiggyg.command.obtain;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.korithekoder.projectpiggyg.command.PiggyGCommand;
import net.korithekoder.projectpiggyg.util.data.PathUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ObtainTrollAttachmentCommandListener extends PiggyGCommand {

	@Override
	protected void onSlashCommandUsed(@NotNull SlashCommandInteractionEvent event) {
		if (!event.getName().equals("obtaintrollattachment")) {
			return;
		}

		Guild guild = event.getGuild();
		String attachmentName = event.getOption("attachment_name").getAsString();

		if (guild == null) {
			event.reply("Pigga you can't fucking use this command in DMs :sob:").queue();
			return;
		}

		File trollAttachment = new File(PathUtil.fromGuildFolder(guild.getId(), "trollattachments", attachmentName));

		if (trollAttachment.exists()) {
			event.replyFiles(FileUpload.fromData(trollAttachment, attachmentName)).queue();
		} else {
			event.reply("Sorry dawg, I couldn't find the file with that name...").queue();
		}
	}
}
