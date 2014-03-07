package jdec.dec;

import java.util.Arrays;
import java.util.Comparator;

import jdec.math.Parity;
import no.uib.cipr.matrix.sparse.CompRowMatrix;

public class SimplexArray {

	private SimplexArray() {
	}

	/**
	 * Find the row indices (of s) corresponding to the simplices stored in the
	 * rows of simplex array v. The rows of s must be stored in lexicographical
	 * order.
	 * 
	 * @param s
	 * @param v
	 * @return
	 */
	public static int[] simplexArraySearchSorted(int[][] s, int[][] v) {
		int k = s[0].length;
		if (s[0].length != v[0].length)
			throw new IllegalArgumentException("Number of columns must agree");

		LexicographicalComparator comparator = new LexicographicalComparator(k);
		int[] indices = new int[v.length];
		for (int i = 0; i < v.length; i++)
			indices[i] = Arrays.binarySearch(s, v[i], comparator);

		return indices;

	}

	/**
	 * Compute the relative parity of an array of simplices
	 * 
	 * @return
	 */
	public static int[] simplexRelativeParity(int[][] s) {
		int n = s.length;
		int[] parity = new int[n];

		for (int i = 0; i < n; i++)
			parity[i] = Parity.parityWRToSorted(s[i]);

		return parity;

	}

	public static simplexArrayBoundary(int[][] s, int[] parity) {

		int nSimplices = s.length;
		int facesPerSimplex = s[0].length;
		int nFaces = nSimplices * facesPerSimplex;

		// faces is an array constructed as follows:
		// faces[ . , [0,n-2) ] are the indices of the faces
		// faces[ . , n-2 ] is the index of the simplex whose boundary produced
		// the face
		// faces[ . , n-1] is the orientation of the face in the boundary
		int[][] faces = new int[nFaces][facesPerSimplex + 1];
		for (int i = 0; i < facesPerSimplex; i++) {
			faces nSimplices * i -- nSimplices*i+1;
			
		}

		CompRowMatrix boundaryOperator = new CompRowMatrix(numRows, numColumns, nz);

	}

	private static class LexicographicalComparator implements Comparator<int[]> {
		private final int dim;

		public LexicographicalComparator(int dim) {
			this.dim = dim;
		}

		public int compare(int[] arg0, int[] arg1) {
			for (int i = 0; i < dim; i++)
				if (arg0[i] < arg1[i])
					return -1;
				else if (arg0[i] > arg1[i])
					return 1;
			return 0;
		}
	}

}
