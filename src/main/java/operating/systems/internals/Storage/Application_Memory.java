package operating.systems.internals.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.DecimalMachine.Program;

public class Application_Memory extends LinkedList<Integer> {

	private static final long serialVersionUID = 1L;

	private String workingDirectory =  "target/classes/";
	
	private byte freeSpacePointer = 0;
	
	private static final Logger logger = LogManager.getLogger("Application Memory");


	private String getFileName(String[] fileNames, byte index) {
		
		return fileNames[index];
	}
	
	private String addPath(String fileName) {

		String relativePath = workingDirectory + fileName;

		return relativePath;
	}
	
	public String getWorkingDirectory() {
		
		return workingDirectory;
	}

	private Scanner getScanner(String fileName) throws FileNotFoundException{

		Scanner code = null;
		String s = addPath(fileName);
		
		File file = new File(s);
		code = new Scanner(file);

		return code;
	}
	
	
	private Program[] programs = new Program[3];
	private byte programsIndex = 0;
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
	 * @return First address (origin) of the program first instruction or error
	 *         code
	 */
	public Program[] load(String[] fileNames) throws FileNotFoundException{
	
		byte numberOfPrograms = 0;
		if ( fileNames != null)
			numberOfPrograms = (byte) fileNames.length;
		else
			return null;
			
		if (numberOfPrograms == 0)
			return programs;
		
		String fileName = getFileName(fileNames, programsIndex);
		Scanner scanner = getScanner(fileName);
		
		programs[programsIndex] = new Program(fileName, freeSpacePointer);
		logger.info("Loading " + fileName + " into application memory...");
		while (scanner.hasNextInt()) {
			// store program
			int instruction = scanner.nextInt();
			add(freeSpacePointer, instruction);
			logger.info(freeSpacePointer++ + ": " + instruction);
		}
		programs[programsIndex].setHaltAddress((byte) (freeSpacePointer - 1));
		// End of file encountered without end of program
		// display end of file encountered without EOP error message;
		scanner.close();
		logger.info("The file \"" + fileName + "\" has been loaded into the Machine's application memory.");

		if (programsIndex == numberOfPrograms)
			return programs;
		else {
			programsIndex++;
			load(fileNames);	
			return programs;
		}
	} // end of absoluteLoader module
}