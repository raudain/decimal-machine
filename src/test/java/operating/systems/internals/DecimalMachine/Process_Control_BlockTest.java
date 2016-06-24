package operating.systems.internals.DecimalMachine;

import static org.junit.Assert.*;

import org.junit.Test;

import operating.systems.internals.Storage.Application_Memory;

public class Process_Control_BlockTest {
	
	@Test
	public void getProgramCounterTest() {
		
		short amSize = 20;
		Application_Memory am = new Application_Memory(amSize);
		short amPointer = am.load("Null_Process");
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block(amPointer, priority);
		short programCounter = pcb.getProgramCounter();
		
		assertEquals(programCounter, amPointer);
	}
	
	@Test
	public void incrementProgramCounterTest() {
		
		short amSize = 20;
		Application_Memory am = new Application_Memory(amSize);
		short amPointer = am.load("Null_Process");
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block(amPointer, priority);
		short unincrementedPc = pcb.getProgramCounter();
		pcb.incrementProgramCounter();
		short incrementedPc = pcb.getProgramCounter();
		
		assertEquals(incrementedPc, unincrementedPc + 1);
	}

}
