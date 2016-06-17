package operating.systems.internals.DecimalMachine;

import operating.systems.internals.Storage.Application_Memory;
import operating.systems.internals.Storage.Central_Processing_Unit;
import operating.systems.internals.Storage.Operating_System_Memory;

public class Process_Control_Block extends Central_Processing_Unit{

	private final byte PRIORITY_INDEX;
	private final byte SIZE;
	private final byte NEXT_PCB_INDEX;
	private final byte END_OF_LIST_MARKER;
	private final byte PROCESS_ID_INDEX;
	private final byte STATE_INDEX;
	private final char READY_STATE;
	//private final char READY_STATE;
	final char WAITING_STATE;
	private final byte PC_INDEX;
	private final byte GPU_INDEX;
	private final byte SP_INDEX;
	private final Operating_System_Memory OSM;
	private final Short POINTER;

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
	public Process_Control_Block(Operating_System_Memory osm, short pcbPointer, byte numberOfRegisters, short processId, byte priority, char state) {
		
		super(numberOfRegisters);

		PRIORITY_INDEX = 4;
		SIZE = 20;
		NEXT_PCB_INDEX = 0;
		END_OF_LIST_MARKER = Application_Memory.END_OF_LIST_INDICATOR;
		PROCESS_ID_INDEX = 1;
		STATE_INDEX = 2;
		READY_STATE = 'R';
		WAITING_STATE = 'W';
		PC_INDEX = 19;
		GPU_INDEX = 10;
		SP_INDEX = 18;
		OSM = osm;
		POINTER = pcbPointer;
		
		// END_OF_LIST_MARKER is a constant set to -1
		setNextPcbPointer(END_OF_LIST_MARKER);

		setProcessId(processId);
		
		// DefaultPriority is a constant set to 128
		setPriority(priority);
		
		// Ready State is a constant set to 'W'
		setState(state);

		return;
	} // end of set process control block

	private void setProcessId(short processId) {
		
		OSM.load(POINTER + PROCESS_ID_INDEX, processId);
	}
	
	

	public short getSize() {

		return SIZE;
	}

	public byte getStateIndex() {

		return STATE_INDEX;
	}

	public void setState(char state) {

		OSM.load(POINTER + STATE_INDEX, state);
	}
	
	public char getReadyStateIndicator() {
		
		return READY_STATE;
	}

	public void setNextPcbPointer(short nextPointer) {

		OSM.load(POINTER + NEXT_PCB_INDEX, nextPointer);
	}

	public short getNextPcbPointer(short pcbPointer) {

		return (short) OSM.fetch((short) (POINTER + NEXT_PCB_INDEX));
	}

	public void setProgramCounter(short programCounter) {

		OSM.load(POINTER + PC_INDEX, programCounter);
	}
	
	public void setPriority(short priority) {

		OSM.load(POINTER + PRIORITY_INDEX, priority);
	}
	
	public byte getNextPcbIndex() {
		
		return NEXT_PCB_INDEX;
	}
	
	public byte getPriorityIndex() {
		
		return PRIORITY_INDEX;
	}
	
	public byte getProcessIdIndex() {
		
		return PROCESS_ID_INDEX;
	}
	
	public byte getProcessId(short pcbPointer) {
		
		return (byte) OSM.fetch((short) (POINTER + PROCESS_ID_INDEX));
	}
	
	public int[] getGprValues(byte numberOfGPRs) {
		
		int[] a = new int[numberOfGPRs];
		for (int i = 0; i < numberOfGPRs + 1; i++)
			a[i] = OSM.fetch((short) (POINTER + GPU_INDEX + i));
		
		return a;
	}
	
	public short getStackPointer(Operating_System_Memory OSM, short pcbPointer) {
		
		return (short) OSM.fetch((short) (pcbPointer + SP_INDEX));

		// Set system mode to User mode
		// byte userMode = 2;
		// psr = userMode; // UserMode is 1, OSMode is 0.
	}
	
	public short getProgramCounter(Operating_System_Memory OSM, short pcbPointer) {
		
		return (short) OSM.fetch((short) (POINTER + PC_INDEX));

		// Set system mode to User mode
		// byte userMode = 2;
		// psr = userMode; // UserMode is 1, OSMode is 0.
	}
	
	public byte getPriority() {
		
		return (byte) OSM.fetch((short) (POINTER + PRIORITY_INDEX));
	}
	
	public String toString() {
		
		return POINTER.toString();
	}
	
	public boolean equals(short pointer) {
		
		return this.POINTER.equals(pointer);
	}
	
	public byte comparePriority(byte priority) {
		
		Byte thisPriority = this.getPriority();

		return (byte) thisPriority.compareTo(priority);
	}
}
