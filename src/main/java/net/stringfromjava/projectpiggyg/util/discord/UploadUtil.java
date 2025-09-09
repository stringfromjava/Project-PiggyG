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

	/**
	 * Generates a unique snowflake ID similar to Discord's.
	 * This ID is based on the current timestamp and some random bits.
	 *
	 * @return A unique snowflake ID as a {@code long}.
	 */
	public static long generateSnowflakeId() {
		long timestamp = System.currentTimeMillis() - 1420070400000L; // Discord epoch (2015-01-01)
		long randomBits = (long) (Math.random() * (1L << 22)); // 22 bits for randomness
		return (timestamp << 22) | randomBits;
	}

	private UploadUtil() {
	}
}
