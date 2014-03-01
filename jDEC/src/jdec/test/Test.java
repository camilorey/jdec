package jdec.test;

import jdec.math.combinatorial.Combinatorial;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int[] test = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		for (int[] combination : Combinatorial.combinations(test, 2, true)) {
			System.out.print("[");

			for (int a : combination)
				System.out.print(a + ", ");

			System.out.print("]\n");
		}
	}
}
