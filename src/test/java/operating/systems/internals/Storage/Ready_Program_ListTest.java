package operating.systems.internals.Storage;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

import operating.systems.internals.DecimalMachine.Process_Control_Block;

public class Ready_Program_ListTest {

	@Test
	public void addNullProgramToEmptyListTest() throws FileNotFoundException{
		
		short amSize = 20;
		Application_Memory am = new Application_Memory(amSize);
		short osmPointer = am.load("Null_Process");
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block(osmPointer, priority);
		Ready_Program_List rpl = new Ready_Program_List();
		
		assertTrue(rpl.add(pcb));
	}
	
	@Test
	public void aTest() throws FileNotFoundException{
		
		short amSize = 20;
		Application_Memory am = new Application_Memory(amSize);
		short osmPointer = am.load("Null_Process");
		byte priority = 0;
		Process_Control_Block pcb = new Process_Control_Block(osmPointer, priority);
		Ready_Program_List rpl = new Ready_Program_List();
		
		assertTrue(rpl.add(pcb));
	}
}
