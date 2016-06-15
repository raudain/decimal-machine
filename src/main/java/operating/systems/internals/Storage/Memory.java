package operating.systems.internals.Storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Memory {

	int[] memory;
	short freeSpacePointer;
	private final byte FIRST_MEMORY_ADDRESS;
	private final byte sizeIndex;
	private final byte END_INDICATOR;
	private short firstFreeMemoryPointer;
	
	private static final Logger logger = LogManager.getLogger("Memory");
	
	public Memory(short size) {
		
		memory = new int[size];
		
		partition(size);
		
		FIRST_MEMORY_ADDRESS = 1;
		
		sizeIndex = 2;
		
		END_INDICATOR = -1;
		
		firstFreeMemoryPointer  = END_INDICATOR;
	}
	
	public void load(int address, int code) {
		
		memory[address] = code;
	}
	
	// keeps the size and free memory indicators in the unused space in memory
	private void partition(short size) {
		
		freeSpacePointer = 1;
		
		// Mark memory empty
		load(FIRST_MEMORY_ADDRESS, END_INDICATOR);
		
		// User free memory has 2000 locations
		load(sizeIndex, size);
	}
	
	public int fetch(short address) {
		
		return memory[address];
	}
	
	public short getSize() {
		
		return (short) memory.length;
	}
	
	public byte getFirstMemoryAddress() {
		
		return FIRST_MEMORY_ADDRESS;
	}
	
	public byte getEndIndicator() {
		
		return END_INDICATOR;
	}
	
	public short getFirstFreeTemporaryMemoryPointer() {
		
		return firstFreeMemoryPointer;
	}
	
	public void setFirstFreeTemporaryMemoryPointer(short pointer) {
		
		firstFreeMemoryPointer = pointer;
	}
	/**
	 * Gives the next free memory addresses block of the requested size to
	 * a program
	 * 
	 * @param requestedSize
	 *            The amount of memory requested by a program.
	 * @return Error status or the first address for the memory block the is
	 *         given.
	 */
	public short allocate(short requestedSize) {
		// Allocate memory from User free space, which is organized as link
		// copy OS code and modify to use freeHeapMemory

		final byte NO_MEMORY_FREE = -11;
		final byte INVALID_SIZE = -12;
		final byte notOK = -1;
		
		// Allocate memory from OS free space, which is organized as link
		if (firstFreeMemoryPointer == END_INDICATOR) {
			logger.error("There is no heap memory free");
			return NO_MEMORY_FREE; // ErrorNoFreeMemory is constant set to < 0
		}

		if (requestedSize < 0 || requestedSize > 2000) {
			System.out.println("Invalid memory request size");
			return INVALID_SIZE; // ErrorInvalidMemorySize is constant < 0
		}

		if (requestedSize == 1)
			requestedSize = 2; // Minimum allocated memory is 2 locations

		Short tempPointer1 = firstFreeMemoryPointer;
		short tempPointer2 = (short) fetch(tempPointer1);
		/* Check each block in the link list until block with requested memory size is found */
		while (tempPointer1 != END_INDICATOR) {

			Short i = (short) fetch((short) (tempPointer1 + 1));

			// if free memory block is the exact size needed. Adjust pointers
			if (i.equals(requestedSize)) {
				// first block
				if (tempPointer1.equals(firstFreeMemoryPointer))
				{
					// first entry is pointer to next block
					setFirstFreeTemporaryMemoryPointer(tempPointer1);
					// reset next pointer
					load(tempPointer1, END_INDICATOR); 
					// in the allocated block
					return tempPointer1; // return memory address
				} else // not first block
				{
					// point to next block
					load(tempPointer2, fetch(tempPointer1));

					// reset next pointer in the allocated block
					load(tempPointer1, END_INDICATOR);
					return tempPointer1; // return memory address
				}
				// Found block with size greater than requested size
			} else if (fetch( (short) (tempPointer1 + 1)) > requestedSize) {
				// first block
				if (tempPointer1 == getFirstFreeTemporaryMemoryPointer()) 
				{
					// move next block pointer
					load(tempPointer1 + requestedSize, fetch(tempPointer1));
					load(tempPointer1 + requestedSize + 1, fetch((short) (tempPointer1 + 1)) - requestedSize);
					// address of reduced block
					setFirstFreeTemporaryMemoryPointer((short) (tempPointer1 + requestedSize));
					// reset next pointer in the allocated block
					load(tempPointer1, END_INDICATOR);

					return tempPointer1; // return memory address
				} else // not first block
				{
					// move next block pointer
					load(tempPointer1 + requestedSize, fetch(tempPointer1)); 
					load(tempPointer1 + requestedSize + 1, fetch((short) (tempPointer1 + 1)) - requestedSize);
					
					// address of reduced block
					load(tempPointer2, tempPointer1 + requestedSize); 
					// reset next pointer
					load(tempPointer1, END_INDICATOR); 
					// in the allocated block
					return tempPointer1; // return memory address
				}
			} else // small block
			{ // look at next block
				tempPointer2 = tempPointer1;
				tempPointer1 = (short) fetch(tempPointer1);
			}
		} // end of while loop

		System.out.println("Error: No free stack memory error");
		return notOK; // ErrorNoFreeMemory is constant set to < 0
	} // end of allocate user memory module
}
