package operating.systems.internals.AssemblyCode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operating.systems.internals.Storage.Application_Memory;
import operating.systems.internals.Storage.Cache;

public class Operand implements AssemblyLanguage {

	/*
	 * There are six different addressing modes in the HYPO machine. They are
	 * explained below.
	 */

	/*
	 * The specified general purpose register contains the instruction.
	 */
	private static final byte REGISTER_MODE = 1;

	/*
	 * Register contains the address of the operand. Operand value is in the
	 * main memory.
	 */
	private final byte REGISTERDEFERRED_ADDRESS_MODE = 2;

	/*
	 * Register contains the address of the operand. Operand value is in the
	 * main memory. register content is incremented by 1 after fetching the
	 * operand value.
	 */
	private final byte AUTOINCREMENT_MODE = 3;

	/*
	 * Register content is decremented by 1. decremented value is the address of
	 * the operand. Operand value is in the main memory.
	 */
	private final byte AUTODECREMENT_MODE = 4;

	/*
	 * Next word contains the address of the operand. Operand value is in the
	 * main memory. GPR is not used. Use PC to get the address.
	 */
	private final byte DIRECT_ADDRESS_MODE = 5;
	
	private final static byte IMMEDIATE_MODE = 6;

	private byte mode;
	private byte gprAddress;
	private short address;
	private byte value;

	private static final Logger logger = LogManager.getLogger("Operand");

	public Operand(byte mode, byte gprAddress) {

		this.mode = mode;
		this.gprAddress = gprAddress;
	}

	private boolean isModeValid() {

		return mode >= 0 && mode <= 6;
	}

	private boolean isGprAddressValid(byte numberOfRegisters) {

		return gprAddress >= 0 && gprAddress < numberOfRegisters;
	}

	public boolean isValid(byte numberOfRegisters) {

		boolean isModeValid = isModeValid();
		if (!isModeValid)
			logger.error("Mode number " + mode + " is invalid");

		boolean isGprAddressValid = isGprAddressValid(numberOfRegisters);
		if (!isGprAddressValid)
			logger.error("The register number " + this.gprAddress + " is out of range");

		return mode > 0 && mode < 6;
	}

	public void setAddress(Cache cpu, Application_Memory am) {

		/*
		 * Next word contains the operand value. Operand value is in the main
		 * memory as part of the instruction. GPR is not used. Address is in the
		 * PC.
		 */
		switch (mode) {

		/*
		 * Operand address is the value of the the register at the general
		 * purpose register address
		 */
		case REGISTERDEFERRED_ADDRESS_MODE:

			address = (short) cpu.fetch(gprAddress);

		case DIRECT_ADDRESS_MODE:

			address = (short) am.fetch(cpu.getProgramCounter());

			cpu.incrementProgramCounter();
		}
	}

	/*
	 * The set value method is dependent on address parameter being set by the
	 * set address method
	 */
	public void setValue(Cache cpu, Application_Memory am) {

		/*
		 * Next word contains the operand value. Operand value is in the main
		 * memory as part of the instruction. GPR is not used. Address is in the
		 * PC.
		 */
		switch (mode) {

		/*
		 * The operand's value is value of central processing unit's register at
		 * the general purpose register address
		 */
		case REGISTER_MODE:

			value = (byte) cpu.fetch(gprAddress);
			
			break;

		// Operand address is in the register
		case REGISTERDEFERRED_ADDRESS_MODE:

			value = (byte) cpu.fetch(address);
			
			break;
			
		// Increments register by one after fetching address
		case AUTOINCREMENT_MODE:

			value = (byte) cpu.fetch(gprAddress);
			cpu.increment(gprAddress);
			
			break;
			
		case AUTODECREMENT_MODE:

			value = (byte) cpu.fetch(gprAddress);
			cpu.decrement(gprAddress);

			break;
			
		// Operand value is in memory
		case DIRECT_ADDRESS_MODE:

			value = (byte) am.fetch(address);

			break;
		// Operand value is in memory
		case IMMEDIATE_MODE:
			
			value = (byte) am.fetch(cpu.getProgramCounter());			
			cpu.incrementProgramCounter();
		} // end of switch OpMode
	} // End of set value method
	
	public byte getValue() {
		
		return value;
	}
	
	public byte getMode() {
		
		return mode;
	}
	
	public static byte getCodeForRegisterMode() {
		
		return REGISTER_MODE;
	}
	
	public static byte getCodeForImmediateMode() {
		
		return IMMEDIATE_MODE;
	}
}// End of Operand class
