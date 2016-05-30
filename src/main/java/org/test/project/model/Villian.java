/**
 * 
 */
package org.test.project.model;

import org.test.project.api.Observable;

/**
 * @author abdelgam
 *
 */
public class Villian extends BasicGameCharacter implements Observable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.test.project.api.Observable#getType()
	 */
	@Override
	public CharacterType getType() {
		return CharacterType.VILLIAN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.test.project.model.BasicGameCharacter#getCharacterType()
	 */
	@Override
	public char getCharacterType() {
		return CharacterType.VILLIAN.getType();
	}

	/**
	 * 
	 */
	public Villian() {
		super();
	}

}
