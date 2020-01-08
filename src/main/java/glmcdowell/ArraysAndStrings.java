package glmcdowell;

import java.util.HashMap;
import java.util.Random;

/**
 * <h1>1.3 URLify</h1>
 * <pre>
 * Write a method to replace all spaces in a string with '%20'. You may assume that the string
 * has sufficient space at the end to hold the additional characters, and that you are given the "true"
 * length of the string. (Note: if implementing in Java, please use a character array so that you can
 * perform this operation in place.)
 * </pre>
 * 
 * <h2>EXAMPLES</h2>
 * Input:	"Mr John Smith    ", 13<br>
 * Output:	"Mr%20John%20Smith"<br>
 * <br>
 * Input:	"Mr John    ", 7<br>
 * Output:	"Mr%20John"
 */ 
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
		quickSort(str.toCharArray(), 0, str.length() - 1);
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
		String string1 = quickSort(str1.toCharArray(), 0, str1.length() - 1);
		String string2 = quickSort(str2.toCharArray(), 0, str2.length() - 1);
		
		return string1.equals(string2);
	}
	
	private static boolean isPermutationBucket(String str1, String str2) {
		if (str1.length() != str2.length()) 
			return false;
		
		String string = str1.concat(str2);
		int[] bucket = new int['~' - ' '];
		for (int index = 0; index < string.length(); ++index) 
			bucket[string.charAt(index) - ' '] += 1;
		for (int index = 0; index < string.length(); ++index) {
			if (bucket[string.charAt(index) - ' '] % 2 != 0)
				return false;
		}
		return true;
	}
	
	/**
	 * <h1>1.3 URLify</h1>
	 * <pre>
	 * Write a method to replace all spaces in a string with '%20'. You may assume that the string
	 * has sufficient space at the end to hold the additional characters, and that you are given the "true"
	 * length of the string. (Note: if implementing in Java, please use a character array so that you can
	 * perform this operation in place.)
	 * </pre>
	 * 
	 * <h2>EXAMPLES</h2>
	 * <pre>
	 * Input:  "Mr John Smith    ", 13
	 * Output: "Mr%20John%20Smith"
	 * 
	 * Input:  "Mr John    ", 7
	 * Output: "Mr%20John"
	 * </pre>
	 * 
	 * <h2>ALGORITHM</h2>
	 * <pre>
	 * 1.  Assign the value of the true length of the {@code String} parameter to the index counter variable i and variable l.
	 * 2.  Check the character at index i of the {@code String} parameter at for a space character.
	 * 3.  
	 *     a. 
	 *        I.   If the character at index i of the {@code String} parameter is a ' ' character, store i to variable s.
	 *        II.  Store the variable l to the variable i.
	 *        III. Store the character at index of i {@code String} parameter to index i + 2.
	 *        IV.  Decrement i by one.
	 *        V.   Repeat steps III to V until i equals s + 2
	 *        VI.  Store the character '0' to index i by one.
	 *        VII. Decrement the variable i.
	 *        IX.  Store the character '2' to index i by one.
	 *        X.   Store the character '%' to index i by one.
	 *        XI.  Decrement the variable i.
	 *     b. If the character at index i of the {@code String} parameter is not a ' ' character decrement the variable i by one.
	 * 7.  Repeat steps 2 and 3 until variable i is less than zero.
	 * </pre>
	 * 
	 <h2>Time Complexity</h2>
	 * O(Length of String *)
	 * 
	 * <h2>Space Complexity</h2>
	 * O(2 * Length of String)
	 * <br>
	 * 
	 * @param string {@code String} to be URLified
	 */
	public static void urlifyBruteforce(String string) {
		
	}
	
	/**
	 * <h2>ALGORITHM</h2>
	 * <pre>
	 * 1. Create the character array arrayCopy with the same physical size of String.
	 * 2. Assign the value 0 to the index counter variable i.
	 * 3. Check if the {@code char} value at index i is a space
	 * 4. a. If it is a space then store a '%' character at arrayCopy[i], increment the variable i,
	 *       store '2' at arrayCopy[i], increment the variable i, store '0' at arrayCopy[i],
	 *       and increment the variable i again
	 *    b. If it is not a space then store the character at the index to arrayCopy[i] and increment the variable i
	 * 5. Repeat steps 3 and 4 until the variable is greater than the true size of the {@code String} parameter
	 * </pre>
	 * 
	 * <h2>Time Complexity</h2>
	 * O(Length of String)
	 * 
	 * <h2>Space Complexity</h2>
	 * O(2 * Length of String)
	 * <br>
	 * 
	 * @param string {@code String} to be URLified
	 */
	public static void urlifyCopy(String string) {
		
	}
	
	/**
	 * <h2>ALGORITHM</h2>
	 * <pre>
	 * 1. Store zero to the counter variable i.
	 * 2. Store zero to the counter variable numberOfSpaces.
	 * 2. Check the character at index i of the String parameter
	 *    a.
	 *       If the character at index i of the String parameter is equal to the character ' ' then increment the counter variables
	 *       i and numberOfSpaces by one.
	 *    b.
	 *       If the character at index i of the String parameter is not equal to the character ' ' then increment the counter 
	 *       variable by one.
	 * 3. Repeat steps 2 and 3 until i is equal to the true length of the String parameter
	 * 4. Check if the character value at index i is a space.
	 *    a. If the character is equal is ' ' then do the following.
	 *       I.   Store the character '0' at the index equal to i + numberOfSpaces of the String parameter.
	 *       II.  Decrement the counter variable i by one.
	 *       III. Store the character '2' at the index equal to i + numberOfSpaces of the String parameter.
	 *       IV.  Decrement the counter variable i by one.
	 *       V.   Store the character '%' at the index equal to i + numberOfSpaces of the String parameter.
	 *       VI.  Decrement the counter variable i by one.
	 *    b. If the character is not equal to ' ' then do the following
	 *       I.   Store the character at the index equal to i String parameter to the index equal to i + numberOfSpaces of the 
	 *            String parameter.
	 *       II.  Decrement the counter variable i by one.
	 * 5. Repeat step 4 until the counter variable i is less than zero.
	 * </pre>
	 * 
	 * <h2>Time Complexity</h2>
	 * O(Length of String + Length of String)
	 * 
	 * <h2>Space Complexity</h2>
	 * O(Length of String)
	 * <br>
	 * @param string {@code String} to be URLified
	 */
	public static void urlify(String string) {
		
	}
	
	public static void main(String[] args) {
		String string = "Roody Audain";
		isUnique(string);
		isUniqueBruteForce(string);
		isUniqueHashMap(string);
		isUniqueChars(string);
		isUniqueCharsNoDataStructure(string);
		String string2 = "niaduA Roody";
		isPermutationBruteForce(string, string2);
		isPermutationHashMap(string, string2);
		System.out.println(isPermutation(string, string2));
		System.out.println(isPermutationBucket(string, string2));
	}
	
	private static String quickSort(char[] array, int leftStart, int rightEnd) {
		if (leftStart >= rightEnd) {
			return new String(array, 0, array.length);
		}
		
		int p = partition(array, leftStart, rightEnd);
		quickSort(array, leftStart, p - 1);
		quickSort(array, p + 1, rightEnd);
		return new String(array, 0, array.length);
	}
	
	// returns random pivot index between low and high inclusive
	private static int getPivot(int start, int end) {
			Random random = new Random();
			return random.nextInt((end - start) + 1) + start;
	}
	
	private static void swap(char[] array, int index1, int index2) {
		char temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
	}
}
