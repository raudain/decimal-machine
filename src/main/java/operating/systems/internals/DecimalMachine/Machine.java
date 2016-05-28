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
public class Machine implements Stopped_Execution_Reason_Code {

	// private byte psr; // Processor status register
	private long clock = 0; // system clock

	private final byte LAST_GPR = 7; // size of cpu register minus one

	private final byte END_OF_LIST_MARKER = Application_Memory.END_OF_LIST_INDICATOR;
	private short RqPointer = END_OF_LIST_MARKER; // Ready Queue set to empty
													// list
	private short WQ = END_OF_LIST_MARKER;

	private final Application_Memory AM;

	// Process Control Block memory free list set to be empty;

	private static final Logger logger = LogManager.getLogger("Machine");

	private final Process_Control_Block PCB = new Process_Control_Block();

	private final char READY_STATE_INDICATOR;
	
	private byte priority;
	
	private byte processId;
	
	private final Central_Processing_Unit CPU;
	
	private final Operating_System_Memory OSM;

	public Machine() {

		CPU = new Central_Processing_Unit();
		AM = new Application_Memory();
		OSM = new Operating_System_Memory();
		
		READY_STATE_INDICATOR = 'R';
		priority = 1;
		processId = 1;

	} // end of constructor

	final byte INVALID_ADDRESS = -1;

	public String addPath(String fileName) {

		String directory = "target/classes/";
		String relativePath = directory + fileName;

		return relativePath;
	}

	/**
	 * Open the file containing HYPO machine program and load the content into
	 * user memory. On successful load, return the PC value in the last line. On
	 * failure, return appropriate error code
	 * 
	 * @param filename
	 *            Name of the executable file to be loaded into main memory
	 * @throws FileNotFoundException
	 */
	public short absoluteLoader(String fileName) throws FileNotFoundException {

		Scanner machineCode = null;
		String s = addPath(fileName);
		try {
			File file = new File(s);
			machineCode = new Scanner(file);
		} catch (FileNotFoundException e) {
			logger.error("Error in absoluteLoader: Program cannot be found in " + "working directory");
			e.printStackTrace();
			throw e;
		}

		// Read from file until end of program or end of file and
		// store program in HYPO memory

		logger.info("Loading " + fileName + " into memory");
		while (machineCode.hasNextLine()) {
			// read address, content from file
			short address = machineCode.nextShort();

			// valid address
			if (address > INVALID_ADDRESS && address < AM.getFirstTemporaryMemoryAddress())
				// store program
				AM.load(address, machineCode.nextInt());

			else if (address <= INVALID_ADDRESS) {
				logger.info("The loader has reach the end of " + fileName + "'s code");
				short origin = machineCode.nextShort();
				machineCode.close();
				return origin;
			}
		}
		// End of file encountered without end of program
		// display end of file encountered without EOP error message;
		machineCode.close();
		logger.error("Error: end of file encountered without EOP");
		final byte NO_END_OF_PROGRAM = -4; // Missing end of program indicator
		return NO_END_OF_PROGRAM; // end of file encountered without EOP error
									// code
	} // end of absoluteLoader module

	public String printHeader() {
		return "Address\tIndex\tValue";
	}

	public void printRq() {
		// Walk thru the queue from the given pointer until end of list
		// Print each PCB as you move from one PCB to the next
		short currentPointer = RqPointer;
		logger.info("Printing all process control blocks in ready queue:");
		String header = printHeader();
		logger.info(header);
		while (currentPointer != END_OF_LIST_MARKER) {
			short nextPointer = (short) AM.fetch((short) (currentPointer + PCB.getNextPcbIndex()));

			if (nextPointer == currentPointer) {
				logger.error("The link list for the ready queue " + "has a self reference!");
				return;
			}

			byte PCBindex;
			for (PCBindex = 0; PCBindex < PCB.getSize(); PCBindex++)
				logger.info(currentPointer + "\t" + PCBindex + "\t" + AM.fetch(currentPointer++));

			currentPointer = nextPointer;

			System.out.println("Printing next process control block:");
		} // end of while loop

		System.out.println("The end of the ready queue has been reached.");
	} // end of PrintPCB module

	final byte OK = 0;

	public boolean isReadyQueueEmpty() {

		return RqPointer == END_OF_LIST_MARKER;
	}

	/**
	 * Inserts a process into the ready queue
	 * 
	 * @param PCBpointer
	 *            PCB: a data structure in the operating system kernel
	 *            containing the information needed to manage a particular
	 *            process. The PCB is "the manifestation of a process in an
	 *            operating system".
	 * 
	 * @return
	 */
	public void insertIntoReadyQueue(short pcbPointer) {
		// Insert PCB according to the Priority Round Robin algorithm
		// Use priority in the PCB to find the correct place to insert.
		short previousPointer = END_OF_LIST_MARKER;
		short tempPointer = RqPointer;

		// Check for invalid PCB memory address
		if (Application_Memory.withinOsMemory(pcbPointer)) {
			logger.error("The process control block pointer is invalid");
			return;
		}

		// set state to ready state
		PCB.setState(OSM, pcbPointer, READY_STATE_INDICATOR);

		// set next pointer to end of list
		PCB.setNextPcbPointer(OSM, pcbPointer, END_OF_LIST_MARKER);

		if (isReadyQueueEmpty()) {
			RqPointer = pcbPointer;
			return;
		}

		// Walk thru RQ and find the place to insert
		// PCB will be inserted at the end of its priority
		byte priorityIndex = PCB.getPriorityIndex();
		while (tempPointer != END_OF_LIST_MARKER) {
			
			// priortity of the program being inserted
			Byte priority1 = PCB.getPriority(OSM, pcbPointer);
			
			// priortity of the program that was already inserted
			Byte priority2 = PCB.getPriority(OSM, tempPointer);
			
			// if a memory location to insert is found
			if (priority1 > priority2) {
				if (tempPointer == END_OF_LIST_MARKER) {
					// Enter PCB in the front of the list as first entry
					PCB.setNextPcbPointer(OSM, pcbPointer, RqPointer);
					RqPointer = pcbPointer;
					logger.info("[PID: #" + PCB.getProcessId(OSM, pcbPointer)
							+ "] has entered the top of the ready queue");
					printRq();
					return;
				}
				// enter PCB in the middle of the list
				PCB.setNextPcbPointer(OSM, pcbPointer, PCB.getNextPcbPointer(OSM, previousPointer));
				PCB.setNextPcbPointer(OSM, previousPointer, pcbPointer);

				logger.info("PCB enters in the middle of the ready queue ");
				printRq();

				return;
			} else // PCB to be inserted has lower or equal priority to the
					// current PCB in RQ
			{ // go to the next PCB in RQ
				previousPointer = tempPointer;
				tempPointer = PCB.getNextPcbPointer(OSM, tempPointer);
			}
		} // end of while loop

		// Insert PCB at the end of the RQ
		PCB.setNextPcbPointer(OSM, previousPointer, pcbPointer);

		System.out.println("PCB enters at the bottom of the ready queue ");
		printRq();

		return;
	} // end of insert process into ready queue module

	/**
	 * Gives the next free memory addresses block of the requested size to
	 * a program
	 * 
	 * @param requestedSize
	 *            The amount of memory requested by a program.
	 * @return Error status or the first address for the memory block the is
	 *         given.
	 */
	public int allocateTemporaryMemory(short requestedSize) {
		// Allocate memory from User free space, which is organized as link
		// copy OS code and modify to use freeHeapMemory

		final byte NO_MEMORY_FREE = -11;
		final byte INVALID_SIZE = -12;
		final byte notOK = -1;
		
		// Allocate memory from OS free space, which is organized as link
		if (AM.getFirstFreeTemporaryMemoryPointer() == END_OF_LIST_MARKER) {
			System.out.println("Error: There is no heap memory free");
			return NO_MEMORY_FREE; // ErrorNoFreeMemory is constant set to < 0
		}

		if (requestedSize < 0 || requestedSize > 2000) {
			System.out.println("Error: Invalid heap size");
			return INVALID_SIZE; // ErrorInvalidMemorySize is constant < 0
		}

		if (requestedSize == 1)
			requestedSize = 2; // Minimum allocated memory is 2 locations

		Short tempPointer1 = AM.getFirstFreeTemporaryMemoryPointer();
		short tempPointer2 = (short) AM.fetch(tempPointer1);
		/* Check each block in the link list until block with requested memory size is found */
		while (tempPointer1 != END_OF_LIST_MARKER) {

			Short i = AM.fetch((short) (tempPointer1 + 1));

			// if free memory block is the exact size needed. Adjust pointers
			if (i.equals(requestedSize)) {
				// first block
				if (currentPointer.equals(MEMORY.getFirstFreeTemporaryMemoryPointer())) 
				{
					// first entry is pointer to next block
					MEMORY.setFirstFreeTemporaryMemoryPointer(currentPointer);
					// reset next pointer
					MEMORY.load(currentPointer, END_OF_LIST_MARKER); 
					// in the allocated block
					return currentPointer; // return memory address
				} else // not first block
				{
					// point to next block
					MEMORY.load(previousPointer, MEMORY.fetch(currentPointer));

					// reset next pointer in the allocated block
					MEMORY.load(currentPointer, END_OF_LIST_MARKER);
					return currentPointer; // return memory address
				}
				// Found block with size greater than requested size
			} else if (MEMORY.fetch( (short) (currentPointer + 1)) > requestedSize) {
				// first block
				if (currentPointer == MEMORY.getFirstFreeTemporaryMemoryPointer()) 
				{
					// move next block pointer
					MEMORY.load(currentPointer + requestedSize, MEMORY.fetch(currentPointer));
					MEMORY.load(currentPointer + requestedSize + 1, MEMORY.fetch((short) (currentPointer + 1)) - requestedSize);
					// address of reduced block
					MEMORY.setFirstFreeTemporaryMemoryPointer((short) (currentPointer + requestedSize));
					// reset next pointer in the allocated block
					MEMORY.load(currentPointer, END_OF_LIST_MARKER);

					return currentPointer; // return memory address
				} else // not first block
				{
					// move next block pointer
					MEMORY.load(currentPointer + requestedSize, MEMORY.fetch(currentPointer)); 
					MEMORY.load(currentPointer + requestedSize + 1, MEMORY.fetch((short) (currentPointer + 1)) - requestedSize);
					
					// address of reduced block
					MEMORY.load(previousPointer, currentPointer + requestedSize); 
					// reset next pointer
					MEMORY.load(currentPointer, END_OF_LIST_MARKER); 
					// in the allocated block
					return currentPointer; // return memory address
				}
			} else // small block
			{ // look at next block
				previousPointer = currentPointer;
				currentPointer = (short) MEMORY.fetch(currentPointer);
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
		
		String filename = "C:\\Users\\roody.audain\\workspace\\Concepts\\DecimalMachine"
				+ "\\src\\main\\resources\\Null_Process";

		// Allocate space for Process Control Block
		short PcbPointer = MEMORY.allocateOSMemory(); // return value contains address
												// or error
		if (PcbPointer < Application_Memory.getFirst_Os_Memory_Address()
				|| PcbPointer > Application_Memory.getLast_Os_Memory_Address()) {
			System.out.println("Error: Memory alocation from OS free space has failed");
			return INVALID_ADDRESS;
		}

		// Initialize PCB. Set nextPCBlink to end of list, default priority,
		// Ready state, PID, rest 0
		PCB.initialize(MEMORY, PcbPointer, processId++, priority++, 'R');

		// Load the program
		Short load;

		load = (Short) absoluteLoader(filename);

		if (load <= INVALID_ADDRESS || load >= MEMORY.getFirstTemporaryMemoryAddress()) {
			logger.error("The Program's origin address is out of range."
					+ " Program counter cannot be set correctly");
			return INVALID_ADDRESS;
		}

		// store PC value in the PCB of the process
		PCB.setProgramCounter(MEMORY, PcbPointer, load); 

		// Store stack information in the PCB – SP, pointer, and size
		// not implemented

		PCB.setPriority(MEMORY, PcbPointer, priority); // Set priority

		// Insert PCB into Ready Queue according to the scheduling algorithm
		insertIntoReadyQueue(PcbPointer);

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
	public short createProcess(String filename, byte priority) throws FileNotFoundException {
		// Allocate space for Process Control Block
		short PcbPointer = MEMORY.allocateOSMemory(); // return value contains address
												// or error
		if (PcbPointer < Application_Memory.getFirst_Os_Memory_Address()
				|| PcbPointer > Application_Memory.getLast_Os_Memory_Address()) {
			System.out.println("Error: Memory alocation from OS free space has failed");
			return INVALID_ADDRESS;
		}

		// Initialize PCB. Set nextPCBlink to end of list, default priority,
		// Ready state, PID, rest 0
		PCB.initialize(MEMORY, PcbPointer, processId++, priority++, 'R');

		// Load the program
		Short load;
		load = (Short) absoluteLoader(filename);
		
		 // store PC value in the PCB of the process
		PCB.setProgramCounter(MEMORY, PcbPointer, load);

		// Store stack information in the PCB – SP, pointer, and size
		// not implemented

		PCB.setPriority(MEMORY, PcbPointer, priority);

		// Insert PCB into Ready Queue according to the scheduling algorithm
		insertIntoReadyQueue(PcbPointer);

		return OK;
	} // end of CreateProcess module

	public void createProcessSystemCall(short processId) {
		// Allocate space for Process Control Block
		short pcbPointer = MEMORY.allocateOSMemory(); // return value contains address
												// or error
		if (pcbPointer < Application_Memory.getFirst_Os_Memory_Address()
				|| pcbPointer > Application_Memory.getLast_Os_Memory_Address()) {
			logger.error("Memory alocation from OS free space has failed");
			CPU.setGeneralPurposeRegister((byte) 0, INVALID_ADDRESS);
		}

		// Initialize PCB. Set nextPCBlink to end of list, default priority,
		// Ready state, PID, rest 0
		PCB.setPcb(MEMORY, pcbPointer, processId, priority, 'W');

		// Insert PCB into Ready Queue according to the scheduling algorithm
		byte status = insertIntoWaitingQueue(pcbPointer);

		if (status != OK)
			CPU.setGeneralPurposeRegister((byte) 0, status);
		else
			CPU.setGeneralPurposeRegister((byte) 0, OK);

	} // end of create child process system call module
	
	/**
	 * Sets RQ to the the next process control block and sets the NEXT_PCB_INEX
	 * of the process control block at the top of the control block (varible
	 * name: RQ) to be END_OF_LIST_MARKER
	 * 
	 * @return process control block pointer of the process to be run
	 */
	public short selectFromRQ() {
		short pcbPointer = RqPointer; // PCBpointer points to first entry in RQ

		if (PCB.getNextPcbPointer(MEMORY, pcbPointer) != END_OF_LIST_MARKER)
			// Set RQ to point to the next process control block
			RqPointer = PCB.getNextPcbPointer(MEMORY, pcbPointer);

		// Set next point to EOL in the PCB
		PCB.setNextPcbPointer(MEMORY, pcbPointer, END_OF_LIST_MARKER);

		// restore CPU
		int[] gprValues = PCB.getGprValues(MEMORY, pcbPointer, CPU.getNumberOfGPRs());
		CPU.setGeneralPurposeRegisters(gprValues);
		CPU.setStackPointer(PCB.getStackPointer(MEMORY, pcbPointer));
		CPU.setProgramCounter(PCB.getProgramCounter(MEMORY, pcbPointer));
		
		return pcbPointer;
	} // end of select process from the ready queue module

	public void saveContext(short PCBpointer) {
		
		for (int i = 0; i <= CPU.getNumberOfGPRs() + 1; i++) {
			PCB.setGeneralPurposeRegister(register, instruction);memory[PCBpointer + gprPCBindex++] = gpr[0];
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
		}

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
					&& gpr[register] <= Application_Memory.getLast_Os_Memory_Address()) {
				op = new Operand((short) gpr[register], memory[(int) gpr[register]]);
			} else {
				System.out.println("Error: Invalid address found fetching operand in " + "autoincrement mode");
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
			if (gpr[register] > FIRST_USER_MEMORY_ADDRESS && gpr[register] <= Application_Memory.getLast_Os_Memory_Address()) {
				op = new Operand((short) gpr[register], INVALID_VALUE);

			} else {
				System.out.println("Error: Invalid address");
				op = new Operand(INVALID_ADDRESS);
			}

			break;

		case DIRECT: // Direct mode
			// Operand address is memory. Operand value is in memory
			if (memory[pc] > FIRST_USER_MEMORY_ADDRESS && memory[pc] <= Application_Memory.getLast_Os_Memory_Address()) {
				op = new Operand((short) memory[pc], memory[(int) memory[pc]]);
			} else {
				System.out.println("Error: Invalid address");
				op = new Operand(INVALID_ADDRESS);
			}

			this.pc++;

			break;

		case IMMEDIATE: // Immediate mode
			if (pc > FIRST_USER_MEMORY_ADDRESS && pc <= Application_Memory.getLast_Os_Memory_Address()) {
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
			if (pc <= Application_Memory.getLast_Os_Memory_Address()) {
				mar = pc;
				/*
				 * Advances program counter by 1 to the address of next
				 * instuction.
				 */
				pc++;
				mbr = (int) memory[mar];
			} else {
				System.out.println("Error: The program is leaving the designated program memory");
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
				System.out.println("Error: invalid general purpose register address");
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
				System.out.println("Error: Invalid general purpose register address");
				return INVALID_ADDRESS;
			}
			// Execute Cycle
			// In the execute cycle, fetch operand value(s) based on the opcode
			// since different opcode has different number of operands

			Operand operand1 = fetchOperand(operand1Mode, operand1GPR);
			Operand operand2 = fetchOperand(operand2Mode, operand2GPR);

			switch (opcode) {
			case 0: // halt
				System.out.println("\nThe CPU has reached a halt " + "instruction.");
				System.out.println("Dumping CPU registers and used " + "temporary memory.");
				dumpMemory();

				// Increment overall clock and this timeslice
				clock += 12;
				ticks += 12;

				return HALTED;

			case 1: // add operation code
				if (operand1.status < 0 || operand2.status < 0) {
					System.out.println("Error: Unsuccessful operand fetch " + "for add instruction");
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
					System.out.println("Error: Operand one's mode cannot be immediate");
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
					System.out.println("Error: Unsuccessful operand fetch for " + "subtract instruction");
					return UNSUCCESSFUL_OPERAND_FETCH;
				}

				// Subtract the operand values and store the result into Op1
				// location
				result = operand1.value - operand2.value;

				if (operand1Mode == REGISTER)
					gpr[operand1GPR] = result;

				else if (operand1Mode == IMMEDIATE) {
					System.out.println("Error: Operand one's mode cannot be immediate");
					return INVALID_MODE;
				}

				/*
				 * Store result in memory location for all other valid
				 * addressing mode for the subtract operation.
				 */
				else
					memory[operand1.address] = result;

				// Increment overall clock and this time slice
				clock += 3;
				ticks += 3;

				break;

			case 3: // Multiply
				if (operand1.status < 0 || operand2.status < 0) {
					System.out.println("Error: Unsuccesful operand fetch for multiply instruction");
					return UNSUCCESSFUL_OPERAND_FETCH;
				}

				// Add the operand values and store the result into Op1 location
				result = operand1.value * operand2.value;

				if (operand1Mode == REGISTER)
					gpr[operand1GPR] = result;

				else if (operand1Mode == IMMEDIATE) {
					System.out.println("Error: Operand one's mode cannot be immediate");
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
					System.out.println("Error: Unsuccessful operand fetch for" + "divide instruction");
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
					System.out.println("Error: Operand one's mode cannot be immediate");
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
					System.out.println("Error: Unsuccessful operand fetch for a" + "move instruction");
					return UNSUCCESSFUL_OPERAND_FETCH;
				}

				if (operand1Mode == REGISTER)
					gpr[operand1GPR] = operand2.value;

				else if (operand1Mode == IMMEDIATE) {
					System.out.println("Error: Operand one's mode cannot be immediate");
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
				if (pc < FIRST_USER_MEMORY_ADDRESS || pc > Application_Memory.getLast_Os_Memory_Address()) {
					System.out.println("Error: Invalid address");
					return INVALID_ADDRESS;
				}

				pc = (short) memory[pc];

				// Increment overall clock and this timeslice
				clock += 2;
				ticks += 2;

				break;

			case 7: // Branch on minus
				if (pc < FIRST_USER_MEMORY_ADDRESS || pc > LAST_USER_MEMORY_ADDRESS) {
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
				if (pc < FIRST_USER_MEMORY_ADDRESS || pc > Application_Memory.getLast_Os_Memory_Address()) {
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
				if (pc < FIRST_USER_MEMORY_ADDRESS || pc > Application_Memory.getLast_Os_Memory_Address()) {
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
				if (sp < FIRST_TEMPORARY_MEMORY_ADDRESS || sp >= Application_Memory.getFirst_Os_Memory_Address()) {
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
					System.out.println("Error: Operand one's mode cannot be immediate");
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
					System.out.println("Error: Unsuccessful operand fetch for system call");
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
			logger.error("Can't interrupt: Program cannot be found in " + "working directory");
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

		if (pointer < FIRST_TEMPORARY_MEMORY_ADDRESS || pointer + size >= Application_Memory.getFirst_Os_Memory_Address()) {
			System.out.print(
					"Error: The free temporary memory method has failed due to " + "receiving an invalid address ");
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
		if (ptr < Application_Memory.getFirst_Os_Memory_Address() || ptr > Application_Memory.getLast_Os_Memory_Address()) // MaxMemoryAddress
		// is
		// a
		// constant
		// set
		// to
		// 9999
		{
			System.out.println("Error: freeOSMemory has failed due to " + "pointing to an invalid address ");
			return INVALID_ADDRESS; // ErrorInvalidMemoryAddress is constantset
									// to < 0
		}
		if (ptr + PCB_SIZE > Application_Memory.getLast_Os_Memory_Address()) { // Invalid
																		// size
			System.out.print("Error: An invalid address was referenced. " + "OS memory was not returned.");
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

		System.out.println("Mem_alloc system call, Status Code: " + gpr[0] + ", GPR [1]: " + gpr[1]
				+ ", Requested memory slots: " + gpr[2]);

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

		System.out.println(
				"Display Mem_free system call, GPR[0]: " + gpr[0] + ", GPR[1]: " + gpr[1] + ", GPR[2]: " + gpr[2]);
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
		short previousPointer = END_OF_LIST_MARKER;
		short currentPointer = WQ;

		// Check for invalid PCB memory address
		if (PCBptr < Application_Memory.getFirst_Os_Memory_Address() || PCBptr > Application_Memory.getLast_Os_Memory_Address()) {
			System.out.println("Error: The process control block pointer is invalid");
			return INVALID_ADDRESS; // ErrorInvalidMemoryAddress is constantset
									// to < 0
		}

		memory[PCBptr + STATE_INDEX] = WAITING_STATE; // set state to ready
														// state
		memory[PCBptr + NEXT_PCB_INDEX] = END_OF_LIST_MARKER; // set next
																// pointer
																// to
		// end of list

		if (WQ == END_OF_LIST_MARKER) // RQ is empty
		{
			WQ = PCBptr;
			return OK;
		}

		while (currentPointer != END_OF_LIST_MARKER) {
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
		short previousPCBptr = END_OF_LIST_MARKER;
		while (memory[ptr] != END_OF_LIST_MARKER) {
			if (memory[ptr + PROCESS_ID_INDEX] == pid) // if found the right
														// process control block
			{
				found = true;
				if (memory[ptr + reasonForWaitingIndex] == waitingForMessageCode) {
					// remove pcb from wq
					memory[ptr + NEXT_PCB_INDEX] = END_OF_LIST_MARKER;
					memory[previousPCBptr + NEXT_PCB_INDEX] = END_OF_LIST_MARKER;
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

		if (ptr != END_OF_LIST_MARKER) {
			// check if process is waiting for msg
			if (memory[ptr + reasonForWaitingIndex] == waitingForMessageCode) { // deliver
																				// msg
				memory[ptr + gpr2index] = OK;
				memory[ptr + gpr0index] = OK;
				memory[ptr + STATE_INDEX] = READY_STATE;
				insertIntoReadyQueue(ptr);
			} else { // put message into message queue
				memory[(int) (memory[ptr + msgQstrtIndex] + memory[ptr + msgCountIndex])] = msgQstrtIndex;
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
			gpr[2] = memory[(short) (memory[runningpcbptr + msgQstrtIndex + msgCountIndex])];
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
		freeTemporaryMemory((short) memory[PCBpointer + temporaryMemoryAddressIndex],
				(short) memory[PCBpointer + temporaryMemorySizeIndex]);

		// Makes a process control block available
		freeOSmemory(PCBpointer);
	} // end of TerminateProcess module

	public void ISRshutdownSystem() {
		// Terminate processes in RQ
		short pointer = RqPointer;
		while (pointer != END_OF_LIST_MARKER) {
			RqPointer = (short) memory[pointer];
			// TerminateProcess(pointer);
			pointer = RqPointer;
		}

		// Terminate processed in WQ
		pointer = WQ;
		while (pointer != END_OF_LIST_MARKER) {
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

	public Operand(byte status)// Unsuccessful operand creation
	{
		this.status = status; // stores error code.
	}
}