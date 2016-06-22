package operating.systems.internals.Storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.DecimalMachine.Process_Control_Block;


public class Application_Memory extends Memory {
	
	private int[] memory; // main memory

	// end marker for PCB PCBmemoryFreeList freeHeapMemory and other lists.
	public static final byte END_OF_LIST_INDICATOR = -1;
		
	private static final Logger logger = LogManager.getLogger("Main Memory");
	
	public Application_Memory(short size) {

		super(size);	
	}

	public boolean isMemoryEmpty() {

		boolean status = true;
		
		if (memory == null)
			return true;

		
		for (short i = 0; i < size() && status == true; i++) {
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
	@Override
	public void dump() {

		short address = getFirstMemoryAddress();

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
			logger.info(lineNumber);
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
	
	// implement on future version
	public void deleteProgram(short originAddress) {
		
		
	}
}