package operating.systems.internals.Storage;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import operating.systems.internals.DecimalMachine.Process_Control_Block;;

public class Ready_Program_List extends LinkedList<Process_Control_Block> {

	private static final long serialVersionUID = 1L;
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

	private void makeSpace(byte index) {
		
		Process_Control_Block tempPcb;
		Process_Control_Block nextPcb;
		for (int i = index; i < size(); i++) {
			tempPcb = get(i);
			nextPcb = get(i + 1);
			set(i + 1, tempPcb);
			tempPcb = nextPcb;
		}
	}
	
	private void putAhead(byte index, Process_Control_Block pcb) {

		makeSpace(index);
		add(index, pcb);
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

		Iterator<Process_Control_Block> itr = iterator();
		byte index = 0;
		while (itr.hasNext()) {
			Process_Control_Block temp = itr.next();
			if (pcb.hasGreaterPriority(temp.getPriority())) {
				putAhead(index, pcb);
				print();
				
				return true;
			}
		}

		addLast(pcb);
		logger.info("PCB enters at the bottom of the ready queue ");
		print();

		return true;
	} // end of insert process into ready queue module
}
