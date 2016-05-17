package operating.systems.internals.DecimalMachine;

public class Process_Control_Block {

	private final byte PRIORITY_INDEX;
	private final byte SIZE;
	private final byte NEXT_PCB_INDEX;
	private final byte END_OF_LIST_MARKER;
	private final byte PROCESS_ID_INDEX;
	private byte processID;
	private final short DEFAULT_PRIORITY;
	private final byte STATE_INDEX;
	private final char READY_STATE;
	final char WAITING_STATE;
	private final byte PC_INDEX;

	public Process_Control_Block() {

		PRIORITY_INDEX = 4;
		SIZE = 20;
		NEXT_PCB_INDEX = 0;
		END_OF_LIST_MARKER = Main_Memory.END_OF_LIST_INDICATOR;
		PROCESS_ID_INDEX = 1;
		// processID = 1;
		DEFAULT_PRIORITY = 128;
		STATE_INDEX = 2;
		READY_STATE = 'R';
		WAITING_STATE = 'W';
		PC_INDEX = 19;
	}

	private void setProcessId(Main_Memory mainMemory, short PcbPointer, short processId) {
		
		mainMemory.load(PcbPointer + PROCESS_ID_INDEX, processId);
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
	public void setPcb(Main_Memory mainMemory, short PcbPointer, short processId, byte priority, char state) {
		// Initialize all values to zero
		for (int i = 0; i < SIZE; i++)
			mainMemory.load(PcbPointer + i, 0);

		byte Priorityindex = 4;
		final short DefaultPriority = 128;

		// END_OF_LIST_MARKER is a constant set to -1
		setNextPcbPointer(mainMemory, PcbPointer, END_OF_LIST_MARKER);

		setProcessId(mainMemory, PcbPointer, processId);
		
		// DefaultPriority is a constant set to 128
		setPriority(mainMemory, PcbPointer, priority);
		
		// Ready State is a constant set to 'W'
		setState(mainMemory, PcbPointer, state);

		return;
	} // end of set process control block

	public byte getSize() {

		return SIZE;
	}

	public void initialize(Main_Memory mainMemory, short pcbPointer, byte processId, byte priority, char state) {

		mainMemory.initializePCB(pcbPointer, processId, priority, state);
	}

	public byte getStateIndex() {

		return STATE_INDEX;
	}

	public void setState(Main_Memory mainMemory, short PcbPointer, char state) {

		mainMemory.load(PcbPointer + STATE_INDEX, state);
	}

	public void setNextPcbPointer(Main_Memory mainMemory, short PcbPointer, short nextPointer) {

		mainMemory.load(PcbPointer + NEXT_PCB_INDEX, nextPointer);
	}

	public short getNextPcbPointer(Main_Memory mainMemory, short PcbPointer) {

		return (short) mainMemory.fetch((short) (PcbPointer + NEXT_PCB_INDEX));
	}

	public void setProgramCounter(Main_Memory mainMemory, short PcbPointer, short programCounter) {

		mainMemory.load(PcbPointer + PC_INDEX, programCounter);
	}
	
	public void setPriority(Main_Memory mainMemory, short PcbPointer, short priority) {

		mainMemory.load(PcbPointer + PRIORITY_INDEX, priority);
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
	
}
