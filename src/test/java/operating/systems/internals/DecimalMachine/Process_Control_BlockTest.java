package operating.systems.internals.DecimalMachine;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

import operating.systems.internals.Storage.Application_Memory;

public class Process_Control_BlockTest {

	@Test
	public void getProgramCounterTest() throws FileNotFoundException {

		Application_Memory am = new Application_Memory();
		String[] fileNames = new String[1];
		fileNames[0] = "Null_Process";
		Program[] programs = am.load(fileNames);
		byte amPointer = programs[0].getOrigin();
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block("Null_Process", amPointer, programs[0].getHaltAddress(),
				priority);
		short programCounter = pcb.getProgramCounter();

		assertEquals(programCounter, amPointer);
	}

	@Test
	public void incrementProgramCounterTest() throws FileNotFoundException {

		Application_Memory am = new Application_Memory();
		String[] fileNames = new String[1];
		fileNames[0] = "Null_Process";
		Program[] programs = am.load(fileNames);
		byte amPointer = programs[0].getOrigin();
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block("Null_Process", amPointer, programs[0].getHaltAddress(),
				priority);
		short unincrementedPc = pcb.getProgramCounter();
		pcb.incrementProgramCounter();
		short incrementedPc = pcb.getProgramCounter();

		assertEquals(incrementedPc, unincrementedPc + 1);
	}

}
