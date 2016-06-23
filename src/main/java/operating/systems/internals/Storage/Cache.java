package operating.systems.internals.Storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cache extends Memory{
	
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
	
	private static final Logger logger = LogManager.getLogger("Machine_Implementation");
	
	public Cache(byte size) {
		
		super(size);
	}
	
	@Override
	public void dump() {

		// print register titles
		logger.info("GPRs:\tG0\tG1\tG2\tG3\tG4\tG5\tG6\tG7\tSP\tPC");

		// print register values
		for (short i = 0; i <= size(); i++)
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
		
		return gprAddress >=0 && gprAddress < size();
	}
}