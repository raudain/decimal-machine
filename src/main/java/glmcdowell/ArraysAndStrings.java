package glmcdowell;

import java.util.HashMap;
import java.util.Random;

public class ArraysAndStrings {

	private static boolean isUniqueBruteForce(String string) {
		for (int index = 0; index < string.length(); ++index) {
			char c = string.charAt(index);
			for (int j = index + 1; j < string.length(); ++j) {
				if (c == string.charAt(j))
					return false;
			}
		}
		return true;
	}
	
	private static boolean isUniqueHashMap(String string) {
		HashMap<Character,Character> hashMap = new HashMap<Character,Character>();
		for (int index = 0; index < string.length(); ++index) {
			char key = string.charAt(index);
			Character value = hashMap.putIfAbsent(key, key);
			if (value != null)
				return false;
		}
		return true;
	}
	
	private static boolean isUnique(String string) {
		char biggestChar = '~';
		int[] bucket = new int[biggestChar];
		for (int i = 0; i < string.length(); ++i) {
			char c = string.charAt(i);
			if (bucket[c] == 0)
				bucket[c] = c;
			else
				return false;
		}
		return true;
	}
	
	private static boolean isUniqueChars(String str) {
		int checker = 0;
		for (int i = 0; i < str.length(); i++) {
			int val = str.charAt(i) - 'a';
			if ((checker & (1 << val)) > 0 ) {
				return false;
			}
			checker |= (1 << val);
		}
		return true;
	}
	
	public static void mergeSort(char[] array) {
		int l = array.length;
		mergeSort(array, new char[l], 0, l - 1);
	}
	
	private static void mergeSort(char[] array, char[] temp, int leftStart, int rightEnd) {
		if (leftStart >= rightEnd) {
			return;
		}
		int middle = (leftStart + rightEnd) / 2;
		mergeSort(array, temp, leftStart, middle);
		mergeSort(array, temp, middle + 1, rightEnd);
		mergeHalves(array, temp, leftStart, rightEnd);
	}
	
	private static void mergeHalves(char[] array, char[] temp, int leftStart, int rightEnd) {
		int leftEnd = (leftStart + rightEnd) / 2;
		int rightStart = leftEnd + 1;
		int size = rightEnd - leftStart + 1;
		
		int left = leftStart;
		int right = rightStart;
		int index = leftStart;
		
		while (left <= leftEnd && right <= rightEnd) {
			if (array[left] <= array[right]) {
				temp[index] = array[left];
				left++;
			} else {
				temp[index] = array[right];
				right++;
			}
			index++;
		}
		
		System.arraycopy(array, left, temp, index, leftEnd - left + 1);
		System.arraycopy(array, right, temp, index, rightEnd - right + 1);
		System.arraycopy(temp, leftStart, array, leftStart, size);
	}
	
	private static void quickSort(String string) {
		char[] array = string.toCharArray();
		quickSort(array, 0, array.length - 1);
	}
	
	private static void quickSort(char[] array, int leftStart, int rightEnd) {
		if (leftStart >= rightEnd) {
			return;
		}
		
		int p = partition(array, leftStart, rightEnd);
		quickSort(array, leftStart, p - 1);
		quickSort(array, p + 1, rightEnd);
	}
	
	private static void swap(char[] array, int index1, int index2) {
		char temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
	}
	
	// returns random pivot index between low and high inclusive
	private static int getPivot(int start, int end) {
		Random random = new Random();
		return random.nextInt((end - start) + 1) + start;
	}
	
	/** 
	 * Moves all n < pivot to left of pivot and all n > pivot
	 * to right of pivot, then returns pivot index.
	 * 
	 * @param array
	 * @param start array index
	 * @param end array index
	 * @return pivot index
	 */
	private static int partition(char[] array, int start, int end) {
		swap(array, start, getPivot(start, end));
		int border = start + 1;
		for (int i = border; i <= end; i++) {
			if (array[i] < array[start]) {
				swap(array, i, border++);
			}
		}
		swap(array, start, border - 1);
		return border - 1;
	}
	
	public static boolean isUniqueCharsNoDataStructure(String str) {
		//mergeSort(array);
		quickSort(str);
		for (int index = 0; index < str.length() - 1; ++index) {
			if (str.charAt(index) == str.charAt(index + 1))
				return false;
		}
		
		return true;
	}
	
	private static boolean isPermutationBruteForce(String str1, String str2) {
		if (str1.length() != str2.length()) 
			return false;
		
		if (permutationBruteForce(str1, str2) && permutationBruteForce(str2, str1)) {
			return true;
		}
		return false;
	}
	
	private static boolean permutationBruteForce(String str1, String str2) {
		for (int i = 0; i < str1.length(); i++) {
			char c1 = str1.charAt(i);
			for (int j = 0; j < str2.length(); j++) {
				char c2 = str2.charAt(j);
				if (c1 == c2)
					break;
				else if (j == str2.length() - 1)
					return false;
			}
		}
		return true;
	}
	
	private static boolean isPermutationHashMap(String str1, String str2) {
		if (str1.length() != str2.length()) 
			return false;
		
		String string = str1.concat(str2);
		HashMap<Character, Integer> hashMap = new HashMap<Character, Integer>();
		for (int index = 0; index < string.length(); ++index) {
			char key = string.charAt(index);
			int value;
			if (hashMap.get(key) == null) {
				value = 0;
			} else {
				value = hashMap.get(key);
			}
			hashMap.put(string.charAt(index), value + 1);
		}
		for (int index = 0; index < string.length(); ++index) {
			char key = string.charAt(index);
			int value = hashMap.get(key);
			int checker = value % 2;
			if (checker != 0)
				return false;
		}
		return true;
	}
	
	private static boolean isPermutation(String str1, String str2) {
		if (str1.length() != str2.length()) 
			return false;
		
		quickSort(str1);
		quickSort(str2);
		return true;
	}
	
	private static boolean isPermutationBucket(String str1, String str2) {
		if (str1.length() != str2.length()) 
			return false;
		
		String string = str1.concat(str2);
		char[] bucket = new char['~' - ' '];
		for (int index = 0; index < string.length(); ++index) 
			bucket[string.charAt(index) - ' '] += 1;
		for (int index = 0; index < string.length(); ++index) {
			if (bucket[string.charAt(index) - ' '] % 2 != 0)
				return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		String string = "Roby Audain";
		isUnique(string);
		isUniqueBruteForce(string);
		isUniqueHashMap(string);
		isUniqueChars(string);
		isUniqueCharsNoDataStructure(string);
		String string2 = "Audain Robz";
		isPermutationBruteForce(string, string2);
		isPermutationHashMap(string, string2);
		isPermutationBucket(string, string2);
		System.out.println(isPermutation(string, string2));
	}
}
