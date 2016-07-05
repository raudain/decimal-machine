package operating.systems.internals.Storage;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

public class CacheTest {

	@Test
	public void getProgramCounterTest() throws FileNotFoundException{
		
		byte numberOfRegisters = 11;
		Cache cache = new Cache(numberOfRegisters);
		short amSize = 20;
		Application_Memory am = new Application_Memory(amSize);
		short origin = am.load("Null_Process");
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
