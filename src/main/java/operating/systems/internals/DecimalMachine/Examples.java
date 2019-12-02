package operating.systems.internals.DecimalMachine;

/**
 * @author Gayle Laakmann Mcdowell
 */
public class Examples {

	/**
	 * Example 1 <br>
	 * O(N)
	 */
	void product(int[] array) {
		int sum = 0;
		int product = 1;
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		System.out.println(sum + ", " + product);
	}
	
	
	/**
	 * Example 2 <br>
	 * O(N^2)
	 */
	void printPairs(int[] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				System.out.println(array[i] + array[j]);
			}
		}
	}
	
	
	/**
	 * Example 3 <br>
	 * O(N^2)
	 */
	void printUnorderedPairs(int[] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = i + 1; j < array.length; j++) {
				System.out.println(array[i] + "," + array[j]);
			}
		}
	}
	
	
	/**
	 * Example 4 <br>
	 * O(ab)
	 */
	void printUnorderedPairs(int[] arrayA, int[] arrayB) {
		for (int i = 0; i < arrayA.length; i++ ) {
			for (int j = 0; j < arrayB.length; j++ ) {
				if (arrayA[i] < arrayB[j]) {
					System.out.println(arrayA[i] + "," + arrayB[j]);
				}
			}
		}
	}
	
	
	/**
	 * Example 5 <br>
	 * O(ab)
	 */
	void Example5(int[] arrayA, int[] arrayB) {
		for (int i = 0; i < arrayA.length; i++ ) {
			for (int j = 0; j < arrayB.length; j++ ) {
				for (int k = 0; k < 100000; k++) {
					System.out.println(arrayA[i] + "," + arrayB[j]);
				}
			}
		}
	}
	
	/**
	 * Example 6 <br>
	 * O(N)
	 */
	void reverse(int[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			int other = array.length - i - 1;
			int temp = array[i];
			array[i] = array[other];
			array[other] = temp;
		}
	}
	
	
	/**
	 * Example 9 <br>
	 * O(N)
	 */
	int sum(Node node) {
		if (node == null) {
			return 0;
		}
		return sum(node.left) + node.value + sum(node.right);
	}	
	
	private class Node {
		int value;
		Node left;
		Node right;
	}
	
	
	/**
	 * Example 10 <br>
	 * O(sqrt(N))
	 */
	boolean isPrime(int n) {
		for (int x = 2; x * x <= n; x++) {
			if (n % x == 0) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Example 11 <br>
	 * O(N)
	 */
	int factorial(int n) {
		if (n < 0) {
			return -1;
		} else if (n == 0) {
			return 1;
		} else {
			return n * factorial(n - 1);
		}
	}
	
	public static void main(String[] args) {
		permutation("Boy");
	}
	
	/**
	 * <h1>Example 12</h1>
	 * <h2>This code counts all permutations of a string</h2>
	 * <h3>O(n^2 * n!)</h3>
	 * 
	 * @Param String that the permutations will be printed from
	 */
	private  static void permutation(String str) {
		permutation(str, "");
	}
	
	private static void permutation(String str, String prefix) {
		String left;
		String right;
		if (str.length() == 0) {
			System.out.println(prefix);
		} else {
			for (int i = 0; i < str.length(); i++) {
				left = str.substring(0, i);
				right = str.substring(i + 1);
				String rem = str.substring(0, i) + str.substring(i + 1);
				permutation(rem, prefix + str.charAt(i));
			}
		}
	}
	
	
	/**
	 * Example 13 <br>
	 * O(2^N)
	 */
	int fib(int n) {
		if (n <= 0) return 0;
		else if (n == 1) return 1;
		return fib(n-1) + fib(n - 2);
	}
	
	/**
	 * Example 14 <br>
	 * O(2^N)
	 */
	void allFib(int n) {
		for (int i = 0; i < n; i++) {
			System.out.println(i + ": " + fib(i));
		}
	}
	
	
	/**
	 * Example 15 <br>
	 * O(N)
	 */
	void Example15(int n) {
		int[] memo = new int[n + 1];
		for (int i = 0; i < n; i++) {
			System.out.println(i + ": " + fib(i, memo));
		}
	}
	
	int fib(int n, int[] memo) {
		if (n <= 0) return 0;
		else if (n == 1) return 1;
		else if (memo[n] > 0) return memo[n];
		
		memo[n] = fib(n - 1, memo) + fib(n - 2, memo);
		return memo[n];
	}
	
	
	/**
	 *  Example 16 <br>
	 *  O(log n)
	 */
	int powersOf2(int n) {
		if (n < 1) {
			return 0;
		} else if (n == 1) {
			System.out.println(1);
			return 1;
		} else {
			int prev = powersOf2(n / 2);
			int curr = prev * 2;
			System.out.println(curr);
			return curr;
		}
	}
}
