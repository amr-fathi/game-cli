package org.test.project.exception;

/**
 * The Class GameException. Represents an applicaiton exception for possible
 * errors during game processing
 */
public class GameException extends Exception {

	public GameException() {
		super();
	}

	public GameException(String message) {
		super(message);
	}

	public GameException(Throwable cause) {
		super(cause);
	}

}
