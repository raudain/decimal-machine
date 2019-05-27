package operating.systems.internals.Storage;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

import operating.systems.internals.DecimalMachine.Process_Control_Block;
import operating.systems.internals.DecimalMachine.Program;

public class Ready_Program_ListTest {

	@Test
	public void addNullProgramToEmptyListTest() throws FileNotFoundException{
		
		Application_Memory am = new Application_Memory();
		String[] fileNames = new String[1];
		fileNames[0] = "Null_Process";
		Program[] programs = am.load(fileNames);
		byte amPointer = programs[0].getOrigin();
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block("Null_Process", amPointer, programs[0].getHaltAddress(), priority);
		Ready_Program_List rpl = new Ready_Program_List();
		
		assertTrue(rpl.add(pcb));
	}
	
	@Test
	public void aTest() throws FileNotFoundException{
		
		Application_Memory am = new Application_Memory();
		String[] fileNames = new String[1];
		fileNames[0] = "Null_Process";
		Program[] programs = am.load(fileNames);
		byte amPointer = programs[0].getOrigin();
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block("Null_Process", amPointer, programs[0].getHaltAddress(), priority);
		Ready_Program_List rpl = new Ready_Program_List();
		
		assertTrue(rpl.add(pcb));
	}
}
