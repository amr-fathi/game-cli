package org.test.project;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.test.project.basic.BasicCommandLineInterface;
import org.test.project.core.Bootstrapper;
import org.test.project.exception.GameException;

import static org.junit.Assert.*;

public class BasicCommandLineInterfaceTest extends AbstractGameTest{
	
	@Autowired
	BasicCommandLineInterface gameInterface;

	@Test
	public void testCreateGameCommand() {
//		String input = "createGame -h\n";
//		OutputStream outputStream = new ByteArrayOutputStream();
//		try {
//			gameInterface.parseArgs(new Scanner(input), new PrintStream(outputStream));
//			String result = outputStream.toString();
//			assertNotNull(result);
//		} catch (GameException e) {
//			e.printStackTrace();
//		}
	}

}
