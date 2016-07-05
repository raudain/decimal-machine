package operating.systems.internals.DecimalMachine;

import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.DecimalMachine.Machine;

public class UI {
	
	private static final Logger logger = LogManager.getLogger("App");

	private static String getInput() {

		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter an executable file's name");
		String commandLineInput = keyboard.nextLine();
		keyboard.close();

		commandLineInput.toLowerCase();

		return "";
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
	public static void main(String[] args) {

		UI ui = new UI();
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
					getInput();
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