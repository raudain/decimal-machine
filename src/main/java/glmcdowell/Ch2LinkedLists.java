package glmcdowell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class Ch2LinkedLists {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/*
	 * <h1>2.1</h1>
	 * Remove Dups: Write code to remove duplicates from an unsorted linked list.
	 * FOLLOW UP
	 * How would you solve this problem if a temporary buffer is not allowed?
	 * 
	 * <h2>Examples</h2>
	 * <h3>General Case</h3>
	 * Input:  3 -> 5 -> 8 -> 5 -> 8 -> 2 -> 1
	 * Output: 3 -> 5 -> 8 -> 5 -> 2 -> 1
	 * 
	 * <h3>Special Case</h3>
	 * 
	 * <h2>Algorithm</h2>
	 * 1. Scan linked list adding elements to set
	 * 2. If the current element is in already in the set then
	 * 
	 * <h4>Time Complexity</h2>
	 * 
	 * <h4>Space Complexity</h2>
	 */
	static void removeDups (LinkedList<Integer> linkedList) {
		Set<Integer> set = new HashSet<Integer>();
		Iterator<Integer> iterator = linkedList.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			if (!set.add(iterator.next())) {
				linkedList.remove(index);
			}
			++index;
		}
	}
	
	/*
	 * <h1>2.2</h1>
	 * Return Kth to Last: Implement an algorithm to find the kth to last element of a singly linked list.
	 * 
	 * <h2>Examples</h2>
	 * <h3>General Case</h3>
	 * 
	 * <h3>Special Case</h3>
	 * 
	 * <h2>Algorithm</h2>
	 * 1. 
	 * 
	 * <h4>Time Complexity</h2>
	 * 
	 * <h4>Space Complexity</h2>
	 */
	

	/*
	 * <h1>2.3</h1>
	 * Delete Middle Node: Implement an algorithm to delete a node in the middle (i.e., any node but
	 * the first and last node, not necessarily the exact middle) of a singly linked list, given only access to
	 * that node.
	 * 
	 * <h2>Examples</h2>
	 * <h3>General Case</h3>
	 * Input: the node c from the linked list a->b->c->d->e->f
	 * Result: nothing is returned, but the new linked list looks like a->b->d->e->f
	 * 
	 * <h3>Special Case</h3>
	 * 
	 * <h2>Algorithm</h2>
	 * 1. 
	 * 
	 * <h4>Time Complexity</h2>
	 * 
	 * <h4>Space Complexity</h2>
	 */
	
	
	/*
	 * <h1>2.4</h1>
	 * Partition: Write code to partition a linked list around a value x, such that all nodes less than x come
	 * before all nodes greater than or equal to x. If x is contained within the list, the values of x only need
	 * to be after the elements less than x (see below). The partition element x can appear anywhere in the
	 * "right partition"; it does not need to appear between the left and right partitions.
	 * 
	 * <h2>Examples</h2>
	 * <h3>General Case</h3>
	 * Input:  3 -> 5 -> 8 -> 5 -> 10 -> 2 -> 1[partition = 5]
	 * Output: 3 -> 1 -> 2 -> 10 -> 5 -> 5 -> 8
	 * 
	 * <h3>Special Case</h3>
	 * 
	 * <h2>Algorithm</h2>
	 * 1. 
	 * 
	 * <h4>Time Complexity</h2>
	 * 
	 * <h4>Space Complexity</h2>
	 */
	
	
	/*
	 * <h1>2.5</h1>
	 * Sum Lists: You have two numbers represented by a linked list, where each node contains a single
	 * digit. The digits are stored in reverse order, such that the 1's digit is at the head of the list. Write a
	 * function that adds the two numbers and returns the sum as a linked list.
	 * 
	 * <h2>Examples</h2>
	 * <h3>General Case</h3>
	 * Input: (7 -> 1 -> 6) + (5 -> 9 -> 2). That is, 617 + 295.
	 * Output: 2 -> 1 -> 9. That is, 912.
	 * FOLOW UP
	 * Suppose the digits are stored in foward order. Repeat the above problem.
	 * EXAMPLE
	 * Input: (6 -> 1 -> 7) + (2 -> 9 -> 5). That is, 617 + 295.
	 * Output: 9 -> 1 -> 2. That is, 912.
	 * 
	 * <h3>Special Case</h3>
	 * 
	 * <h2>Algorithm</h2>
	 * 1. 
	 * 
	 * <h4>Time Complexity</h2>
	 * 
	 * <h4>Space Complexity</h2>
	 */
	
	
	/*
	 * <h1>2.6</h1>
	 * Palindrome: Implement a function to check if a linked list is a palindrome
	 * 
	 * <h2>Examples</h2>
	 * <h3>General Case</h3>
	 * 
	 * <h3>Special Case</h3>
	 * 
	 * <h2>Algorithm</h2>
	 * 1. 
	 * 
	 * <h4>Time Complexity</h2>
	 * 
	 * <h4>Space Complexity</h2>
	 */
	
	
	/*
	 * <h1>2.7</h1>
	 * Intersection: Given two (singly linked lists, determine if the two lists intersect. Return the inter-
	 * secting node. Note that the intersection is defined based on reference, not value. That is, if the kth
	 * node of the first linked list is the exact same node (by reference) as the jth node of the second
	 * linked list, then they are intersecting.
	 * 
	 * <h2>Examples</h2>
	 * <h3>General Case</h3>
	 * 
	 * <h3>Special Case</h3>
	 * 
	 * <h2>Algorithm</h2>
	 * 1. 
	 * 
	 * <h4>Time Complexity</h2>
	 * 
	 * <h4>Space Complexity</h2>
	 */
	
	
	/*
	 * <h1>2.8</h1>
	 * Loop Detection: Given a circular linked list, implement an algorithm that returns the node at the beginning of the loop.
	 * DEFINITION
	 * Circular linkded list: A (corrupt) linked list which a node's next pointer points to an earlier node, so
	 * as to make a loop in the linked list.
	 * 
	 * <h2>Examples</h2>
	 * <h3>General Case</h3>
	 * Input:  A -> B -> C -> D -> E -> C [the same C as earlier]
	 * Output: C
	 * 
	 * <h3>Special Case</h3>
	 * 
	 * <h2>Algorithm</h2>
	 * 1. 
	 * 
	 * <h4>Time Complexity</h2>
	 * 
	 * <h4>Space Complexity</h2>
	 */
}
