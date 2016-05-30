/**
 * 
 */
package org.test.project.basic;

import java.io.PrintStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.util.StringUtils;
import org.test.project.api.GameCommandHandler;
import org.test.project.api.GameContext;
import org.test.project.api.GameInterface;
import org.test.project.api.Observable;
import org.test.project.core.CommandType;
import org.test.project.exception.GameException;
import org.test.project.exception.GameInvalidArgumentException;
import org.test.project.model.BasicGameCharacter;
import org.test.project.model.BasicGameContext;
import org.test.project.model.CharacterType;
import org.test.project.model.GameProfile;
import org.test.project.model.Steps;
import org.test.project.model.Villian;
import org.test.project.util.CommandLineInterfaceHelper;

/**
 * @author abdelgam
 *
 */
public class BasicCommandLineInterface implements GameInterface {

	private static final int DB_ALREADY_INITIALIZED = 30000;
	private final static String NEW_LINE = System.getProperty("line.separator");
	private BasicJdbcCommandHandler commandHandler;
	private GameProfile currentGameProfile;
	private BasicGameContext currentGameContext;
	private Map<Integer, String> currentCharTemplates;
	private BasicGameCharacter currentGameCharacter;
	private Villian villianFound;

	private JdbcTemplate jdbcTemplate;

	private static final Options createGameHelpOption = new Options();
	private static final Options resumeGameHelpOption = new Options();
	private static final Options createCharHelpOption = new Options();
	private static final Options exploreHelpOption = new Options();
	private static final Options fightGameHelpOption = new Options();
	private static final Options createGameOptions = new Options();
	private static final Options resumeGameOptions = new Options();
	private static final Options createCharacterOptions = new Options();
	private static final Options exploreOptions = new Options();
	private static final Options fightOptions = new Options();

	private Observable[] gameMap = new Observable[100];

	private final static Logger log = Logger
			.getLogger(BasicCommandLineInterface.class);

	@Value("${game.active.profile}")
	private String activeGameProfileId;

	public BasicCommandLineInterface(GameCommandHandler ch, GameContext gc) {
		super();
		this.commandHandler = (BasicJdbcCommandHandler) ch;
		this.currentGameContext = (BasicGameContext) gc;
	}

	public BasicJdbcCommandHandler getCommandHandler() {
		return commandHandler;
	}

	public void setCommandHandler(BasicJdbcCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	public BasicGameContext getGameContext() {
		return currentGameContext;
	}

	public void setGameContext(BasicGameContext gameContext) {
		this.currentGameContext = gameContext;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * Initializes the Basic game command line interface settings
	 *
	 * @throws GameException
	 *             the game exception
	 */
	@PostConstruct
	public void init() throws GameException {
		initDb();
		loadGameProfile();
		loadCharacterTemplates();
		loadGameMap();
		initClParser();
		log.info("Game Initialization completed");
	}

	/**
	 * Load game map. Initialize game map
	 */
	private void loadGameMap() {
		// TODO Auto-generated method stub
		Villian villian1 = new Villian();
		villian1.setCharName("villian1");
		villian1.setExperience(10);
		villian1.setGameProfileCharId(getCurrentGameProfile().getProfileId());
		villian1.setLocation(10);
		villian1.setRank(1);
		gameMap[villian1.getLocation()] = villian1;
		Villian villian2 = new Villian();
		villian2.setCharName("villian2");
		villian2.setExperience(30);
		villian2.setGameProfileCharId(getCurrentGameProfile().getProfileId());
		villian2.setLocation(30);
		villian2.setRank(2);
		gameMap[villian2.getLocation()] = villian2;
		Villian villian3 = new Villian();
		villian3.setCharName("villian3");
		villian3.setExperience(70);
		villian3.setGameProfileCharId(getCurrentGameProfile().getProfileId());
		villian3.setLocation(70);
		villian3.setRank(3);
		gameMap[villian3.getLocation()] = villian3;
	}

	/**
	 * Inits the db.
	 */
	private void initDb() {
		try {
			log.debug("Initializting DB schema");
			ScriptUtils.executeSqlScript(
					jdbcTemplate.getDataSource().getConnection(),
					new ClassPathResource("db/sql/create-db.sql"));
			ScriptUtils.executeSqlScript(
					jdbcTemplate.getDataSource().getConnection(),
					new ClassPathResource("db/sql/insert-data.sql"));
		} catch (Exception e) {
			// if table exist, error is okay.
			Throwable cause = e.getCause();
			if (cause instanceof SQLException && ((SQLException) cause)
					.getErrorCode() == DB_ALREADY_INITIALIZED) {
				log.debug("DB schema already exists");
			} else {
				log.error("Failure during DB initialization. " + e);
				CommandLineInterfaceHelper.printErrorMessage(
						"Failure during DB initialization. Message: "
								+ e.getMessage(),
						System.out);
			}
		}
		log.debug("DB schema initialized!");
	}

	/**
	 * Inits the command line parser settings.
	 */
	@SuppressWarnings("static-access")
	private void initClParser() {
		log.debug("Initializing parser");
		// Initialize default options
		final Option createGameHelp = OptionBuilder
				.withDescription(
						CommandLineInterfaceHelper.CREATE_GAME_HELP_DESC)
				.hasArg(false).isRequired(false).create('h');
		final Option resumeGameHelp = OptionBuilder
				.withDescription(
						CommandLineInterfaceHelper.RESUME_GAME_HELP_DESC)
				.hasArg(false).isRequired(false).create('h');
		final Option createCharHelp = OptionBuilder
				.withDescription(String.format(
						CommandLineInterfaceHelper.CREATE_CHAR_HELP_DESC,
						getCurrentCharTemplates()))
				.hasArg(false).isRequired(false).create('h');
		final Option exploreHelp = OptionBuilder
				.withDescription(CommandLineInterfaceHelper.EXPLORE_HELP_DESC)
				.hasArg(false).isRequired(false).create('h');
		final Option fightGameHelp = OptionBuilder
				.withDescription(CommandLineInterfaceHelper.FIGHT_HELP_DESC)
				.hasArg(false).isRequired(false).create('h');
		createGameHelpOption.addOption(createGameHelp);
		resumeGameHelpOption.addOption(resumeGameHelp);
		createCharHelpOption.addOption(createCharHelp);
		exploreHelpOption.addOption(exploreHelp);
		fightGameHelpOption.addOption(fightGameHelp);
		// Initialize create game options
		createGameOptions.addOption(OptionBuilder.hasArg().withArgName("name")
				.withDescription(
						CommandLineInterfaceHelper.CREATE_GAME_NAME_DESC)
				.isRequired().create('n'));
		createGameOptions.addOption(createGameHelp);

		// Initialize resume game options
		resumeGameOptions
				.addOption(OptionBuilder.hasArg().withArgName("game_id")
						.withDescription(
								CommandLineInterfaceHelper.RESUME_GAME_ID_DESC)
				.isRequired(true).create("id"));
		resumeGameOptions.addOption(resumeGameHelp);

		// Initialize create character options
		createCharacterOptions
				.addOption(OptionBuilder.hasArg().withArgName("name")
						.withDescription(
								CommandLineInterfaceHelper.CREATE_CHAR_NAME_DESC)
				.isRequired(true).create("n"));
		createCharacterOptions
				.addOption(OptionBuilder.hasArg().withArgName("template")
						.withDescription(
								CommandLineInterfaceHelper.CREATE_CHAR_TEMPLATE_DESC)
				.isRequired(true).create("t"));
		createCharacterOptions.addOption(createCharHelp);

		// Initialize explore options
		exploreOptions
				.addOption(OptionBuilder.hasArg().withArgName("steps")
						.withDescription(
								CommandLineInterfaceHelper.EXPLORE_STEPS_DESC)
				.isRequired(true).create('s'));
		exploreOptions.addOption(exploreHelp);

		// Initialize fight
		fightOptions.addOption(fightGameHelp);
		// Initialize output coloring
		AnsiConsole.systemInstall();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.test.project.api.GameInterface#launchInterface() Performs
	 * specific launch code for the basic command line interface implementation
	 */
	@Override
	public void launchInterface() {
		StringBuffer consoleBuffer = new StringBuffer();
		consoleBuffer.append(
				"*****************Welcome to game command line interface***************");
		consoleBuffer.append(NEW_LINE);
		CommandLineInterfaceHelper.printInfoMessage(consoleBuffer.toString(),
				System.out);
		consoleBuffer.setLength(0);
		try {
			parseArgs(new Scanner(System.in), System.out);
		} catch (GameInvalidArgumentException e) {
			log.error("User input validation failure. " + e.getMessage(), e);
			CommandLineInterfaceHelper.printErrorMessage(
					"User input validation failure. " + e.getMessage(),
					System.out);
			System.exit(1);
		} catch (GameException e) {
			log.error("Game processing failure. " + e.getMessage(), e);
			CommandLineInterfaceHelper.printErrorMessage(
					"Game processing failure. " + e.getMessage(), System.out);
			System.exit(1);
		}

	}

	/**
	 * Parses the args.
	 *
	 * @param scanner
	 *            the scanner representing input through console
	 * @param out
	 *            the output to console
	 * @throws GameException
	 *             the game exception
	 */
	public void parseArgs(Scanner scanner, PrintStream out)
			throws GameException {
		CommandLineInterfaceHelper.printHelp(out);
		CommandLineInterfaceHelper
				.printInfoMessage("Please type a command to execute", out);
		String command = scanner.nextLine();
		validateAndExecuteCommand(scanner, out, command);
	}

	/**
	 * Validate and execute command.
	 *
	 * @param scanner
	 *            the scanner representing input through console
	 * @param out
	 *            the output to console
	 * @param command
	 *            the command
	 * @throws GameException
	 *             the game exception
	 */
	private void validateAndExecuteCommand(Scanner scanner, PrintStream out,
			String command) throws GameException {
		String[] args = normalizeCommand(command);
		if (args == null || args.length < 1) {
			throw new GameInvalidArgumentException(
					"Invalid command args. Command args cannot be empty");
		}
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		if (args[0].equalsIgnoreCase(CommandType.createGame.toString())) {
			try {
				checkForHelpOption(cmd, parser, createGameOptions,
						CommandType.createGame, createGameHelpOption, args, out,
						scanner);
				cmd = parser.parse(createGameOptions, args);
				String newGameName = cmd.getOptionValue("n");
				int gameId = processCreateGameCommand(newGameName);
				CommandLineInterfaceHelper.printSuccessMessage(
						"created new game id: " + gameId, out);
				parseArgs(scanner, out);
			} catch (ParseException e) {
				CommandLineInterfaceHelper.printHelpForCommand(
						createGameOptions, CommandType.createGame.toString(),
						out);
				throw new GameInvalidArgumentException(e);
			}
		} else if (args[0]
				.equalsIgnoreCase(CommandType.resumeGame.toString())) {
			try {
				checkForHelpOption(cmd, parser, resumeGameOptions,
						CommandType.resumeGame, resumeGameHelpOption, args, out,
						scanner);
				cmd = parser.parse(resumeGameOptions, args);
				String gameId = cmd.getOptionValue("id");
				processResumeGameCommand(gameId);
				CommandLineInterfaceHelper.printSuccessMessage(
						"successfully loaded game id: " + gameId, out);
				parseArgs(scanner, out);
			} catch (ParseException e) {
				CommandLineInterfaceHelper.printHelpForCommand(
						resumeGameOptions, CommandType.resumeGame.toString(),
						out);
				throw new GameInvalidArgumentException(e);
			}
		} else if (args[0]
				.equalsIgnoreCase(CommandType.createCharacter.toString())) {
			try {
				checkForHelpOption(cmd, parser, createCharacterOptions,
						CommandType.createCharacter, createCharHelpOption, args,
						out, scanner);
				cmd = parser.parse(createCharacterOptions, args);
				String name = cmd.getOptionValue("n");
				String template = cmd.getOptionValue("t");
				int charId = processCreateCharacterCommand(name, template);
				CommandLineInterfaceHelper.printSuccessMessage(
						"successfully created new character id: " + charId,
						out);
				parseArgs(scanner, out);
			} catch (ParseException e) {
				CommandLineInterfaceHelper.printHelpForCommand(
						createCharacterOptions,
						CommandType.createCharacter.toString(), out);
				throw new GameInvalidArgumentException(e);
			}
		} else if (args[0].equalsIgnoreCase(CommandType.explore.toString())) {
			try {
				checkForHelpOption(cmd, parser, exploreOptions,
						CommandType.explore, exploreHelpOption, args, out,
						scanner);
				cmd = parser.parse(exploreOptions, args);
				String steps = cmd.getOptionValue("s");
				processExploreCommand(steps, out);
				parseArgs(scanner, out);
			} catch (ParseException e) {
				CommandLineInterfaceHelper.printHelpForCommand(exploreOptions,
						CommandType.explore.toString(), out);
				throw new GameInvalidArgumentException(e);
			}
		} else if (args[0].equalsIgnoreCase(CommandType.fight.toString())) {
			try {
				checkForHelpOption(cmd, parser, fightOptions, CommandType.fight,
						fightGameHelpOption, args, out, scanner);
				cmd = parser.parse(fightOptions, args);
				processFightCommand(out);
				parseArgs(scanner, out);
			} catch (ParseException e) {
				CommandLineInterfaceHelper.printHelpForCommand(fightOptions,
						CommandType.fight.toString(), out);
				throw new GameInvalidArgumentException(e);
			}
		} else if (args[0].equalsIgnoreCase(CommandType.exit.toString())) {
			CommandLineInterfaceHelper.printInfoMessage(
					"Exiting game command line interface. BYE!!!", out);
			AnsiConsole.systemUninstall();
			System.exit(1);
		}
	}

	/**
	 * Check for help option. Performs reusable checking on commands help option
	 *
	 * @param cmd
	 *            the cmd
	 * @param parser
	 *            the parser
	 * @param commandOptions
	 *            the command options
	 * @param command
	 *            the command
	 * @param helpOptions
	 *            the help options
	 * @param args
	 *            the args
	 * @param out
	 *            the out
	 * @param scanner
	 *            the scanner
	 * @throws ParseException
	 *             the parse exception
	 * @throws GameException
	 *             the game exception
	 */
	private void checkForHelpOption(CommandLine cmd, CommandLineParser parser,
			Options commandOptions, CommandType command, Options helpOptions,
			String[] args, PrintStream out, Scanner scanner)
					throws ParseException, GameException {
		args = CommandLineInterfaceHelper.removeFirstArg(args);
		cmd = parser.parse(helpOptions, args, true);
		if (cmd.hasOption('h')) {
			CommandLineInterfaceHelper.printHelpForCommand(commandOptions,
					command.toString(), out);
			parseArgs(scanner, out);
		}

	}

	/**
	 * Process fight command.
	 *
	 * @param out
	 *            the out
	 * @throws GameException
	 *             the game exception
	 */
	private void processFightCommand(PrintStream out) throws GameException {
		if (isNotEmptyContext(getCurrentGameContext())) {
			if (isNotEmptyGameCharacter(getCurrentGameCharacter())) {
				if (getVillianFound() != null) {
					boolean fightWon = commandHandler.handleFight(
							getCurrentGameCharacter(), getVillianFound());
					if (fightWon) {
						CommandLineInterfaceHelper.printSuccessMessage(
								"Conragts! you won the fight agianst "
										+ getVillianFound().getCharacterName(),
								out);
					} else {
						CommandLineInterfaceHelper
								.printInfoMessage(
										"Unfortunately! you lost the fight agianst "
												+ getVillianFound()
														.getCharacterName()
												+ ", who is of higher rank. You need to upgrade your rank (future enhancement)",
										out);
					}
					CommandLineInterfaceHelper.printInfoMessage(
							"Your current location is : "
									+ getCurrentGameCharacter().getLocation(),
							out);
				} else {
					throw new GameException(
							"No villian found to fight. You must explore till you find a villian!");
				}
			} else {
				throw new GameException(
						"No character created. To fight villians, you need to create a character first!");
			}
		} else {
			throw new GameException(
					"No game session loaded. To fight villians, you need to create/resume a game session first!");
		}
	}

	/**
	 * Process explore command.
	 *
	 * @param steps
	 *            the steps
	 * @param out
	 *            the out
	 * @throws GameException
	 *             the game exception
	 */
	private void processExploreCommand(String steps, PrintStream out)
			throws GameException {
		if (isNotEmptyContext(getCurrentGameContext())) {
			if (isNotEmptyGameCharacter(getCurrentGameCharacter())) {
				Integer numericSteps;
				try {
					numericSteps = Integer.parseInt(steps);
				} catch (NumberFormatException e) {
					throw new GameInvalidArgumentException(
							"Steps must be numeric.");
				}
				if (!(numericSteps + getCurrentGameCharacter()
						.getLocation() >= gameMap.length)) {
					Steps stepUnits = new Steps(new Integer(steps));
					Villian villian = (Villian) commandHandler.handleExplore(
							stepUnits, gameMap, getCurrentGameCharacter());
					if (villian != null) {
						setVillianFound(villian);
						CommandLineInterfaceHelper.printInfoMessage(
								"Found a Villian at location: "
										+ villian.getLocation()
										+ ". To fight, type in the fight commmand",
								out);
					} else {
						CommandLineInterfaceHelper.printInfoMessage(
								"Exploration finished. Current location is: "
										+ getCurrentGameCharacter()
												.getLocation(),
								out);
					}
				} else {
					throw new GameException(
							"The game map is of 100 steps only, please enter less amount of steps");
				}
			} else {
				throw new GameException(
						"No character created. To explore, you need to create a character first!");
			}
		} else {
			throw new GameException(
					"No game session loaded. To explore, you need to create/resume a game session first!");
		}
	}

	/**
	 * Checks if is not empty game character.
	 *
	 * @param gameCharacter
	 *            the game character
	 * @return true, if is not empty game character
	 */
	private boolean isNotEmptyGameCharacter(BasicGameCharacter gameCharacter) {
		if (gameCharacter != null && gameCharacter.getCharId() != 0)
			return true;
		return false;
	}

	/**
	 * Process create character command.
	 *
	 * @param name
	 *            the name
	 * @param template
	 *            the template
	 * @return the int
	 * @throws GameException
	 *             the game exception
	 */
	private int processCreateCharacterCommand(String name, String template)
			throws GameException {
		if (isNotEmptyContext(getCurrentGameContext())) {
			BasicGameCharacter character = createStandardCharacter();
			character.setCharName(name);
			int profileId = getCharacterProfileIdForName(template);
			if (profileId != 0) {
				character.setGameProfileCharId(profileId);
				character = (BasicGameCharacter) commandHandler
						.handleCreateCharacter(character);
				setCurrentGameCharacter(character);
			} else {
				throw new GameInvalidArgumentException(
						"Invalid character template.");
			}

			return character.getCharId();
		} else {
			throw new GameException(
					"No game session loaded. To create a character, you need to create/resume a game session first!");
		}
	}

	/**
	 * Checks if is not empty context.
	 *
	 * @param gameContext
	 *            the game context
	 * @return true, if is not empty context
	 */
	private boolean isNotEmptyContext(BasicGameContext gameContext) {
		if (gameContext != null && gameContext.getGameContextId() != 0)
			return true;
		return false;
	}

	/**
	 * Gets the character profile id for profile name.
	 *
	 * @param template
	 *            the template
	 * @return the character profile id for name
	 */
	private int getCharacterProfileIdForName(String template) {
		Iterator<Entry<Integer, String>> itr = getCurrentCharTemplates()
				.entrySet().iterator();
		Entry<Integer, String> entry = null;
		while (itr.hasNext()) {
			entry = itr.next();
			if (entry.getValue().equalsIgnoreCase(template))
				return entry.getKey().intValue();
		}
		return 0;
	}

	/**
	 * Creates a standard character.
	 *
	 * @return the basic game character
	 */
	private BasicGameCharacter createStandardCharacter() {
		BasicGameCharacter standard = new BasicGameCharacter();
		standard.setCharacterType(CharacterType.HERO.getType());
		standard.setExperience(100);
		standard.setLocation(0);
		standard.setRank(5);
		standard.setGameContextId(getCurrentGameContext().getGameContextId());
		return standard;
	}

	/**
	 * Process resume game command.
	 *
	 * @param gameId
	 *            the game id
	 * @throws GameException
	 *             the game exception
	 */
	private void processResumeGameCommand(String gameId) throws GameException {
		BasicGameContext gameContext = new BasicGameContext();
		gameContext.setGameContextId(new Integer(gameId).intValue());
		commandHandler.handleResumeGame(gameContext);
		setCurrentGameContext(gameContext);
		BasicGameCharacter gameCharacter = commandHandler
				.handleLoadGameCharacter(gameContext);
		if (gameCharacter != null) {
			setCurrentGameCharacter(gameCharacter);
		}
	}

	/**
	 * Process create game command.
	 *
	 * @param newGameName
	 *            the new game name
	 * @return the int
	 */
	private int processCreateGameCommand(String newGameName) {
		BasicGameContext gameContext = null;
		if (StringUtils.hasText(newGameName)) {
			gameContext = new BasicGameContext(newGameName,
					new Date(System.currentTimeMillis()));
		} else {
			gameContext = new BasicGameContext("",
					new Date(System.currentTimeMillis()));
		}
		commandHandler.handleCreateGame(gameContext);
		setCurrentGameContext(gameContext);
		return gameContext.getGameContextId();
	}

	/**
	 * Normalize command.
	 *
	 * @param command
	 *            the command
	 * @return the string[]
	 */
	private String[] normalizeCommand(String command) {
		String[] splitted = command.split("\\s+");
		return splitted;
	}

	/**
	 * Load game profiles.
	 *
	 * @throws GameInvalidArgumentException
	 *             the game invalid argument exception
	 */
	private void loadGameProfile() throws GameInvalidArgumentException {
		Map<Integer, GameProfile> gameProfilesAvailable = commandHandler
				.findAllGameProfiles();
		if (gameProfilesAvailable.keySet()
				.contains(new Integer(activeGameProfileId).intValue())) {
			GameProfile activeProfile = commandHandler.loadGameProfile(
					new Integer(activeGameProfileId).intValue());
			System.out.println("Successfully loaded game profile: "
					+ activeProfile.getProfileName());
			setCurrentGameProfile(activeProfile);
		} else {
			throw new GameInvalidArgumentException(
					"Invalid game profile : " + activeGameProfileId);
		}
	}

	/**
	 * Load character templates for current game profile.
	 *
	 * @throws GameException
	 *             the game exception
	 */
	private void loadCharacterTemplates() throws GameException {
		if (getCurrentGameProfile() != null) {
			Map<Integer, String> charTemplates = commandHandler
					.findCharacterTemplatesForGameProfile(
							getCurrentGameProfile());
			if (!charTemplates.isEmpty())
				setCurrentCharTemplates(charTemplates);
			else
				throw new GameException(
						"No character templates found for current game profile. Please load the GAME_PROFILE_CHARACTER table with corresponding character templates");
		}

	}

	private GameProfile getCurrentGameProfile() {
		return currentGameProfile;
	}

	private void setCurrentGameProfile(GameProfile currentGameProfile) {
		this.currentGameProfile = currentGameProfile;
	}

	private BasicGameContext getCurrentGameContext() {
		return currentGameContext;
	}

	private void setCurrentGameContext(BasicGameContext currentGameContext) {
		this.currentGameContext = currentGameContext;
	}

	/**
	 * @return the currentCharTemplates
	 */
	private Map<Integer, String> getCurrentCharTemplates() {
		return currentCharTemplates;
	}

	/**
	 * @param currentCharTemplates
	 *            the currentCharTemplates to set
	 */
	private void setCurrentCharTemplates(
			Map<Integer, String> currentCharTemplates) {
		this.currentCharTemplates = currentCharTemplates;
	}

	/**
	 * @return the currentGameCharacter
	 */
	private BasicGameCharacter getCurrentGameCharacter() {
		return currentGameCharacter;
	}

	/**
	 * @param currentGameCharacter
	 *            the currentGameCharacter to set
	 */
	private void setCurrentGameCharacter(
			BasicGameCharacter currentGameCharacter) {
		this.currentGameCharacter = currentGameCharacter;
	}

	/**
	 * @return the villianFound
	 */
	private Villian getVillianFound() {
		return villianFound;
	}

	/**
	 * @param villianFound
	 *            the villianFound to set
	 */
	private void setVillianFound(Villian villianFound) {
		this.villianFound = villianFound;
	}
}
