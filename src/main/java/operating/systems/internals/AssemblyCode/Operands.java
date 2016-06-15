package operating.systems.internals.AssemblyCode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.Storage.Application_Memory;
import operating.systems.internals.Storage.Central_Processing_Unit;

public class Operands implements AssemblyLanguage {

	private byte modeOfOperand1;
	private byte modeOfOperand2;
	private byte operand1GprAddress;
	private byte operand2GprAddress;
	private Operand operand1;
	private Operand operand2;

	private static final Logger logger = LogManager.getLogger("Machine");

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

		operand2 = new Operand(modeOfOperand2, operand1GprAddress);
	}

	public boolean isValid(byte numberOfRegisters) {

		return operand1.isValid(numberOfRegisters) && operand2.isValid(numberOfRegisters);
	}

	public byte getValueOfOperand1(Central_Processing_Unit cpu, Application_Memory am) {
		
		operand1.setAddress(cpu, am);
		operand1.setValue(cpu, am);
		
		return operand1.getValue();
	}
	
	public byte getValueOfOperand2(Central_Processing_Unit cpu, Application_Memory am) {
		
		operand2.setAddress(cpu, am);
		operand2.setValue(cpu, am);
		
		return operand2.getValue();
	}
	
	public byte getModeOfOperand1() {
		
		return operand1.getMode();
	}
	
	public byte getOperand1GprAddress() {
		
		return operand1GprAddress;
	}
}