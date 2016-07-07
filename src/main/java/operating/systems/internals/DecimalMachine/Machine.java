package operating.systems.internals.DecimalMachine;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
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

	private final byte MEMORY_SIZE;

	private final Stack<Short> STACK;
	
	private final byte HALT;

	private final Ready_Program_List RPL;
	
	private short executionTime;

	private static final Logger logger = LogManager.getLogger("Machine");
	
	private final LinkedList<Short> OM;
	
	public Machine() {

		MEMORY_SIZE = 75;
		HALT = 2;
		executionTime = 0;
		
		AM = new Application_Memory(MEMORY_SIZE);
		STACK = new Stack<Short>();
		RPL = new Ready_Program_List();
		OM = new LinkedList<Short>();
	} // end of constructor

	/*
	 * Since the program counter manipulation is needed in a couple
	 * instructions, this method is not included in the central processing unit
	 * class
	 * 
	 * @return Time it took to execute the instruction;
	 */
	private int executionCount = 1;
	/**
	 * @param instruction
	 * @param pcb
	 * @return
	 */
	private short executeInstruction(Instruction instruction, Process_Control_Block pcb) {

		logger.info("Executing instruction number " + executionCount++);

		byte firstGPRAddress = instruction.getFirstGPRAddress();
		pcb.isValidGPRAddress(firstGPRAddress);
		byte secondGPRAddress = instruction.getSecondGPRAddress();
		pcb.isValidGPRAddress(secondGPRAddress);
		instruction.isValid();

		Operands operands = instruction.getOperands();
		final short valueOfOperand1;
		final short valueOfOperand2;
		final byte operand1GPRAddress;
		int result;
		
		switch (instruction.getOperationCode()) {

		case 0: 
			
			logger.info("Processing halt operation...");
			logger.info("Output:");
			
			for (Short i : OM) 
				logger.info(i);
			
			logger.info("End of output");
					
			final short haltOperationDuration = 2000;
			executionTime += haltOperationDuration;
			
			return executionTime;

		case 1: 

			logger.info("Processing add operation...");
			
			/*
			 * Add the operand's values and store the result into the first
			 * operand's address
			 */
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			valueOfOperand2 = operands.getValueOfOperand2(pcb, AM);
			result = valueOfOperand1 + valueOfOperand2;
			operand1GPRAddress = operands.getOperand1GprAddress();
			pcb.load(operand1GPRAddress, result);

			final byte addOperationDuration = 3;
			executionTime += addOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			pcb.incrementProgramCounter();
			
			return executionTime;

		case 2: 

			logger.info("Processing subtract operation...");
			/*
			 * Subtract the operand's values and store the result into the first
			 * operand's address
			 */
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			valueOfOperand2 = operands.getValueOfOperand2(pcb, AM);
			result = valueOfOperand1 - valueOfOperand2;
			operand1GPRAddress = operands.getOperand1GprAddress();
			pcb.load(operand1GPRAddress, result);

			final byte subtractOperationDuration = 3;
			executionTime += subtractOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			pcb.incrementProgramCounter();
			
			return executionTime;

		case 3: 

			logger.info("Processing multiply operation...");
			/*
			 * Multiply the operand's values and store the result into the first
			 * operand's address
			 */
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			valueOfOperand2 = operands.getValueOfOperand2(pcb, AM);
			result = valueOfOperand1 * valueOfOperand2;
			operand1GPRAddress = operands.getOperand1GprAddress();
			pcb.load(operand1GPRAddress, result);

			final byte muliplyOperationDuration = 6;
			executionTime += muliplyOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			pcb.incrementProgramCounter();
			
			return executionTime;
			
		case 4: 

			logger.info("Processing divide operation...");
			/*
			 * Divide the operand's values and store the result into the first
			 * operand's address
			 */
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			valueOfOperand2 = operands.getValueOfOperand2(pcb, AM);
			result = valueOfOperand1 / valueOfOperand2;
			operand1GPRAddress = operands.getOperand1GprAddress();
			pcb.load(operand1GPRAddress, result);

			final byte divideOperationDuration = 6;
			executionTime += divideOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			pcb.incrementProgramCounter();
			
			return executionTime;

		case 5: 

			logger.info("Processing move operation...");
			valueOfOperand2 = operands.getValueOfOperand2(pcb, AM);
			operand1GPRAddress = operands.getOperand1GprAddress();
			pcb.load(operand1GPRAddress, valueOfOperand2);

			final byte moveOperationDuration = 2;
			executionTime += moveOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			pcb.incrementProgramCounter();
			
			return executionTime;

		case 6: 

			logger.info("Processing jump operation...");
			pcb.incrementProgramCounter();	
			short pc = pcb.getProgramCounter();
			short jumpToAddress = (short) AM.fetch(pc);
			pcb.setProgramCounter(jumpToAddress);
			
			final byte jumpOperationDuration = 2;
			executionTime += jumpOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 7: 

			logger.info("Processing branch on negative value operation...");
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			if (valueOfOperand1 < 0) {
				// Store branch address in the PC
				short jumpAddress = operands.getValueOfOperand2(pcb, AM);
				pcb.setProgramCounter(jumpAddress);
			} else
				// Don't jump and proceed to next instruction
				pcb.incrementProgramCounter(); 

			final byte branchOnNegativeOperationDuration = 4;
			executionTime += branchOnNegativeOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 8: 

			logger.info("Processing branch on positive value operation...");
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			// Store branch address in the PC is true
			if (valueOfOperand1 > 0)
				// Store branch address in the PC
				pcb.setProgramCounter((short) AM.fetch(pcb.getProgramCounter()));

			// No branch, skip branch address to go to next instruction
			else
				pcb.incrementProgramCounter();

			final byte branchOnPositiveOperationDuration = 4;
			executionTime += branchOnPositiveOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 9:

			logger.info("Processing branch on zero operation...");
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			// Store branch address in the PC is true
			if (valueOfOperand1 == 0)
				pcb.setProgramCounter((short) AM.fetch((short) pcb.getProgramCounter()));
			// No branch, skip branch address to go to next instruction
			else
				pcb.incrementProgramCounter();

			final byte branchOnZeroOperationDuration = 4;
			executionTime += branchOnZeroOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 10:

			logger.info("Processing push operation...");
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			STACK.push(valueOfOperand1);

			final byte pushOperationDuration = 2;
			executionTime += pushOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			pcb.incrementProgramCounter();
			
			return executionTime;

		case 11:

			logger.info("Processing pop operation...");
			operand1GPRAddress = operands.getOperand1GprAddress();
			pcb.load(operand1GPRAddress, STACK.pop());

			final byte popOperationDuration = 2;
			executionTime += popOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			pcb.incrementProgramCounter();
			
			return executionTime;
			
		case 12:
			
			logger.info("Storing output...");
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			OM.add(valueOfOperand1);
			
			final byte storeOperationDuration = 127;
			executionTime += storeOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			pcb.incrementProgramCounter();
			
			return executionTime;
			
		default:
			
			logger.error("Unknown operation code");
			pcb.incrementProgramCounter();
			
			return executionTime; // the method must return a short
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
		byte status;
		while (executionTime < maximumExecutionTime) {
			// the first program has the highest priority
			Process_Control_Block pcb = RPL.removeFirst();
			
			// Fetch (read) the first word of the instruction pointed by PC
			short programCounter = pcb.getProgramCounter();
			
			Instruction instruction = new Instruction(AM.fetch(programCounter));
			
			executionTime = executeInstruction(instruction, pcb);
			RPL.add(pcb);
		} // end of while loop

		short halt = 2000;
		if (executionTime >= halt) {
			logger.info("Program has halted");
			executionTime = 0;
			status = HALT;
			return status;
		}
		else {
			logger.info("Time slice complete");
			executionTime = 0;
			boolean interrupt = false;
			if (interrupt) 	
				return 0;
			else
				status = execute();
			
		}
		return status;
	} // end of execute program module
	
	/**
	 * @return halt code or zero if not halted
	 */
	public byte run(String fileName, byte priority) throws FileNotFoundException{

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

	public String getWorkingDirectory() {
		
		return AM.getWorkingDirectory();
	}
} // End of Machine class