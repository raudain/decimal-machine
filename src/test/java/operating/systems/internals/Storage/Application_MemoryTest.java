package operating.systems.internals.Storage;

import static org.junit.Assert.*;

import org.junit.Test;

public class Application_MemoryTest {

	@Test
	public void loadTest() {
		
		short size = 19;
		Application_Memory am = new Application_Memory(size);
		Short actual = am.load("p150");
		Short expected = 1;
		assertEquals(expected, actual);
	}
}
