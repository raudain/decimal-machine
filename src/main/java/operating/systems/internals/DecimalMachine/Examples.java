package operating.systems.internals.DecimalMachine;

public class Examples {

	
	void product(int[] array) {
		int sum = 0;
		int product = 1;
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		System.out.println(sum + ", " + product);
	}
	
	
	void printPairs(int[] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				System.out.println(array[i] + array[j]);
			}
		}
	}
	
	
	void printUnorderedPairs(int[] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = i + 1; j < array.length; j++) {
				System.out.println(array[i] + "," + array[j]);
			}
		}
	}
	
	
	void printUnorderedPairs(int[] arrayA, int[] arrayB) {
		for (int i = 0; i < arrayA.length; i++ ) {
			for (int j = 0; j < arrayB.length; j++ ) {
				if (arrayA[i] < arrayB[j]) {
					System.out.println(arrayA[i] + "," + arrayB[j]);
				}
			}
		}
	}
	
	
	void Example5(int[] arrayA, int[] arrayB) {
		for (int i = 0; i < arrayA.length; i++ ) {
			for (int j = 0; j < arrayB.length; j++ ) {
				for (int k = 0; k < 100000; k++) {
					System.out.println(arrayA[i] + "," + arrayB[j]);
				}
			}
		}
	}
	
	
	void reverse(int[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			int other = array.length - i - 1;
			int temp = array[i];
			array[i] = array[other];
			array[other] = temp;
		}
	}
	
	
	/*
	 * Example 8
	 * 
	 * Algorithm where the parameter is an array of strings. The each string is sorted then the array is sorted
	 * 
	 */
	
	
	public int sum(Node node) {
		if (node == null) {
			return 0;
		}
		return sum(node.left) + node.value + sum(node.right);
	}	
	
	class Node {
		int value;
		Node left;
		Node right;
	}
	
	
	boolean isPrime(int n) {
		for (int x = 2; x * x <= n; x++) {
			if (n % x == 0) {
				return false;
			}
		}
		return true;
	}
	
	
	int factorial(int n) {
		if (n < 0) {
			return -1;
		} else if (n == 0) {
			return 1;
		} else {
			return n * factorial(n - 1);
		}
	}
	
	
	void permutation(String str) {
		permutation(str, "");
	}
	
	void permutation(String str, String prefix) {
		if (str.length() == 0) {
			System.out.println(prefix);
		} else {
			for (int i = 0; i < str.length(); i++) {
				String rem = str.substring(0, i) + str.substring(i + 1);
				permutation(rem, prefix + str.charAt(i));
			}
		}
	}
	
	
	void allFib(int n) {
		for (int i = 0; i < n; i++) {
			System.out.println(i + ": " + fib(i));
		}
	}
	
	int fib(int n) {
		if (n <= 0) return 0;
		else if (n == 1) return 1;
		return fib(n-1) + fib(n - 2);
	}
	
	
}
