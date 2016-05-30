package org.test.project.api;

import org.test.project.exception.GameException;

/**
 * @author abdelgam
 * 
 *         Interface representing a generic command handler with necessary
 *         command handling functions
 *
 */
public interface GameCommandHandler {

	/**
	 * Handle create game.
	 *
	 * @param ctx
	 *            the initial game context instance
	 * @return the persisted game context instance
	 */
	GameContext handleCreateGame(GameContext ctx);

	/**
	 * Handle resume game.
	 *
	 * @param ctx
	 *            the initial game context instance
	 * @return the loaded game context instance
	 * @throws GameException
	 *             the game exception
	 */
	GameContext handleResumeGame(GameContext ctx) throws GameException;

	/**
	 * Handle create character.
	 *
	 * @param character
	 *            the initial character instance
	 * @return the persisted game character instance
	 */
	GameCharacter handleCreateCharacter(GameCharacter character);

	/**
	 * Handle explore.
	 *
	 * @param units
	 *            the units of displacement
	 * @param args
	 *            the possible args (i.e. Map)
	 * @return the observable representing any observations while exploring
	 *         (i.e. an enemy)
	 */
	Observable handleExplore(DisplacementUnits units, Object... args);

	/**
	 * Handle fight.
	 *
	 * @param gameCharacter
	 *            the fighting game character
	 * @param villain
	 *            the villain/enemy to fight
	 * @return true, if successful
	 */
	boolean handleFight(GameCharacter gameCharacter, GameCharacter villain);
}
