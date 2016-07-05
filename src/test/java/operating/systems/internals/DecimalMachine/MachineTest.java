package operating.systems.internals.DecimalMachine;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

public class MachineTest {

	@Test
	public void runTest() throws FileNotFoundException{
		
		Machine machine = new Machine();
		byte priority = 127;
		Byte status = machine.run("p150", priority);
		
		Byte haltCode = machine.getHaltCode();
		
		assertTrue(status.equals(haltCode));
	}

}
