/**
 * 
 */
package org.test.project.model;

/**
 * @author abdelgam
 *
 */
public enum CharacterType {

	VILLIAN('B'), HERO('G');

	private CharacterType(char t) {
		this.type = t;
	}

	char type;

	public char getType() {
		return type;
	}

}
