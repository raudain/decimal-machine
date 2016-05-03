package operating.systems.internals.DecimalMachine;

/**
 *
 * @author roody
 */
public interface Stopped_Execution_Reason_Code {
	final short PROCEED = 1; // The process can execute after the system call
	final short HALTED = 10;
	final short TIME_SLICE = 200; // Timeslice is a constant set to 200 clock
									// ticks
	final byte WAITING_FOR_MESSAGE = 9;
}
