package operating.systems.internals.DecimalMachine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public class Application_Memory {

	private final short SIZE_OF_MEMORY;
	
	private int[] memory; // main memory

	// end marker for PCB PCBmemoryFreeList freeHeapMemory and other lists.
	public static final byte END_OF_LIST_INDICATOR = -1;

	private short firstFreeTemporaryMemoryPointer;

	private final short LAST_USER_MEMORY_ADDRESS;

	private short firstHeapAddress;

	private short osMemoryPointer;
	
	private final short FREE_HEAP_SIZE_INDEX;
	
	private short freeHeapSize;
	
	private static final Logger logger = LogManager.getLogger("Main Memory");
	
	private final Process_Control_Block PCB;
	
	private final byte PCB_SIZE;

	public Application_Memory() {

		SIZE_OF_MEMORY = 7999;
		memory = new int[SIZE_OF_MEMORY];
		LAST_USER_MEMORY_ADDRESS = 3999;
		firstHeapAddress = 4000;
		firstFreeTemporaryMemoryPointer  = END_OF_LIST_INDICATOR;
		FREE_HEAP_SIZE_INDEX = FIRST_HEAP_MEMORY_ADDRESS + 1; // create free heap class
		freeHeapSize = 2000;
		PCB = new Process_Control_Block();
		PCB_SIZE = PCB.getSize();
		
		partitionMemory();
	}

	public static short getFirst_Os_Memory_Address() {
		return 6000;
	}

	public static short getLast_Os_Memory_Address() {
		return 7999;
	}

	public static boolean withinOsMemory(short pointer) {

		if (pointer < getFirst_Os_Memory_Address() || pointer > getLast_Os_Memory_Address())
			return true;

		return false;
	}

	private void partitionMemory() {

		/*
		 * Create temporary memory free list User free space starts at location
		 * 4000 FIRST_TEMPORARY_MEMORY_ADDRESS = 4000 END_OF_LIST_INDICATOR = -1
		 */
		firstFreeTemporaryMemoryPointer = FIRST_TEMPORARY_MEMORY_ADDRESS;
		
		// Mark heap stack memory empty
		load(FIRST_TEMPORARY_MEMORY_ADDRESS, END_OF_LIST_INDICATOR);
		
		// User free memory has 2000 locations
		load(USER_FREE_MEMORY_SIZE_INDEX, USER_FREE_MEMORY_SIZE);
	}
	
	public short getFirstTemporaryMemoryAddress() {
		
		return FIRST_HEAP_MEMORY_ADDRESS;
	}
	
	public void load(int address, int code) {
		
		memory[address] = code;
	}
	
	public int fetch(short address) {
		
		return memory[address];
	}
	
	public short getSizeOfMemory() {
		
		return SIZE_OF_MEMORY;
	}
	
	public boolean isMemoryEmpty() {

		boolean status = true;
		
		if (memory == null)
			return true;

		
		for (short i = 0; i < getSizeOfMemory() && status == true; i++) {
			Integer memoryStore = fetch(i);
			if (!memoryStore.equals(0)) {
				status = false;
			}
		}
		
		return status;
	}

	/**
	 * DumpMemory
	 * 
	 * Task Description:
	 * 
	 * 
	 * @param aString
	 *            String to be displayed
	 * @param StartAddress
	 *            Start address of memory location
	 * @param Size
	 *            Number of locations to dump
	 */
	public void dumpMemory() {

		short address = getFirstTemporaryMemoryAddress();

		if (isMemoryEmpty()) {
			logger.info("Memory is Empty");
			return;
		}

		// print memory header
		logger.info("\nAddress\t+0\t+1\t+2\t+3\t+4\t+5\t+6\t+7\t+8\t+9");
		int lineNumber = address;

		byte numberOfZeros = 0;
		byte noMoreData = 10;

		while (numberOfZeros < noMoreData) {
			System.out.print(lineNumber);
			int i = 0;
			while (i < 9) // print 10 values from memory
			{
				Integer memoryStore = fetch(address);
				logger.info("\t" + memoryStore);
				if (memoryStore.equals(0))
					numberOfZeros++;
				address++;
				i++;
			} // end of inner while i < 9
				// Print the last memory value of the line.
			logger.info("\t" + fetch(address++));

			lineNumber += 10;// Increment line title
		} // end of outer while numberOfZeros < noMoreData

	} // end of dump memory module

	

	/**
	 * AllocateOSMemory allocates memory for each process
	 * 
	 * @retrun fist address of a process control block or error
	 */
	public short allocateOSMemory() {
		final byte NO_FREE_MEMORY = -11;
		final byte PCB_SIZE_INDEX = 1;
		final byte notOK = -1;

		// Allocate memory from OS free space, which is organized as link
		if (osMemoryPointer == END_OF_LIST_INDICATOR) {
			logger.error("No free OS memory");
			return NO_FREE_MEMORY; // ErrorNoFreeMemory is constant set to < 0
		}

		short currentPointer = osMemoryPointer;
		short Previouspointer = (short) memory[currentPointer];
		while (currentPointer != END_OF_LIST_INDICATOR) {
			// Check each block in the link list until block with requested
			// memory size is found
			if (fetch((short) (currentPointer + PCB_SIZE_INDEX)) == PCB_SIZE) { 
				// Found block with requested size. Adjust pointers
				if (currentPointer == osMemoryPointer) // first block
				{
					// first entry is pointer to next block
					osMemoryPointer = (short) fetch(currentPointer); 
					// reset next pointer in the allocated block
					load(currentPointer, END_OF_LIST_INDICATOR); 

					return currentPointer; // return memory address
				} else // not first block
				{
					// point to next block
					load(Previouspointer, fetch(currentPointer)); 
					// reset next pointer in the allocated block
					load(currentPointer, END_OF_LIST_INDICATOR); 
					
					return currentPointer; // return memory address
				}
				// Found block with size greater than requested size
			} else if (fetch((short) (currentPointer + PCB_SIZE_INDEX)) > PCB_SIZE) { 
				if (currentPointer == osMemoryPointer) // first block
				{
					// move next block pointer
					load(currentPointer + PCB_SIZE, fetch(currentPointer)); 
					
					// need comment ??????????????????????????????????????????????????????????????????????????????????????????
					load(currentPointer + PCB_SIZE + 1, fetch((short) (currentPointer + 1)) - PCB_SIZE);

					// address of reduced block
					osMemoryPointer = (short) (currentPointer + PCB_SIZE); 
					// reset next pointer in the allocated block
					load(currentPointer, END_OF_LIST_INDICATOR);
					
					return currentPointer; // return memory address
					
				} else // not first block
				{
					// move next block pointer
					load(currentPointer + PCB_SIZE, fetch(currentPointer)); 
					load(currentPointer + PCB_SIZE + 1, fetch((short) (currentPointer + 1)) - PCB_SIZE);
					
					// address of reduced block
					load(Previouspointer, currentPointer + PCB_SIZE); 
					
					// reset next pointer in the allocated block
					memory[currentPointer] = END_OF_LIST_INDICATOR; 
					
					// return memory address
					return currentPointer; 
				}
			} else // small block
			{ // look at next block
				Previouspointer = currentPointer;
				currentPointer = (short) fetch(currentPointer);
			}
		} // end of while currentPointer loop

		logger.error("No free OS memory error");
		return notOK; // ErrorNoFreeMemory is constant set to < 0
	} // end of Allocate OS Memory() module
	
	
	public short getFirstFreeTemporaryMemoryPointer() {
		
		return firstFreeTemporaryMemoryPointer;
	}
	
	public void setFirstFreeTemporaryMemoryPointer(short pointer) {
		
		firstFreeTemporaryMemoryPointer = pointer;
	}
}
