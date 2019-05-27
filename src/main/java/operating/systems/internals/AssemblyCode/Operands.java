package operating.systems.internals.AssemblyCode;

import operating.systems.internals.DecimalMachine.Process_Control_Block;
import operating.systems.internals.Storage.Application_Memory;
import operating.systems.internals.Storage.Cache;

public class Operands {

	private byte modeOfOperand1;
	private byte modeOfOperand2;
	private byte operand1GprAddress;
	private byte operand2GprAddress;
	private Operand operand1;
	private Operand operand2;

	public Operands(int operands) {

		modeOfOperand1 = (byte) (operands / 1000);

		/*
		 * gets the machine code minus the opcode and the addressing mode for
		 * the left hand operand
		 */
		short remainder = (short) (operands % 1000);

		// assigns the left hand operand
		operand1GprAddress = (byte) (remainder / 100);

		operand1 = new Operand(modeOfOperand1, operand1GprAddress);
		/*
		 * gets the machine code for the the right hand operand and addressing
		 * mode
		 */
		remainder %= 100;
		// assigns the addressing mode for the right hand operand
		modeOfOperand2 = (byte) (remainder / 10);

		// assigns the right hand operand
		operand2GprAddress = (byte) (remainder % 10);

		operand2 = new Operand(modeOfOperand2, operand2GprAddress);
	}

	public boolean isModeValid() {

		return operand1.isModeValid() && operand2.isModeValid();
	}

	public int getValueOfOperand1(Process_Control_Block pcb, Application_Memory am) {
		
		operand1.setValue(pcb, am);
		
		return operand1.getValue();
	}
	
	public int getValueOfOperand2(Process_Control_Block pcb, Application_Memory am) {
		
		operand2.setValue(pcb, am);
		
		return operand2.getValue();
	}
	
	public byte getModeOfOperand1() {
		
		return operand1.getMode();
	}
	
	public byte getOperand1GprAddress() {
		
		return operand1GprAddress;
	}

	public byte getOperand2GprAddress() {
		// TODO Auto-generated method stub
		return operand2GprAddress;
	}
}