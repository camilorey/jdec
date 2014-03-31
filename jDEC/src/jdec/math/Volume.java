package jdec.math;

import no.uib.cipr.matrix.DenseCholesky;
import no.uib.cipr.matrix.DenseLU;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.UnitLowerTriangDenseMatrix;
import no.uib.cipr.matrix.UpperSPDDenseMatrix;
import no.uib.cipr.matrix.UpperTriangDenseMatrix;

public class Volume {

	private Volume() {
	}

	/**
	 * Calculate the unsigned volume of a list of N = K+1 distinct points
	 * defining a simplex in K-space.
	 * 
	 * @see http://www.ics.uci.edu/~eppstein/junkyard/circumcenter.html
	 * 
	 * @param points
	 *            points as a K+1 X K array, N=K+1 number of points
	 * @return the unsigned volume
	 * 
	 * @see http://www.math.niu.edu/~rusin/known-math/97/volumes.polyh
	 */
	public static double unsignedVolume(double[][] points, int[] indices) {
		DenseMatrix m = new DenseMatrix(indices.length, points[0].length);
		int n = m.numRows();
		int k = m.numColumns();
		for (int idx : indices)
			for (int i = 0; i < points[idx].length; i++)
				m.set(idx, i, points[idx][i]);

		if (n == 0 || n > k + 1)
			throw new IllegalArgumentException("cannot calculate volume of "
					+ n + " points in dimension " + k);
		if (n == 1)
			return 1; // standard value for 0-simplices

		UpperSPDDenseMatrix A = new UpperSPDDenseMatrix(n - 1);

		double[] pt0 = points[indices[0]];
		for (int i = 1; i < n; i++) {
			double[] pti = points[indices[i]];
			for (int j = i; j < n; j++) {
				double[] ptj = points[indices[j]];
				for (int c = 0; c < k; c++)
					A.add(i - 1, j - 1, (pti[c] - pt0[c]) * (ptj[c] - pt0[c]));
			}
		}

		UpperTriangDenseMatrix U = new DenseCholesky(n - 1, true).factor(A)
				.getU();
		double det = 1;
		for (int i = 1; i < n; i++)
			det *= U.get(i - 1, i - 1) / i;
		return det < 0 ? -det : det;

	}

	public static double unsignedVolume(double[][] points, int from, int to) {
		DenseMatrix m = new DenseMatrix(to - from, points[0].length);
		int n = m.numRows();
		int k = m.numColumns();
		for (int idx = from; idx < to; idx++)
			for (int i = 0; i < points[idx].length; i++)
				m.set(idx, i, points[idx][i]);

		if (n == 0 || n > k + 1)
			throw new IllegalArgumentException("cannot calculate volume of "
					+ n + " points in dimension " + k);
		if (n == 1)
			return 1; // standard value for 0-simplices

		UpperSPDDenseMatrix A = new UpperSPDDenseMatrix(n - 1);

		double[] pt0 = points[from];
		for (int i = 1; i < n; i++) {
			double[] pti = points[from + i];
			for (int j = i; j < n; j++) {
				double[] ptj = points[from + j];
				for (int c = 0; c < k; c++)
					A.add(i - 1, j - 1, (pti[c] - pt0[c]) * (ptj[c] - pt0[c]));
			}
		}

		UpperTriangDenseMatrix U = new DenseCholesky(n - 1, true).factor(A)
				.getU();
		double det = 1;
		for (int i = 1; i < n; i++)
			det *= U.get(i - 1, i - 1) / i;
		return det < 0 ? -det : det;
	}

	public static double signedVolume(double[][] points, int[] indices) {
		DenseMatrix m = new DenseMatrix(indices.length, points[0].length);
		for (int idx : indices)
			for (int i = 0; i < points[idx].length; i++)
				m.set(idx, i, points[idx][i]);

		int n = m.numRows();
		int k = m.numColumns();
		if (n != k + 1)
			throw new IllegalArgumentException(n + " points in dimension " + k
					+ " do not define a simplex");
		if (n == 1)
			return 1; // standard value for 0-simplices

		DenseMatrix A = new DenseMatrix(n - 1, k);
		double[] pt0 = points[indices[0]];
		for (int i = 1; i < n; i++) {
			double[] pti = points[indices[i]];
			for (int c = 0; c < k; c++)
				A.set(i - 1, c, pti[c] - pt0[c]);
		}
		DenseLU lu = new DenseLU(n - 1, n - 1).factor(A);
		UnitLowerTriangDenseMatrix L = lu.getL();
		UpperTriangDenseMatrix U = lu.getU();
		double det = 1;
		for (int i = 1; i < n; i++)
			det *= L.get(i - 1, i - 1) / i; // divide by i to produce n! in the
		// denominator
		for (int i = 1; i < n; i++)
			det *= U.get(i - 1, i - 1);

		return det;
	}

}
