package jdec.linalg;

import no.uib.cipr.matrix.AbstractMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

public abstract class AbstractOperator extends AbstractMatrix {

	protected AbstractOperator(int numRows, int numColumns) {
		super(numRows, numColumns);
	}

	/**
	 * <code>y = alpha*A*x + y</code>
	 * 
	 * @param x
	 *            Vector of size <code>A.numColumns()</code>
	 * @param y
	 *            Vector of size <code>A.numRows()</code>
	 * @return y
	 */
	@Override
	public abstract Vector multAdd(double alpha, Vector x, Vector y);

	@Override
	public abstract Vector transMultAdd(double alpha, Vector x, Vector y);

	@Override
	public Matrix rank1(double alpha, Vector x, Vector y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix rank2(double alpha, Vector x, Vector y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix multAdd(double alpha, Matrix B, Matrix C) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix transAmultAdd(double alpha, Matrix B, Matrix C) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix transBmultAdd(double alpha, Matrix B, Matrix C) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix transABmultAdd(double alpha, Matrix B, Matrix C) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix rank1(double alpha, Matrix C) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix transRank1(double alpha, Matrix C) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix rank2(double alpha, Matrix B, Matrix C) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix transRank2(double alpha, Matrix B, Matrix C) {
		throw new UnsupportedOperationException();
	}

	@Override
	public abstract Matrix scale(double alpha);

	@Override
	public Matrix set(double alpha, Matrix B) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix add(double alpha, Matrix B) {
		throw new UnsupportedOperationException();
	}

	@Override
	public abstract Matrix transpose();

	@Override
	public Matrix transpose(Matrix B) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Only override the norm if you can do it easily...
	 * 
	 */
	@Override
	public double norm(Norm type) {
		throw new UnsupportedOperationException();
	}

	public abstract Vector getImageVectorTemplate();

	public abstract Vector getDomainVectorTemplate();

	protected Vector getImageVectorTemplateOf(Matrix A) {
		if (A instanceof AbstractOperator)
			return ((AbstractOperator) A).getImageVectorTemplate();
		else
			return new DenseVector(numRows());
	}

	protected Vector getDomainVectorTemplateOf(Matrix A) {
		if (A instanceof AbstractOperator)
			return ((AbstractOperator) A).getDomainVectorTemplate();
		else
			return new DenseVector(numColumns());
	}

}
