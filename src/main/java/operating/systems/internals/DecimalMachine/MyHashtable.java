package operating.systems.internals.DecimalMachine;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

public class MyHashtable<K> {

	/**
	 * The hash table data.
	 */
	private LinkedList<String>[] data;

	/**
	 * Constructs a new, empty hashtable with the specified initial capacity and the
	 * specified load factor.
	 *
	 * @param initialCapacity the initial capacity of the hashtable.
	 * @param loadFactor      the load factor of the hashtable.
	 * @exception IllegalArgumentException if the initial capacity is less than
	 *                                     zero, or if the load factor is
	 *                                     nonpositive.
	 */
	private MyHashtable(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal Load: " + loadFactor);

		if (initialCapacity == 0)
			initialCapacity = 1;
		data = new LinkedList[initialCapacity];
	}

	/**
	 * Constructs a new, empty hashtable with the specified initial capacity and
	 * default load factor (0.75).
	 *
	 * @param initialCapacity the initial capacity of the hashtable.
	 * @exception IllegalArgumentException if the initial capacity is less than
	 *                                     zero.
	 */
	private MyHashtable(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}

	/**
	 * Constructs a new, empty hashtable with a default initial capacity (11) and
	 * load factor (0.75).
	 */
	private MyHashtable() {
		this(11, 0.75f);
	}

	private String put(String key) {
		// Make sure the value is not null
		if (key == null) {
			throw new NullPointerException();
		}

		int hashCode = key.hashCode();
		int index = hashCode % data.length;
		LinkedList<String> linkedList;
		if (data[index] != null) {
			linkedList = data[index];
		} else {
			linkedList = new LinkedList<String>();
		}
		linkedList.add(key);
		data[index] = linkedList;

		return key;
	}

	public static void main(String[] args) {
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		MyHashtable<String> myHashtable = new MyHashtable<String>();

		String string1 = "hi";
		hashtable.put(string1, string1);
		myHashtable.put(string1);

		String string2 = "abc";
		hashtable.put(string2, string2);
		myHashtable.put(string2);

		String string3 = "aa";
		hashtable.put(string3, string3);
		myHashtable.put(string3);

		String string4 = "qs";
		hashtable.put(string4, string4);
		myHashtable.put(string4);

		String string5 = "pl";
		hashtable.put(string5, string5);
		myHashtable.put(string5);

		Enumeration<String> enumeration = hashtable.keys();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			System.out.println("[Key = " + key + "] [Hash code = " + key.hashCode() + "]");
		}
		System.out.println("");
		LinkedList<String>[] myData = myHashtable.data;
		for (int i = 0; i < myData.length; i++) {
			LinkedList<String> linkedList = myData[i];
			if (linkedList != null) {
				ListIterator<String> listIterator = linkedList.listIterator();
				while (listIterator.hasNext()) {
					String key = listIterator.next();
					String output = "[Key = " + key + "] [Hash code = " + key.hashCode() + "] [Index = " + i + "]";
					System.out.println(output);
				}
			}
		}
	}
}
