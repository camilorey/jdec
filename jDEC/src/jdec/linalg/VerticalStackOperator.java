package jdec.linalg;

import java.util.ArrayList;
import java.util.List;

import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

public class VerticalStackOperator extends AbstractOperator {
	private final List<Matrix> matrices;

	private VerticalStackOperator(List<Matrix> matrices, int nRows, int nColumns) {
		super(nRows, nColumns);
		this.matrices = matrices;
	}

	public static VerticalStackOperator stack(Matrix... matrices) {
		if (matrices.length == 0)
			throw new IllegalArgumentException(
					"Must specify at least one operator");
		int nColumns = -1;
		int nRows = 0;
		List<Matrix> ms = new ArrayList<Matrix>();
		for (Matrix m : matrices) {
			if (nColumns == -1)
				nColumns = m.numColumns();
			else if (nColumns != m.numColumns())
				throw new IllegalArgumentException(
						"Incompatible matrix dimensions");
			nRows += m.numRows();
			ms.add(m);
		}
		return new VerticalStackOperator(ms, nRows, nColumns);
	}

	public static VerticalStackOperator stack(List<Matrix> matrices) {
		if (matrices.size() == 0)
			throw new IllegalArgumentException(
					"Must specify at least one operator");
		int nColumns = -1;
		int nRows = 0;
		for (Matrix m : matrices) {
			if (nColumns == -1)
				nColumns = m.numColumns();
			else if (nColumns != m.numColumns())
				throw new IllegalArgumentException(
						"Incompatible matrix dimensions");
			nRows += m.numRows();
		}
		return new VerticalStackOperator(new ArrayList<Matrix>(matrices),
				nRows, nColumns);
	}

	@Override
	public Vector multAdd(double alpha, Vector x, Vector y) {
		if (!(y instanceof StackedVector))
			throw new IllegalArgumentException(
					"Stacked operators can only be used with stacked vectors");

		StackedVector stackedY = (StackedVector) y;
		if (stackedY.vectors.size() != matrices.size())
			throw new IllegalArgumentException();
		for (int i = 0; i < matrices.size(); i++)
			matrices.get(i).multAdd(alpha, x, stackedY.vectors.get(i));
		return stackedY;
	}

	@Override
	public Vector transMultAdd(double alpha, Vector x, Vector y) {
		if (!(x instanceof StackedVector))
			throw new IllegalArgumentException(
					"Stacked operators can only be used with stacked vectors");

		StackedVector stackedX = (StackedVector) x;
		if (stackedX.vectors.size() != matrices.size())
			throw new IllegalArgumentException();
		for (int i = 0; i < matrices.size(); i++)
			matrices.get(i).transMultAdd(alpha, stackedX.vectors.get(i), y);
		return y;
	}

	@Override
	public Matrix scale(double alpha) {
		for (Matrix m : matrices)
			m.scale(alpha);
		return this;
	}

	@Override
	public Matrix transpose() {
		List<Matrix> mT = new ArrayList<Matrix>();
		for (Matrix m : matrices)
			mT.add(m.transpose());
		return HorizontalStackOperator.stack(mT);
	}

	@Override
	public Vector getImageVectorTemplate() {
		List<Vector> templates = new ArrayList<Vector>();
		for (Matrix matrix : matrices)
			templates.add(getImageVectorTemplateOf(matrix));
		return StackedVector.stack(templates);
	}

	@Override
	public Vector getDomainVectorTemplate() {
		return getDomainVectorTemplateOf(matrices.get(0));
	}

}
