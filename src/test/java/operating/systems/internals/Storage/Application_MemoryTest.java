package operating.systems.internals.Storage;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

import operating.systems.internals.DecimalMachine.Program;

public class Application_MemoryTest {

	@Test
	public void loadTest() throws FileNotFoundException {
		
		Application_Memory am = new Application_Memory();
		String[] fileNames = new String[1];
		fileNames[0] = "count";
		Program[] programs = am.load(fileNames);
		short actual = programs[0].getOrigin();
		byte expected = 0;
		assertEquals(expected, actual);
	}
}
