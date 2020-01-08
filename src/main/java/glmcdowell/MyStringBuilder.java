package glmcdowell;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class MyStringBuilder implements java.io.Serializable, CharSequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The value is used for character storage.
	 */
	char[] value;

	/**
	 * The count is the number of characters used.
	 */
	int count;

	/**
	 * Constructs a string builder with no characters in it and an initial capacity
	 * of 16 characters.
	 */
	public MyStringBuilder() {
		
		value = new char[16];
	}
	
	public MyStringBuilder(int capacity) {
		
		value = new char[capacity];
	}
	
	/**
     * Constructs a string builder initialized to the contents of the
     * specified string. The initial capacity of the string builder is
     * {@code 16} plus the length of the string argument.
     *
     * @param   str   the initial contents of the buffer.
     */
    public MyStringBuilder(String str) {
        
    	int l = str.length() + 16;
    	new MyStringBuilder(l);
        append(str);
    }

	/**
     * The maximum size of array to allocate (unless necessary).
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Returns a capacity at least as large as the given minimum capacity.
     * Returns the current capacity increased by the same amount + 2 if
     * that suffices.
     * Will not return a capacity greater than {@code MAX_ARRAY_SIZE}
     * unless the given minimum capacity is greater than that.
     *
     * @param  minCapacity the desired minimum capacity
     * @throws OutOfMemoryError if minCapacity is less than zero or
     *         greater than Integer.MAX_VALUE
     */
    private int newCapacity(int minCapacity) {
        // overflow-conscious code
        int newCapacity = (value.length << 1) + 2;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        return (newCapacity <= 0 || MAX_ARRAY_SIZE - newCapacity < 0)
            ? hugeCapacity(minCapacity)
            : newCapacity;
    }

    private int hugeCapacity(int minCapacity) {
        if (Integer.MAX_VALUE - minCapacity < 0) { // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE)
            ? minCapacity : MAX_ARRAY_SIZE;
    }
	
	/**
     * For positive values of {@code minimumCapacity}, this method
     * behaves like {@code ensureCapacity}, however it is never
     * synchronized.
     * If {@code minimumCapacity} is non positive due to numeric
     * overflow, this method throws {@code OutOfMemoryError}.
     */
    private void ensureCapacityInternal(int minimumCapacity) {
        // overflow-conscious code
        if (minimumCapacity - value.length > 0) {
            value = Arrays.copyOf(value,
                    newCapacity(minimumCapacity));
        }
    }

	private MyStringBuilder appendNull() {
        int c = count;
        ensureCapacityInternal(c + 4);
        final char[] value = this.value;
        value[c++] = 'n';
        value[c++] = 'u';
        value[c++] = 'l';
        value[c++] = 'l';
        count = c;
        return this;
    }
	
	public MyStringBuilder append(String str) {

		if (str == null)
			return appendNull();

		int len = str.length();
		ensureCapacityInternal(count + len);
		str.getChars(0, len, value, count);
		count += len;
		
		return this;
	}

	@Override
    public String toString() {
        // Create a copy, don't share the array
        return new String(value, 0, count);
    }

	static String joinWords(String[] words) {
		MyStringBuilder sentence = new MyStringBuilder();
		for (String w : words) {
			sentence.append(w);
		}
		return sentence.toString();
	}
	public static void main(String[] args) {

		String[] words = new String[2];
		words[0] = "Hello";
		words[1] = "Roody";
		String s = joinWords(words);
		System.out.println(s);
	}

	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	public char charAt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	public CharSequence subSequence(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public Appendable append(CharSequence csq) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void append(char c) {
		ensureCapacityInternal(count + 1);
		value[count++] = c;
	}
}
