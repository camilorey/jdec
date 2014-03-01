package jdec.test;

import jdec.math.combinatorial.Combinatorial;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int[] test = new int[] { 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 5, 6, 7, 0, 0 };
		Combinatorial.rotateLeftDisjoint(test, 4, 7, 10, 12, 1);
		test = new int[] { 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 5, 6, 7, 0, 0 };
		Combinatorial.rotateLeftDisjoint(test, 4, 7, 10, 12, 2);
		test = new int[] { 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 5, 6, 7, 0, 0 };
		Combinatorial.rotateLeftDisjoint(test, 4, 7, 10, 12, 3);
		test = new int[] { 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 5, 6, 7, 0, 0 };
		Combinatorial.rotateLeftDisjoint(test, 4, 7, 10, 12, 4);
		test = new int[] { 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 5, 6, 7, 0, 0 };
		Combinatorial.rotateLeftDisjoint(test, 4, 7, 10, 12, 5);
		test = new int[] { 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 5, 6, 7, 0, 0 };
		Combinatorial.rotateLeftDisjoint(test, 4, 7, 10, 12, 6);
		test = new int[] { 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 5, 6, 7, 0, 0 };
		Combinatorial.rotateLeftDisjoint(test, 4, 7, 10, 12, 7);
		test = new int[] { 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 5, 6, 7, 0, 0 };
		Combinatorial.rotateLeftDisjoint(test, 4, 7, 10, 12, 1);
		Combinatorial.rotateLeftDisjoint(test, 4, 7, 10, 12, 1);
		for (int[] permutation : Combinatorial.permutationIndices(4)) {
			System.out.print("[");

			for (int a : permutation)
				System.out.print(a + ", ");

			System.out.print("]\n");
		}
	}
}
