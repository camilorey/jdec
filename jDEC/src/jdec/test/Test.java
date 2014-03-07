package jdec.test;

import jdec.dec.SimplexArray;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int[][] s = new int[][] { { 0, 1 }, { 0, 2 }, { 1, 2 }, { 1, 3 } };
		int[][] v = new int[][] { { 1, 2 }, { 0, 2 } };
		for (int i : SimplexArray.simplexArraySearchSorted(s, v))
			System.out.print(i + " ");
		System.out.println();

		// System.out.println(Volume.unsignedVolume(new double[][] { { 0, 0 }
		// }));
		// System.out.println(Volume.unsignedVolume(new double[][] { { 0, 0 },
		// { 1, 0 } }));
		// System.out.println(Volume.unsignedVolume(new double[][] { { 0, 0, 0
		// },
		// { 0, 1, 0 }, { 1, 0, 0 } }));
		//
		// System.out
		// .println(Volume.signedVolume(new double[][] { { 0 }, { 1 } }));
		// System.out.println(Volume.signedVolume(new double[][] { { 0, 0 },
		// { 1, 0 }, { 0, 1 } }));
		// System.out.println(Volume.signedVolume(new double[][] { { 0, 0, 0 },
		// { 3, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } }));
		//
		// printArray(Circumcenter.barycentricCoordCircumcenter(new double[][] {
		// { 0 }, { 4 } }));
		// printArray(Circumcenter.barycentricCoordCircumcenter(new double[][] {
		// { 0, 0 }, { 4, 0 } }));
		// printArray(Circumcenter.barycentricCoordCircumcenter(new double[][] {
		// { 0, 0 }, { 4, 0 }, { 0, 4 } }));
		// printArray(Circumcenter.circumcenter(new double[][] { { 0 }, { 1 }
		// }));
		// printArray(Circumcenter.circumcenter(new double[][] { { 0, 0 },
		// { 1, 0 } }));
		// printArray(Circumcenter.circumcenter(new double[][] { { 0, 0 },
		// { 1, 0 }, { 0, 1 } }));

	}

	private static void printArray(double[] array) {
		System.out.print("[");
		int l = array.length;
		for (int i = 0; i < l - 1; i++)
			System.out.print(array[i] + " ");
		System.out.println(array[l - 1] + "]\n");
	}
}
