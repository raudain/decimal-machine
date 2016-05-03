package operating.systems.internals.DecimalMachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * byte: The byte data type is an 8-bit signed two's complement integer. It has
 * a minimum value of -128 and a maximum value of 127 (inclusive).
 * 
 * short: The short data type is a 16-bit signed two's complement integer. It
 * has a minimum value of -32,768 and a maximum value of 32,767 (inclusive).
 * 
 * int: The int data type is a 32-bit signed two's complement integer. It has a
 * minimum value of -2,147,483,648 and a maximum value of 2,147,483,647
 * (inclusive).
 * 
 * long: The long data type is a 64-bit signed two's complement integer. It has
 * a minimum value of -9,223,372,036,854,775,808 and a maximum value of
 * 9,223,372,036,854,775,807 (inclusive).
 * 
 * @author Roody Audain
 * @version 03 August 2013
 *
 */
public class Machine implements Memory_Address_Book,
		Stopped_Execution_Reason_Code {

	private long[] gpr; // General Purpose Registers
	private final int SIZE_OF_MEMORY = 7999;
	private short pc; // Program Counter
	private long[] memory; // main memory

	private short sp; // Stack Pointer
	// private byte psr; // Processor status register
	private long clock = 0; // system clock

	private final byte LAST_GPR = 7; // size of cpu register minus one
	// end marker for PCB PCBmemoryFreeList freeHeapMemory and other lists.
	private final short END_OF_LIST = -1;
	private short RQ = END_OF_LIST; // Ready Queue set to empty list
	private short WQ = END_OF_LIST;

	// User free memory list is set to empty list;
	private short firstFreeTemporaryMemoryPointer = END_OF_LIST;
	private short PCBmemoryFreeList = END_OF_LIST; // OS Free memory list set to
													// empty
	// list;

	
	private static final Logger logger = LogManager
			.getLogger("Machine_Implementation");

	private void partitionMemory() {

		memory = new long[SIZE_OF_MEMORY];
		/*
		 * Create temporary memory free list User free space starts at location
		 * 4000 FIRST_TEMPORARY_MEMORY_ADDRESS = 4000 END_OF_LIST = -1
		 */
		firstFreeTemporaryMemoryPointer = FIRST_TEMPORARY_MEMORY_ADDRESS;
		// End of heap stack memory
		memory[FIRST_TEMPORARY_MEMORY_ADDRESS] = END_OF_LIST;
		// User free memory has 2000 locations
		memory[FIRST_TEMPORARY_MEMORY_ADDRESS + 1] = 2000;
		// OS free memory starts at location 8000
		PCBmemoryFreeList = FIRST_OS_MEMORY_ADDRESS;
		// End of operating system's memory
		memory[FIRST_OS_MEMORY_ADDRESS] = END_OF_LIST;
		// OS free memory is 2000 locations
		memory[FIRST_OS_MEMORY_ADDRESS + 1] = 2000;
	}

	/**
	 * Set all virtual hardware component variables to 0 and loads free memory
	 * space data
	 */
	public Machine() {
		// Initialize all hardware components to zero
		gpr = new long[LAST_GPR + 1];
		// initialize all memory locations to zero;
		partitionMemory();

		// initialize all General purpose registers to 0
		pc = 0; // 0 == start of memeory

		sp = 0; // 4000 == start of stack/heap memory

		// psr = 0; // Processor status register
	} // end of constructor

	final byte PCB_SIZE = 20;
	byte processID = 1;
	final byte NEXT_PCB_INDEX = 0;
	final byte PROCESS_ID_INDEX = 1;
	final byte STATE_INDEX = 2;
	final char READY_STATE = 'R';
	final short DEFAULT_PRIORITY = 128;

	/**
	 * initializePCB fills out some PCB values for a ready process.
	 * 
	 * @param PCBpointer
	 *            Start of a particular process control block about to be
	 *            zeroed.
	 */
	public void initializePCB(short PCBpointer) {
		// Initialize all values to zero
		for (int i = 0; i < PCB_SIZE; i++)
			memory[PCBpointer + i] = 0;

		byte Priorityindex = 4;

		// END_OF_LIST is a constant set to -1
		memory[PCBpointer + NEXT_PCB_INDEX] = END_OF_LIST;

		/*
		 * ProcessID is a global variable and is initialized to 1. Allocate PID
		 * and set it in the PCB pid zero is invalid
		 */
		memory[PCBpointer + PROCESS_ID_INDEX] = processID++;
		// DefaultPriority is a constant set to 128
		memory[PCBpointer + Priorityindex] = DEFAULT_PRIORITY;
		memory[PCBpointer + STATE_INDEX] = READY_STATE; // Ready State is a
														// constant set to 'R'

		// memory [PCBpointer + ] =;

		return;
	} // end of Initialize PCB method

	final char WAITING_STATE = 'W';

	/**
	 * setPCB fills out some values the child process's process control block
	 * for a waiting process.
	 * 
	 * @param PCBpointer
	 *            Start of a particular process control block about to be
	 *            zeroed.
	 * 
	 * @param pr
	 *            Memory location where the process ID is stored
	 */
	public void setPCB(short PCBptr, short pr) {
		// Initialize all values to zero
		for (int i = 0; i < PCB_SIZE; i++)
			memory[PCBptr + i] = 0;

		byte Priorityindex = 4;
		final short DefaultPriority = 128;

		// END_OF_LIST is a constant set to -1
		memory[PCBptr + NEXT_PCB_INDEX] = END_OF_LIST;

		memory[PCBptr + PROCESS_ID_INDEX] = pr;
		// DefaultPriority is a constant set to 128
		memory[PCBptr + Priorityindex] = DefaultPriority;
		// Ready State is a constant set to 'W'
		memory[PCBptr + STATE_INDEX] = WAITING_STATE;

		return;
	} // end of set process control block

	final byte INVALID_ADDRESS = -1;

	public String addDirectory(String fileName) {

		String directory = "C:\\Users\\roody.audain\\"
				+ "workspace\\Concepts\\"
				+ "DecimalMachine\\src\\main\\"
				+ "resources\\";
		String s = directory + fileName;

		return s;
	}

	/**
	 * Open the file containing HYPO machine program and load the content into
	 * user memory. On successful load, return the PC value in the last line. On
	 * failure, return appropriate error code
	 * 
	 * @param filename
	 *            Name of the executable file to be loaded into main memory
	 *
	 * @return error code Tells what error occurred that failed to load program
	 *         into memory Error codes are :
	 * 		   FILE_NOT_FOUND = -2
	 *         FILE_NOT_OPENED = -3 
	 *         NO_END_OF_PROGRAM = -4 
	 *         INVALID_ADDRESS = -1
	 */
	public short absoluteLoader(String fileName) {

		final byte FILE_NOT_FOUND = -2;
		final byte NO_END_OF_PROGRAM = -4; // Missing end of program indicator

		Scanner machineCode = null;
		String s = addDirectory(fileName);
		try {	
			File file = new File(s);
			machineCode = new Scanner(file);
		} catch (FileNotFoundException e) {
			logger.error("Error in absoluteLoader: Program cannot be found in "
					+ "working directory");
			e.printStackTrace();
			
			return FILE_NOT_FOUND;
		}

		// Read from file until end of program or end of file and
		// store program in HYPO memory

		System.out.println("Loading " + fileName + " into memory");
		while (machineCode.hasNextLine()) {
			// read address, content from file
			short address = machineCode.nextShort();

			if (address > INVALID_ADDRESS
					&& address < FIRST_TEMPORARY_MEMORY_ADDRESS)
				// valid address and hence store content
				memory[address] = machineCode.nextInt();

			else if (address <= INVALID_ADDRESS) {
				logger.info("The loader has reach the end of "
						+ fileName + "'s code");
				short origin = machineCode.nextShort();
				machineCode.close();
				return origin;
			} else { // Invalid address, display invalid address error message
				System.out.println("Error: Invalid address in machine code");
				machineCode.close();
				return INVALID_ADDRESS;
			}
		}
		// End of file encountered without end of p rogram
		// display end of file encountered without EOP error message;
		machineCode.close();
		System.out.println("Error: end of file encountered without EOP");
		return NO_END_OF_PROGRAM; // end of file encountered without EOP error
									// code
	} // end of absoluteLoader module

	void printRQ() {
		// Walk thru the queue from the given pointer until end of list
		// Print each PCB as you move from one PCB to the next
		short currentPointer = RQ;
		System.out
				.println("Printing all process control blocks in ready queue:");
		System.out.println("Address\tIndex\tValue");
		while (currentPointer != END_OF_LIST) {
			short nextPointer = (short) memory[currentPointer + NEXT_PCB_INDEX];

			if (nextPointer == currentPointer) {
				logger.error("The link list for the ready queue "
						+ "has a self reference!");
				return;
			}

			byte PCBindex;
			for (PCBindex = 0; PCBindex < PCB_SIZE; PCBindex++)
				System.out.println(currentPointer + "\t" + PCBindex + "\t"
						+ memory[currentPointer++]);

			currentPointer = nextPointer;

			System.out.println("Printing next process control block:");
		} // end of while loop

		System.out.println("The end of the ready queue has been reached.");
	} // end of PrintPCB module

	public boolean isMemoryEmpty() {

		if (memory == null)
			return true;

		boolean status = true;
		for (int i = 0; i < SIZE_OF_MEMORY && status == true; i++)
			if (memory[i] != 0) {
				status = false;
			}
		return status;
	}

	/**
	 * DumpMemory
	 * 
	 * Task Description:
	 * 
	 * 
	 * @param aString
	 *            String to be displayed
	 * @param StartAddress
	 *            Start address of memory location
	 * @param Size
	 *            Number of locations to dump
	 */
	public void dumpMemory() {
		int address = 0;

		// print register titles
		System.out.println("GPRs:\tG0\tG1\tG2\tG3\tG4\tG5\tG6\tG7\tSP\tPC");

		// print register values
		while (address <= LAST_GPR)
			System.out.printf("\t%d", gpr[address++]);

		// print stack pointer and program counter value
		System.out.printf("\t%d\t%d\n", sp, pc);

		address = FIRST_TEMPORARY_MEMORY_ADDRESS;

		if (isMemoryEmpty()) {
			logger.info("Memory is Empty");
			return;
		}

		// print memory header
		System.out.println("\nAddress\t+0\t+1\t+2\t+3\t+4\t+5\t+6\t+7\t+8\t+9");
		int lineNumber = address;

		byte numberOfZeros = 0;
		byte noMoreData = 10;

		while (numberOfZeros < noMoreData) {
			System.out.print(lineNumber);
			int i = 0;
			while (i < 9) // print 10 values from memory
			{
				System.out.print("\t" + memory[address]);
				if (memory[address] == 0)
					numberOfZeros++;
				address++;
				i++;
			} // end of inner while i < 9
				// Print the last memory value of the line.
			System.out.println("\t" + memory[address++]);

			lineNumber += 10;// Increment line title
		} // end of outter while numberOfZeros < noMoreData

		// Displays clock
		System.out.println("Clock == " + clock);
	} // end of dump memory module

	byte notOK = -1;

	/**
	 * AllocateOSMemory allocates memory for each process
	 * 
	 * @retrun fist address of a process control block or error
	 */
	short AllocateOSMemory() {
		final byte NO_FREE_MEMORY = -11;
		final byte PCB_SIZE_INDEX = 1;

		// Allocate memory from OS free space, which is organized as link
		if (PCBmemoryFreeList == END_OF_LIST) {
			System.out.println("Error: No free OS memory");
			return NO_FREE_MEMORY; // ErrorNoFreeMemory is constant set to < 0
		}

		short currentPointer = PCBmemoryFreeList;
		short Previouspointer = (short) memory[currentPointer];
		while (currentPointer != END_OF_LIST) {
			// Check each block in the link list until block with requested
			// memory size is found
			if (memory[currentPointer + PCB_SIZE_INDEX] == PCB_SIZE) { // Found
																		// block
																		// with
																		// requested
																		// size.
																		// Adjust
																		// pointers
				if (currentPointer == PCBmemoryFreeList) // first block
				{
					PCBmemoryFreeList = (short) memory[currentPointer]; // first
																		// entry
																		// is
																		// pointer
																		// to
																		// next
																		// block
					memory[currentPointer] = END_OF_LIST; // reset next pointer
															// in the allocated
															// block

					return currentPointer; // return memory address
				} else // not first block
				{
					memory[Previouspointer] = memory[currentPointer]; // point
																		// to
																		// next
																		// block
					memory[currentPointer] = END_OF_LIST; // reset next pointer
															// in the allocated
															// block
					return currentPointer; // return memory address
				}
			} else if (memory[currentPointer + PCB_SIZE_INDEX] > PCB_SIZE) { // Found
																				// block
																				// with
																				// size
																				// greater
																				// than
																				// requested
																				// size
				if (currentPointer == PCBmemoryFreeList) // first block
				{
					memory[currentPointer + PCB_SIZE] = memory[currentPointer]; // move
																				// next
																				// block
																				// pointer
					memory[currentPointer + PCB_SIZE + 1] = memory[currentPointer + 1]
							- PCB_SIZE;

					PCBmemoryFreeList = (short) (currentPointer + PCB_SIZE); // address
																				// of
																				// reduced
																				// block
					memory[currentPointer] = END_OF_LIST; // reset next pointer
															// in the allocated
															// block
					return currentPointer; // return memory address
				} else // not first block
				{
					memory[currentPointer + PCB_SIZE] = memory[currentPointer]; // move
																				// next
																				// block
																				// pointer
					memory[currentPointer + PCB_SIZE + 1] = memory[currentPointer + 1]
							- PCB_SIZE;
					memory[Previouspointer] = currentPointer + PCB_SIZE; // address
																			// of
																			// reduced
																			// block
					memory[currentPointer] = END_OF_LIST; // reset next pointer
															// in the allocated
															// block
					return currentPointer; // return memory address
				}
			} else // small block
			{ // look at next block
				Previouspointer = currentPointer;
				currentPointer = (short) memory[currentPointer];
			}
		} // end of while currentPointer loop

		System.out.println("Error: No free OS memory error");
		return notOK; // ErrorNoFreeMemory is constant set to < 0
	} // end of Allocate OS Memory() module

	final byte priorityIndex = 4;
	final byte OK = 0;

	public byte insertIntoReadyQueue(short PCBpointer) {
		// Insert PCB according to Priority Round Robin algorithm
		// Use priority in the PCB to find the correct place to insert.
		short previousPointer = END_OF_LIST;
		short currentPointer = RQ;

		// Check for invalid PCB memory address
		if (PCBpointer < FIRST_OS_MEMORY_ADDRESS
				|| PCBpointer > LAST_OS_MEMORY_ADDRESS) {
			System.out
					.println("Error: The process control block pointer is invalid");
			return INVALID_ADDRESS; // ErrorInvalidMemoryAddress is constantset
									// to < 0
		}

		memory[PCBpointer + STATE_INDEX] = READY_STATE; // set state to ready
														// state
		memory[PCBpointer + NEXT_PCB_INDEX] = END_OF_LIST; // set next pointer
															// to end of list

		if (RQ == END_OF_LIST) // RQ is empty
		{
			RQ = PCBpointer;
			return OK;
		}

		// Walk thru RQ and find the place to insert
		// PCB will be inserted at the end of its priority
		while (currentPointer != END_OF_LIST) {
			if (memory[PCBpointer + priorityIndex] > memory[currentPointer
					+ priorityIndex]) { // found the place to insert
				if (previousPointer == END_OF_LIST) {
					// Enter PCB in the front of the list as first entry
					memory[PCBpointer + NEXT_PCB_INDEX] = RQ;
					RQ = PCBpointer;
					System.out.println("[PID: #"
							+ memory[PCBpointer + PROCESS_ID_INDEX]
							+ "] has entered the top of the ready queue");
					printRQ();
					return OK;
				}
				// enter PCB in the middle of the list
				memory[PCBpointer + NEXT_PCB_INDEX] = memory[previousPointer
						+ NEXT_PCB_INDEX];
				memory[previousPointer + NEXT_PCB_INDEX] = PCBpointer;

				System.out
						.println("PCB enters in the middle of the ready queue ");
				printRQ();

				return OK;
			} else // PCB to be inserted has lower or equal priority to the
					// current PCB in RQ
			{ // go to the next PCB in RQ
				previousPointer = currentPointer;
				currentPointer = (short) memory[currentPointer + NEXT_PCB_INDEX];
			}
		} // end of while loop

		// Insert PCB at the end of the RQ
		memory[previousPointer + NEXT_PCB_INDEX] = PCBpointer;

		System.out.println("PCB enters at the bottom of the ready queue ");
		printRQ();

		return OK;
	} // end of insert process into ready queue module

	/**
	 * Gives the next free memory addresses block of the requested size to
	 * programs upon request
	 * 
	 * @param requestedSize
	 *            The amount of memory requested by a program.
	 * @return Error status or the first address for the memory block the is
	 *         given.
	 */
	short allocateTemporaryMemory(short requestedSize) {
		// Allocate memory from User free space, which is organized as link
		// copy OS code and modify to use freeHeapMemory

		final byte NO_MEMORY_FREE = -11;
		final byte INVALID_SIZE = -12;
		// Allocate memory from OS free space, which is organized as link
		if (firstFreeTemporaryMemoryPointer == END_OF_LIST) {
			System.out.println("Error: There is no heap memory free");
			return NO_MEMORY_FREE; // ErrorNoFreeMemory is constant set to < 0
		}

		if (requestedSize < 0 || requestedSize > 2000) {
			System.out.println("Error: Invalid heap size");
			return INVALID_SIZE; // ErrorInvalidMemorySize is constant < 0
		}

		if (requestedSize == 1)
			requestedSize = 2; // Minimum allocated memory is 2 locations

		short Currentpointer = firstFreeTemporaryMemoryPointer;
		short Previouspointer = (short) memory[Currentpointer];
		while (Currentpointer != END_OF_LIST) {
			// Check each block in the link list until block with requested
			// memory size is found
			if (memory[Currentpointer + 1] == requestedSize) { // Found block
																// with
																// requested
																// size. Adjust
																// pointers
				if (Currentpointer == firstFreeTemporaryMemoryPointer) // first
																		// block
				{
					firstFreeTemporaryMemoryPointer = (short) memory[Currentpointer]; // first
																						// entry
																						// is
																						// pointer
																						// to
																						// next
																						// block
					memory[Currentpointer] = END_OF_LIST; // reset next pointer
															// in the allocated
															// block
					return Currentpointer; // return memory address
				} else // not first block
				{
					memory[Previouspointer] = memory[Currentpointer]; // point
																		// to
																		// next
																		// block
					memory[Currentpointer] = END_OF_LIST; // reset next pointer
															// in the allocated
															// block
					return Currentpointer; // return memory address
				}
			} else if (memory[Currentpointer + 1] > requestedSize) { // Found
																		// block
																		// with
																		// size
																		// greater
																		// than
																		// requested
																		// size
				if (Currentpointer == firstFreeTemporaryMemoryPointer) // first
																		// block
				{
					// move next block pointer
					memory[Currentpointer + requestedSize] = memory[Currentpointer];
					memory[Currentpointer + requestedSize + 1] = memory[Currentpointer + 1]
							- requestedSize;
					// address of reduced block
					firstFreeTemporaryMemoryPointer = (short) (Currentpointer + requestedSize);
					// reset next pointer in the allocated block
					memory[Currentpointer] = END_OF_LIST;

					return Currentpointer; // return memory address
				} else // not first block
				{
					memory[Currentpointer + requestedSize] = memory[Currentpointer]; // move
																						// next
																						// block
																						// pointer
					memory[Currentpointer + requestedSize + 1] = memory[Currentpointer + 1]
							- requestedSize;
					memory[Previouspointer] = Currentpointer + requestedSize; // address
																				// of
																				// reduced
																				// block
					memory[Currentpointer] = END_OF_LIST; // reset next pointer
															// in the allocated
															// block
					return Currentpointer; // return memory address
				}
			} else // small block
			{ // look at next block
				Previouspointer = Currentpointer;
				Currentpointer = (short) memory[Currentpointer];
			}
		} // end of while loop

		System.out.println("Error: No free stack memory error");
		return notOK; // ErrorNoFreeMemory is constant set to < 0
	} // end of allocate user memory module

	/**
	 * createProcess gets the null program ready to be run.
	 * 
	 */
	public short createProcess() throws FileNotFoundException {
		short priority = 1;
		String filename = "C:\\Users\\roody.audain\\workspace\\Concepts\\DecimalMachine"
				+ "\\src\\main\\resources\\Null_Process";

		// Allocate space for Process Control Block
		short PCBpointer = AllocateOSMemory(); // return value contains address
												// or error
		if (PCBpointer < FIRST_OS_MEMORY_ADDRESS
				|| PCBpointer > LAST_OS_MEMORY_ADDRESS) {
			System.out
					.println("Error: Memory alocation from OS free space has failed");
			return INVALID_ADDRESS;
		}

		// Initialize PCB. Set nextPCBlink to end of list, default priority,
		// Ready state, PID, rest 0
		initializePCB(PCBpointer);

		// Load the program
		short load;

		load = absoluteLoader(filename);

		if (load <= INVALID_ADDRESS || load >= FIRST_TEMPORARY_MEMORY_ADDRESS) {
			System.out
					.println("Error: The Program's origin address is out of range."
							+ " Program counter cannot be set correctly");
			return INVALID_ADDRESS;
		}

		byte PCindex = 19;
		memory[PCBpointer + PCindex] = load; // store PC value in the PCB of the
												// process

		// Store stack information in the PCB – SP, pointer, and size
		// not implemented

		memory[PCBpointer + priorityIndex] = priority; // Set priority

		// Insert PCB into Ready Queue according to the scheduling algorithm
		insertIntoReadyQueue(PCBpointer);

		return OK;
	} // end of CreateProcess method

	/**
	 * createProcess gets the program ready to be run.
	 * 
	 * @param filename
	 *            name of file in the local directory to be loaded
	 * @param priority
	 *            The higher the priority the more chances the process will get
	 *            resources
	 */
	public short createProcess(String filename, int priority)
			throws FileNotFoundException {
		// Allocate space for Process Control Block
		short PCBpointer = AllocateOSMemory(); // return value contains address
												// or error
		if (PCBpointer < FIRST_OS_MEMORY_ADDRESS
				|| PCBpointer > LAST_OS_MEMORY_ADDRESS) {
			System.out
					.println("Error: Memory alocation from OS free space has failed");
			return INVALID_ADDRESS;
		}

		// Initialize PCB. Set nextPCBlink to end of list, default priority,
		// Ready state, PID, rest 0
		initializePCB(PCBpointer);

		// Load the program
		short load;
		load = absoluteLoader(filename);

		if (load <= INVALID_ADDRESS || load >= FIRST_TEMPORARY_MEMORY_ADDRESS) {
			System.out
					.println("Error: The Program's origin address is out of range."
							+ " Program counter cannot be set correctly");
			return INVALID_ADDRESS;
		}

		byte PCindex = 19;
		memory[PCBpointer + PCindex] = load; // store PC value in the PCB of the
												// process

		// Store stack information in the PCB – SP, pointer, and size
		// not implemented

		memory[PCBpointer + priorityIndex] = priority; // Set priority

		// Insert PCB into Ready Queue according to the scheduling algorithm
		insertIntoReadyQueue(PCBpointer);

		return OK;
	} // end of CreateProcess module

	/**
	 * createProcessSystemCall gets a child process ready to receive a message.
	 * 
	 * @param filename
	 *            name of file in the local directory to be loaded
	 * @param priority
	 *            The higher the priority the more chances the process will get
	 *            resources
	 */
	void createProcessSystemCall(short pid) {
		// Allocate space for Process Control Block
		short PCBpointer = AllocateOSMemory(); // return value contains address
												// or error
		if (PCBpointer < FIRST_OS_MEMORY_ADDRESS
				|| PCBpointer > LAST_OS_MEMORY_ADDRESS) {
			System.out
					.println("Error: Memory alocation from OS free space has failed");
			gpr[0] = INVALID_ADDRESS;
		}

		// Initialize PCB. Set nextPCBlink to end of list, default priority,
		// Ready state, PID, rest 0
		setPCB(PCBpointer, pid);

		// Insert PCB into Ready Queue according to the scheduling algorithm
		byte status = insertIntoWaitingQueue(PCBpointer);

		if (status != OK)
			gpr[0] = status;
		else
			gpr[0] = OK;

	} // end of create child process system call module

	/**
	 * Sets RQ to the the next process control block and sets the NEXT_PCB_INEX
	 * of the process control block at the top of the control block (varible
	 * name: RQ) to be END_OF_LIST
	 * 
	 * @return process control block pointer of the process to be run
	 */
	public short selectFromRQ() {
		short PCBpointer = RQ; // PCBpointer points to first entry in RQ

		if (memory[PCBpointer + NEXT_PCB_INDEX] != END_OF_LIST)
			// Set RQ to point to the next process control block
			RQ = (short) memory[PCBpointer + NEXT_PCB_INDEX];

		// Set next point to EOL in the PCB
		memory[PCBpointer + NEXT_PCB_INDEX] = END_OF_LIST;

		restoreCPU(PCBpointer);

		return PCBpointer;
	} // end of select process from the ready queue module

	void restoreCPU(short PCBpointer) {
		System.out.println("Now running process #"
				+ memory[PCBpointer + PROCESS_ID_INDEX]);
		// PCBpointer is assumed to be correct

		// Copy CPU register values from PCB into the CPU registers
		byte gprPCBindex = 10;
		gpr[0] = memory[PCBpointer + gprPCBindex++];
		gpr[1] = memory[PCBpointer + gprPCBindex++];
		gpr[2] = memory[PCBpointer + gprPCBindex++];
		gpr[3] = memory[PCBpointer + gprPCBindex++];
		gpr[4] = memory[PCBpointer + gprPCBindex++];
		gpr[5] = memory[PCBpointer + gprPCBindex++];
		gpr[6] = memory[PCBpointer + gprPCBindex++];
		gpr[7] = memory[PCBpointer + gprPCBindex++];
		// Restore SP and PC
		sp = (short) memory[PCBpointer + gprPCBindex++];
		pc = (short) memory[PCBpointer + gprPCBindex];

		// Set system mode to User mode
		// byte userMode = 2;
		// psr = userMode; // UserMode is 1, OSMode is 0.

		printRQ();
	} // end of dispatcher module

	public void saveContext(short PCBpointer) {
		byte gprPCBindex = 10;
		memory[PCBpointer + gprPCBindex++] = gpr[0];
		memory[PCBpointer + gprPCBindex++] = gpr[1];
		memory[PCBpointer + gprPCBindex++] = gpr[2];
		memory[PCBpointer + gprPCBindex++] = gpr[3];
		memory[PCBpointer + gprPCBindex++] = gpr[4];
		memory[PCBpointer + gprPCBindex++] = gpr[5];
		memory[PCBpointer + gprPCBindex++] = gpr[6];
		memory[PCBpointer + gprPCBindex++] = gpr[7];
		// Restore SP and PC
		memory[PCBpointer + gprPCBindex++] = sp;
		memory[PCBpointer + gprPCBindex] = pc;

		return;
	} // end of SaveContext() function

	// There are six different addressing modes in the HYPO machine. They are
	// explained below.
	final byte INVALID = 0; // Mode digit is not used in the instruction
	final byte REGISTER = 1; // The specified general purpose register contains
								// the operand value.
	// Register contains the address of the operand. Operand value is in the
	// main memory.
	final byte REGISTERDEFERRED = 2;

	/*
	 * Register contains the address of the operand. Operand value is in the
	 * main memory. register content is incremented by 1 after fetching the
	 * operand value.
	 */
	final byte AUTOINCREMENT = 3;

	/*
	 * Register content is decremented by 1. decremented value is the address of
	 * the operand. Operand value is in the main memory.
	 */
	final byte AUTODECREMENT = 4;

	/*
	 * Next word contains the address of the operand. Operand value is in the
	 * main memory. GPR is not used. Use PC to get the address.
	 */
	final byte DIRECT = 5;

	/*
	 * Next word contains the operand value. Operand value is in the main memory
	 * as part of the instruction. GPR is not used. Address is in the PC.
	 */
	final byte IMMEDIATE = 6;

	final byte INVALID_MODE = -6; // invalid mode
	final byte UNSUCCESSFUL_OPERAND_FETCH = -9; // Operand fetch was
												// unsuccessful error code

	final byte FIRST_USER_MEMORY_ADDRESS = 0;

	/**
	 * Task Description: fetches an operands
	 * 
	 * @param OpMode
	 *            Operand mode value
	 * @param OpReg
	 *            Operand GPR value
	 * 
	 *            Output Parameters OpAddress Address of operand OpValue Operand
	 *            value when mode and GPR are valid
	 *
	 * @return Operand including the operands address (if valid) and value. if
	 *         operand is invalid then it's address is -1 and value is -616.
	 */
	Operand fetchOperand(byte addressingMode, // Operand mode, input parameter
			byte register) // operand GPR, input parameter
	{
		final short INVALID_VALUE = 616;

		Operand op = new Operand(INVALID); // return operand
		// Fetch operand value based on the operand mode
		switch (addressingMode) {
		case INVALID:
			op = new Operand(INVALID_MODE);

			break;

		case REGISTER:
			op = new Operand(INVALID_ADDRESS, gpr[register]);

			break;

		case REGISTERDEFERRED: // Operand address is in the register
			if (gpr[register] >= FIRST_USER_MEMORY_ADDRESS
					&& gpr[register] <= LAST_OS_MEMORY_ADDRESS) {
				op = new Operand((short) gpr[register],
						memory[(int) gpr[register]]);
			} else {
				System.out
						.println("Error: Invalid address found fetching operand in "
								+ "autoincrement mode");
				op = new Operand(INVALID_ADDRESS);
			}

			break;

		// Increments register by one after fetching address
		case AUTOINCREMENT:
			op = new Operand((short) gpr[register], (short) gpr[register]);
			gpr[register] += 1; // Increment register value by 1

			break;

		case AUTODECREMENT: // Autodecrement mode
			gpr[register] -= 1; // Decrement register value by 1
			// Operand address is in the register. Operand value is in memory
			if (gpr[register] > FIRST_USER_MEMORY_ADDRESS
					&& gpr[register] <= LAST_OS_MEMORY_ADDRESS) {
				op = new Operand((short) gpr[register], INVALID_VALUE);

			} else {
				System.out.println("Error: Invalid address");
				op = new Operand(INVALID_ADDRESS);
			}

			break;

		case DIRECT: // Direct mode
			// Operand address is memory. Operand value is in memory
			if (memory[pc] > FIRST_USER_MEMORY_ADDRESS
					&& memory[pc] <= LAST_OS_MEMORY_ADDRESS) {
				op = new Operand((short) memory[pc], memory[(int) memory[pc]]);
			} else {
				System.out.println("Error: Invalid address");
				op = new Operand(INVALID_ADDRESS);
			}

			this.pc++;

			break;

		case IMMEDIATE: // Immediate mode
			if (pc > FIRST_USER_MEMORY_ADDRESS && pc <= LAST_OS_MEMORY_ADDRESS) {
				op = new Operand(pc, memory[pc]); // operand value is in memory
				this.pc++;// increment program counter by one
			} else {
				System.out.println("Error: Invalid address");
				op = new Operand(INVALID_ADDRESS);
			}

			break;

		default: // Invalid mode
			System.out.println("Error: Fetched operand has unkown mode.");
			op = new Operand(INVALID_MODE);

		} // end of switch OpMode

		return op; // return successful or unsuccessful operand fetch
	} // end of FetchOperand module

	/**
	 * The execute program function executes the program or programs that have
	 * already loaded in the main memory. This function executes one instruction
	 * at a time pointed using a program counter by performing (a) fetch
	 * instruction cycle, (b) decode instruction cycle, and (c) execute
	 * instruction cycle. It performs all possible error checking such as
	 * invalid memory address reference, invalid mode, and division by zero
	 * error. After executing each instruction, it increases the clock by an
	 * instruction execution time. It returns the status of execution as an byte
	 * value.
	 * 
	 * @return An error code, 10 for halt or system call code. Error codes:
	 *         invalid address = -1 invalid mode = -6 divide by zero = -8
	 *         unsuccessful operand fetch = -9 invalid operation code = -10
	 */
	public short executeProgram() {
		short mar; // Memory Address Register
		int mbr; // Memory Buffer Register
		int ir; // Instruction register

		final byte DIVIDE_BY_ZERO = -8;
		final byte INVALID_OPCODE = -10;

		short ticks = 0;

		byte systemCallCode;
		while (ticks < TIME_SLICE) {
			// Fetch (read) the first word of the instruction pointed by PC
			if (pc <= LAST_OS_MEMORY_ADDRESS) {
				mar = pc;
				/*
				 * Advances program counter by 1 to the address of next
				 * instuction.
				 */
				pc++;
				mbr = (int) memory[mar];
			} else {
				System.out
						.println("Error: The program is leaving the designated program memory");
				return INVALID_ADDRESS;
			}

			/*
			 * Decode the first word of the instruction into opcode, mode and
			 * gpr using integer division and modulo operators example machine
			 * code: oc M R M R 5 4 3 2 1
			 */
			ir = mbr;
			int opcode = ir / 10000; // Integer division, gives opcode
			if (opcode > 12 || opcode < 0) {
				System.out.println("Error: Invalid operation code");
				return INVALID_OPCODE;
			}
			// Modulo gives machine code line minus the opcode

			short remainder = (short) (ir % 10000);
			// assigns the addressing mode for the left hand operand
			byte operand1Mode = (byte) (remainder / 1000);
			if (operand1Mode > 6 || operand1Mode < 0) {
				System.out.println("Error: Operand one has invalid mode");
				return INVALID_MODE;
			}

			/*
			 * gets the machine code minus the opcode and the addressing mode
			 * for the left hand operand
			 */
			remainder %= 1000;
			byte operand1GPR = (byte) (remainder / 100); // assigns the left
															// hand operand
			if (operand1GPR > LAST_GPR || operand1GPR < 0) {
				System.out
						.println("Error: invalid general purpose register address");
				return INVALID_ADDRESS;
			}
			// gets the machine code for the the right hand operand and
			// addressing mode
			remainder %= 100;
			// assigns the addressing mode for the right hand operand
			byte operand2Mode = (byte) (remainder / 10);
			if (operand2Mode > 6 || operand2Mode < 0) {
				System.out.println("Error: Operand two has invalid mode");
				return INVALID_MODE;
			}
			byte operand2GPR = (byte) (remainder % 10); // assigns the right had
														// operand
			if (operand2GPR > LAST_GPR || operand2GPR < 0) {
				System.out
						.println("Error: Invalid general purpose register address");
				return INVALID_ADDRESS;
			}
			// Execute Cycle
			// In the execute cycle, fetch operand value(s) based on the opcode
			// since different opcode has different number of operands

			Operand operand1 = fetchOperand(operand1Mode, operand1GPR);
			Operand operand2 = fetchOperand(operand2Mode, operand2GPR);

			switch (opcode) {
			case 0: // halt
				System.out.println("\nThe CPU has reached a halt "
						+ "instruction.");
				System.out.println("Dumping CPU registers and used "
						+ "temporary memory.");
				dumpMemory();

				// Increment overall clock and this timeslice
				clock += 12;
				ticks += 12;

				return HALTED;

			case 1: // add operation code
				if (operand1.status < 0 || operand2.status < 0) {
					System.out.println("Error: Unsuccessful operand fetch "
							+ "for add instruction");
					return UNSUCCESSFUL_OPERAND_FETCH;
				}

				/*
				 * Add the operand values and store the result into Operand
				 * one's address
				 */
				long result = operand1.value + operand2.value;

				if (operand1Mode == REGISTER)
					gpr[operand1GPR] = result;

				else if (operand1Mode == IMMEDIATE) {
					System.out
							.println("Error: Operand one's mode cannot be immediate");
					return INVALID_MODE;
				}

				/*
				 * Store result in memory location for all other valid
				 * addressing mode for the add operation.
				 */
				else
					memory[operand1.address] = result;

				// Increment overall clock and this timeslice
				clock += 3;
				ticks += 3;

				break;

			case 2: // Subtract
				if (operand1.status < 0 || operand2.status < 0) {
					System.out.println("Error: Unsuccessful operand fetch for "
							+ "subtract instruction");
					return UNSUCCESSFUL_OPERAND_FETCH;
				}

				// Subtract the operand values and store the result into Op1
				// location
				result = operand1.value - operand2.value;

				if (operand1Mode == REGISTER)
					gpr[operand1GPR] = result;

				else if (operand1Mode == IMMEDIATE) {
					System.out
							.println("Error: Operand one's mode cannot be immediate");
					return INVALID_MODE;
				}

				/*
				 * Store result in memory location for all other valid
				 * addressing mode for the subtract operation.
				 */
				else
					memory[operand1.address] = result;

				// Increment overall clock and this timeslice
				clock += 3;
				ticks += 3;

				break;

			case 3: // Multiply
				if (operand1.status < 0 || operand2.status < 0) {
					System.out
							.println("Error: Unsuccesful operand fetch for multiply instruction");
					return UNSUCCESSFUL_OPERAND_FETCH;
				}

				// Add the operand values and store the result into Op1 location
				result = operand1.value * operand2.value;

				if (operand1Mode == REGISTER)
					gpr[operand1GPR] = result;

				else if (operand1Mode == IMMEDIATE) {
					System.out
							.println("Error: Operand one's mode cannot be immediate");
					return INVALID_MODE;
				}
				/*
				 * Store result in memory location for all other valid
				 * addressing mode for the multiply operation.
				 */
				else
					memory[operand1.address] = result;

				// Increment overall clock and this timeslice
				clock += 6;
				ticks += 6;

				break;

			case 4: // Divide
				if (operand1.status < 0 || operand2.status < 0) {
					System.out.println("Error: Unsuccessful operand fetch for"
							+ "divide instruction");
					return UNSUCCESSFUL_OPERAND_FETCH;
				}

				if (operand1.value == 0) {
					System.out.println("Error: Cannot divide by zero");
					return DIVIDE_BY_ZERO;
				}

				// Divide the operand values and store the result into Op1
				// location
				result = operand1.value / operand2.value;

				if (operand1Mode == REGISTER)
					gpr[operand1GPR] = result;

				else if (operand1Mode == IMMEDIATE) {
					System.out
							.println("Error: Operand one's mode cannot be immediate");
					return INVALID_MODE;
				}

				/*
				 * Store result in memory location for all other valid
				 * addressing mode for the divide operation.
				 */
				else
					memory[operand1.address] = result;

				// Increment overall clock and this timeslice
				clock += 6;
				ticks += 6;

				break;

			case 5: // Move
				if (operand1.status < 0 || operand2.status < 0) {
					System.out
							.println("Error: Unsuccessful operand fetch for a"
									+ "move instruction");
					return UNSUCCESSFUL_OPERAND_FETCH;
				}

				if (operand1Mode == REGISTER)
					gpr[operand1GPR] = operand2.value;

				else if (operand1Mode == IMMEDIATE) {
					System.out
							.println("Error: Operand one's mode cannot be immediate");
					return INVALID_MODE;
				}

				/*
				 * Store result in memory location for all other valid
				 * addressing mode for the divide operation.
				 */
				else
					memory[operand1.address] = operand2.value;

				// Increment overall clock and this timeslice
				clock += 2;
				ticks += 2;

				break;

			case 6: // Branch or jump instruction
				if (pc < FIRST_USER_MEMORY_ADDRESS
						|| pc > LAST_OS_MEMORY_ADDRESS) {
					System.out.println("Error: Invalid address");
					return INVALID_ADDRESS;
				}

				pc = (short) memory[pc];

				// Increment overall clock and this timeslice
				clock += 2;
				ticks += 2;

				break;

			case 7: // Branch on minus
				if (pc < FIRST_USER_MEMORY_ADDRESS
						|| pc > LAST_USER_MEMORY_ADDRESS) {
					System.out.println("Error: Invalid address");
					return INVALID_ADDRESS;
				}

				if (operand1.value < 0)
					pc = (short) memory[pc]; // Store branch address in the PC

				else
					pc++; // No branch, skip branch address to go to next
							// instruction

				// Increment overall clock and this timeslice
				clock += 4;
				ticks += 4;

				break;

			case 8: // Branch on plus
				if (pc < FIRST_USER_MEMORY_ADDRESS
						|| pc > LAST_OS_MEMORY_ADDRESS) {
					System.out.println("Error: Invalid address");
					return INVALID_ADDRESS;
				}

				// Store branch address in the PC is true
				if (operand1.value > 0)
					pc = (short) memory[pc];

				// No branch, skip branch address to go to next instruction
				else
					pc++;

				// Increment overall clock and this timeslice
				clock += 4;
				ticks += 4;

				break;

			case 9: // branch on zero
				if (pc < FIRST_USER_MEMORY_ADDRESS
						|| pc > LAST_OS_MEMORY_ADDRESS) {
					System.out.println("Error: Invalid address");
					return INVALID_ADDRESS;
				}

				// Store branch address in the PC is true
				if (operand1.value == 0)
					pc = (short) memory[pc];
				// No branch, skip branch address to go to next instruction
				else
					pc++;

				// Increment overall clock and this timeslice
				clock += 4;
				ticks += 4;

				break;

			case 10: // Push
				if (sp < FIRST_TEMPORARY_MEMORY_ADDRESS
						|| sp >= FIRST_OS_MEMORY_ADDRESS) {
					System.out.println("Error: Invalid stack address");
					return INVALID_ADDRESS;
				}

				memory[sp] = operand1.value;
				sp++;

				// Increment overall clock and this timeslice
				clock += 2;
				ticks += 2;

				break;

			case 11: // Pop
				if (operand1Mode == REGISTER)
					gpr[operand1GPR] = memory[sp];

				else if (operand1Mode == IMMEDIATE) {
					System.out
							.println("Error: Operand one's mode cannot be immediate");
					return INVALID_MODE;
				}

				/*
				 * Store result in memory location for all other valid
				 * addressing mode for the divide operation.
				 */
				else
					memory[operand1.address] = memory[sp];

				sp--;

				// Increment overall clock and this timeslice
				clock += 2;
				ticks += 2;

				break;

			case 12: // System call
				if (operand1.status < 0) {
					System.out
							.println("Error: Unsuccessful operand fetch for system call");
					return UNSUCCESSFUL_OPERAND_FETCH;
				}

				systemCallCode = systemCall((byte) operand1.value);

				// Increment overall clock and this timeslice
				clock += 12;
				ticks += 12;

				return systemCallCode;
			} // end of opcode switch statement
		} // end of while loop

		System.out.println("Time slice complete");
		System.out.println("Dumping CPU registers and used temporary memory.");
		dumpMemory();
		return TIME_SLICE; // Time slice expired
	} // end of execute program module

	void ISRrunProgramInterrupt() throws FileNotFoundException {
		final int PROGRAM_NOT_FOUND = -5;
		// Prompt and read filename;
		try {
			Scanner k = new Scanner(System.in);
			System.out.println("Enter an executable's file name");
			String filename = k.nextLine();
			k.close();
			createProcess(filename, DEFAULT_PRIORITY);
		} catch (FileNotFoundException e) {
			logger.error("Can't interrupt: Program cannot be found in "
							+ "working directory");
			gpr[0] = PROGRAM_NOT_FOUND;
		}
	} // end of ISRrunProgram() function

	/**
	 * possible interrupts: 0 – no interrupt
	 * 
	 * 1 – run program
	 * 
	 * 2 – shutdown system
	 * 
	 * 3 – Input operation completion (io_getc)
	 * 
	 * 4 – Output operation completion (io_putc
	 * 
	 * @return Error code
	 */
	public byte CheckAndProcessInterrupt() throws FileNotFoundException {
		// Prompt and read interrupt ID

		System.out.print("\nProcessing interrupt id: " + gpr[0]);

		// Process interrupt
		byte status = 0;// 0 == Success
		switch ((byte) gpr[0]) {
		case 0: // No interrupt
			System.out.println(" No interrupt");
			break;

		case 1: // Run program
			ISRrunProgramInterrupt();
			break;

		case 2: // Shutdown system
			ISRshutdownSystem(); // Terminate processes in RQ and WQ
			break;

		case 3: // Input operation completion – io_getc
			// ISRinputCompletionInterrutp();
			break;

		case 4: // Output operation completion – io_putc
			// ISRoutputCompletionInterrupt();
			break;

		default: // Invalid Interrupt ID
			System.out.println("Error: Invalid interrupt ID message");
			status = -13; // -13 == Invalid interrrupt ID
			break;
		} // end of switch InterruptID

		return status;
	} // end of CheckAndProcessInterrupt function

	/**
	 * Sets the firstFreeTemporaryMemoryPointer to deallocate temporary memory.
	 * 
	 * @param pointer
	 *            First address of the process's temporary memory block
	 * @param size
	 *            Amount of allocated temporary memory.
	 * @return Error Code for invalid address or "OK" code if no errors.
	 */
	byte freeTemporaryMemory(short pointer, short size) {
		if (size == 1)
			size = 2; // minimum allocated size

		if (pointer < FIRST_TEMPORARY_MEMORY_ADDRESS
				|| pointer + size >= FIRST_OS_MEMORY_ADDRESS) {
			System.out
					.print("Error: The free temporary memory method has failed due to "
							+ "receiving an invalid address ");
			return INVALID_ADDRESS; // INVALID_ADDRESS is a constant set to -1
		}
		/*
		 * Check for invalid size and minimum size. Check for minimum allocated
		 * size, which is 2 even if user asks for 1 location
		 */
		short totalSizeOfTemporaryMemory = 2000;
		if (size < 1 || size > totalSizeOfTemporaryMemory) {
			System.out.print("Error: Memory not returned.");
			return INVALID_ADDRESS; // INVALID_ADDRESS = -1
		}

		// Make allocated memory free. Insert at the beginning of the link list
		final byte nextLink = 0;
		memory[pointer + nextLink] = firstFreeTemporaryMemoryPointer; // freeHeapMemory
																		// is
																		// global
																		// variable
																		// set
																		// to
																		// EOL
		// Set to proper value in InitializeSystem function
		// NextLink is constant set to 0, first value
		memory[pointer + 1] = size; // set size of free block in the second word
									// of the free block
		firstFreeTemporaryMemoryPointer = pointer; // Set freeHeapMemory point
													// to the returned block

		return OK;

	} // end of free temporary memory module

	/**
	 * Dis-allocates process control block when a program has halted.
	 * 
	 * @param ptr
	 *            Memory address of the process control block to be
	 *            relinquished.
	 * @return Error code of successful return of memory code (OK = 0).
	 */
	byte freeOSmemory(short ptr) // return value contains OK or error code
	{
		if (ptr < FIRST_OS_MEMORY_ADDRESS || ptr > LAST_OS_MEMORY_ADDRESS) // MaxMemoryAddress
																			// is
																			// a
																			// constant
																			// set
																			// to
																			// 9999
		{
			System.out.println("Error: freeOSMemory has failed due to "
					+ "pointing to an invalid address ");
			return INVALID_ADDRESS; // ErrorInvalidMemoryAddress is constantset
									// to < 0
		}
		if (ptr + PCB_SIZE > LAST_OS_MEMORY_ADDRESS) { // Invalid size
			System.out.print("Error: An invalid address was referenced. "
					+ "OS memory was not returned.");
			return INVALID_ADDRESS; // All error codes are less than 0.
		}

		// Return memory to OS free space. Insert at the beginning of the link
		// list
		memory[ptr + NEXT_PCB_INDEX] = PCBmemoryFreeList; // PCBmemoryFreeList
															// is global
															// variable set to
															// EOL
		// Set to proper value in InitializeSystem function
		// NextLink is constant set to 0, first value

		memory[ptr + 1] = PCB_SIZE; // set size of free block in the second word
									// of the free block
		PCBmemoryFreeList = ptr; // Set PCBmemoryFreeList point to the returned
									// block

		return OK;
	} // end of free operating system memory module

	void allocateTemporaryMemorySystemCall() {
		// Allocate memory from user free list
		// Return status from the function is either the address of allocated
		// memory or an error code
		short requestedSize = (short) gpr[2];

		gpr[1] = allocateTemporaryMemory(requestedSize); // Requested memory
															// size is in GPR2
		if (gpr[1] < 0)
			gpr[0] = gpr[1]; // Set GPR0 to have the error status

		else
			gpr[0] = OK;

		System.out.println("Mem_alloc system call, Status Code: " + gpr[0]
				+ ", GPR [1]: " + gpr[1] + ", Requested memory slots: "
				+ gpr[2]);

	} // end of memory allocation system call module

	void MemFreeSystemCall() {
		// Return dynamically allocated memory to the user free list
		// GPR[1] has memory address and GPR[2] has memory size to be released
		// Return status in GPR[0]

		short size = (short) gpr[2];

		// check for size out of range

		// Check size of 1 and change it to 2
		if (size == 1)
			size = 2;

		gpr[0] = freeTemporaryMemory((short) gpr[1], size);

		System.out.println("Display Mem_free system call, GPR[0]: " + gpr[0]
				+ ", GPR[1]: " + gpr[1] + ", GPR[2]: " + gpr[2]);
	} // end of free memory system call module

	/**
	 * Input parameter values for each system call is passed through GPRs.
	 * Output parameter values are returned by the operating system through
	 * GPRs. Set the system mode to OS mode on entry into the system call
	 * function. Before return, set the system mode to user mode. Implement each
	 * system call as a separate function to make the code modular and easy to
	 * manage. Inside each function, access input parameter values from the GPRs
	 * according to the system call specification in MTOPS. Also, set the output
	 * parameter values in the GPRs according to the system call specification
	 * in MTOPS. Print error messages inside the function, when an error occurs.
	 * 
	 * @param actionCode
	 *            The type of system call
	 * @return ?????????????????????????????????????????????????????????????
	 */
	byte systemCall(byte actionCode) {
		// byte operatingSystemMode = 0;
		// byte applicationMode = 1;

		// psr = operatingSystemMode; // Set system mode to OS mode

		byte status = OK;

		switch (actionCode) {
		case 1: // Create process – user process is creating a child process.
			// This is not same as run program interrupt
			createProcessSystemCall((short) gpr[1]);

			break;

		case 2:
			// ProcessDeleteSystemCall();

			break;

		case 4: // Dynamic memory allocation: Allocate user free memory system
				// call
			allocateTemporaryMemorySystemCall();

			// psr = applicationMode;
			return PROCEED;

		case 5: // Free dynamically allocated user memory system call
			MemFreeSystemCall();

			break;

		case 6:
			receiveMessage((short) 5);

		case 7:
			msg_qsendSystemCall(gpr[1], gpr[2]);

			break;

		default: // Invalid system call ID

			System.out.println("Error: Invalid system call ID");
			status = notOK;
			break;
		} // end of systemCallCode switch statement
		return status;
	} // end of system call module

	final byte reasonForWaitingIndex = 3;
	final byte waitingForMessageCode = 7;

	/**
	 * insertIntoWaitingQueue populates the waiting queue with process which
	 * need to wait. Process are always inserted at the bottom of the waiting
	 * queue.
	 * 
	 * @param PCBptr
	 *            memory address of the process's process control block.
	 */
	byte insertIntoWaitingQueue(short PCBptr) {
		short previousPointer = END_OF_LIST;
		short currentPointer = WQ;

		// Check for invalid PCB memory address
		if (PCBptr < FIRST_OS_MEMORY_ADDRESS || PCBptr > LAST_OS_MEMORY_ADDRESS) {
			System.out
					.println("Error: The process control block pointer is invalid");
			return INVALID_ADDRESS; // ErrorInvalidMemoryAddress is constantset
									// to < 0
		}

		memory[PCBptr + STATE_INDEX] = WAITING_STATE; // set state to ready
														// state
		memory[PCBptr + NEXT_PCB_INDEX] = END_OF_LIST; // set next pointer to
														// end of list

		if (WQ == END_OF_LIST) // RQ is empty
		{
			WQ = PCBptr;
			return OK;
		}

		while (currentPointer != END_OF_LIST) {
			previousPointer = currentPointer;
			currentPointer = (short) memory[currentPointer + NEXT_PCB_INDEX];
		} // end of while loop
		memory[previousPointer + NEXT_PCB_INDEX] = PCBptr;

		return OK;
	} // end of insert process into waiting queue module

	final byte INVALID_PID = -30;

	/**
	 * @param PID
	 *            The process identification number of the process to be found.
	 */
	short searchAndRemovePCBfromWaitingQueue(long pid) {
		boolean found = false;
		short ptr = WQ;
		short previousPCBptr = END_OF_LIST;
		while (memory[ptr] != END_OF_LIST) {
			if (memory[ptr + PROCESS_ID_INDEX] == pid) // if found the right
														// process control block
			{
				found = true;
				if (memory[ptr + reasonForWaitingIndex] == waitingForMessageCode) {
					// remove pcb from wq
					memory[ptr + NEXT_PCB_INDEX] = END_OF_LIST;
					memory[previousPCBptr + NEXT_PCB_INDEX] = END_OF_LIST;
				}
			} else {
				previousPCBptr = ptr;
				ptr = (short) memory[ptr + NEXT_PCB_INDEX];
			}
		} // end while not end of list
		if (found)
			return ptr;
		else {
			System.out.println("Error: Invalid process identification number");
			return INVALID_PID;
		}
	}

	final byte msgQstrtIndex = 7, msgCountIndex = 9;

	byte msg_qsendSystemCall(long receivepid, long messageStartAddress) {
		final byte gpr0index = 10, gpr2index = 12;

		short ptr = searchAndRemovePCBfromWaitingQueue(receivepid);
		if (ptr == INVALID_PID)
			return INVALID_PID;

		if (ptr != END_OF_LIST) {
			// check if process is waiting for msg
			if (memory[ptr + reasonForWaitingIndex] == waitingForMessageCode) { // deliver
																				// msg
				memory[ptr + gpr2index] = OK;
				memory[ptr + gpr0index] = OK;
				memory[ptr + STATE_INDEX] = READY_STATE;
				insertIntoReadyQueue(ptr);
			} else { // put message into message queue
				memory[(int) (memory[ptr + msgQstrtIndex] + memory[ptr
						+ msgCountIndex])] = msgQstrtIndex;
				memory[ptr + msgCountIndex]++;
				insertIntoWaitingQueue(ptr);
			}
		}
		return OK;
	} // end send mesg system call

	byte receiveMessage(short runningpcbptr) {
		/*
		 * check running process msg count to see whether there is a message in
		 * the msg queue
		 */
		if (memory[(int) (runningpcbptr + msgCountIndex)] > 0) { // set gpr 2
			gpr[2] = memory[(short) (memory[runningpcbptr + msgQstrtIndex
					+ msgCountIndex])];
			gpr[0] = OK;
			// Decrement mesg count by 1
			// move the remaining messages forward by one position

			// decrement msg count by 1.
		} else { // no msg in the queue
					// insert process into wq
			insertIntoWaitingQueue(runningpcbptr);
			// set state to waiting
			// set reason to waiting for message
		}
		return OK;
	}

	/**
	 * Frees memory like back before program started.
	 * 
	 * @param PCBpointer
	 *            Process control block pointer of halted program.
	 */
	public void terminateProcess(short PCBpointer) {
		final byte temporaryMemoryAddressIndex = 11;
		final byte temporaryMemorySizeIndex = 12;

		// Recover stack memory
		freeTemporaryMemory((short) memory[PCBpointer
				+ temporaryMemoryAddressIndex], (short) memory[PCBpointer
				+ temporaryMemorySizeIndex]);

		// Makes a process control block available
		freeOSmemory(PCBpointer);
	} // end of TerminateProcess module

	public void ISRshutdownSystem() {
		// Terminate processes in RQ
		short pointer = RQ;
		while (pointer != END_OF_LIST) {
			RQ = (short) memory[pointer];
			// TerminateProcess(pointer);
			pointer = RQ;
		}

		// Terminate processed in WQ
		pointer = WQ;
		while (pointer != END_OF_LIST) {
			WQ = (short) memory[pointer];
			terminateProcess(pointer);
			pointer = WQ;
		}

		return;
	} // end of ISR shutdown system module

} // End of Homework class

class Operand {
	short address;
	long value;
	byte status;

	public Operand(short address, long value) {
		this.address = address;
		this.value = value;
		this.status = 0;// 0 == Successful operand creation
	}

	public Operand(byte status)// unseccessful operand creation
	{
		this.status = status; // stores error code.
	}
}