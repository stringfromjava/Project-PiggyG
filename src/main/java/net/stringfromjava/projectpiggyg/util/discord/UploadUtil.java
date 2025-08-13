package net.stringfromjava.projectpiggyg.util.discord;

import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling uploads onto Discord.
 */
public final class UploadUtil {

	/**
	 * Gets and returns a {@link java.util.List} of all file names
	 * of attachments that are part of a {@link net.dv8tion.jda.api.entities.Message}.
	 *
	 * @param message The message to get the attachment file names from.
	 * @return All attachment filenames contained in a {@link java.util.List}.
	 */
	public static List<String> getAttachmentNamesFromMessage(Message message) {
		ArrayList<String> toReturn = new ArrayList<>();
		for (Message.Attachment attachment : message.getAttachments()) {
			toReturn.add(attachment.getFileName());
		}
		return toReturn;
	}

	private UploadUtil() {
	}
}
