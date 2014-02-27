package jdec.test;

import jdec.math.combinatorial.Combinatorial;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int[] permutation : Combinatorial.permutationIndices(4)) {
			System.out.print("[");

			for (int a : permutation)
				System.out.print(a + ", ");

			System.out.print("]\n");
		}
	}

}
