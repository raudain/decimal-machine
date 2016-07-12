package operating.systems.internals.DecimalMachine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.DecimalMachine.Machine;

public class UI {

	private static final Logger logger = LogManager.getLogger("Application Memory");

	private static String[] getInput(){

		System.out.println("Enter an executable file names");
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
		String commandLineInput = null;
		try {
			commandLineInput = keyboard.readLine();
		} catch (IOException e) {
			logger.error("Nothing was entered");
			getInput();
		}
		

		return commandLineInput.split(" ");
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
		// Priority one is the lowest priority, and 127 is the highest

		String[] fileNames = getInput();
		Program[] programs = null;
		try {
			programs = machine.load(fileNames);
		} catch (FileNotFoundException e) {
			logger.error("File not found");
			main(args);
		}
		machine.createPcbs(programs);
		// Programs are run until in this loop until they halt
		machine.execute();
		System.out.println("The Machine has shutdown. Goodbye!");
	} // end of main method
} // end of class