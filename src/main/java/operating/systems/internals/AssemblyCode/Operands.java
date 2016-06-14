package operating.systems.internals.AssemblyCode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Operands implements AssemblyLanguage {

	private byte modeOfOperand1;
	private byte modeOfOperand2;
	private byte operand1GPRAddress;
	private byte operand2GPRAddress;
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
		operand1GPRAddress = (byte) (remainder / 100);

		operand1 = new Operand(modeOfOperand1, operand1GPRAddress);
		/*
		 * gets the machine code for the the right hand operand and addressing
		 * mode
		 */
		remainder %= 100;
		// assigns the addressing mode for the right hand operand
		modeOfOperand2 = (byte) (remainder / 10);

		// assigns the right hand operand
		operand2GPRAddress = (byte) (remainder % 10);

		operand2 = new Operand(modeOfOperand2, operand1GPRAddress);
	}

	public boolean isValid() {

		return operand1.isModeValid() && operand2.isModeValid();
	}

	public byte getOperand1GprAddress() {

		return operand1GPRAddress;
	}

	public byte getOperand1Gpr() {

		return operand1GPRAddress;
	}

	public byte getOperand2GprAddress() {

		return operand2GPRAddress;
	}
}