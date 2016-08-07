package operating.systems.internals.AssemblyCode;

public class Instruction {

	private byte operationCode;
	private Operands operands;

	/*
	 * Decode the first word of the instruction into opcode, mode and general
	 * purpose register using integer division and modulo operators. Example
	 * machine code: operation_code | operand_1_mode | R | M | R | 5 | 4 | 3 | 2 | 1
	 */
	public Instruction(int rawInstruction) {

		operationCode = (byte) (rawInstruction / 10000);

		// Modulo gives machine code line minus the opcode
		operands = new Operands(rawInstruction % 10000);
	}

	public byte getOperationCode() {
		
		return operationCode;
	}
	
	private boolean isValidOperationCode() {

		return operationCode > 0 && operationCode < 12;
	}

	public boolean isValid() {

		return isValidOperationCode() && operands.isModeValid();
	}
	
	public Operands getOperands() {
		
		return operands;
	}

	public byte getFirstGPRAddress() {
		
		byte operand1GprAddress = operands.getOperand1GprAddress();
		return operand1GprAddress;
	}

	public byte getSecondGPRAddress() {
		
		byte operand1GprAddress = operands.getOperand2GprAddress();
		return operand1GprAddress;
	}
}
