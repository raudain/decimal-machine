package operating.systems.internals.Storage;

import static org.junit.Assert.*;

import org.junit.Test;

import operating.systems.internals.DecimalMachine.Process_Control_Block;

public class Ready_Program_ListTest {

	@Test
	public void addNullProgramToEmptyListTest() {
		
		byte numberOfRegisters = 11;
		Cache cache = new Cache(numberOfRegisters);
		short osmSize = 21;
		Operating_System_Memory osm = new Operating_System_Memory(osmSize);
		short amSize = 20;
		Application_Memory am = new Application_Memory(amSize);
		short osmPointer = am.load("Null_Process");
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block((byte) cache.size(), osm, osmPointer, priority);
		Ready_Program_List rpl = new Ready_Program_List();
		
		assertTrue(rpl.add(pcb));
	}

}
