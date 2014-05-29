package jdec.linalg;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

public class CSRMatrix extends AbstractOperator {

	private final double[] a;
	private final int[] ja;
	private final int[] ia;

	public CSRMatrix(int numRows, int numColumns, double[] data,
			int[] columnIndex, int[] rowPtr) {
		super(numRows, numColumns);
		this.a = data;
		this.ia = rowPtr;
		this.ja = columnIndex;
		assert a.length == ja.length;
		assert ia.length == numRows + 1;
	}

	/**
	 * creates an empty CSR Matrix
	 * 
	 * @param numRows
	 * @param numColumns
	 */
	public CSRMatrix(int numRows, int numColumns) {
		this(numRows, numColumns, new double[0], new int[0],
				new int[numRows + 1]);
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
		double[] aT = new double[a.length];
		int[] iaT = new int[numColumns + 1];
		int[] jaT = new int[a.length];

		// find out the number of elements in each column
		int[] count = new int[numColumns];
		for (int i = 0; i < a.length; i++)
			count[ja[i]]++;

		// fill the transpose matrix's row pointers
		for (int i = 1; i < numColumns; i++) {
			iaT[i] = iaT[i - 1] + count[i];
			// count[i] will be useful in next section
			count[i] = iaT[i];
		}
		iaT[numColumns] = a.length;

		// now fill data and
		int q;
		for (int i = 0; i < numRows; i++)
			for (int j = ia[i]; j < ia[i + 1]; j++) {
				// i will be the transpose matrix's column number
				// q contains the pointer to the next data slot
				jaT[q = count[ja[j]]++] = i;
				aT[q] = a[j];
			}

		return new CSRMatrix(numColumns, numRows, aT, jaT, iaT);

	}

	/**
	 * Returns a Matrix acting as the transpose of this matrix. not recommended,
	 * as mult/transMult performances are inverted, with transposeMult being
	 * faster than mult. Most algorithms expect the opposite behavior.
	 * 
	 * @return
	 */
	public Matrix transposeSymbolic() {
		return new CSRMatrix(numColumns, numRows, a, ja, ia) {
			@Override
			public Vector multAdd(double alpha, Vector x, Vector y) {
				return CSRMatrix.this.transMultAdd(alpha, x, y);
			}

			@Override
			public Vector transMultAdd(double alpha, Vector x, Vector y) {
				return CSRMatrix.this.multAdd(alpha, x, y);
			}

			@Override
			public double norm(Norm type) {
				switch (type) {
				case Infinity:
					return CSRMatrix.this.norm(Norm.One);
				case One:
					return CSRMatrix.this.norm(Norm.Infinity);
				default:
					return CSRMatrix.this.norm(type);
				}

			}

			@Override
			public Matrix transpose() {
				return CSRMatrix.this;
			}

		};

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

	@Override
	public Vector getImageVectorTemplate() {
		return new DenseVector(numRows());
	}

	@Override
	public Vector getDomainVectorTemplate() {
		return new DenseVector(numColumns());
	}
}
