package operating.systems.internals.Storage;

import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cache extends LinkedList<Integer> {

	private static final long serialVersionUID = 6716543651766640811L;
	
	private byte freeGprAddressPointer;
	
	private static final Logger logger = LogManager.getLogger("Cache");
	
	public Cache() {
		super();
		freeGprAddressPointer = 1;
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
	
	@Override
	public void add(int index, Integer element) {
		
		freeGprAddressPointer++;
		super.add(index, element);
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
	
	public boolean isGprAddressWithinRange(byte gprAddress) {
		
		return gprAddress >=0 && gprAddress <= freeGprAddressPointer;
	}
	
	public void dumpRegisters() {
		
		byte numberOfRegisters = (byte) size();
		for (int i = 0; i < numberOfRegisters; i++)
			logger.info("Register number "+ i + " = " + get(i));
	}
}