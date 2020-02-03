package glmcdowell;

import java.util.HashMap;
import java.util.Random;

/**
 */
public class ArraysAndStrings {
	
	public static void main(String[] args) {
		String str  = "waterbottle";
		String str2 = "erbottlewat";
		isUnique(str);
		isUniqueBruteForce(str);
		isUniqueHashMap(str);
		isUniqueChars(str);
		isUniqueCharsNoDataStructure(str);
		isPermutationBruteForce(str, str2);
		isPermutationHashMap(str, str2);
		isPermutation(str, str2);
		isPermutationBucket(str, str2);
		urlify(str, 1);
		isPermutationPalindrone(str);
		oneChangeDiff(str, str2);
		compress(str);
		/*int numRows = 4;
		int numColumns = 3;
		int matrix[][] = new int[numRows][numColumns];
		
		rotate(image);
		
		zeroMatrix(matrix, numColumns);*/
		
		System.out.println(isRotation(str, str2));
	}
	
	public int[][] getRandomMatrix(int rows, int columns) {
		int matrix[][] = new int[rows][columns];
		Random random = new Random();
		matrix[0][0] = getRandom(random);
		matrix[0][1] = getRandom(random);
		matrix[0][2] = getRandom(random);
		matrix[0][3] = getRandom(random);
		matrix[0][4] = getRandom(random);
		matrix[0][5] = getRandom(random);
		matrix[1][0] = getRandom(random);
		matrix[1][1] = getRandom(random);
		matrix[1][2] = getRandom(random);
		matrix[1][3] = getRandom(random);
		matrix[1][4] = getRandom(random);
		matrix[1][5] = getRandom(random);
		matrix[2][0] = getRandom(random);
		matrix[2][1] = getRandom(random);
		matrix[2][2] = getRandom(random);
		matrix[2][3] = getRandom(random);
		matrix[2][4] = getRandom(random);
		matrix[2][5] = getRandom(random);
		matrix[3][0] = getRandom(random);
		matrix[3][1] = getRandom(random);
		matrix[3][2] = getRandom(random);
		matrix[3][3] = getRandom(random);
		matrix[3][4] = getRandom(random);
		matrix[3][5] = getRandom(random);
		matrix[4][0] = getRandom(random);
		matrix[4][1] = getRandom(random);
		matrix[4][2] = getRandom(random);
		matrix[4][3] = getRandom(random);
		matrix[4][4] = getRandom(random);
		matrix[4][5] = getRandom(random);
		matrix[5][0] = getRandom(random);
		matrix[5][1] = getRandom(random);
		matrix[5][2] = getRandom(random);
		matrix[5][3] = getRandom(random);
		matrix[5][4] = getRandom(random);
		matrix[5][5] = getRandom(random);
		return matrix;
	}

	private static char getRandom(Random random) {
		int c = random.nextInt((90 - 65) + 1) + 65;
		return (char) c;
	}
	
	public int[][] getMatrix(int rows, int columns) {
		int matrix[][] = new int[rows][columns];
		matrix[0][0] = 10;
		matrix[0][1] = 1;
		matrix[0][2] = 0;
		matrix[1][0] = 3;
		matrix[1][1] = 4;
		matrix[1][2] = 5;
		matrix[2][0] = 12;
		matrix[2][1] = 6;
		matrix[2][2] = 7;
		matrix[3][0] = 8;
		matrix[3][1] = 9;
		matrix[3][2] = 11;
		
		printMatrix(matrix, columns);
		System.out.println();
		System.out.println("    ||");
		System.out.println("    \\/");
		System.out.println();
		
		return matrix;
	}
	
	

	private static boolean isUniqueBruteForce(String string) {
		for (int index = 0; index < string.length(); ++index) {
			char c = string.charAt(index);
			for (int j = index + 1; j < string.length(); ++j) {
				if (c == string.charAt(j))
					return false;
			}
		}
		new String(string.toCharArray());
		return true;
	}

	private static boolean isUniqueHashMap(String string) {
		HashMap<Character, Character> hashMap = new HashMap<Character, Character>();
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
			if ((checker & (1 << val)) > 0) {
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
	 * Moves all n < pivot to left of pivot and all n > pivot to right of pivot,
	 * then returns pivot index.
	 * 
	 * @param array
	 * @param start array index
	 * @param end   array index
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

	public static boolean isUniqueCharsNoDataStructure(String str) {
		// mergeSort(array);
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

	private static String quickSort(char[] array, int leftStart, int rightEnd) {
		if (leftStart >= rightEnd) {
			return new String(array, 0, array.length);
		}

		int p = partition(array, leftStart, rightEnd);
		quickSort(array, leftStart, p - 1);
		quickSort(array, p + 1, rightEnd);
		return new String(array, 0, array.length);
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
	 * *
	 * <h1>1.3 URLify</h1>
	 * 
	 * <pre>
	 * Write a method to replace all spaces in a string with '%20'. You may assume that the string
	 * has sufficient space at the end to hold the additional characters, and that you are given the "true"
	 * length of the string. (Note: if implementing in Java, please use a character array so that you can
	 * perform this operation in place.)
	 * </pre>
	 * 
	 * <h2>EXAMPLES</h2>
	 * 
	 * <h3>General Cases</h3>
	 * 
	 * <pre>
	 *          0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16
	 * Input:  [M] [r] [ ] [J] [o] [h] [n] [ ] [S] [m] [i] [t] [h] , 13
	 * Output: [M] [r] [%] [2] [0] [J] [o] [h] [n] [%] [2] [0] [S] [m] [i] [t] [h] 
	 * 
	 *          0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21  22  23  24
	 * Input:  [M] [r] [ ] [J] [o] [h] [n] [ ] [J] [a] [c] [o] [b] [ ] [S] [m] [i] [t] [h] ..., 19     
	 * Output: [M] [r] [%] [2] [0] [J] [o] [h] [n] [%] [2] [0] [J] [a] [c] [o] [b] [%] [2] [0] [S] [m] [i] [t] [h] 
	 * 
	 * Input:  [M] [r] [ ] [J] [o] [h] [n] , 7
	 * Output: [M] [r] [%] [2] [0] [J] [o] [h] [n]
	 * </pre>
	 * 
	 * <h3>Base Cases</h3>
	 * 
	 * <pre>
	 * Input:  [A] , 1
	 * Output: [A]
	 * 
	 * Input:  [ ] , 1
	 * Output: [ ] [ ] [ ]
	 * </pre>
	 * 
	 * <h3>Special Cases</h3>
	 * 
	 * <pre>
	 * Input:  [null] , 1
	 * Output: [null]
	 * </pre>
	 * 
	 * <h2>ALGORITHM</h2>
	 * 
	 * <pre>
	 * (1) Count the number of spaces in the String parameter.
	 * (2) Scan the String parameter from right to left
	 *     a. Move the characters not equal to ' ' to the right by two spaces time the number of space character
	 *     b. Replace the character equal to ' ' with "%20".
	 * </pre>
	 * 
	 * <h2>Time Complexity</h2> O(Length of String + (Length of String - index of
	 * last space))
	 * 
	 * <h2>Space Complexity</h2> O(2 * Length of String) <br>
	 * 
	 * @param string {@code String} to be URLified
	 */
	private static String urlify(String str, int trueLength) {
		int numSpaces = 0;
		for (int i = 0; i < trueLength; ++i)
			if (str.charAt(i) == ' ')
				++numSpaces;

		int right = numSpaces * 2 + trueLength - 1;
		char a[] = str.toCharArray();
		for (int i = trueLength - 1; i < right; --i)
			right = copyRight(a, i, right);

		return new String(a);
	}

	private static int copyRight(char a[], int i, int right) {
		if (a[i] == ' ') {
			a[right] = '0';
			a[--right] = '2';
			a[--right] = '%';
			return right - 1;
		} else {
			a[right] = a[i];
			return right - 1;
		}
	}

	/**
	 * <h1>1.4</h1>
	 * 
	 * <pre>
	 * Palindrome Permutation: Given a string, write a function to check if it is a permutation of a palin-
	 * drome. A palindrome is a word or phrase that is the same forwards and backwards. A permutation
	 * is a rearrangement of letters. The palindrome does not need to be limited to just dictionary words.
	 * </pre>
	 * 
	 * <h2>Examples</h2>
	 * 
	 * <h3>General Case</h3>
	 * 
	 * <pre>
	 * Input:   Tact Coa
	 * Output:  True (permutations: "taco cat", "atco cta", etc.)
	 * </pre>
	 * 
	 * <h3>Base Case</h3>
	 * 
	 * <pre>
	 * Input:   'S'
	 * Output:  true
	 * 
	 * Input:   ' '
	 * Output:  
	 * 
	 * Input:   "r "
	 * Output:  true
	 * 
	 * Input:   " z"
	 * Output:  true
	 * 
	 * Input:   "Ra"
	 * Output:  true
	 * </pre>
	 * 
	 * <h3>Error Case</h3>
	 * 
	 * <pre>
	 * Input:  "abcd"
	 * Output: false
	 * 
	 * Input:  "bbcef"
	 * Output: false
	 * </pre>
	 * 
	 * <h2>Algorithms</h2>
	 * 
	 * <h3>Map</h3>
	 * 
	 * <pre>
	 * 1. Map each character included in the input string with its frequency
	 * 2. If there are more then two characters that don't have a pair then return false.
	 * 3. Otherwise return true.
	 * </pre>
	 * 
	 * <h4>Time Complexity</h2> O(Length of String + Length of String)
	 * 
	 * <h4>Space Complexity</h2> O(Length of String + Length of String)
	 * 
	 * <h3>Bit Vector</h3>
	 * 
	 * <pre>
	 * 1. Increment then decrement the frequency of each character in the input string
	 * 2. If there are more then two bits in the bit vector that are not zer then return false.
	 * 3. Otherwise return true.
	 * </pre>
	 * 
	 * <h4>Time Complexity</h2> O(Length of String + Length of String)
	 * 
	 * <h4>Space Complexity</h2> O(1)
	 * 
	 * @param input Original String
	 * @param str   String to check if it is a permutation of a palindrome of
	 *              parameter {@code input}
	 * @return True if parameter {@code str} is a permutation of a palindrome of
	 *         parameter {@code input}
	 */
	private static boolean isPermutationPalindrone(String string) {
		int checker = 0, index;
		String str = string.toLowerCase();
		for (index = 0; index < str.length(); ++index) {
			int i = 1 << str.charAt(index);
			if ((checker & i) == 0)
				checker |= i;
			else
				checker -= i;
		}

		int oddCount = 0;
		for (index = 0; index < str.length(); ++index) {
			int i = 1 << str.charAt(index);
			if ((checker & i) != 0)
				++oddCount;
		}

		return oddCount < 2;
	}

	public static int getCharNumber(Character c) {
		int a = Character.getNumericValue('a');
		int z = Character.getNumericValue('z');

		int val = Character.getNumericValue(c);
		if (a <= val && val <= z) {
			return val - a;
		}
		return -1;
	}

	boolean isPermutationOfPalindrome(String phrase) {
		int bitVector = createBitVector(phrase);
		return bitVector == 0 || checkExactlyOneBitSet(bitVector);
	}

	/*
	 * Create a bit vector for the string. For each letter with value 9, toggle the
	 * ith bit.
	 */
	int createBitVector(String phrase) {
		int bitVector = 0;
		for (char c : phrase.toCharArray()) {
			int x = getCharNumber(c);
			bitVector = toggle(bitVector, x);
		}
		return bitVector;
	}

	/* Toggle the ith bit in the integer. */
	int toggle(int bitVector, int index) {
		if (index < 0)
			return bitVector;

		int mask = 1 << index;
		if ((bitVector & mask) == 0) {
			bitVector |= mask;
		} else {
			bitVector &= ~mask;
		}
		return bitVector;
	}

	/*
	 * Check that at most one bit is set by subtracting one from the integer and
	 * ANDing it with the original integer.
	 */
	public static boolean checkExactlyOneBitSet(int bitVector) {
		return (bitVector & (bitVector - 1)) == 0;
	}

	/**
	 * <h1>1.5</h1>
	 * 
	 * <pre>
	 * One Away: There are three types of edits that can be performed on
	 * strings: insert a character, remove a character, or replace a character.
	 * Given two strings, write a function to check if they are one edit (or zero
	 * edits) away.
	 * </pre>
	 * 
	 * <h2>Examples</h2>
	 * 
	 * <h3>General Case</h3>
	 * 
	 * <pre>
	 * pale,  ple  -> true 
	 * pales, pale -> true 
	 * pale,  bale -> true
	 * pale,  bae  -> false
	 * </pre>
	 * 
	 * <h2>Algorithms</h2>
	 * 
	 * <pre>
	 * 1. Loop through each character of each input string and returns false if there is more than one
	 *    difference.
	 * </pre>
	 * 
	 * <h4>Time Complexity</h2> O(Length of the shortest string)
	 * 
	 * <h4>Space Complexity</h2> O(1)
	 * 
	 * @param str1 First input string to be compared with the second input string
	 * @param str2 Second input string to be compared with the first input string
	 *             parameter {@code input}
	 * @return True if the parameter strings are equal or one edit away from being
	 *         equal. Else return false.
	 */
	private static boolean oneChangeDiff(String first, String second) {
		// todo Error checking

		if (Math.abs(first.length() - second.length()) > 1)
			return false;

		boolean diff = false;
		if (first.length() == second.length()) {
			for (int i = 0; i < first.length(); ++i) {
				if (first.charAt(i) != second.charAt(i)) {
					if (diff)
						return false;
					diff = true;
				}
			}
		} else {
			int index1 = 0, index2 = 0;
			while (index1 < first.length() && index2 < second.length()) {
				if (first.charAt(index1) != second.charAt(index2)) {
					if (index1 != index2)
						return false;
					if (first.length() > second.length())
						++index1;
					else
						++index2;
				} else {
					++index1;
					++index2;
				}
			}
		}

		return true;
	}

	/**
	 * 1.6 String Compression: Implement a method to perform basic string
	 * compression using the counts of repeated characters. For example, the string
	 * aabcccccaaa would become a2b1c5a3. If the "compressed" string would not
	 * become smaller than the original string, your method should return the
	 * original string. You can assume the string has only uppercase and lowercase
	 * letters (a-z).
	 * 
	 * Examples
	 * <h3>General Case</h3>
	 * 
	 * <pre>
	 * aabcccccaaa -> a2b1c5a3
	 * kglassssssssssssslsdas -> k1g1l1a1s13l1s1d1a1s1
	 * </pre>
	 * 
	 * <h3>Special Case</h3>
	 * 
	 * <pre>
	 * zzee -> zzee
	 * sda -> sda
	 * </pre>
	 * 
	 * <h2>Algorithm</h2>
	 * 
	 * <pre>
	 * 1. Append each letter with the number of times it is seen consecutively from left to right.
	 * </pre>
	 * 
	 * <h4>Time Complexity</h2> O(Length of the string)
	 * 
	 * <h4>Space Complexity</h2> O(N)
	 * 
	 * @param str Input string to be compressed
	 * @return The compressed string or the parameter string if it is shorter than
	 *         the compressed string
	 */
	private static String compress(String str) {
		int count = 1, index = 0;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(str.charAt(index));
		for (index = 0; index < str.length() - 1; ++index) {
			char letter = str.charAt(index);
			char nextLetter = str.charAt(index + 1);
			if (letter == nextLetter)
				++count;
			else {
				stringBuilder.append(count);
				stringBuilder.append(nextLetter);
				letter = nextLetter;
				count = 1;
			}
		}
		stringBuilder.append(count);
		String result = stringBuilder.toString();

		if (result.length() < str.length())
			return result;
		else
			return str;
	}

	/**
	 * 1.7 Rotate Matrix: Given an image represented by an NxN matrix, where each
	 * pixel in the image is 4 bytes, write a method to rotate the image by 90
	 * degrees. Can you do this in place?
	 * 
	 * Examples
	 * <h3>General Case</h3>
	 * (0,0) (0,1) (0,2) (0,3)
	 *  [W]   [B]   [R]   [G]
	 * 
	 * (1,0) (1,1) (1,2) (1,3)
	 *  [G]   [A]   [C]   [H]
	 * 
	 * (2,0) (2,1) (2,2) (2,3)
	 *  [D]   [E]   [F]   [I]
	 * 
	 * (3,0) (3,1) (3,2) (3,3)
	 *  [K]   [L]   [M]   [N]
	 * 
	 *            ||
	 *            \/
	 * 
	 * (0,0) (0,1) (0,2) (0,3) [K] [D] [G] [W]
	 * 
	 * (1,0) (1,1) (1,2) (1,3) [L] [E] [A] [B]
	 * 
	 * (2,0) (2,1) (2,2) (2,3) [M] [F] [C] [R]
	 * 
	 * (3,0) (3,1) (3,2) (3,3) [N] [I] [H] [G]
	 * 
	 * <h3>Base Case</h3>
	 * 
	 * (0,0) (0,1) [R] [G]
	 * 
	 * (1,0) (1,1) [B] [Y]
	 * 
	 *        ||
	 *        \/
	 * 
	 * (0,0) (0,1) [B] [R]
	 * 
	 * (1,0) (1,1) [Y] [G]
	 * 
	 * <h2>Algorithm</h2>
	 * <pre>
	 * 1. If N is even then rotate the core 2X2 matrix in place
	 * 2. Rotate the outer rows in columns in place
	 * </pre>
	 * 
	 * <h4>Time Complexity</h2> O(N/2)N)
	 * 
	 * <h4>Space Complexity</h2> O(N)
	 * 
	 * @param str Input string to be compressed
	 * @return The compressed string or the parameter string if it is shorter than
	 *         the compressed string
	 */
	public static void rotate(int image[][]) {
		boolean evenLength = image.length % 2 == 0;
		int coreIndex = evenLength ? (image.length / 2) - 1 : image.length / 2;

		if (evenLength) {
			int maxIndex = coreIndex + 1;
			int topLeft = image[coreIndex][coreIndex];
			// top left
			int temp = image[maxIndex][coreIndex];
			image[coreIndex][coreIndex] = temp;
			// bottom left
			image[maxIndex][coreIndex] = image[maxIndex][maxIndex];
			// bottom right
			image[maxIndex][maxIndex] = image[coreIndex][maxIndex];
			// top right
			image[coreIndex][maxIndex] = topLeft;
		}

		if (image.length > 2) 
			setSides(image, coreIndex - 1);

		printMatrix(image, image.length);
	}

	private static void setSides(int image[][], int ring) {
		if (ring < 0)
			return;

		int tempRow[] = new int[image.length - ring];
		setSide(image, "top", ring, tempRow);
		setSide(image, "left", ring, tempRow);
		setSide(image, "bottom", ring, tempRow);
		setSide(image, "right", ring, tempRow);
		setSides(image, ring - 1);
	}

	private static void setSide(int image[][], String row, int ring, int tempRow[]) {
		int max = image.length - 1 - ring;
		int count = max;
		switch(row) {
		
		case "top":
			for (int i = ring; i <= max; ++i) {
				tempRow[count] = image[ring][count];
				image[ring][count] = image[i][ring];
				--count;
			}
			break;
			
		case "left":
			for (int i = max; i > ring; --i) 
				image[i][ring] = image[max][i];
			
			break;
			
		case "bottom":
			for (int i = ring + 1; i < max; ++i)
				image[max][--count] = image[i][max];
			
			image[max][max] = tempRow[max];
			break;
		
		case "right":
			for (int i = ring; i < max; ++i) 
				image[i][max] = tempRow[i];
			
			break;
		}
	}

	private static void printMatrix(int image[][], int numColumns) {
		for (int row = 0; row < image.length; ++row)
			for (int column = 0; column < numColumns; ++column) {
				System.out.print("[" + image[row][column] + "]");
				if (column == numColumns - 1)
					System.out.println();
			}
	}

	/**
	 * 1.8 Zero Matrix: Write an algorithm such that if an element in an MxN matrix
	 * is 0, its entire row and column are set to 0.
	 * 
	 * Examples
	 * <h3>General Case</h3>
	 * (0,0) (0,1) (0,2)
	 *  [1]   [2]   [3]
	 *  
	 *  (1,0) (1,1) (1,2)
	 *  [4]   [0]   [6] 
	 *  
	 *  (2,0) (2,1) (2,2)
	 *  [7]   [8]   [9]
	 *  
	 *        ||
	 *        \/
	 *        
	 *  (0,0) (0,1) (0,2)
	 *  [1]   [0]   [3]
	 *  
	 *  (1,0) (1,1) (1,2)
	 *  [0]   [0]   [0] 
	 *  
	 *  (2,0) (2,1) (2,2)
	 *  [7]   [0]   [9]
	 *  
	 * <h2>Algorithm</h2>
	 * 
	 * <h3>Bit Vector<h3>
	 * <pre>
	 * 1. Check each row and add the row index a zero is found in to a bit vector and the column index a zero is found in to another
	 *    bit vector 
	 * 2. Set zeros for any row or column that a zero is found
	 * </pre>
	 * 
	 * <h4>Time Complexity</h2>
	 * O(M*N)
	 * 
	 * <h4>Space Complexity</h2>
	 * O(1)
	 * 
	 * @param matrix
	 */
	public static void zeroMatrix (int matrix[][], int numColumns) {
		int zeroRows = 0;
		int zeroColumns = 0;
		
		for (int row = 0; row < matrix.length; ++row) {
			for (int column = 0; column < numColumns; ++column) {
				if (matrix[row][column] == 0)  {
					zeroRows |= 1 << row;
					zeroColumns |= 1 << column;
				}
			}
		}
		
		for (int row = 0; row < matrix.length; ++row) {
			if ((zeroRows & (1 << row)) != 0)
				for (int column = 0; column < numColumns; ++column)
					matrix[row][column] = 0;
		}
		
		for (int column = 0; column < numColumns; ++column) {
			if ((zeroColumns & (1 << column)) != 0)
				for (int row = 0; row < matrix.length; ++row)
					matrix[row][column] = 0;
		}
		
		printMatrix(matrix, numColumns);
	}

	/**
	 * <h1>1.9</h1>
	 * String rotation: Assume you have a method isSubstring which checks if one
	 * word is a substring of another. Given two strings, s1 and s2, write code to
	 * check if s2 is a rotation of s1 using only one call to
	 * isSubstring(eg.,"waterbottle" is a rotation of "erbottlewat").
	 * 
	 * <h2>Examples</h2>
	 * <h3>General Case</h3>
	 * waterbottle
	 * erbottlewat
	 * terbottlewa
	 * aterbottlew
	 * 
	 * <h3>Special Case</h3>
	 * asabas
	 * sabasa
	 * 
	 * <h2>Algorithm</h2>
	 * 1. Use the isSubstring method on the s1 with the fist and last character of s2
	 * 2. If the return is false then return false;
	 * 2. If the return is true then find the index of the substring in s1;
	 * 3. Compare if every character is equal from the last character of s2 to the index;
	 * 4. If they not equal return false.
	 * 5. Otherwise return true;
	 * 
	 * <h4>Time Complexity</h2>
	 * O(Length of String)
	 * 
	 * <h4>Space Complexity</h2>
	 * O(1)
	 * 
	 * @param s1 String to be checked
	 * @param s2 String to be checked
	 */
	private static boolean isRotation(String s1, String s2) {
		if (s1.length() != s2.length()) return false;
		
		String subString = "" + s2.charAt(s2.length() -1) + s2.charAt(0); 
		
		int indexSubString = s1.indexOf(subString);
		
		if (indexSubString == -1) return false;
		
		return isSubString(s2, s1.substring(indexSubString) + s1.substring(0, indexSubString));
	}
	
	private static boolean isSubString(String s2, String subString) {
		int subStringIndex = 2;
		for (int i = 1; i < s2.length() - 1; ++i) {
			if (s2.charAt(i) != subString.charAt(subStringIndex)) return false;
			
			++subStringIndex;
		}
		return true;
	}
}
