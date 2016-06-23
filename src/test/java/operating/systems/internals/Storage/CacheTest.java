package operating.systems.internals.Storage;

import static org.junit.Assert.*;

import org.junit.Test;

public class CacheTest {

	@Test
	public void getProgramCounterTest() {
		
		byte numberOfRegisters = 11;
		Cache cache = new Cache(numberOfRegisters);
		short amSize = 20;
		Application_Memory am = new Application_Memory(amSize);
		short origin = am.load("Null_Process");
		cache.setProgramCounter(origin);
		
		assertEquals(origin, cache.getProgramCounter());
	}
}
