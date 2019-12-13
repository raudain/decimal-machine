package operating.systems.internals.DecimalMachine;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

public class MyHashtable implements Cloneable, Serializable {

	/**
     * The hash table data.
     */
    private LinkedList[] table;
    
    private int compute(int key) {
    	int hashCode = key % 2;
    	
    	return hashCode;
    }
	
	public static void main(String[] args) {
		Hashtable h = new Hashtable();
		MyHashtable mh = new MyHashtable();
		
	}
}
