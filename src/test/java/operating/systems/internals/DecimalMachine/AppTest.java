package operating.systems.internals.DecimalMachine;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	
	/**
	 * Tests initialization
	 */
	public void testGetInput() {
		UI app = new UI();
		String input = app.getInput();
		String emptyString = "";
		assert(!input.equals(emptyString));
	}
}
