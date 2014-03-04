package jdec.math;

import java.util.Arrays;

import javax.swing.text.AbstractDocument.LeafElement;

import no.uib.cipr.matrix.DenseCholesky;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.UpperSPDDenseMatrix;
import no.uib.cipr.matrix.Vector;

public class Circumcenter {

	private static final double DEFAULT_TOLERANCE = 1.e-8;

	private Circumcenter() {
	}

	public static boolean isWellCentered(double[][] pts) {
		return isWellCentered(pts, DEFAULT_TOLERANCE);

	}

	public static boolean isWellCentered(double[][] points, double tolerance) {
		for (double coordinate : barycentricCoordCircumcenter(points))
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
	public static double[] barycentricCoordCircumcenter(double[][] points) {
		DenseMatrix m = new DenseMatrix(points);
		int n = m.numRows();
		int k = m.numColumns();
		if (n != k + 1)
			throw new IllegalArgumentException(
					"Cannot evaluate barycentric coordinates in dimension " + k
							+ " w.r.t. " + n + " points");
		UpperSPDDenseMatrix A = new UpperSPDDenseMatrix(n + 1);
		for (int i = 0; i < n; i++)
			for (int j = i; j < n; j++)
				for (int c = 0; c < k; c++)
					A.add(j, i, 2 * points[i][c] * points[j][c]);
		for (int i = 0; i < n; i++) {
			A.set(i, n, 1);
		}
		Vector b = new DenseVector(n + 1);
		for (int i = 0; i < n; i++)
			for (int j = 0; j < k; j++)
				b.add(i, points[i][j] * points[i][j]);
		b.set(n, 1);
		DenseMatrix x = new DenseMatrix(n + 1, 1);
		// TODO: check that it IS SPD !!
		DenseCholesky chol = new DenseCholesky(n + 1, true);
		assert chol.isSPD();
		chol.factor(A).solve(x);
		return Arrays.copyOf(x.getData(), n);
	}

	public double[] circumcenter(double[][] points) {
		double[] bary = barycentricCoordCircumcenter(points);
		double[] center = new double[points[0].length + 1];
		for (double[] point : points)
			for (int j = 0; j < point.length; j++)
				center[j] += bary[j] * point[j];
		for (int j = 0; j < center.length - 1; j++) {
			center[j] /= points.length;
			center[center.length - 1] += (center[j] - points[0][j])
					* (center[j] - points[0][j]);
		}
		center[center.length - 1] = Math.sqrt(center[center.length - 1]);
		return center;
	}
}
