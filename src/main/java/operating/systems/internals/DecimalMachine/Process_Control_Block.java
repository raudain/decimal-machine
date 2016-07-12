package operating.systems.internals.DecimalMachine;

import operating.systems.internals.Storage.Cache;

public class Process_Control_Block extends Cache{

	private static final byte SIZE = 20;
	private final String NAME;
	private byte priority;
	private int instructionExecutionCount;
	private final byte origin;
	private final byte haltAddress;
	/**
	 * setPCB fills out some values the child process's process control block
	 * for a waiting process.
	 * 
	 * @param PCBpointer
	 *            Start of a particular process control block about to be
	 *            zeroed.
	 * 
	 * @param pr
	 *            Memory location where the process ID is stored
	 */
	public Process_Control_Block(String fileName, byte amPointer, byte haltInstructionAddress, byte priority) {

		super((byte) 3);
		NAME = fileName;
		this.priority = priority;
		instructionExecutionCount = 0;
		origin = amPointer;
		haltAddress = haltInstructionAddress;
		
		setProgramCounter(amPointer);
	} // End of constructor

	public short getSize() {

		return SIZE;
	}

	public byte getPriority() {

		return priority;
	}

	public boolean hasGreaterPriority(byte priority) {

		Byte thisPriority = this.priority;

		if (thisPriority.compareTo(priority) > 0)
			return true;
		else
			return false;
	}

	public void incrementExecutionCount() {
		
		instructionExecutionCount++;
	}

	public int getInstructionExecutionCount() {
		
		return instructionExecutionCount;
	}

	public String getName() {
		
		return NAME;
	}

	public boolean isAddressWithinCode(short jumpAddress) {
		
		if ( jumpAddress >= origin || jumpAddress <= haltAddress)
			return true;
		else
			return false;
	}

	public byte getOrigin() {
		
		return origin;
	}
}
