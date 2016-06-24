package operating.systems.internals.DecimalMachine;

import static org.junit.Assert.*;

import org.junit.Test;

import operating.systems.internals.Storage.Application_Memory;
import operating.systems.internals.Storage.Cache;
import operating.systems.internals.Storage.Operating_System_Memory;

public class Process_Control_BlockTest {

	@Test
	public void getOsmPointerTest() {
		
		byte numberOfRegisters = 11;
		Cache cache = new Cache(numberOfRegisters);
		short osmSize = 21;
		Operating_System_Memory osm = new Operating_System_Memory(osmSize);
		short amSize = 20;
		Application_Memory am = new Application_Memory(amSize);
		short osmPointer = am.load("Null_Process");
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block((byte) cache.size(), osm, osmPointer, priority);
		
		assertEquals(pcb.getOsmPointer(), osmPointer);
	}
	
	@Test
	public void incrementProgramCounterTest() {
		
		byte numberOfRegisters = 11;
		Cache cache = new Cache(numberOfRegisters);
		short osmSize = 21;
		Operating_System_Memory osm = new Operating_System_Memory(osmSize);
		short amSize = 20;
		Application_Memory am = new Application_Memory(amSize);
		short osmPointer = am.load("Null_Process");
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block((byte) cache.size(), osm, osmPointer, priority);
		short unincrementedPc = osmPointer;
		pcb.incrementProgramCounter();
		short incrementedPc = pcb.getProgramCounter();
		
		assertEquals(incrementedPc, unincrementedPc + 1);
	}

}
