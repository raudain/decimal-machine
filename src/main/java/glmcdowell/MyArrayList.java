package glmcdowell;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.RandomAccess;

public class MyArrayList<E> extends AbstractList<E> 
	implements List<E>, RandomAccess, Cloneable, Serializable
	{

	private static final long serialVersionUID = 8683452581122892189L;

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 10;
    
	int size;
	String[] data;
	
	public MyArrayList() {
		size = 0;
		data = new String[DEFAULT_CAPACITY];
	}
	
	public MyArrayList(int capacity) {
		size = 0;
		data = new String[capacity];
	}
	
	@Override
	public E get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return size;
	}
	
	private void ensureCapacity(int minCapacity) {
		int length = data.length;
		if (minCapacity > data.length) {
			String[] temp = data;
			data = new String[length * 2];
			data = temp.clone();
		}
	}
	
	private void add(String w) {
		int minCapacity = size + 1;
		this.ensureCapacity(minCapacity);
		data[size] = w;
		String s = data.toString();
		++size;
	}
	
	private MyArrayList<String> merge(String[] words, String[] more) {
		MyArrayList<String> sentence = new MyArrayList<String>();
		for (String w: words) sentence.add(w);
		for (String w : more) sentence.add(w);
		return sentence;
	}
	
	
	@Override
	public String toString() {
		return "MyArrayList [data=" + Arrays.toString(data) + "]";
	}

	public static void main(String[] args) {
		String[] words = new String[2];
		words[0] = "Hello";
		words[1] = "Roody";
		
		String[] more = new String[3];
		more[0] = "How";
		more[1] = "are";
		more[2] = "you";
		
		MyArrayList<String> myArrayList = new MyArrayList<String>();
		myArrayList = myArrayList.merge(words, more);
		String out = myArrayList.toString();
		System.out.println(out);
	}
}
