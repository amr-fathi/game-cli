package org.test.project.basic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.test.project.api.DisplacementUnits;
import org.test.project.api.GameCharacter;
import org.test.project.api.GameCommandHandler;
import org.test.project.api.GameContext;
import org.test.project.api.Observable;
import org.test.project.exception.GameException;
import org.test.project.model.BasicGameCharacter;
import org.test.project.model.BasicGameContext;
import org.test.project.model.GameProfile;
import org.test.project.model.Steps;

@Component
public class BasicJdbcCommandHandler implements GameCommandHandler {

	private final static Logger LOG = Logger
			.getLogger(BasicJdbcCommandHandler.class);

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.test.project.api.GameCommandHandler#handleCreateGame(org.test.project
	 * .api.GameContext) JDBC handling for create game command
	 */
	@Override
	public GameContext handleCreateGame(GameContext ctx) {
		final String sql = "insert into GAME_CONTEXT (game_name, game_start_date) values (?, ?)";
		final BasicGameContext basicCtx = (BasicGameContext) ctx;
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		final PreparedStatementCreator psc = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				final PreparedStatement ps = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, basicCtx.getGameName());
				ps.setDate(2, basicCtx.getGameStartDate());
				return ps;
			}
		};
		this.jdbcTemplate.update(psc, generatedKeyHolder);
		basicCtx.setGameContextId(generatedKeyHolder.getKey().intValue());
		LOG.info("Created new game ID: " + basicCtx.getGameContextId());
		LOG.debug("created new game: " + basicCtx.toString());
		return basicCtx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.test.project.api.GameCommandHandler#handleResumeGame(org.test.project
	 * .api.GameContext) JDBC handling for resume game command
	 */
	@Override
	public GameContext handleResumeGame(GameContext ctx) throws GameException {
		String sql = "select * from GAME_CONTEXT where GAME_CONTEXT_ID = ?";
		BasicGameContext gameToResume = (BasicGameContext) ctx;
		BasicGameContext loadedContext;
		try {
			loadedContext = this.jdbcTemplate.queryForObject(sql,
					new Object[]{gameToResume.getGameContextId()},
					new BeanPropertyRowMapper<BasicGameContext>(
							BasicGameContext.class));
		} catch (EmptyResultDataAccessException e) {
			throw new GameException("Invalid game id: "
					+ gameToResume.getGameContextId() + ".ID Not found in DB.");
		}
		LOG.info("Loaded game ID: " + loadedContext.getGameContextId());
		LOG.debug("Loaded game: " + loadedContext.toString());
		return loadedContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.test.project.api.GameCommandHandler#handleCreateCharacter(org.test.
	 * project.api.GameCharacter) JDBC handling for create character command
	 */
	@Override
	public GameCharacter handleCreateCharacter(GameCharacter character) {
		final String sql = "insert into game_character (char_name, experience, rank, game_profile_char_id, location, game_context_id, character_type) values (?, ?, ?, ?, ?, ? ,?)";
		final BasicGameCharacter basicChar = (BasicGameCharacter) character;
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		final PreparedStatementCreator psc = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				final PreparedStatement ps = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, basicChar.getCharName());
				ps.setInt(2, basicChar.getExperience());
				ps.setInt(3, basicChar.getRank());
				ps.setInt(4, basicChar.getGameProfileCharId());
				ps.setInt(5, basicChar.getLocation());
				ps.setInt(6, basicChar.getGameContextId());
				ps.setString(7, basicChar.getCharacterType() + "");
				return ps;
			}
		};
		this.jdbcTemplate.update(psc, generatedKeyHolder);
		basicChar.setCharId(generatedKeyHolder.getKey().intValue());
		LOG.info("Created new game character ID: " + basicChar.getCharId()
				+ ". Name: " + basicChar.getCharName());
		LOG.debug("created new character: " + basicChar.toString());
		return basicChar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.test.project.api.GameCommandHandler#handleFight(org.test.project.api.
	 * GameCharacter, org.test.project.api.GameCharacter) JDBC handling for
	 * fight command
	 */
	@Override
	public boolean handleFight(GameCharacter gameCharacter,
			GameCharacter villain) {
		boolean wonFight = false;
		BasicGameCharacter basicCharacter = (BasicGameCharacter) gameCharacter;
		BasicGameCharacter basicVillian = (BasicGameCharacter) villain;
		if (basicCharacter.getRank() > basicVillian.getRank()) {
			wonFight = true;
			basicCharacter.setExperience(basicCharacter.getExperience()
					+ basicVillian.getExperience());
			basicCharacter.setLocation(basicVillian.getLocation() + 1);
			handleUpdateCharacter(basicCharacter);
			LOG.info("Game character ID: " + basicCharacter.getCharId()
					+ ", Won the fight against : "
					+ basicVillian.getCharName());
			LOG.debug("Character atrributes after the fight: "
					+ basicCharacter.toString());
		} else {
			wonFight = false;
			LOG.info("Game character ID: " + basicCharacter.getCharId()
					+ ", Lost the fight against : "
					+ basicVillian.getCharName());
		}
		return wonFight;
	}

	/**
	 * Find all game profiles.
	 *
	 * @return the map
	 */
	public Map<Integer, GameProfile> findAllGameProfiles() {
		String sql = "select * from GAME_PROFILE";
		List<GameProfile> gameProfiles = this.jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<GameProfile>(GameProfile.class));
		Map<Integer, GameProfile> result = new HashMap<Integer, GameProfile>();
		if (gameProfiles != null) {
			int indx = 1;
			for (GameProfile profile : gameProfiles) {
				result.put(indx, profile);
				indx++;
			}
		}
		return result;
	}

	/**
	 * Load game profile.
	 *
	 * @param id
	 *            the id
	 * @return the game profile
	 */
	public GameProfile loadGameProfile(int id) {
		String sql = "select * from GAME_PROFILE where PROFILE_ID = ?";
		GameProfile loadedProfile = this.jdbcTemplate.queryForObject(sql,
				new Object[]{id},
				new BeanPropertyRowMapper<GameProfile>(GameProfile.class));
		return loadedProfile;
	}

	/**
	 * Find character templates for game profile.
	 *
	 * @param profile
	 *            the profile
	 * @return the map
	 */
	public Map<Integer, String> findCharacterTemplatesForGameProfile(
			GameProfile profile) {
		String sql = "select * from game_profile_character where profile_id = ?";
		Map<Integer, String> result = new HashMap<Integer, String>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql,
				profile.getProfileId());
		for (Map row : rows) {
			result.put((Integer) row.get("game_profile_char_id"),
					String.valueOf(row.get("game_profile_char_name")));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.test.project.api.GameCommandHandler#handleExplore(org.test.project.
	 * api.DisplacementUnits, java.lang.Object[]) JDBC handling for explore
	 * command
	 */
	@Override
	public Observable handleExplore(DisplacementUnits units, Object... args) {
		Observable[] map = null;
		BasicGameCharacter heroCharacter = null;
		Steps steps = (Steps) units;
		if (args.length > 1) {
			if (args[0] != null) {
				map = (Observable[]) args[0];
			}
			if (args[1] != null) {
				heroCharacter = (BasicGameCharacter) args[1];
			}
			int newLocation = heroCharacter.getLocation() + steps.getUnits();
			for (int i = heroCharacter.getLocation(); i <= newLocation; i++) {
				if (map[i] != null) {
					LOG.info(
							"While exploring, found villian at location: " + i);
					LOG.debug("While exploring, found villian at location: " + i
							+ ". Found a "
							+ ((Observable) map[i]).getType().toString());
					heroCharacter.setLocation(i);
					handleUpdateCharacter(heroCharacter);
					return map[i];
				}
			}
			heroCharacter.setLocation(newLocation);
			handleUpdateCharacter(heroCharacter);
		}
		return null;
	}

	/**
	 * Handle update character.
	 *
	 * @param character
	 *            the character
	 */
	private void handleUpdateCharacter(BasicGameCharacter character) {
		final String sql = "update game_character set experience=?, rank=?, location=? where char_id=?";
		final BasicGameCharacter basicChar = (BasicGameCharacter) character;
		final PreparedStatementCreator psc = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				final PreparedStatement ps = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, basicChar.getExperience());
				ps.setInt(2, basicChar.getRank());
				ps.setInt(3, basicChar.getLocation());
				ps.setInt(4, basicChar.getCharId());
				return ps;
			}
		};
		this.jdbcTemplate.update(psc);
		LOG.info("Updated game character ID: " + basicChar.getCharId()
				+ ". Name: " + basicChar.getCharName());
		LOG.debug("Update game character: " + basicChar.toString());
	}

	public BasicGameCharacter handleLoadGameCharacter(
			BasicGameContext gameContext) throws GameException {
		String sql = "select * from GAME_CHARACTER where GAME_CONTEXT_ID = ?";
		BasicGameCharacter loadedCharacter;
		try {
			loadedCharacter = this.jdbcTemplate.queryForObject(sql,
					new Object[]{gameContext.getGameContextId()},
					new BeanPropertyRowMapper<BasicGameCharacter>(
							BasicGameCharacter.class));
		} catch (EmptyResultDataAccessException e) {
			throw new GameException("No game characters found for game ID: "
					+ gameContext.getGameContextId());
		}
		return loadedCharacter;
	}
}
