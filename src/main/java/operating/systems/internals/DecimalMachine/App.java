package operating.systems.internals.DecimalMachine;

import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.DecimalMachine.Stopped_Execution_Reason_Code;
import operating.systems.internals.DecimalMachine.Machine;

public class App implements Stopped_Execution_Reason_Code {

	private static final Logger logger = LogManager.getLogger("App");
	private boolean firstInput;
	private final byte highestPriority;

	public App() {
		
		firstInput = true;
		highestPriority = 127;
	}

	public static Machine init() {
		// Initialize machine's system
		Machine machine = new Machine();

		try {
			// Priority one is the lowest priority, and 255 is the highest
			machine.createProcess();
		} catch (FileNotFoundException e) {
			System.out.println("Error: Null program cannot be found "
					+ "in working directory");
			e.printStackTrace();
		}

		return machine;
	}

	public String getInput() {

		String greeting = "Welcome to the decimal machine. The first programs entered get a higher"
				+ " execution prority then each successive program entered/n";
		String anotherOne = "Enter another executable's file name to load"
				+ " another program. Enter \"run\" to start"
				+ " program(s) or " + "enter shutdown to halt the machine";
		String s = "";

		if (firstInput) {
			s = greeting;
			firstInput = false;
		} else
			s = anotherOne;

		System.out.println(s);
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

		Machine hdm = init();
		App app = new App();
		
		// Priority one is the lowest priority, and 127 is the highest
		byte priority = app.highestPriority;
		
		String firstCommandLineInput = app.getInput();
		
		// If shutdown command is entered before any programs are entered
		if ("shutdown".equals(firstCommandLineInput)) {
			hdm.ISRshutdownSystem();
			logger.info("System is shutting down");
			return;
		}

		else if ("".equals(firstCommandLineInput))
			logger.error("Error: Blank entered");

		else
			try {
				hdm.createProcess(firstCommandLineInput, priority);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		while (!("shutdown".equals(firstCommandLineInput))) {
			String commandLineInput = app.getInput();

			if ("run".equals(commandLineInput)) {
				boolean running_nullProcess = false;
				// Programs are run until in this loop until they halt
				while (running_nullProcess == false) {

					logger.info("Selecting a process out of the ready Queue");
					short runningPCBpointer = hdm.selectFromRQ();

					/*
					 * The null process is loaded into the operating systems's
					 * memory first so it's process control block pointer is in
					 * the first possible memory address of the operating
					 * system.
					 */
					final short FIRST_OS_MEMORY_ADDRESS = 6000;
					if (runningPCBpointer == FIRST_OS_MEMORY_ADDRESS) {
						running_nullProcess = true;
						logger.info("All programs are finished running");

						/*
						 * run null process when the machine is waiting for user
						 * input
						 */
						hdm.executeProgram();
						continue;
					}

					short executionStatus = hdm.executeProgram();

					switch (executionStatus) {

					/*
					 * Operating system took control of CPU but gave it back to
					 * the process
					 */
					case PROCEED:
						executionStatus = hdm.executeProgram();

						/*
						 * When a processes time expires the process at the top
						 * of the ready queue is ran
						 */
					case TIME_SLICE:
						logger.info("The program dumped above has expired its time "
								+ "slice");
						hdm.saveContext(runningPCBpointer);
						hdm.insertIntoReadyQueue(runningPCBpointer);
						continue;

					case HALTED:
						// All memory allocated to the now halted process is
						// freed.
						hdm.terminateProcess(runningPCBpointer);
						continue;

						/*
						 * else if(status == StartOfInputOperation) // io_getc {
						 * // set reason for waiting to Input Completion Event
						 * memory[RunningPCBptr +ReasonForWaitingIndex] =
						 * InputCompletionEvent; // Enter process into WQ
						 * InsertIntoWQ(RunningPCBptr); } else if(status ==
						 * StartOfOutputOperation) // io_putc { // set reason
						 * for waiting to Output Completion Event
						 * Memory[RunningPCBptr +ReasonForWaitingIndex] =
						 * OutputCompletionEvent; // Enter process into WQ
						 * InsertIntoWQ(RunningPCBptr); }
						 */
						// msgq_receive
					case WAITING_FOR_MESSAGE:
						// set reason for waiting to Message Arrival Event
						// final byte REASON_FOR_WAITING_INDEX = 3;
						// memory[runningPCBpointer + REASON_FOR_WAITING_INDEX]
						// = MessageArrivalEvent;
						// hdm.insertIntoWaitingQueue(runningPCBpointer);
						break;

					default:
						// Unknown programming error
						System.out.println("Unknown code error");

					} // End of switch statement for executionStatus
					try {
						executionStatus = hdm.CheckAndProcessInterrupt();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (executionStatus < 0)
						System.out.println("Unknown Error");
				} // end of the inner loop "while running"
			} // end of if "run" entered
			else if ("shutdown".equals(commandLineInput)) {
				hdm.ISRshutdownSystem();
				System.out.println("System is shutting down");
				return;
			} else if ("".equals(commandLineInput))
				System.out.println("Error: Blank entered");
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
					priority = app.highestPriority;

				try {
					hdm.createProcess(commandLineInput, priority);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}// end of if statement
		}// end of while not shutdown outer loop
		hdm.ISRshutdownSystem();
		System.out.println("System is shutting down");
	} // end of main method
} // end of class