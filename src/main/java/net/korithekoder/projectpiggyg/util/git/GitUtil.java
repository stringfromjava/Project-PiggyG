package net.korithekoder.projectpiggyg.util.git;

/**
 * Utility class for getting info of the current Git repository.
 */
public final class GitUtil {

	/**
	 * A record holding all info about the current Git repository.
	 *
	 * @param commit     The current commit.
	 * @param branch     The current branch.
	 * @param remoteUrl  The repository that PiggyG is being worked on.
	 * @param isModified Does the current repository have any changes made to it?
	 */
	public record RepoInfo(String commit, String branch, String remoteUrl, String isModified) {
		@Override
		public String commit() {
			return commit != null && commit.length() > 7 ? commit.substring(0, 8) : commit;
		}

		@Override
		public String isModified() {
			return (isModified != null && !isModified.isBlank()) ? "Yes" : "No";
		}
	}

	/**
	 * Gets basic Git info about the current app running.
	 *
	 * @return A {@code RepoInfo} record with basic info.
	 */
	public static RepoInfo getRepoInfo() {
		String commit = runGitCommand("rev-parse", "HEAD");
		String branch = runGitCommand("rev-parse", "--abbrev-ref", "HEAD");
		String remoteUrl = runGitCommand("config", "--get", "remote.origin.url");
		String isModified = runGitCommand("status", "--porcelain");
		return new RepoInfo(commit, branch, remoteUrl, isModified);
	}

	private static String runGitCommand(String... args) {
		try {
			String[] command = new String[args.length + 1];
			command[0] = "git";
			System.arraycopy(args, 0, command, 1, args.length);
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);
			Process process = pb.start();
			try (java.io.BufferedReader reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(process.getInputStream()))) {
				return reader.readLine();
			}
		} catch (Exception e) {
			return null;
		}
	}

	private GitUtil() {
	}
}
