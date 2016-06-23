package operating.systems.internals.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application_Memory extends Memory {

	// end marker for PCB PCBmemoryFreeList freeHeapMemory and other lists.
	public static final byte END_OF_LIST_INDICATOR = -1;

	private static final Logger logger = LogManager.getLogger("Main Memory");

	public Application_Memory(short size) {

		super(size);
	}

	private String addPath(String fileName) {

		String directory = "target/classes/";
		String relativePath = directory + fileName;

		return relativePath;
	}

	private Scanner getScanner(String fileName) {

		Scanner code = null;
		String s = addPath(fileName);
		try {
			File file = new File(s);
			code = new Scanner(file);
		} catch (FileNotFoundException e) {
			logger.error("Program cannot be found in working directory");
			e.printStackTrace();
		}

		return code;
	}

	private byte stop(int address) {

		logger.error("The address numbered " + address + " in the machine code is not within Java's \"short\" range");
		return -1;
	}

	/**
	 * Open the file containing HYPO machine program and load the content into
	 * the application memory. The address of the application memory the program
	 * is loaded into is included in the program code. On successful load,
	 * return the PC value in the last line. On failure, return appropriate
	 * error code.
	 * 
	 * @param filename
	 *            Name of the executable file to be loaded into main memory
	 * 
	 * @throws FileNotFoundException
	 * 
	 * @return First address (origin) of the program or error code
	 */
	public short load(String fileName) {

		Scanner code = getScanner(fileName);

		// get the program's first address
		int a = code.nextShort();
		if (!inJavasShortRange(a))
			return stop(a);

		short origin = (short) a;
		short address = origin;
		logger.info("Loading " + fileName + " into application memory");

		while (code.hasNextLine()) {
			// store program
			int instruction = code.nextInt();
			load(address, instruction);
			if (inJavasShortRange(address)){
				// read address, content from file
				if (code.hasNextShort())
					address = code.nextShort();
			} else
				return stop(address);
		}
		// End of file encountered without end of program
		// display end of file encountered without EOP error message;
		code.close();
		logger.info("The loader has reach the end of " + fileName + "'s code");

		return origin;
	} // end of absoluteLoader module

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

		short address = 0;

		if (isEmpty()) {
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
}