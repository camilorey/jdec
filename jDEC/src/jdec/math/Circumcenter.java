package jdec.math;

import java.util.Arrays;

import no.uib.cipr.matrix.DenseLU;
import no.uib.cipr.matrix.DenseMatrix;

public class Circumcenter {

	private static final double DEFAULT_TOLERANCE = 1.e-8;

	private Circumcenter() {
	}

	public static boolean isWellCentered(double[][] pts, int[] indices) {
		return isWellCentered(pts, indices, DEFAULT_TOLERANCE);

	}

	public static boolean isWellCentered(double[][] points, int[] indices,
			double tolerance) {
		for (double coordinate : barycentricCoordCircumcenter(points, indices))
			if (coordinate < tolerance)
				return false;
		return true;
	}

	/**
	 * Calculate barycentric coordinates relative to a list of N = K+1 distinct
	 * points defining a simplex in K-space.
	 * 
	 * @see http://www.ics.uci.edu/~eppstein/junkyard/circumcenter.html
	 * 
	 * @param points
	 *            points as a K+1 X K array, N=K+1 number of points
	 * @return the K+1 barycentric coordinates, with sum equal to 1
	 */
	public static double[] barycentricCoordCircumcenter(double[][] points,
			int[] indices) {
		DenseMatrix m = new DenseMatrix(indices.length, points[0].length);
		for (int idx : indices)
			for (int i = 0; i < points[idx].length; i++)
				m.set(idx, i, points[idx][i]);
		int n = m.numRows();
		int k = m.numColumns();
		if (n > k + 1)
			throw new IllegalArgumentException(
					"Cannot evaluate barycentric coordinates in dimension " + k
							+ " w.r.t. " + n + " points");
		DenseMatrix A = new DenseMatrix(n + 1, n + 1);
		DenseMatrix x = new DenseMatrix(n + 1, 1);
		for (int i = 0; i < n; i++) {
			double[] pti = points[indices[i]];
			for (int j = 0; j < n; j++) {
				double scal = 0;
				double[] ptj = points[indices[j]];
				for (int c = 0; c < k; c++)
					scal += pti[c] * ptj[c];
				A.set(i, j, 2 * scal);
				if (i == j)
					x.set(i, 0, scal);
			}
		}
		for (int i = 0; i < n; i++) {
			A.set(i, n, 1);
			A.set(n, i, 1);
		}
		x.set(n, 0, 1);
		new DenseLU(n + 1, n + 1).factor(A).solve(x);
		return Arrays.copyOf(x.getData(), n);
	}

	public static double[] circumcenter(double[][] points, int[] indices) {
		double[] bary = barycentricCoordCircumcenter(points, indices);
		double[] center = new double[points[0].length + 1];
		for (int i = 0; i < indices.length; i++) {
			double[] pti = points[indices[i]];
			for (int j = 0; j < pti.length; j++)
				center[j] += bary[i] * pti[j];
		}
		double[] pt0 = points[indices[0]];
		for (int j = 0; j < center.length - 1; j++)
			center[center.length - 1] += (center[j] - pt0[j])
					* (center[j] - pt0[j]);
		center[center.length - 1] = Math.sqrt(center[center.length - 1]);
		return center;
	}
}
