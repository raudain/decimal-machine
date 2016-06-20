package operating.systems.internals.DecimalMachine;

import java.io.FileNotFoundException;
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

	public static String getFirstInput() {

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
	
	public static String getInput() {

		String prompt = "Enter another executable's file name to load" + " another program. Enter \"run\" to start"
				+ " program(s) or " + "enter shutdown to halt the machine";

		logger.info(prompt);
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

		// Priority one is the lowest priority, and 127 is the highest
		final byte highestPriority = 127;
		byte priority = highestPriority;

		String firstCommandLineInput = getFirstInput();

		machine.processInput(firstCommandLineInput, priority);
		
		while (!("shutdown".equals(firstCommandLineInput))) {
			String commandLineInput = getInput();

			if ("run".equals(commandLineInput)) {
				boolean running_nullProcess = false;
				// Programs are run until in this loop until they halt
				while (running_nullProcess == false) {

					logger.info("Selecting a process out of the ready Queue");
					short runningPcbPointer = machine.selectFromRQ();

					/*
					 * The null process is loaded into the operating systems's
					 * memory first so it's process control block pointer is in
					 * the first possible memory address of the operating
					 * system.
					 */
					final short FIRST_OS_MEMORY_ADDRESS = 6000;
					if (runningPcbPointer == FIRST_OS_MEMORY_ADDRESS) {
						running_nullProcess = true;
						logger.info("All programs are finished running");

						/*
						 * run null process when the machine is waiting for user
						 * input
						 */
						machine.executeProgram();
						continue;
					}

					short executionStatus = machine.executeProgram();
					final byte CONTINUE_EXECUTION = 1;
					final byte TIME_SLICE_EXPIRED = 2;
					final byte EXECUTION_COMPLETE = 3;

					switch (executionStatus) {

					/*
					 * Operating system took control of CPU but gave it back to
					 * the process
					 */
					case CONTINUE_EXECUTION:
						executionStatus = machine.executeProgram();

						/*
						 * When a processes time expires the process at the top
						 * of the ready queue is ran
						 */
					case TIME_SLICE_EXPIRED:
						logger.info("The program dumped above has expired its time " + "slice");
						Process_Control_Block pcb = Process_Control_Block.getPcb(machine.getOsm(), runningPcbPointer);
						machine.benchProgram(pcb);
						continue;

					case EXECUTION_COMPLETE:
						// All memory allocated to the now halted process is
						// freed.
						machine.terminateProcess(runningPcbPointer);
						continue;

					default:
						// Unknown programming error
						logger.error("Unknown code error");

					} // End of switch statement for executionStatus
					try {
						executionStatus = machine.CheckAndProcessInterrupt();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (executionStatus < 0)
						logger.error("Unknown Error");
				} // end of the inner loop "while running"
			} // end of if "run" entered
			else if ("shutdown".equals(commandLineInput)) {
				machine.shutdownSystem();
				logger.info("System is shutting down");
				return;
			} else if ("".equals(commandLineInput))
				logger.error("Blank entered");
			else // assumes the command line input is a program name
			{
				/*
				 * Each new process's priorty is lower this the preciously
				 * created process
				 */
				priority -= 1;
				/*
				 * A process's priority cannot be lower than the Null_Process's
				 * priority which is zero.
				 */
				if (priority == 0)
					priority = highestPriority;

				try {
					machine.createProcess(commandLineInput, priority);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} // end of if statement
		} // end of while not shutdown outer loop
		machine.shutdownSystem();
		logger.info("System is shutting down");
	} // end of main method
} // end of class