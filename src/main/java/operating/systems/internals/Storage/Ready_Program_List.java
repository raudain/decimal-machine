package operating.systems.internals.Storage;

import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import operating.systems.internals.DecimalMachine.Process_Control_Block;;

public class Ready_Program_List extends LinkedList<Process_Control_Block> {

	private static final Logger logger = LogManager.getLogger("Ready_Program_List");

	private void print() {

		// Walk thru the queue from the given pointer until end of list
		// Print each PCB as you move from one PCB to the next
		logger.info("Printing all process control blocks in ready queue:");
		Process_Control_Block pcb = getFirst();
		int i = 0;
		while (!isEmpty()) {
			logger.info(pcb.toString());
			pcb = get(i++);

			logger.info("Printing next process control block:");
		} // end of while loop

		logger.info("The end of the ready queue has been reached.");
	} // end of print method

	private void setToTop(Process_Control_Block pcb) {
		
		Process_Control_Block tempPcb = getFirst();
		addFirst(pcb);
		for (int i = 1; i <= size(); i++) {
			add(i, tempPcb);
			if (i + 1 <= size())
					tempPcb = get(i + 1);
		}
	}
	
	/**
	 * Inserts a process into the ready queue
	 * 
	 * @param PCBpointer
	 *            PCB: a data structure in the operating system kernel
	 *            containing the information needed to manage a particular
	 *            process. The PCB is "the manifestation of a process in an
	 *            operating system".
	 * 
	 * @return
	 */
	public boolean add(Process_Control_Block pcb) {

		if (isEmpty()) {
			addFirst(pcb);
			return true;
		}

		// Insert PCB according to the Priority Round Robin algorithm.

		/*
		 * Use priority in the PCB to find the correct place to insert. set
		 * state to ready state
		 */
		pcb.setState(pcb.getReadyStateIndicator());

		/*
		 * Iterate RQ and find the place to insert PCB will be inserted at the
		 * end of its priority
		 */
		Process_Control_Block tempPcb = getFirst();

		while (!tempPcb.equals(getLast())) {
			byte priorityComparisonResult = pcb.comparePriority(tempPcb.getPriority());
			// if the new process control block has the greatest priority
			if (priorityComparisonResult > 0 ) {
				if (tempPointer == END_OF_LIST_MARKER) {
					setToTop(pcb);
					logger.info(
							"[PID: #" + PCB.getProcessId(OSM, pcbPointer) + "] has entered the top of the ready queue");
					print();
					return true;
				}
				// enter PCB in the middle of the list
				PCB.setNextPcbPointer(OSM, pcbPointer, PCB.getNextPcbPointer(OSM, previousPointer));
				PCB.setNextPcbPointer(OSM, previousPointer, pcbPointer);

				logger.info("PCB enters in the middle of the ready queue ");
				printRq();

				return;
			} else // PCB to be inserted has lower or equal priority to the
					// current PCB in RQ
			{ // go to the next PCB in RQ
				previousPointer = tempPointer;
				tempPointer = PCB.getNextPcbPointer(OSM, tempPointer);
			}
		} // end of while loop

		// Insert PCB at the end of the RQ
		PCB.setNextPcbPointer(OSM, previousPointer, pcbPointer);

		System.out.println("PCB enters at the bottom of the ready queue ");
		printRq();

		return;
	} // end of insert process into ready queue module
}
