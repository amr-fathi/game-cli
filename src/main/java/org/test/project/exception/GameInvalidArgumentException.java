/**
 * 
 */
package org.test.project.exception;

/**
 * The Class GameInvalidArgumentException. Represents application exceptions due
 * to invalid user inputs
 *
 * @author abdelgam
 */
public class GameInvalidArgumentException extends GameException {

	public GameInvalidArgumentException() {
		super();
	}

	public GameInvalidArgumentException(String message) {
		super(message);
	}

	public GameInvalidArgumentException(Throwable cause) {
		super(cause);
	}

}
