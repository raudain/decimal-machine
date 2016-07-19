package operating.systems.internals.DecimalMachine;

import java.io.FileNotFoundException;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.AssemblyCode.Instruction;
import operating.systems.internals.AssemblyCode.Operands;
import operating.systems.internals.Storage.Application_Memory;
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

	private final Ready_Program_List RPL;

	private short executionTime;

	private static final Logger logger = LogManager.getLogger("Machine");

	private final LinkedList<Short> OM;

	private byte numberOfPrograms;

	public Machine() {

		executionTime = 0;

		AM = new Application_Memory();
		RPL = new Ready_Program_List();
		OM = new LinkedList<Short>();
		numberOfPrograms = 0;
	} // end of constructor

	/*
	 * Since the program counter manipulation is needed in a couple
	 * instructions, this method is not included in the central processing unit
	 * class
	 * 
	 * @return Time it took to execute the instruction;
	 */
	/**
	 * @param instruction
	 * @param pcb
	 * @return
	 */
	private short executeInstruction(Instruction instruction, Process_Control_Block pcb) {

		pcb.incrementExecutionCount();
		logger.info("Executing instruction number " + pcb.getInstructionExecutionCount() + " of the program named "
				+ pcb.getName());		
		byte firstGPRAddress = instruction.getFirstGPRAddress();
		pcb.isGprAddressWithinRange(firstGPRAddress);
		byte secondGPRAddress = instruction.getSecondGPRAddress();
		pcb.isGprAddressWithinRange(secondGPRAddress);
		instruction.isValid();

		Operands operands = instruction.getOperands();
		final int valueOfOperand1;
		final int valueOfOperand2;
		final byte operand1GPRAddress;
		Integer calculation;

		switch (instruction.getOperationCode()) {

		case 0:

			logger.info("Processing halt operation...");
			logger.info("Output:");

			for (Short i : OM)
				logger.info(i);

			logger.info("End of output");

			final short haltOperationDuration = 2000;
			executionTime += haltOperationDuration;

			OM.removeAll(OM);

			return executionTime;

		case 1:

			logger.info("Processing add operation...");

			/*
			 * Add the operand's values and store the result into the first
			 * operand's address
			 */
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			valueOfOperand2 = operands.getValueOfOperand2(pcb, AM);
			calculation = valueOfOperand1 + valueOfOperand2;
			operand1GPRAddress = operands.getOperand1GprAddress();
			if (operand1GPRAddress >= pcb.size())
				pcb.add(operand1GPRAddress, calculation);
			else
				pcb.set(operand1GPRAddress, calculation);
			
			logger.info("General purpose register number " +  operand1GPRAddress + " = " + calculation);
			
			if (!calculation.equals(pcb.get(operand1GPRAddress))) {
				logger.warn("General purpose register not loaded correctly");
			}
			
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
			calculation = valueOfOperand1 - valueOfOperand2;
			operand1GPRAddress = operands.getOperand1GprAddress();
			if (operand1GPRAddress >= pcb.size())
				pcb.add(operand1GPRAddress, calculation);
			else
				pcb.set(operand1GPRAddress, calculation);

			final byte subtractOperationDuration = 3;
			executionTime += subtractOperationDuration;
			logger.info("Execution time equals " + executionTime);

			pcb.incrementProgramCounter();

			return executionTime;

		case 3:

			logger.info("Processing origin fetch operation...");
			operand1GPRAddress = operands.getOperand1GprAddress();
			byte origin = pcb.getOrigin();
			if (operand1GPRAddress >= pcb.size())
				pcb.add(operand1GPRAddress, (int) origin);
			else
				pcb.set(operand1GPRAddress, (int) origin);
			
			final byte originFetchOperationDuration = 110;
			executionTime += originFetchOperationDuration;
			logger.info("Execution time equals " + executionTime);

			pcb.incrementProgramCounter();

			return executionTime;

		case 5:

			logger.info("Processing move operation...");
			valueOfOperand2 = operands.getValueOfOperand2(pcb, AM);
			operand1GPRAddress = operands.getOperand1GprAddress();
			if (operand1GPRAddress >= pcb.size())
				pcb.add(operand1GPRAddress, valueOfOperand2);
			else
				pcb.set(operand1GPRAddress, valueOfOperand2);

			final byte moveOperationDuration = 2;
			executionTime += moveOperationDuration;
			logger.info("Execution time equals " + executionTime);

			pcb.incrementProgramCounter();

			return executionTime;

		case 7:

			logger.info("Processing branch on negative value operation...");
			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			if (valueOfOperand1 < 0) {
				// Store branch address in the PC
				short jumpAddress = (short) operands.getValueOfOperand2(pcb, AM);
				if (pcb.isAddressWithinCode(jumpAddress))
					pcb.setProgramCounter(jumpAddress);
				else {
					logger.error("The jump address is not within the program's code");
					
					// Don't jump and proceed to next instruction
					pcb.incrementProgramCounter();
				}
			} else
				// Don't jump and proceed to next instruction
				pcb.incrementProgramCounter();

			final byte branchOnNegativeOperationDuration = 4;
			executionTime += branchOnNegativeOperationDuration;
			logger.info("Execution time equals " + executionTime);

			return executionTime;

		case 12:

			valueOfOperand1 = operands.getValueOfOperand1(pcb, AM);
			OM.add((short) valueOfOperand1);
			if (valueOfOperand1 > 150)
				logger.warn("Store value is greater than 150");

			logger.info("The number " + valueOfOperand1 + " was stored to output memory");

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

	byte numberOfHalts = 0;

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
	 * @return The code for halt instruction detection or 0 if maximum execution
	 *         time was exceeded
	 */
	public void execute() {

		final short maximumExecutionTime = 200;
		Process_Control_Block pcb = RPL.removeFirst();
		logger.info("Executing " + pcb.getName() + "...");
		while (executionTime < maximumExecutionTime) {
			// Fetch (read) the first word of the instruction pointed by PC
			short programCounter = pcb.getProgramCounter();
			int rawinstruction = 0; // zero is the halt instruction
			if (AM.isAddressInRange(programCounter)) 
					rawinstruction = AM.get(programCounter);
			else
				logger.error("The address numbered " + programCounter + " found in the program counter is not within range");

			Instruction instruction = new Instruction(rawinstruction);
			executionTime = executeInstruction(instruction, pcb);
			logger.info("Dumping registers");
			pcb.dumpRegisters();
			logger.info("Dump complete");
		}
		
		short halt = 2000;
		if (executionTime >= halt) {
			logger.info(pcb.getName() + " has halted");
			executionTime = 0;
			numberOfHalts++;
			if (numberOfHalts != numberOfPrograms)
				execute();
			else
				return;
		} else {
			logger.info("Time slice complete");
			RPL.add(pcb);
			executionTime = 0;
			boolean interrupt = false;
			if (interrupt)
				return;
			else
				execute();
		}
	} // end of the execute method

	public Program[] load(String[] fileNames) throws FileNotFoundException {

		Program[] programs = AM.load(fileNames);
		numberOfPrograms = (byte) fileNames.length;
		return programs;
	}

	byte index = 0;
	byte priority = 127;

	public void createPcbs(Program[] programs) {

		if (programs == null)
			return;

		String name = programs[index].getName();
		logger.info("A process control block for the program \"" + name + "\" has been created");
		Process_Control_Block pcb = new Process_Control_Block(name, programs[index].getOrigin(),
				programs[index++].getHaltAddress(), priority--);
		RPL.add(pcb);

		if (index >= numberOfPrograms)
			return;
		else
			createPcbs(programs);
	}

	public void benchProgram(Process_Control_Block pcb) {

		RPL.add(pcb);
	}

	public String getWorkingDirectory() {

		return AM.getWorkingDirectory();
	}
} // End of Machine class