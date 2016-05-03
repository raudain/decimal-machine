package operating.systems.internals.DecimalMachine;

import java.io.FileNotFoundException;

import org.junit.Test;

public class MachineTest {

	@Test
	public void addDirectoryTest() {
		
		final Machine machine = new Machine();
		final String DIRECTORY = "C:\\Users\\roody.audain\\"
				+ "workspace\\Concepts\\"
				+ "DecimalMachine\\src\\main\\"
				+ "resources\\";
		final String FILE_NAME = "p75";
		final String STATUS = machine.addDirectory(FILE_NAME);
		
		assert (STATUS.equals(DIRECTORY + FILE_NAME));
	}
	
	@Test
	public void absoluteLoaderPositiveTest() {
		
		final Machine machine = new Machine();
		final String FILE_NAME = "p75";
		final short STATUS = machine.absoluteLoader(FILE_NAME);
		
		assert (STATUS >= 0);
	}

	@Test
	public void absoluteLoaderFileNotFoundTest() {
		
		final Machine machine = new Machine();
		final String FILE_NAME = "imaginary";
		final short FILE_NOT_FOUND_INDICATOR = -5;
		final short STATUS = machine.absoluteLoader(FILE_NAME);
		
		assert (FILE_NOT_FOUND_INDICATOR == STATUS);
	}
}