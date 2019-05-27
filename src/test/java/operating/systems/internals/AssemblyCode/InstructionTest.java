package operating.systems.internals.AssemblyCode;

//import static org.junit.Assert.*;

import org.junit.Test;

public class InstructionTest {

	@Test
	public void getOperandsTest() {
		
		Instruction instruction = new Instruction(51260);
		Operands operands = instruction.getOperands();
		operands.getModeOfOperand1();
	}
}
