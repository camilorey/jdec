package jdec.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import jdec.dec.SimplexArray;
import jdec.mesh.SimplicialMesh;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("/tmp/bunny.ply"));

			scanner.findWithinHorizon("element vertex", 0);
			int nPts = scanner.nextInt();
			scanner.findWithinHorizon("element face", 0);
			int nT = scanner.nextInt();
			scanner.findWithinHorizon("end_header", 0);
			double[][] vertices = new double[nPts][];
			for (int i = 0; i < nPts; i++) {
				vertices[i] = new double[] { scanner.nextDouble(),
						scanner.nextDouble(), scanner.nextDouble() };
				scanner.nextDouble();
				scanner.nextDouble();
			}

			int[][] triangles = new int[nT][];
			for (int i = 0; i < nT; i++) {
				scanner.nextInt();
				triangles[i] = new int[] { scanner.nextInt(),
						scanner.nextInt(), scanner.nextInt() };
			}
			SimplicialMesh sm = new SimplicialMesh(vertices, triangles);

			System.out.println("Boundary size: " + sm.boundary().size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
		}

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
