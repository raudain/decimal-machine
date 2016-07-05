package operating.systems.internals.AssemblyCode;

import static org.junit.Assert.*;

import org.junit.Test;

import operating.systems.internals.Storage.Application_Memory;
import operating.systems.internals.Storage.Cache;

public class OperandsTest {

	@Test
	public void getOperandsGprAddressTest() {

		Operands operands = new Operands(1260);
		
		byte firstGprAddress = operands.getOperand1GprAddress();
		byte secondGprAddress = operands.getOperand2GprAddress();
		
		byte expectedFirstGprAddress = 2;
		byte expectedSecondGprAddress = 0;
		
		assertEquals(expectedFirstGprAddress, firstGprAddress);
		assertEquals(expectedSecondGprAddress, secondGprAddress);
	}
}
