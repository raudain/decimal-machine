package operating.systems.internals.AssemblyCode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.Storage.Central_Processing_Unit;

public class Operand implements AssemblyLanguage {

	/*
	 * There are six different addressing modes in the HYPO machine. They are
	 * explained below.
	 */

	/*
	 * The specified general purpose register contains the instruction.
	 */
	public static final byte REGISTER_ADDRESS_MODE_CODE = 1;

	/*
	 * Register contains the address of the operand. Operand value is in the
	 * main memory.
	 */
	public static final byte REGISTERDEFERRED_ADDRESS_MODE_CODE = 2;

	/*
	 * Register contains the address of the operand. Operand value is in the
	 * main memory. register content is incremented by 1 after fetching the
	 * operand value.
	 */
	public static final byte AUTOINCREMENT_ADDRESS_MODE_CODE = 3;

	/*
	 * Register content is decremented by 1. decremented value is the address of
	 * the operand. Operand value is in the main memory.
	 */
	public static final byte AUTODECREMENT_ADDRESS_MODE_CODE = 4;

	/*
	 * Next word contains the address of the operand. Operand value is in the
	 * main memory. GPR is not used. Use PC to get the address.
	 */
	public static final byte DIRECT_ADDRESS_MODE_CODE = 5;
	
	private byte mode;
	private byte gprAddress;
	private short address;
	private byte value;

	private static final Logger logger = LogManager.getLogger("Machine");
	
	public Operand(byte mode, byte gprAddress) {

		this.mode = mode;
		this.gprAddress = gprAddress;
		address = getAddress();
		value = getValue(address);
	}

	private boolean isModeValid() {

		return mode > 0 && mode <= 6;
	}

	private boolean isGprAddressValid(byte numberOfRegisters) {

		return gprAddress > 0 && gprAddress < numberOfRegisters;
	}

	public boolean isValid(byte numberOfRegisters) {

		boolean isModeValid = isModeValid();
		if (!isModeValid) 
			logger.error("Operand has invalid mode");

		boolean isGprAddressValid = isGprAddressValid(numberOfRegisters);
		if (!isGprAddressValid) 
			logger.error("Invalid general purpose register address");

		return mode > 0 && mode < 6;
	}

	private short getAddress() {
		
		
		return 0;
	}
	
	private byte getValue(Central_Processing_Unit cpu) {
		
		if (!cpu.isValidAddress(gprAddress)) {
			IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Operand has invalid mode");
			throw illegalArgumentException;
		}
		
		// Fetch operand value based on the operand mode
		byte currentValue = (byte) cpu.fetch(gprAddress);
		short pc = cpu.getProgramCounter();

		/*
		 * Next word contains the operand value. Operand value is in the
		 * main memory as part of the instruction. GPR is not used. Address
		 * is in the PC.
		 */
		final byte IMMEDIATE = 6;
		switch (mode) {

		// The operand's value is value of register at the operand's general purpose register address
		case REGISTER_ADDRESS_MODE_CODE:
			return currentValue;

		// Operand address is in the register
		case REGISTERDEFERRED_ADDRESS_MODE_CODE:
			instruction = new Instruction((short) CPU.fetch(register), AM.fetch((short) CPU.fetch(register)));
			return instruction;

		// Increments register by one after fetching address
		case AUTOINCREMENT_ADDRESS_MODE_CODE:
			instruction = new Instruction(reg, reg);
			// Increment register value by 1
			CPU.load(register, reg + 1);
			return instruction;

		// Autodecrement mode
		case AUTODECREMENT_ADDRESS_MODE_CODE:
			// Decrement register value by 1
			CPU.load(register, reg - 1);
			// Operand address is in the register. Operand value is in
			// memory
			instruction = new Instruction(reg, INVALID_VALUE);
			return instruction;

		case DIRECT_ADDRESS_MODE_CODE: // Direct mode
			// Operand address is memory. Operand value is in memory
			int operandAtPc = AM.fetch(pc);
			if (AM.isValidAddress((short) operandAtPc)) {
				instruction = new Instruction((short) operandAtPc, AM.fetch((short) operandAtPc));
			} else {
				logger.error("Invalid address");
			}

			CPU.setProgramCounter((short) (pc + 1));
			return instruction;

		case IMMEDIATE: // Immediate mode
			if (AM.isValidAddress(pc)) {
				// operand value is in memory
				instruction = new Instruction(pc, AM.fetch(pc));
				// increment program counter by one
				CPU.incrementProgramCounter();
			} else
				logger.error("Invalid address");

			return instruction;

		default: // Invalid mode
			logger.error("Fetched operand has unkown mode.");
		} // end of switch OpMode

		return instruction; // return successful or unsuccessful operand
							// fetch
	} // End of getOperandValue method
}// End of Operand class
