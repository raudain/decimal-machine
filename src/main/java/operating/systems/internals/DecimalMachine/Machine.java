package operating.systems.internals.DecimalMachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.AssemblyCode.Instruction;
import operating.systems.internals.AssemblyCode.Operand;
import operating.systems.internals.AssemblyCode.Operands;
import operating.systems.internals.Storage.Application_Memory;
import operating.systems.internals.Storage.Cache;
import operating.systems.internals.Storage.Operating_System_Memory;
import operating.systems.internals.Storage.Ready_Program_List;

/**
 * Facade design pattern for Memory and CPU
 * 
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
 * 
 * @author Roody Audain
 * @version 03 August 2013
 *
 */
public class Machine {

	private final Application_Memory AM;

	// Process Control Block memory free list set to be empty;

	// private Process_Control_Block PCB;

	private final char READY_STATE_INDICATOR;

	private final byte SIZE_OF_AM;

	private final byte SIZE_OF_OSM;

	private final Stack<Byte> STACK;

	private final byte NUMBER_OF_REGISTERS;
	
	private final byte HALT;
	
	private final Cache CPU;

	private final Operating_System_Memory OSM;

	private final Ready_Program_List RPL;

	private static final Logger logger = LogManager.getLogger("Machine");
	
	public Machine() {

		SIZE_OF_AM = 75;
		SIZE_OF_OSM = 75;
		NUMBER_OF_REGISTERS = 11;

		CPU = new Cache(NUMBER_OF_REGISTERS);
		AM = new Application_Memory(SIZE_OF_AM);
		OSM = new Operating_System_Memory(SIZE_OF_OSM);
		STACK = new Stack<Byte>();
		RPL = new Ready_Program_List();
		// PCB = new Process_Control_Block(NUMBER_OF_REGISTERS);

		READY_STATE_INDICATOR = 'R';
		HALT = 2;
	} // end of constructor

	private String addPath(String fileName) {

		String directory = "target/classes/";
		String relativePath = directory + fileName;

		return relativePath;
	}

	/**
	 * Open the file containing HYPO machine program and load the content into
	 * the application memory. The address of the application memory the program
	 * is loaded into is included in the program code. On successful load,
	 * return the PC value in the last line. On failure, return appropriate
	 * error code.
	 * 
	 * @param filename
	 *            Name of the executable file to be loaded into main memory
	 * 
	 * @throws FileNotFoundException
	 * 
	 * @return First address of the program
	 */
	public short load(String fileName) {

		Scanner machineCode = null;
		String s = addPath(fileName);
		try {
			File file = new File(s);
			machineCode = new Scanner(file);
		} catch (FileNotFoundException e) {
			logger.error("Error in absoluteLoader: Program cannot be found in " + "working directory");
			e.printStackTrace();
		}

		// Read from file until end of program or end of file and
		// store program in HYPO memory

		logger.info("Loading " + fileName + " into memory");
		while (machineCode.hasNextLine()) {
			// read address, content from file
			short address = machineCode.nextShort();

			// store program
			AM.load(address, machineCode.nextInt());

			if (address == AM.getEndIndicator()) {
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

	final byte OK = 0;

	/*
	 * include in later release public void createProcessSystemCall(short
	 * processId) { // Allocate space for Process Control Block short pcbPointer
	 * = OSM.allocate(PCB.size());
	 * 
	 * // Initialize PCB. Set nextPCBlink to end of list, default priority, //
	 * Ready state, PID, rest 0 Process_Control_Block pcb = new
	 * Process_Control_Block(OSM, pcbPointer, (byte) CPU.getSize(), processId,
	 * priority, 'W');
	 * 
	 * // Insert PCB into Ready Queue according to the scheduling algorithm
	 * insertIntoWaitingQueue(pcb);
	 * 
	 * } // end of create child process system call module
	 */

	final byte INVALID_MODE = -6; // invalid mode
	final byte UNSUCCESSFUL_OPERAND_FETCH = -9; // Operand fetch was
												// unsuccessful error code

	final byte FIRST_USER_MEMORY_ADDRESS = 0;
	byte invalidIndicator = -1;

	/*
	 * Since the program counter manipulation is needed in a couple
	 * instructions, this method is not included in the central processing unit
	 * class
	 * 
	 * @return Time it took to execute the instruction;
	 */
	public short executeInstruction(Instruction instruction) {

		// check if instruction is valid. If not valid then error is logged
		instruction.isValid((byte) CPU.size());

		// Execute Cycle
		// In the execute cycle, fetch operand value(s) based on the opcode
		// since different opcode has different number of operands

		Operands operands = instruction.getOperands();
		final byte valueOfOperand1 = operands.getValueOfOperand1(CPU, AM);
		final byte valueOfOperand2 = operands.getValueOfOperand2(CPU, AM);
		final byte operand1GPRAddress = operands.getOperand1GprAddress();
		int result;
		short executionTime = 0;
		short pc = CPU.getProgramCounter();
		switch (instruction.getOperationCode()) {

		case 0: // halt
			logger.info("\n The Machine has reached a halt instruction.");
			logger.info("Dumping CPU registers and used temporary memory.");
			CPU.dump();

			final short haltOperationDuration = 2000;
			executionTime += haltOperationDuration;

			return executionTime;

		case 1: // add operation code

			/*
			 * Add the operand's values and store the result into the first
			 * operand's address
			 */
			result = valueOfOperand1 + valueOfOperand2;
			CPU.load(operand1GPRAddress, result);

			final byte addOperationDuration = 3;
			executionTime += addOperationDuration;

			return executionTime;

		case 2: // Subtract

			/*
			 * Subtract the operand's values and store the result into the first
			 * operand's address
			 */
			result = valueOfOperand1 - valueOfOperand2;
			CPU.load(operand1GPRAddress, result);

			final byte subtractOperationDuration = 3;
			executionTime += subtractOperationDuration;

			return executionTime;

		case 3: // Multiply

			/*
			 * Multiply the operand's values and store the result into the first
			 * operand's address
			 */
			result = valueOfOperand1 * valueOfOperand2;
			CPU.load(operand1GPRAddress, result);

			final byte muliplyOperationDuration = 6;
			executionTime += muliplyOperationDuration;

			return executionTime;
			
		case 4: // Divide

			/*
			 * Divide the operand's values and store the result into the first
			 * operand's address
			 */
			result = valueOfOperand1 / valueOfOperand2;
			CPU.load(operand1GPRAddress, result);

			final byte divideOperationDuration = 6;
			executionTime += divideOperationDuration;

			return executionTime;

		case 5: // Move

			CPU.load(operand1GPRAddress, valueOfOperand2);

			final byte moveOperationDuration = 2;
			executionTime += moveOperationDuration;

			return executionTime;

		case 6: // Branch or jump instruction

			CPU.setProgramCounter((short) AM.fetch(pc));

			final byte jumpOperationDuration = 2;
			executionTime += jumpOperationDuration;

			return executionTime;

		case 7: // Branch on negative

			if (valueOfOperand1 < 0)
				// Store branch address in the PC
				CPU.setProgramCounter((short) AM.fetch(pc));

			else
				CPU.incrementProgramCounter(); // No branch, skip branch address
												// to go to next
			// instruction

			final byte branchOnNegativeOperationDuration = 4;
			executionTime += branchOnNegativeOperationDuration;

			return executionTime;

		case 8: // Branch on positive

			// Store branch address in the PC is true
			if (valueOfOperand1 > 0)
				// Store branch address in the PC
				CPU.setProgramCounter((short) AM.fetch(pc));

			// No branch, skip branch address to go to next instruction
			else
				CPU.incrementProgramCounter();

			final byte branchOnPositiveOperationDuration = 4;
			executionTime += branchOnPositiveOperationDuration;

			return executionTime;

		case 9: // branch on zero

			// Store branch address in the PC is true
			if (valueOfOperand1 == 0)
				CPU.setProgramCounter((short) AM.fetch((short) pc));
			// No branch, skip branch address to go to next instruction
			else
				CPU.incrementProgramCounter();

			final byte branchOnZeroOperationDuration = 4;
			executionTime += branchOnZeroOperationDuration;

			return executionTime;

		case 10: // Push

			STACK.push(valueOfOperand1);

			final byte pushOperationDuration = 2;
			executionTime += pushOperationDuration;

			return executionTime;

		case 11: // Pop

			CPU.load(operand1GPRAddress, STACK.pop());

			final byte popOperationDuration = 2;
			executionTime += popOperationDuration;

			return executionTime;

		/*
		 * include this fuctionality in later version
		 * 
		 * case 12: // System call
		 * 
		 * byte systemCallCode = systemCall(valueOfOperand1);
		 * 
		 * final byte systemCallDuration = 12; executionTime +=
		 * systemCallDuration;
		 * 
		 * return systemCallCode;
		 */
			
		default:
			return 0;
		} // end of opcode switch statement
	} // End of execute instruction class

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
	 * @return The code for halt instruction detection and the code for time slice complete
	 */
	public byte execute() {

		Short executionTime = 0;
		final short maximumExecutionTime = 200;
		while (executionTime < maximumExecutionTime) {
			// Fetch (read) the first word of the instruction pointed by PC
			Instruction instruction = new Instruction(AM.fetch(CPU.getProgramCounter()));
			executionTime = (short) (executionTime + executeInstruction(instruction));
			CPU.incrementProgramCounter();
		} // end of while loop

		short halt = 2000;
		if (executionTime >= halt) {
			logger.info("Dumping CPU registers and used temporary memory.");
			CPU.dump();
			
			return HALT; 
		}
		else {
			logger.info("Time slice complete");
			logger.info("Dumping CPU registers and used temporary memory.");		
			CPU.dump();
			
			return 0;
		}	
	} // end of execute program module
	
	// process id starts at 0 and is incremented for every new process
	byte processId = 0;
	public byte run(String fileName, byte priority) {

		short osmPointer = load(fileName);
		Process_Control_Block pcb = new Process_Control_Block((byte) CPU.size(), OSM, osmPointer, processId++, priority,
				READY_STATE_INDICATOR);
		RPL.add(pcb);
		byte status = execute();
		
		return status;
	}

	
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
	byte CheckAndProcessInterrupt() throws FileNotFoundException {
		// Prompt and read interrupt ID

		byte interruptId = CPU.getInterruptId();
		logger.info("\nProcessing interrupt id: " + interruptId);

		// Process interrupt
		byte status = 0;// 0 == Success
		switch (interruptId) {
		case 0: // No interrupt
			System.out.println(" No interrupt");
			break;

		case 1: // Run a different program

			String userInput = UI.getInput();
			processInput(userInput, priority);

		case 2: // Shutdown system
			shutdownSystem(); // Terminate processes in RQ and WQ
			break;
		} // end of switch InterruptID

		return status;
	} // end of CheckAndProcessInterrupt function
	*/

	/**
	 * Include in later version
	 * 
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
	 * 
	 *         byte systemCall(byte actionCode) { // byte operatingSystemMode =
	 *         0; // byte applicationMode = 1;
	 * 
	 *         // psr = operatingSystemMode; // Set system mode to OS mode
	 * 
	 *         byte status = OK;
	 * 
	 *         switch (actionCode) {
	 * 
	 *         case 1: // Create process – user process is creating a child
	 *         process.
	 * 
	 *         // This is not same as run program interrupt
	 *         createProcessSystemCall((short) gpr[1]);
	 * 
	 *         break;
	 * 
	 *         return status; } // end of system call module
	 *

	private final byte reasonForWaitingIndex = 3;
	private final byte waitingForMessageCode = 7;
	private final char WAITING_STATE = 'W';

	/**
	 * insertIntoWaitingQueue populates the waiting queue with process which
	 * need to wait. Process are always inserted at the bottom of the waiting
	 * queue.
	 * 
	 * @param PCBptr
	 *            memory address of the process's process control block.
	 * 
	 * @return
	 *
	public void insertIntoWaitingQueue(Process_Control_Block pcb) {
		short previousPointer = END_OF_LIST_MARKER;
		short currentPointer = WQ;

		pcb.setState(OSM, pcbPointer, WAITING_STATE);
		// set next pointer to end of list. RQ is empty
		pcb.setNextPcbPointer(OSM, pcbPointer, END_OF_LIST_MARKER);
		if (WQ == END_OF_LIST_MARKER) {
			WQ = PCBptr;
			return OK;
		}

		while (currentPointer != END_OF_LIST_MARKER) {
			previousPointer = currentPointer;
			currentPointer = (short) memory[currentPointer + NEXT_PCB_INDEX];
		} // end of while loop
		memory[previousPointer + NEXT_PCB_INDEX] = PCBptr;
	} // end of insert process into waiting queue module
 	
	final byte INVALID_PID = -30;

	/**
	 * @param PID
	 *            The process identification number of the process to be found.
	 *
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
					// remove PCB from wq
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
	} // end of the searchAndRemovePCBfromWaitingQueue method

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
	} // end of receiveMessage method

	/**
	 * Frees memory like back before program started.
	 * 
	 *
	public void terminateProcess() {

		AM.deleteProgram();

	} // end of TerminateProcess module*/

	public Operating_System_Memory getOsm() {

		return OSM;
	}

	public void benchProgram(Process_Control_Block pcb) {

		RPL.add(pcb);
	}

	public byte getHaltCode() {
		
		return HALT;
	}
} // End of Machine class