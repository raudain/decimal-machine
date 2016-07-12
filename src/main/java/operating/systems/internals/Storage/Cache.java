package operating.systems.internals.Storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cache {
	
	private int[] memory;

	private final short SIZE;

	public Cache(byte size) {

		SIZE = size;
		memory = new int[SIZE];
	}

	protected boolean addressInMemoryRange(short address) {

		if (address < 0 || address >= SIZE)
			return false;
		else
			return true;
	}

	public void load(short address, int code) {

		if (addressInMemoryRange(address))
			memory[address] = code;
		else
			logger.warn("The address numbered " + address + " was not in range so was not loaded into memory");
	}

	public int fetch(short address) {

		if (addressInMemoryRange(address))
			return memory[address];
		else {
			logger.warn("The data in address numbered " + address + " was not in range so could not be fetched");
			return 0;
		}
	}

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
	
	private static final Logger logger = LogManager.getLogger("Cache");
	
	
	public void setProgramCounter(short pc) {
		
		programCounter = pc;
	}
	
	public short getProgramCounter() {
		
		return programCounter;
	}
	
	public void incrementProgramCounter() {
		
		programCounter++;
	}
	
	public boolean isValidGPRAddress(byte gprAddress) {
		
		return gprAddress >=0 && gprAddress < SIZE;
	}	
}