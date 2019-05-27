package operating.systems.internals.DecimalMachine;

public class Program {

	String name;
	
	byte origin;
	
	byte haltAddress;

	public Program(String fileName, byte origin) {
	
		name = fileName;
		this.origin = origin;
	}
	
	public String getName() {
		
		return name;
	}

	public byte getOrigin() {
		
		return origin;
	}

	public byte getHaltAddress() {
		// TODO Auto-generated method stub
		return haltAddress;
	}

	public void setHaltAddress(byte freeSpacePointer) {
		
		haltAddress = freeSpacePointer;
	}


}
