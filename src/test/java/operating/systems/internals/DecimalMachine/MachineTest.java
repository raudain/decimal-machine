package operating.systems.internals.DecimalMachine;

import static org.junit.Assert.*;

import org.junit.Test;

import operating.systems.internals.Storage.Application_Memory;
import operating.systems.internals.Storage.Cache;
import operating.systems.internals.Storage.Operating_System_Memory;
import operating.systems.internals.Storage.Ready_Program_List;

public class MachineTest {

	@Test
	public void runTest() {
		
		Machine machine = new Machine();
		byte priority = 127;
		Byte status = machine.run("p150", priority);
		Byte haltCode = machine.getHaltCode();
		
		assertTrue(status.equals(haltCode));
	}

}
