package net.korithekoder.projectpiggyg;

/**
 * The entry point for the PiggyG Discord bot.
 * This class does not need to be modified. This is only used to
 * start PiggyG and ensures he logs into the Discord API.
 * Your code should go into your event listeners, commands, etc.
 * <p>
 * TIP: If you want to change the setup of PiggyG, you can
 * check out {@link net.korithekoder.projectpiggyg.Initialize}.
 */
public class Main {

	/**
	 * The main method that starts it all.
	 *
	 * @param args The arguments passed down when the program is run through
	 *             the command line.
	 */
	public static void main(String[] args) {
		Initialize.init();
	}
}