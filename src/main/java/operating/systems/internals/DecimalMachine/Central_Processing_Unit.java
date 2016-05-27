package operating.systems.internals.DecimalMachine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Central_Processing_Unit {
	
	private int[] generalPurposeRegisters;
	
	/*
	 * A program counter is a register 
	 * in a computer processor that 
	 * contains the address (location) of 
	 * the instruction being executed at 
	 * the current time. As each 
	 * instruction gets fetched, the 
	 * program counter increases its 
	 * stored value by 1.
	 */
	private short programCounter;


	/*
	 * A stack pointer is a small register 
	 * that stores the address of the last 
	 * program request in a stack. A stack 
	 * is a specialized buffer which stores 
	 * data from the top down. As new 
	 * requests come in, they "push down" 
	 * the older ones.
	 */
	private short stackPointer;
	
	private final byte NUMBER_OF_REGISTERS;
	
	private static final Logger logger = LogManager.getLogger("Machine_Implementation");

	// psr = 0; // Processor status register
	
	public Central_Processing_Unit() {
		
		NUMBER_OF_REGISTERS = 8;
	}
	
	public byte getNumberOfGPRs() {
		
		return NUMBER_OF_REGISTERS;
	}
	
	public void setGeneralPurposeRegisters(int[] a) {
		
		generalPurposeRegisters.clone();
	}
	
	public void dumpRegisters() {

		// print register titles
		logger.info("GPRs:\tG0\tG1\tG2\tG3\tG4\tG5\tG6\tG7\tSP\tPC");

		// print register values
		for (int i = 0; i <= NUMBER_OF_REGISTERS; i++)
			System.out.printf("\t%d", generalPurposeRegisters[i]);
	}
	
	public void dumpStackPointer() {

		// print stack pointer and program counter value
		logger.info("\t%d\n", stackPointer);
	}

	public void dumpProgramCounter() {
		logger.info("\t%d\n", programCounter);
	}

}
