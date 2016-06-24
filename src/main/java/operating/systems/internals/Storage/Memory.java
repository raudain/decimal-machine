package operating.systems.internals.Storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Memory {

	private int[] memory;

	private boolean empty;

	private final short SIZE;

	private static final Logger logger = LogManager.getLogger("Memory");

	protected Memory(short size) {

		SIZE = size;
		memory = new int[SIZE];
		empty = true;
	}

	protected boolean isEmpty() {

		return empty;
	}

	protected boolean inJavasShortRange(int address) {

		if (address < 0 || address > 32767)
			return false;
		else
			return true;
	}

	protected boolean addressInMemoryRange(short address) {

		if (address < 0 || address >= SIZE)
			return false;
		else
			return true;
	}

	public void load(short address, int code) {

		empty = false;
		if (addressInMemoryRange(address))
			memory[address] = code;
		else
			logger.warn("The address numbered " + address + " was not in range so was not loaded into memory");
	}

	public int fetch(short address) {

		if (addressInMemoryRange(address))
			return memory[address];
		else {
			logger.warn("The data in address numbered " + address + " was not in range so could not be fetched");
			return 0;
		}
	}

	public short size() {

		return SIZE;
	}

	public abstract void dump();
}
