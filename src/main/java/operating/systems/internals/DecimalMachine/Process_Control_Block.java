package operating.systems.internals.DecimalMachine;

import operating.systems.internals.Storage.Cache;
import operating.systems.internals.Storage.Operating_System_Memory;

/* Array Index	Order of Items in the PCB 
↓                   ↓
0           Next PCB pointer
1                  PID
2                 State
3         Reason for waiting Code
4                Priority
5           Stack start address
6               Stack size
7       Message queue start address
8           Message queue size
9      Number of messages in the queue
10                GPR 0 is status
11 GPR 1 and the first memory address for tempory storage
*  if the process has recieved storage.
12                GPR 2 and the amount of tempory storge slots
13                GPR 3
14                GPR 4
15                GPR 5
16                GPR 6
17                GPR 7
18                  SP
19                  PC
*/
public class Process_Control_Block {

	private static final byte SIZE = 20;
	private final Operating_System_Memory OSM;
	private final Short OSM_POINTER;
	private byte priority;
	
	private static final byte PRIORITY_INDEX = 2;
	private final byte PC_INDEX;
	private final byte GPU_INDEX;
	private final byte SP_INDEX;


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
	public Process_Control_Block(Operating_System_Memory osm, short osmPointer, byte priority) {



		OSM = osm;
		OSM_POINTER = osmPointer;
		this.priority = priority;
		PC_INDEX = 19;
		GPU_INDEX = 10;
		SP_INDEX = 18;
	} // End of constructor

	public static Process_Control_Block getPcb(Operating_System_Memory osm, short osmPointer) {

		byte priority = (byte) osm.fetch((short) (osmPointer + PRIORITY_INDEX));
		Process_Control_Block pcb = new Process_Control_Block(osm, osmPointer, priority);

		return pcb;
	} // End of constructor

	public short getSize() {

		return SIZE;
	}

	public short getOsmPointer() {
		
		return OSM_POINTER;
	}
	
	public void setProgramCounter(short programCounter) {

		OSM.load((short) (OSM_POINTER + PC_INDEX), programCounter);
	}

	public int[] getGprValues(byte numberOfGPRs) {

		int[] a = new int[numberOfGPRs];
		for (int i = 0; i < numberOfGPRs + 1; i++)
			a[i] = OSM.fetch((short) (OSM_POINTER + GPU_INDEX + i));

		return a;
	}

	public short getStackPointer(Operating_System_Memory OSM, short pcbPointer) {

		return (short) OSM.fetch((short) (pcbPointer + SP_INDEX));

		// Set system mode to User mode
		// byte userMode = 2;
		// psr = userMode; // UserMode is 1, OSMode is 0.
	}

	public short getProgramCounter(Operating_System_Memory OSM, short pcbPointer) {

		return (short) OSM.fetch((short) (OSM_POINTER + PC_INDEX));

		// Set system mode to User mode
		// byte userMode = 2;
		// psr = userMode; // UserMode is 1, OSMode is 0.
	}

	public byte getPriority() {

		return priority;
	}

	public String toString() {

		return OSM_POINTER.toString();
	}



	public boolean hasGreaterPriority(byte priority) {

		Byte thisPriority = this.priority;

		if (thisPriority.compareTo(priority) > 0)
			return true;
		else
			return false;
	}
}
