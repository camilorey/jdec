package jdec.dec;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Arrays;

import jdec.linalg.CSRMatrix;
import jdec.math.LexicographicalComparator;
import jdec.math.Parity;
import no.uib.cipr.matrix.Matrix;

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
	public static int[] simplexParity(int[][] s) {
		int n = s.length;
		int[] parity = new int[n];

		for (int i = 0; i < n; i++)
			parity[i] = Parity.parityWRToSorted(s[i]);

		return parity;

	}

	static class BoundaryOperator {
		final int[][] uniqueFaces;
		final Matrix operator;

		public BoundaryOperator(int[][] uFaces, Matrix op) {
			this.uniqueFaces = uFaces;
			this.operator = op;

		}
	}

	public static BoundaryOperator simplexArrayBoundary(int[][] s, int[] parity) {

		int nSimplices = s.length;
		int facesPerSimplex = s[0].length;
		int nFaces = nSimplices * facesPerSimplex;

		// faces is an array constructed as follows:
		// faces[ . , [0,n-1) ] are the indices of the faces
		// faces[ . , n-1 ] is the index of the simplex whose boundary produced
		// the face
		// faces[ . , n] is the orientation of the face in the boundary
		// where n is the dimension of the input simplices
		int[][] faces = new int[nFaces][facesPerSimplex + 1];
		for (int i = 0; i < facesPerSimplex; i++) {
			int fStart = nSimplices * i;
			for (int f = fStart; f < fStart + nSimplices; f++) {
				System.arraycopy(s[f], 0, faces[f], 0, i);
				System.arraycopy(s[f], i + 1, faces[f], i, facesPerSimplex - i
						- 1);
				faces[f][facesPerSimplex - 1] = f - fStart;
				faces[f][facesPerSimplex] = (parity[i] + i) % 2 == 0 ? 1 : -1;
			}
		}

		// sort faces lexicographically
		LexicographicalComparator comparator = LexicographicalComparator
				.getComparator(facesPerSimplex - 1);
		Arrays.sort(faces, comparator);

		// find unique faces
		IntList indices = new IntArrayList();
		int[] prevFace = null;
		for (int i = 0; i < nFaces; i++) {
			if (prevFace == null || comparator.compare(prevFace, faces[i]) != 0)
				indices.add(i);
			prevFace = faces[i];
		}

		// build unique faces list and boundary operator arrays

		int nUniqueFaces = indices.size();
		int[][] uniqueFaces = new int[nUniqueFaces][];
		int[] rowPtrs = new int[nUniqueFaces + 1];
		int c = 0;
		for (int f : indices) {
			// row pointers are the indices of the unique faces
			rowPtrs[c] = f;
			uniqueFaces[c++] = faces[f];
		}
		rowPtrs[nUniqueFaces] = nFaces;

		// the column indices are the generating simplices indices
		// matrix elements are +1/-1 according to relative orientation
		int[] csrIndices = new int[faces.length];
		double[] csrData = new double[faces.length];
		for (int i = 0; i < faces.length; i++) {
			csrIndices[i] = faces[i][facesPerSimplex - 1];
			csrData[i] = faces[i][facesPerSimplex];
		}
		return new BoundaryOperator(uniqueFaces, new CSRMatrix(nUniqueFaces,
				nSimplices, csrData, csrIndices, rowPtrs));
	}
}
