package operating.systems.internals.DecimalMachine;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.Test;

public class MachineTest {

	/*@Test
	public void addPathTest() {

		final Machine machine = new Machine();
		final String path = "target/classes/";
		final String FILE_NAME = "p75";
		final String d = machine.addPath(FILE_NAME);
		boolean b = d.equals(path + FILE_NAME);
		assertTrue(b);
	}

	@Test
	public void absoluteLoaderPositiveTest() throws FileNotFoundException {

		final Machine machine = new Machine();
		final String FILE_NAME = "p75";
		final short STATUS = (Short) machine.absoluteLoader(FILE_NAME);

		assertTrue(STATUS >= 0);
	}

	@Test(expected = FileNotFoundException.class)
	public void absoluteLoaderFileNotFoundTest() throws FileNotFoundException {

		final Machine machine = new Machine();
		final String FILE_NAME = "imaginary";
		machine.absoluteLoader(FILE_NAME);
	}*/

	@Test
	public void insertIntoReadyQueueTest() {
		
		final Machine machine = new Machine();
		byte b = 1;
		machine.insertIntoReadyQueue(b);
	}
	
	@Test
	public void printHeaderTest() {
		final Machine machine = new Machine();
		machine.printHeader();
	}
	
	public void printRQTest() {
		final Machine machine = new Machine();
		machine.printRq();
	}
}