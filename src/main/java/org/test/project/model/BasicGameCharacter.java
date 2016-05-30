/**
 * 
 */
package org.test.project.model;

import org.test.project.api.GameCharacter;

/**
 * @author abdelgam
 *
 */
public class BasicGameCharacter implements GameCharacter {

	private int charId;
	private String charName;
	private int experience;
	private int rank;
	private int gameProfileCharId;
	private int location;
	private int gameContextId;
	private char characterType;

	public int getCharId() {
		return charId;
	}
	public void setCharId(int charId) {
		this.charId = charId;
	}
	public String getCharName() {
		return charName;
	}
	public void setCharName(String charName) {
		this.charName = charName;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getGameProfileCharId() {
		return gameProfileCharId;
	}
	public void setGameProfileCharId(int gameProfileCharId) {
		this.gameProfileCharId = gameProfileCharId;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public int getGameContextId() {
		return gameContextId;
	}
	public void setGameContextId(int gameContextId) {
		this.gameContextId = gameContextId;
	}
	public char getCharacterType() {
		return characterType;
	}
	public void setCharacterType(char type) {
		this.characterType = type;
	}

	public BasicGameCharacter() {
		super();
	}
	@Override
	public String getCharacterName() {
		return this.charName;
	}
	@Override
	public Integer getCharacterExperience() {
		return this.experience;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasicGameCharacter [charId=" + charId + ", charName=" + charName
				+ ", experience=" + experience + ", lvl=" + rank
				+ ", gameProfileCharId=" + gameProfileCharId + ", location="
				+ location + ", gameContextId=" + gameContextId
				+ ", characterType=" + characterType + "]";
	}

}
