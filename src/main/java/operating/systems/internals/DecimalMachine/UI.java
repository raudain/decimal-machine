package operating.systems.internals.DecimalMachine;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.DecimalMachine.Machine;

public class UI {

	private static final Logger logger = LogManager.getLogger("App");

	private static String getGreeting() {

		return "Welcome to the decimal machine. The first programs entered gets a higher"
				+ " execution prority then each successive program entered/n";
	}

	private static String getFirstInput() {

		String s = "";

		s = getGreeting();

		logger.info(s);
		String commandLineInput = "";
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter an executable's file name");
		commandLineInput = keyboard.nextLine();
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

		final Machine machine = new Machine();
		byte lowestPriority = 0;
		machine.run("Null_Process", lowestPriority);
		// Priority one is the lowest priority, and 127 is the highest
		final byte highestPriority = 127;
		byte priority = highestPriority;

		String userInput = getFirstInput();
		while (!(userInput.equals("shutdown"))) {
			boolean running_nullProcess = false;
			// Programs are run until in this loop until they halt
			while (running_nullProcess == false) {
				Byte executionStatus = machine.run("userInput", priority--);
				logger.info("Selecting a process out of the ready Queue");

				byte halt = machine.getHaltCode();
				
				if (executionStatus.equals(halt)) {					
					//machine.terminateProcess();
					machine.run("Null_Process", lowestPriority);
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