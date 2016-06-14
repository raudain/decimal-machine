package operating.systems.internals.DecimalMachine;

import operating.systems.internals.Storage.Application_Memory;
import operating.systems.internals.Storage.Central_Processing_Unit;
import operating.systems.internals.Storage.Simple_Memory;

public class Process_Control_Block extends Central_Processing_Unit{

	private final byte PRIORITY_INDEX;
	private final byte SIZE;
	private final byte NEXT_PCB_INDEX;
	private final byte END_OF_LIST_MARKER;
	private final byte PROCESS_ID_INDEX;
	private final byte STATE_INDEX;
	//private final char READY_STATE;
	final char WAITING_STATE;
	private final byte PC_INDEX;
	private final byte GPU_INDEX;
	private final byte SP_INDEX;

	public Process_Control_Block(byte numberOfRegisters) {

		super(numberOfRegisters);
		
		PRIORITY_INDEX = 4;
		SIZE = 20;
		NEXT_PCB_INDEX = 0;
		END_OF_LIST_MARKER = Application_Memory.END_OF_LIST_INDICATOR;
		PROCESS_ID_INDEX = 1;
		STATE_INDEX = 2;
		//READY_STATE = 'R';
		WAITING_STATE = 'W';
		PC_INDEX = 19;
		GPU_INDEX = 10;
		SP_INDEX = 18;
	}

	private void setProcessId(Simple_Memory OSM, short pcbPointer, short processId) {
		
		OSM.load(pcbPointer + PROCESS_ID_INDEX, processId);
	}
	
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
	public void setPcb(Simple_Memory OSM, short pcbPointer, short processId, byte priority, char state) {
		// Initialize all values to zero
		for (int i = 0; i < SIZE; i++)
			OSM.load(pcbPointer + i, 0);

		byte Priorityindex = 4;
		final short DefaultPriority = 128;

		// END_OF_LIST_MARKER is a constant set to -1
		setNextPcbPointer(OSM, pcbPointer, END_OF_LIST_MARKER);

		setProcessId(OSM, pcbPointer, processId);
		
		// DefaultPriority is a constant set to 128
		setPriority(OSM, pcbPointer, priority);
		
		// Ready State is a constant set to 'W'
		setState(OSM, pcbPointer, state);

		return;
	} // end of set process control block

	public short getSize() {

		return SIZE;
	}

	/**
	 * initializePCB fills out some PCB values for a ready process.
	 * 
	 * @param PCBpointer
	 *            Start of a particular process control block about to be
	 *            zeroed.
	 */
	public void initialize(Simple_Memory OSM, short PCBpointer, byte processId, byte priority, char state) {
		// Initialize all values to zero
		for (int i = 0; i < SIZE; i++)
			OSM.load(PCBpointer + i, 0);

		// END_OF_LIST_MARKER is a constant set to -1
		OSM.load(PCBpointer + NEXT_PCB_INDEX, Application_Memory.END_OF_LIST_INDICATOR);

		/*
		 * ProcessID is a global variable and is initialized to 1. Allocate PID
		 * and set it in the PCB pid zero is invalid
		 */
		OSM.load(PCBpointer + PROCESS_ID_INDEX, processId);
		
		// always set to default priority which is 128
		OSM.load(PCBpointer + PRIORITY_INDEX, priority);
		
		// always set to ready state which is 'R'
		OSM.load(PCBpointer + STATE_INDEX, state); 


		return;
	} // end of Initialize PCB method

	public byte getStateIndex() {

		return STATE_INDEX;
	}

	public void setState(Simple_Memory OSM, short pcbPointer, char state) {

		OSM.load(pcbPointer + STATE_INDEX, state);
	}

	public void setNextPcbPointer(Simple_Memory OSM, short pcbPointer, short nextPointer) {

		OSM.load(pcbPointer + NEXT_PCB_INDEX, nextPointer);
	}

	public short getNextPcbPointer(Simple_Memory OSM, short pcbPointer) {

		return (short) OSM.fetch((short) (pcbPointer + NEXT_PCB_INDEX));
	}

	public void setProgramCounter(Simple_Memory OSM, short pcbPointer, short programCounter) {

		OSM.load(pcbPointer + PC_INDEX, programCounter);
	}
	
	public void setPriority(Simple_Memory OSM, short pcbPointer, short priority) {

		OSM.load(pcbPointer + PRIORITY_INDEX, priority);
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
	
	public byte getProcessId(Simple_Memory OSM, short pcbPointer) {
		
		return (byte) OSM.fetch((short) (pcbPointer + PROCESS_ID_INDEX));
	}
	
	public int[] getGprValues(Simple_Memory OSM, short pcbPointer, byte numberOfGPRs) {
		
		int[] a = new int[numberOfGPRs];
		for (int i = 0; i < numberOfGPRs + 1; i++)
			a[i] = OSM.fetch((short) (pcbPointer + GPU_INDEX + i));
		
		return a;
	}
	
	public short getStackPointer(Simple_Memory OSM, short pcbPointer) {
		
		return (short) OSM.fetch((short) (pcbPointer + SP_INDEX));

		// Set system mode to User mode
		// byte userMode = 2;
		// psr = userMode; // UserMode is 1, OSMode is 0.
	}
	
	public short getProgramCounter(Simple_Memory OSM, short pcbPointer) {
		
		return (short) OSM.fetch((short) (pcbPointer + PC_INDEX));

		// Set system mode to User mode
		// byte userMode = 2;
		// psr = userMode; // UserMode is 1, OSMode is 0.
	}
	
	public byte getPriority(Simple_Memory OSM, short pcbPointer) {
		
		return (byte) OSM.fetch((short) (pcbPointer + PRIORITY_INDEX));
	}
}
