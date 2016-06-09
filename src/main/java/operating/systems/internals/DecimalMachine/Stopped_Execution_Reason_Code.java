package operating.systems.internals.DecimalMachine;

/**
 *
 * @author Roody
 */
public interface Stopped_Execution_Reason_Code {
	final short PROCEED = 1; // The process can execute after the system call
	final short HALTED = 10;
	final byte WAITING_FOR_MESSAGE = 9;
}
