package operating.systems.internals.Storage;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

import operating.systems.internals.DecimalMachine.Program;

public class CacheTest {

	@Test
	public void getProgramCounterTest() throws FileNotFoundException{
		
		byte numberOfRegisters = 11;
		Cache cache = new Cache(numberOfRegisters);
		Application_Memory am = new Application_Memory();
		String[] fileNames = new String[1];
		fileNames[0] = "Null_Process";
		Program[] programs = am.load(fileNames);
		short origin = programs[0].getOrigin();
		cache.setProgramCounter(origin);
		
		assertEquals(origin, cache.getProgramCounter());
	}
	
	@Test
	public void incrementProgramCounterTest() {
		
		byte numberOfRegisters = 11;
		Cache cache = new Cache(numberOfRegisters);
		short unincrementedPc = cache.getProgramCounter();
		cache.incrementProgramCounter();
		short incrementedPc = cache.getProgramCounter();
		
		assertEquals(incrementedPc, unincrementedPc + 1);
	}
}
