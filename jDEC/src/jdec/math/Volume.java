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
	public static double unsignedVolume(double[][] points) {
		DenseMatrix m = new DenseMatrix(points);
		int n = m.numRows();
		int k = m.numColumns();
		if (n == 0 || n > k + 1)
			throw new IllegalArgumentException("cannot calculate volume of "
					+ n + " points in dimension " + k);
		if (n == 1)
			return 1; // standard value for 0-simplices

		UpperSPDDenseMatrix A = new UpperSPDDenseMatrix(n - 1);

		for (int i = 1; i < n; i++)
			for (int j = i; j < n; j++)
				for (int c = 0; c < k; c++)
					A.add(i - 1, j - 1, (points[i][c] - points[0][c])
							* (points[j][c] - points[0][c]));
		UpperTriangDenseMatrix U = new DenseCholesky(n - 1, true).factor(A)
				.getU();
		double det = 1;
		for (int i = 1; i < n; i++)
			det *= U.get(i - 1, i - 1) / i;
		return det < 0 ? -det : det;

	}

	public static double signedVolume(double[][] points) {
		DenseMatrix m = new DenseMatrix(points);
		int n = m.numRows();
		int k = m.numColumns();
		if (n != k + 1)
			throw new IllegalArgumentException(n + " points in dimension " + k
					+ " do not define a simplex");
		if (n == 1)
			return 1; // standard value for 0-simplices

		DenseMatrix A = new DenseMatrix(n - 1, k);
		for (int i = 1; i < n; i++)
			for (int c = 0; c < k; c++)
				A.set(i - 1, c, points[i][c] - points[0][c]);
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

	public static double[][] selectPoints(double[][] points, int[] indices) {
		double[][] selPoints = new double[indices.length][];
		int c = 0;
		for (int idx : indices)
			selPoints[c++] = points[idx];
		return selPoints;
	}

	public static double unsignedVolume(double[][] points, int[] indices) {
		return unsignedVolume(selectPoints(points, indices));
	}

	public static double signedVolume(double[][] points, int[] indices) {
		return signedVolume(selectPoints(points, indices));
	}

}
