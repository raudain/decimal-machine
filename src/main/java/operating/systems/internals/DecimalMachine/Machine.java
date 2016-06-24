package operating.systems.internals.DecimalMachine;

import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.AssemblyCode.Instruction;
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
		HALT = 2;

		CPU = new Cache(NUMBER_OF_REGISTERS);
		AM = new Application_Memory(SIZE_OF_AM);
		OSM = new Operating_System_Memory(SIZE_OF_OSM);
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
	private short executeInstruction(Instruction instruction) {

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
			logger.info("The Machine has reached a halt instruction.");
			logger.info("Dumping CPU registers and used temporary memory.");
			CPU.dump();

			final short haltOperationDuration = 2000;
			executionTime += haltOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
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
			logger.info("Execution time equals " + executionTime);
			
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
			logger.info("Execution time equals " + executionTime);
			
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
			logger.info("Execution time equals " + executionTime);
			
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
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 5: // Move

			CPU.load(operand1GPRAddress, valueOfOperand2);

			final byte moveOperationDuration = 2;
			executionTime += moveOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 6: // Branch or jump instruction

			CPU.setProgramCounter((short) AM.fetch(pc));

			final byte jumpOperationDuration = 2;
			executionTime += jumpOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
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
			logger.info("Execution time equals " + executionTime);
			
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
			logger.info("Execution time equals " + executionTime);
			
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
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 10: // Push

			STACK.push(valueOfOperand1);

			final byte pushOperationDuration = 2;
			executionTime += pushOperationDuration;
			logger.info("Execution time equals " + executionTime);
			
			return executionTime;

		case 11: // Pop

			CPU.load(operand1GPRAddress, STACK.pop());

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
		Short executionTime = 0;
		final short maximumExecutionTime = 200;
		while (executionTime < maximumExecutionTime) {
			// the first program has the highest priority
			Process_Control_Block pcb = RPL.removeFirst();
			
			// Fetch (read) the first word of the instruction pointed by PC
			short programCounter = pcb.getOsmPointer();
			
			Instruction instruction = new Instruction(AM.fetch(programCounter));
			
			executionTime = (short) (executionTime + executeInstruction(instruction));
			//pcb.incrementProgramCounter();
			RPL.add(pcb);
		} // end of while loop

		short halt = 2000;
		if (executionTime >= halt) {
			logger.info("Program has halted");
			
			return HALT; 
		}
		else {
			logger.info("Time slice complete");
			logger.info("Dumping CPU registers and used temporary memory.");		
			CPU.dump();
			
			return 0;
		}	
	} // end of execute program module
	
	/**
	 * @return halt code or zero if not halted
	 */
	public byte run(String fileName, byte priority) {

		short osmPointer = AM.load(fileName);
		Process_Control_Block pcb = new Process_Control_Block(OSM, osmPointer, priority);
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