package operating.systems.internals.DecimalMachine;

import operating.systems.internals.Storage.Central_Processing_Unit;
import operating.systems.internals.Storage.Operating_System_Memory;

public class Process_Control_Block extends Central_Processing_Unit {

	private static final byte SIZE = 20;
	private static final byte PROCESS_ID_INDEX = 1;
	private static final byte PRIORITY_INDEX = 2;
	private static final byte STATE_INDEX = 3;
	private final char READY_STATE;
	// private final char READY_STATE;
	final char WAITING_STATE;
	private final byte PC_INDEX;
	private final byte GPU_INDEX;
	private final byte SP_INDEX;
	private final Operating_System_Memory OSM;
	private final Short OSM_POINTER;

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
	public Process_Control_Block(byte numberOfRegisters, Operating_System_Memory osm, short osmPointer, short processId,
			byte priority, char state) {

		super(numberOfRegisters);

		OSM = osm;
		OSM_POINTER = osmPointer;

		setProcessId(processId);

		// DefaultPriority is a constant set to 128
		setPriority(priority);

		// Ready State is a constant set to 'W'
		setState(state);
		
		READY_STATE = 'R';
		WAITING_STATE = 'W';
		PC_INDEX = 19;
		GPU_INDEX = 10;
		SP_INDEX = 18;
	} // End of constructor

	public static Process_Control_Block getPcb(Operating_System_Memory osm, short pointer) {

		byte numberOfRegisters = SIZE;
		byte processId = (byte) osm.fetch((short) (pointer + PROCESS_ID_INDEX));
		byte priority = (byte) osm.fetch((short) (pointer + PRIORITY_INDEX));
		char state = (char) osm.fetch((short) (pointer + STATE_INDEX));
		Process_Control_Block pcb = new Process_Control_Block(osm, pointer, numberOfRegisters, processId, priority,
				state);

		return pcb;
	} // End of constructor

	private void setProcessId(short processId) {

		OSM.load(POINTER + PROCESS_ID_INDEX, processId);
	}

	public short getSize() {

		return SIZE;
	}

	public void setState(char state) {

		OSM.load(POINTER + STATE_INDEX, state);
	}

	public char getReadyStateIndicator() {

		return READY_STATE;
	}

	public void setProgramCounter(short programCounter) {

		OSM.load(POINTER + PC_INDEX, programCounter);
	}

	public void setPriority(short priority) {

		OSM.load(POINTER + PRIORITY_INDEX, priority);
	}

	public byte getProcessId() {

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

	public boolean hasGreaterPriority(byte priority) {

		Byte thisPriority = this.getPriority();

		if (thisPriority.compareTo(priority) > 0)
			return true;
		else
			return false;
	}
}
