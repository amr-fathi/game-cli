/**
 * 
 */
package org.test.project.model;

import java.sql.Date;

import org.test.project.api.GameContext;

/**
 * @author abdelgam
 *
 */
public class BasicGameContext implements GameContext {
	private int gameContextId;
	private String gameName;
	private Date gameStartDate;

	/**
	 * 
	 */
	public BasicGameContext() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasicGameContext [gameContextId=" + gameContextId
				+ ", gameName=" + gameName + ", gameStartDate=" + gameStartDate
				+ "]";
	}

	public BasicGameContext(String gameName, Date gameStartDate) {
		this.gameContextId = -1;
		this.gameName = gameName;
		this.gameStartDate = gameStartDate;
	}
	/**
	 * @return the gameContextId
	 */
	public int getGameContextId() {
		return gameContextId;
	}
	/**
	 * @param gameContextId
	 *            the gameContextId to set
	 */
	public void setGameContextId(int gameContextId) {
		this.gameContextId = gameContextId;
	}
	/**
	 * @return the gameName
	 */
	public String getGameName() {
		return gameName;
	}
	/**
	 * @param gameName
	 *            the gameName to set
	 */
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	/**
	 * @return the gameStartDate
	 */
	public Date getGameStartDate() {
		return gameStartDate;
	}
	/**
	 * @param gameStartDate
	 *            the gameStartDate to set
	 */
	public void setGameStartDate(Date gameStartDate) {
		this.gameStartDate = gameStartDate;
	}
}
