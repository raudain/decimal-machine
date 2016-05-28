package operating.systems.internals.DecimalMachine;

public class Operating_System_Memory {
	
	private final short SIZE_OF_MEMORY;
	
	private int[] memory; // main memory
	
	public Operating_System_Memory() {
		
		SIZE_OF_MEMORY = 140;
		memory = new int[SIZE_OF_MEMORY];
	}
	
	public void load(int address, int code) {
		
		memory[address] = code;
	}
	
	public int fetch(short address) {
		
		return memory[address];
	}
}
