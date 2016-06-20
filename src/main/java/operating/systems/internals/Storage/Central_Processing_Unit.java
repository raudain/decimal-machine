package operating.systems.internals.Storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Central_Processing_Unit extends Memory{
	
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
	
	private final byte INTERRUPT_ID_INDEX;
	
	private static final Logger logger = LogManager.getLogger("Machine_Implementation");

	// psr = 0; // Processor status register
	
	public Central_Processing_Unit(byte size) {
		
		super(size);
		
		INTERRUPT_ID_INDEX = 0;
	}
	
	public void setGeneralPurposeRegisters(int[] gprValues) {
		
		for (byte i = 0; i <= size() + 1; i++) 
			load(i, gprValues[i]);
	}
	
	public void dumpRegisters() {

		// print register titles
		logger.info("GPRs:\tG0\tG1\tG2\tG3\tG4\tG5\tG6\tG7\tSP\tPC");

		// print register values
		for (short i = 0; i <= getSize(); i++)
			logger.info("\t%d", fetch(i));
	}

	public void dumpProgramCounter() {
		
		logger.info("\t%d\n", programCounter);
	}
	
	public void setProgramCounter(short pc) {
		
		programCounter = pc;
	}
	
	public short getProgramCounter() {
		
		return programCounter;
	}
	
	public void incrementProgramCounter() {
		
		programCounter++;
	}
	
	// increase register's value by one
	public void increment(byte address) {
		
		load(address, fetch(address) + 1);
	}
	
	// decreases register's value by one
	public void decrement(byte address) {
		
		load(address, fetch(address) - 1);
	}
	
	public boolean isValidGPRAddress(byte gprAddress) {
		
		return gprAddress >=0 && gprAddress < getSize();
	}
	
	public byte getInterruptId() {
		
		return (byte) fetch(INTERRUPT_ID_INDEX);
	}
	
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
	 *         invalid address: -1 invalid mode: -6 divide by zero: -8
	 *         unsuccessful operand fetch: -9 invalid operation code: -10
	 */
	byte execute() {

		byte status = 0;
		while (executionTime < timeSlice) {

			status = executeInstruction();
		} // end of while loop

		logger.info("Time slice complete");
		logger.info("Dumping CPU registers and used temporary memory.");
		CPU.dumpRegisters();

		return status;
	} // end of execute program module
}
