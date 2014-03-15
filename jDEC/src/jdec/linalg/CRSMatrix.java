package jdec.linalg;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

public class CRSMatrix extends AbstractOperator {

	private final double[] a;
	private final int[] ja;
	private final int[] ia;

	public CRSMatrix(int numRows, int numColumns, double[] data,
			int[] columnIndex, int[] rowPtr) {
		super(numRows, numColumns);
		this.a = data;
		this.ia = rowPtr;
		this.ja = columnIndex;
		assert a.length == ja.length;
		assert ia.length == numRows + 1;
	}

	@Override
	public Vector multAdd(double alpha, Vector x, Vector y) {
		if (!(x instanceof DenseVector) || !(y instanceof DenseVector))
			throw new UnsupportedOperationException();
		double[] xd = ((DenseVector) x).getData();
		double[] yd = ((DenseVector) x).getData();
		double scal;
		for (int i = 0; i < numRows; i++) {
			scal = 0;
			for (int j = ia[i]; j < ia[i + 1]; j++)
				scal += xd[ja[j]] * a[j];
			yd[i] += alpha * scal;
		}
		return y;
	}

	@Override
	public Vector transMultAdd(double alpha, Vector x, Vector y) {
		if (!(x instanceof DenseVector) || !(y instanceof DenseVector))
			throw new UnsupportedOperationException();
		double[] xd = ((DenseVector) x).getData();
		double[] yd = ((DenseVector) x).getData();
		if (alpha == 0)
			return y;
		y.scale(1 / alpha);
		for (int i = 0; i < numRows; i++)
			for (int j = ia[i]; j < ia[i + 1]; j++)
				yd[ja[j]] += xd[i] * a[j];
		return y.scale(alpha);
	}

	@Override
	public Matrix scale(double alpha) {
		if (alpha != 1)
			for (int i = 0; i < a.length; i++)
				a[i] *= alpha;
		return this;
	}

	@Override
	public Matrix transpose() {
		return;
	}

	@Override
	public double norm(Norm type) {
		switch (type) {
		case Frobenius:
			double norm = 0;
			for (double d : a)
				norm += d * d;
			return Math.sqrt(norm);
		case Infinity:
			norm = Double.MIN_VALUE;
			double[] colSums = new double[numColumns];
			for (int i = 0; i < a.length; i++)
				colSums[ja[i]] += a[i];
			for (int i = 0; i < numColumns; i++)
				if (colSums[i] > norm)
					norm = colSums[i];
		case Maxvalue:
			norm = Double.MIN_VALUE;
			for (double d : a)
				if (d > norm)
					norm = d;
			return norm;
		case One:
			norm = Double.MIN_VALUE;
			double sum = 0;
			for (int i = 0; i < numRows; i++) {
				sum = 0;
				for (int j = ia[i]; j < ia[i + 1]; j++) {
					double d = a[j];
					sum += d < 0 ? -d : d;
				}
				if (sum > norm)
					norm = sum;
			}
			return norm;
		default:
			throw new UnsupportedOperationException();

		}
	}
}
