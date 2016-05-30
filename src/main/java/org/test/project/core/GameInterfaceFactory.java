/**
 * 
 */
package org.test.project.core;

import org.springframework.context.ConfigurableApplicationContext;
import org.test.project.api.GameInterface;

/**
 * @author abdelgam
 *
 */
public class GameInterfaceFactory {

	private static GameInterfaceFactory instance = null;
	private static GameInterface controller = null;
	private GameInterfaceFactory() {

	}

	public static synchronized GameInterfaceFactory getInstance() {
		if (instance == null) {
			instance = new GameInterfaceFactory();
		}
		return instance;
	}

	public GameInterface getGameInterface(ConfigurableApplicationContext ctx) {
		controller = (GameInterface) ctx.getBean("gameInterface");
		return controller;
	}
}
