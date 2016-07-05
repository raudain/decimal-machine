package operating.systems.internals.DecimalMachine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.DecimalMachine.Machine;

public class UI {
	
	private static final Logger logger = LogManager.getLogger("UI");

	private static String getInput() throws IOException {

		System.out.println("Enter an executable file's name");
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));	
		String commandLineInput = keyboard.readLine();

		return commandLineInput;
	}

	/**
	 * main function that (a) calls InitializeSystem function
	 * 
	 * (b) reads a command (name of the executable file) from the user to
	 * execute a program
	 * 
	 * (c) calls ExecuteProgram function to execute the program loaded into the
	 * Hypo memory, and
	 * 
	 * (d) calls the DumpMemory function after loading a program and after
	 * executing the loaded program. The main function checks the function
	 * return value from each function and takes appropriate action.
	 * 
	 * @param args
	 *            command line arguments are not used.
	 */
	public static void main(String[] args) throws IOException {

		final Machine machine = new Machine();
		byte lowestPriority = 0;
		// Priority one is the lowest priority, and 127 is the highest
		final byte highestPriority = 127;
		byte priority = highestPriority;

		String userInput = getInput();
		while (!(userInput.equals("shutdown"))) {
			boolean running_nullProcess = false;
			// Programs are run until in this loop until they halt
			while (running_nullProcess == false) {
				Byte executionStatus = -1;
				try {
				executionStatus = machine.run("userInput", priority--);
				} catch (FileNotFoundException e) {
					logger.error(userInput + " was not found in " + machine.getWorkingDirectory());
					userInput = getInput();
					break;
				}
				logger.info("Selecting a process out of the ready Queue");

				byte halt = machine.getHaltCode();
				
				if (executionStatus.equals(halt)) {					
					//machine.terminateProcess();
					try {
						machine.run("Null_Process", lowestPriority);
					} catch (FileNotFoundException e) {
						
					}
				} else
					executionStatus = machine.execute();
			} // end of while loop
			
			if ("shutdown".equals(userInput)) {
				logger.info("System is shutting down");
				return;
			} else if ("".equals(userInput))
				logger.error("Blank entered");
		} // end of while not shutdown outer loop
	} // end of
																		// main
																		// method
} // end of class