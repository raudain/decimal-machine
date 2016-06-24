package operating.systems.internals.DecimalMachine;

import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.AssemblyCode.Instruction;
import operating.systems.internals.AssemblyCode.Operands;
import operating.systems.internals.Storage.Application_Memory;
import operating.systems.internals.Storage.Cache;
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

	private final byte SIZE_OF_AM;

	private final Stack<Byte> STACK;
	
	private final byte HALT;

	private final Ready_Program_List RPL;
	
	private short executionTime;

	private static final Logger logger = LogManager.getLogger("Machine");
	
	public Machine() {

		SIZE_OF_AM = 75;
		HALT = 2;
		executionTime = 0;
		
		AM = new Application_Memory(SIZE_OF_AM);
		STACK = new Stack<Byte>();
		RPL = new Ready_Program_List();
	} // end of constructor

	/*
	 * Since the program counter manipulation is needed in a couple
	 * instructions, this method is not included in the central processing unit
	 * class
	 * 
	 * @return Time it took to execute the instruction;
	 */
	private byte executionCount = 1;
	private short executeInstruction(Instruction instruction, Cache cache) {

		logger.info("Executing instruction number " + executionCount++);
		// check if instruction is valid. If not valid then error is logged
		instruction.isValid((byte) cache.size());

		// Execute Cycle
		// In the execute cycle, fetch operand value(s) based on the opcode
		// since different opcode has different number of operands

		Operands operands = instruction.getOperands();
		final byte valueOfOperand1 = operands.getValueOfOperand1(cache, AM);
		final byte valueOfOperand2 = operands.getValueOfOperand2(cache, AM);
		final byte operand1GPRAddress = operands.getOperand1GprAddress();
		int result;
		short pc = cache.getProgramCounter();
		switch (instruction.getOperationCode()) {

		case 0: 
			
			logger.info("Processing halt operation...");
			logger.info("Dumping CPU registers and used temporary memory.");
			cache.dump();

			final short haltOperationDuration = 2000;
			executionTime += haltOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 1: 

			logger.info("Processing add operation...");
			
			/*
			 * Add the operand's values and store the result into the first
			 * operand's address
			 */
			result = valueOfOperand1 + valueOfOperand2;
			cache.load(operand1GPRAddress, result);

			final byte addOperationDuration = 3;
			executionTime += addOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 2: 

			logger.info("Processing subtract operation...");
			/*
			 * Subtract the operand's values and store the result into the first
			 * operand's address
			 */
			result = valueOfOperand1 - valueOfOperand2;
			cache.load(operand1GPRAddress, result);

			final byte subtractOperationDuration = 3;
			executionTime += subtractOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 3: 

			logger.info("Processing multiply operation...");
			/*
			 * Multiply the operand's values and store the result into the first
			 * operand's address
			 */
			result = valueOfOperand1 * valueOfOperand2;
			cache.load(operand1GPRAddress, result);

			final byte muliplyOperationDuration = 6;
			executionTime += muliplyOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;
			
		case 4: 

			logger.info("Processing divide operation...");
			/*
			 * Divide the operand's values and store the result into the first
			 * operand's address
			 */
			result = valueOfOperand1 / valueOfOperand2;
			cache.load(operand1GPRAddress, result);

			final byte divideOperationDuration = 6;
			executionTime += divideOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 5: 

			logger.info("Processing move operation...");
			cache.load(operand1GPRAddress, valueOfOperand2);

			final byte moveOperationDuration = 2;
			executionTime += moveOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 6: 

			logger.info("Processing jump operation...");
			cache.setProgramCounter((short) AM.fetch(pc));

			final byte jumpOperationDuration = 2;
			executionTime += jumpOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 7: 

			logger.info("Processing branch on negitive value operation...");
			if (valueOfOperand1 < 0)
				// Store branch address in the PC
				cache.setProgramCounter((short) AM.fetch(pc));

			else
				cache.incrementProgramCounter(); // No branch, skip branch address
												// to go to next
			// instruction

			final byte branchOnNegativeOperationDuration = 4;
			executionTime += branchOnNegativeOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 8: 

			logger.info("Processing branch on positive value operation...");
			// Store branch address in the PC is true
			if (valueOfOperand1 > 0)
				// Store branch address in the PC
				cache.setProgramCounter((short) AM.fetch(pc));

			// No branch, skip branch address to go to next instruction
			else
				cache.incrementProgramCounter();

			final byte branchOnPositiveOperationDuration = 4;
			executionTime += branchOnPositiveOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 9:

			logger.info("Processing branch on zero operation...");
			// Store branch address in the PC is true
			if (valueOfOperand1 == 0)
				cache.setProgramCounter((short) AM.fetch((short) pc));
			// No branch, skip branch address to go to next instruction
			else
				cache.incrementProgramCounter();

			final byte branchOnZeroOperationDuration = 4;
			executionTime += branchOnZeroOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 10:

			logger.info("Processing push operation...");
			STACK.push(valueOfOperand1);

			final byte pushOperationDuration = 2;
			executionTime += pushOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 11:

			logger.info("Processing pop operation...");
			cache.load(operand1GPRAddress, STACK.pop());

			final byte popOperationDuration = 2;
			executionTime += popOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;
			
		default:
			return executionTime;
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
	 * @return The code for halt instruction detection or 0 if maximum execution time was met
	 */
	public byte execute() {

		logger.info("Execution has started");
		final short maximumExecutionTime = 200;
		while (executionTime < maximumExecutionTime) {
			// the first program has the highest priority
			Process_Control_Block pcb = RPL.removeFirst();
			
			// Fetch (read) the first word of the instruction pointed by PC
			short programCounter = pcb.getProgramCounter();
			
			Instruction instruction = new Instruction(AM.fetch(programCounter));
			
			executionTime = (short) (executionTime + executeInstruction(instruction, pcb.getCache()));
			pcb.incrementProgramCounter();
			RPL.add(pcb);
		} // end of while loop

		short halt = 2000;
		if (executionTime >= halt) {
			logger.info("Program has halted");
			
			return HALT; 
		}
		else {
			logger.info("Time slice complete");
			
			return 0;
		}	
	} // end of execute program module
	
	/**
	 * @return halt code or zero if not halted
	 */
	public byte run(String fileName, byte priority) {

		short amPointer = AM.load(fileName);
		Process_Control_Block pcb = new Process_Control_Block(amPointer, priority);
		RPL.add(pcb);
		byte status = execute();
		
		return status;
	}

	public void benchProgram(Process_Control_Block pcb) {

		RPL.add(pcb);
	}

	public byte getHaltCode() {
		
		return HALT;
	}
} // End of Machine class