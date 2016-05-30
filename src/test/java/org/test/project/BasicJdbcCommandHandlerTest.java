package org.test.project;

import java.sql.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.test.project.api.Observable;
import org.test.project.basic.BasicJdbcCommandHandler;
import org.test.project.exception.GameException;
import org.test.project.model.BasicGameCharacter;
import org.test.project.model.BasicGameContext;
import org.test.project.model.GameProfile;
import org.test.project.model.Steps;
import org.test.project.model.Villian;

public class BasicJdbcCommandHandlerTest extends AbstractGameTest{
	
	@Autowired
	BasicJdbcCommandHandler commandHandler;
	
	@Test
	public void testFindAllProfiles() {
		Map<Integer, GameProfile> result = commandHandler.findAllGameProfiles();
		Assert.assertNotNull("Failure - expected not null", result);
		Assert.assertEquals("Failure - expected a map of size 2", 2, result.size());
	}
	
	@Test
	public void testHandleCreateGame() {
		BasicGameContext gameContext = new BasicGameContext();
		gameContext.setGameName("gameContext1");
		gameContext.setGameStartDate(new Date(System.currentTimeMillis()));
		BasicGameContext createdGame = (BasicGameContext) commandHandler.handleCreateGame(gameContext);
		Assert.assertNotNull("Failure - expected not null", createdGame);
		Assert.assertNotEquals("Failure expected id != 0", 0, createdGame.getGameContextId());
		Assert.assertEquals(gameContext.getGameStartDate(), createdGame.getGameStartDate());
	}
	
	@Test
	public void testHandleCreateCharacter() {
		BasicGameContext gameContext = new BasicGameContext();
		gameContext.setGameName("gameContext1");
		gameContext.setGameStartDate(new Date(System.currentTimeMillis()));
		commandHandler.handleCreateGame(gameContext);
		BasicGameCharacter character = new BasicGameCharacter();
		character.setCharName("gameChar1");
		character.setGameContextId(gameContext.getGameContextId());
		character.setGameProfileCharId(1);
		character.setExperience(100);
		character.setRank(5);
		BasicGameCharacter createdChar = (BasicGameCharacter) commandHandler.handleCreateCharacter(character);
		Assert.assertNotNull("Failure - expected not null", createdChar);
		Assert.assertNotEquals("Failure expected id != 0", 0, createdChar.getCharId());
		Assert.assertEquals("Failure - expected location=0", 0, createdChar.getLocation());
		Assert.assertEquals("Failure - expected experience=100", 100, createdChar.getExperience());
		Assert.assertEquals("Failure - expected rank=5", 5, createdChar.getRank());
	}
	
	@Test
	public void testHandleResumeGame() throws GameException {
		BasicGameContext gameContext = new BasicGameContext();
		gameContext.setGameName("gameContext1");
		gameContext.setGameStartDate(new Date(System.currentTimeMillis()));
		commandHandler.handleCreateGame(gameContext);
		BasicGameContext resumedGame = (BasicGameContext) commandHandler.handleResumeGame(gameContext);
		Assert.assertNotNull("Failure - expected not null", resumedGame);
		Assert.assertEquals(gameContext.getGameName(), resumedGame.getGameName());
		Assert.assertEquals(gameContext.getGameContextId(), resumedGame.getGameContextId());
		Assert.assertEquals(gameContext.getGameStartDate().toString(), resumedGame.getGameStartDate().toString());
	}
	
	@Test
	public void testHandleExplore() {
		BasicGameContext gameContext = new BasicGameContext();
		gameContext.setGameName("gameContext1");
		gameContext.setGameStartDate(new Date(System.currentTimeMillis()));
		commandHandler.handleCreateGame(gameContext);
		BasicGameCharacter character = new BasicGameCharacter();
		character.setCharName("gameChar1");
		character.setGameContextId(gameContext.getGameContextId());
		character.setGameProfileCharId(1);
		character.setExperience(100);
		character.setRank(5);
		BasicGameCharacter createdChar = (BasicGameCharacter) commandHandler.handleCreateCharacter(character);
		Observable[] gameMap = new Observable[10];
		Villian villian1 = new Villian();
		villian1.setCharName("villian1");
		villian1.setExperience(10);
		villian1.setGameProfileCharId(1);
		villian1.setLocation(3);
		villian1.setRank(1);
		gameMap[villian1.getLocation()] = villian1;
		Villian villianFound = (Villian) commandHandler.handleExplore(new Steps(4), gameMap, character);
		Assert.assertNotNull("Failure - expected not null", villianFound);
		Assert.assertEquals(villianFound.getCharName(), villian1.getCharName());
		Assert.assertEquals(villianFound.getExperience(), villian1.getExperience());
		Assert.assertEquals(villianFound.getLocation(), villian1.getLocation());
		Assert.assertEquals(villianFound.getRank(), villian1.getRank());
		Assert.assertEquals(3, character.getLocation());
	}
	
	@Test
	public void testHandleFight() {
		BasicGameContext gameContext = new BasicGameContext();
		gameContext.setGameName("gameContext1");
		gameContext.setGameStartDate(new Date(System.currentTimeMillis()));
		commandHandler.handleCreateGame(gameContext);
		BasicGameCharacter character = new BasicGameCharacter();
		character.setCharName("gameChar1");
		character.setGameContextId(gameContext.getGameContextId());
		character.setGameProfileCharId(1);
		character.setExperience(100);
		character.setRank(5);
		BasicGameCharacter createdChar = (BasicGameCharacter) commandHandler.handleCreateCharacter(character);
		Observable[] gameMap = new Observable[10];
		Villian villian1 = new Villian();
		villian1.setCharName("villian1");
		villian1.setExperience(10);
		villian1.setGameProfileCharId(1);
		villian1.setLocation(3);
		villian1.setRank(1);
		gameMap[villian1.getLocation()] = villian1;
		Villian villianFound = (Villian) commandHandler.handleExplore(new Steps(4), gameMap, character);
		boolean fightWon = commandHandler.handleFight(character, villianFound);
		Assert.assertTrue(fightWon);
	}

}
