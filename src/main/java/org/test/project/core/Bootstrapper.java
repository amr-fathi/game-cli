package org.test.project.core;

import org.apache.log4j.Logger;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.test.project.api.GameInterface;

@SpringBootApplication
@ImportResource("springContext.xml")
public class Bootstrapper {

	private final static Logger LOG = Logger.getLogger(Bootstrapper.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Bootstrapper.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.setLogStartupInfo(false);
		ConfigurableApplicationContext ctx = app.run(args);
		Bootstrapper btstrpr = ctx.getBean(Bootstrapper.class);
		btstrpr.init(args, ctx);
	}

	public void init(String[] args, ConfigurableApplicationContext ctx) {
		GameInterfaceFactory factory = GameInterfaceFactory.getInstance();
		GameInterface controller = factory.getGameInterface(ctx);
		controller.launchInterface();
	}
}
