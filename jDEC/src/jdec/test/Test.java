package jdec.test;

import jdec.math.Combinatorial;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int[] test = new int[] { 0, 0, 0, 1, 1, 2 };
		int c = 0;
		for (int[] combination : Combinatorial.combinations(test, 3, true)) {
			System.out.print("[");

			for (int a : combination)
				System.out.print(a + ", ");

			System.out.print("]\n");
			c++;
		}
		System.out.println(c);
	}
}
