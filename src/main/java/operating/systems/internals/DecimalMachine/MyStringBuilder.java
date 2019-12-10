package operating.systems.internals.DecimalMachine;

import java.io.IOException;
import java.io.Serializable;

public class MyStringBuilder implements java.io.Serializable, Appendable, CharSequence {

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

	@Override
	public int length() {

		return count;
	}

	@Override
	public char charAt(int index) {

		if ((index < 0) || (index >= count))
			throw new StringIndexOutOfBoundsException(index);
		return value[index];
	}

	@Override
	public CharSequence subSequence(int start, int end) {

		if ((start < 0) || (start >= count))
			throw new StringIndexOutOfBoundsException(start);
		if ((end < 0) || (end >= count))
			throw new StringIndexOutOfBoundsException(start);

		String s = "";
		for (int i = start; i < end; i++) {
			s += value[i];
		}

		return s;
	}

	@Override
	public Appendable append(CharSequence csq) throws IOException {
		// TODO Auto-generated method stub
		StringBuilder b = new StringBuilder();
		return null;
	}

	@Override
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Appendable append(char c) throws IOException {
		// TODO Auto-generated method stub
		return null;
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

		int n = length();
		char[] v = new char[n];
		v = value;
		for (int i = 0; i < str.length(); i++) {
			v[n] = str.charAt(i);
			++n;
		}

		return this;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {

		MyStringBuilder msb = new MyStringBuilder();

	}

}
