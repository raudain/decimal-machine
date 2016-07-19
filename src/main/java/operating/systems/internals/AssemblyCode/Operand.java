package operating.systems.internals.AssemblyCode;

import operating.systems.internals.DecimalMachine.Process_Control_Block;
import operating.systems.internals.Storage.Application_Memory;

public class Operand {

	/*
	 * There are six different addressing modes in the HYPO machine. They are
	 * explained below.
	 */

	/*
	 * The specified general purpose register contains the instruction.
	 */
	private static final byte REGISTER_MODE = 1;
	
	private final static byte IMMEDIATE_MODE = 6;

	private byte mode;
	private byte gprAddress;
	private int value;

	public Operand(byte mode, byte gprAddress) {

		this.mode = mode;
		this.gprAddress = gprAddress;
	}

	public boolean isModeValid() {

		return mode >= 0 && mode <= 6;
	}

	/*
	 * The set value method is dependent on address parameter being set by the
	 * set address method
	 */
	public void setValue(Process_Control_Block pcb, Application_Memory am) {

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

			value = pcb.get(gprAddress);
			
			break;
			
		// Operand's address is the next word and the value is in application memory
		case IMMEDIATE_MODE:
			
			pcb.incrementProgramCounter();
			short nextWordsAddress = pcb.getProgramCounter();
			value = am.get(nextWordsAddress);			
			
		} // end of switch OpMode
	} // End of set value method
	
	public int getValue() {
		
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
