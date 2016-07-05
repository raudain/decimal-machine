package operating.systems.internals.DecimalMachine;

import operating.systems.internals.Storage.Cache;

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
public class Process_Control_Block extends Cache{

	private static final byte SIZE = 20;
	private byte priority;

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
	public Process_Control_Block(short amPointer, byte priority) {

		super((byte) 3);
		
		this.priority = priority;
		
		
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
}
