package org.test.project.util;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.io.PrintStream;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class CommandLineInterfaceHelper {

	private static String version = "1.0";

	// Create game options descriptions
	public final static String CREATE_GAME_HELP_DESC = "This command is used to create a new game session for the choosen game profile";
	public final static String CREATE_GAME_NAME_DESC = "New game name. Must be unique";

	// Resume game options descriptions
	public final static String RESUME_GAME_HELP_DESC = "This command is used to resume a previously saved game";
	public final static String RESUME_GAME_ID_DESC = "Saved game ID";

	// Save game options descriptions
	public final static String SAVE_GAME_HELP_DESC = "This command is used to save the current game session";

	// Create character options descriptions
	public final static String CREATE_CHAR_HELP_DESC = "This command is used to create a new game character for the current game session. Available character templates are : %s";
	public final static String CREATE_CHAR_NAME_DESC = "New game character name. Must be unique";
	public final static String CREATE_CHAR_TEMPLATE_DESC = "New game character template.";

	// Explore options descriptions
	public final static String EXPLORE_HELP_DESC = "This is used to order your character to explore by a given displacement unit.";
	public final static String EXPLORE_STEPS_DESC = "Numeric unit of displacement in steps.";

	// Fight options descriptions
	public final static String FIGHT_HELP_DESC = "This is used to order your character to fight an explored enemy";

	public static void printHelp(PrintStream out) {
		printHelpHeader(out);
		printInfoMessage("\nusage: <command> [params...]", out);
		printInfoMessage(
				"<command>\tcreateGame|resumeGame|createCharacter|explore|fight|saveGame|exit",
				out);
		printInfoMessage("params...\tfor more info, use \' <command> -h\'",
				out);
	}

	public static void printHelpHeader(PrintStream out) {
		printInfoMessage("Game puzzle Command Line Interface  (version: "
				+ version + ")", out);
	}

	public static void printHelpForCommand(Options o, String command,
			PrintStream out) {
		printHelpHeader(out);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(command, o, true);
	}

	public static String[] removeFirstArg(String[] source) {
		// remove first argument as it specifies command type
		String[] dest = new String[source.length - 1];
		if (source.length > 1) {
			System.arraycopy(source, 1, dest, 0, source.length - 1);
		}
		return dest;
	}

	public static void printInfoMessage(String message, PrintStream out) {
		out.println(ansi().fg(YELLOW).a(message).reset());
	}

	public static void printErrorMessage(String message, PrintStream out) {
		out.println(ansi().fg(RED).a(message).reset());
	}

	public static void printSuccessMessage(String message, PrintStream out) {
		out.println(ansi().fg(GREEN).a(message).reset());
	}
}
