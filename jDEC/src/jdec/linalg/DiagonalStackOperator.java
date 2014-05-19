package jdec.linalg;

import java.util.ArrayList;
import java.util.List;

import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

public class DiagonalStackOperator extends AbstractOperator {
	private final List<Matrix> matrices;

	private DiagonalStackOperator(List<Matrix> matrices, int nRows, int nColumns) {
		super(nRows, nColumns);
		this.matrices = matrices;
	}

	public static DiagonalStackOperator stack(Matrix... matrices) {
		if (matrices.length == 0)
			throw new IllegalArgumentException(
					"Must specify at least one operator");
		int nRows = 0;
		int nColumns = 0;
		List<Matrix> ms = new ArrayList<Matrix>();
		for (Matrix m : matrices) {
			nRows += m.numRows();
			nColumns += m.numColumns();
			ms.add(m);
		}
		return new DiagonalStackOperator(ms, nRows, nColumns);
	}

	public static DiagonalStackOperator stack(List<Matrix> matrices) {
		if (matrices.size() == 0)
			throw new IllegalArgumentException(
					"Must specify at least one operator");
		int nRows = 0;
		int nColumns = 0;
		for (Matrix m : matrices) {
			nRows += m.numRows();
			nColumns += m.numColumns();
		}
		return new DiagonalStackOperator(new ArrayList<Matrix>(matrices),
				nRows, nColumns);
	}

	@Override
	public Vector multAdd(double alpha, Vector x, Vector y) {
		if (!(x instanceof StackedVector) || !(y instanceof StackedVector))
			throw new IllegalArgumentException(
					"Stacked operators can only be used with stacked vectors");

		StackedVector stackedX = (StackedVector) x;
		StackedVector stackedY = (StackedVector) y;
		if (stackedX.vectors.size() != matrices.size()
				|| stackedY.vectors.size() != matrices.size())
			throw new IllegalArgumentException();
		for (int i = 0; i < matrices.size(); i++)
			matrices.get(i).multAdd(alpha, stackedX.vectors.get(i),
					stackedY.vectors.get(i));
		return stackedY;
	}

	@Override
	public Vector transMultAdd(double alpha, Vector x, Vector y) {
		if (!(x instanceof StackedVector) || !(y instanceof StackedVector))
			throw new IllegalArgumentException(
					"Stacked operators can only be used with stacked vectors");

		StackedVector stackedX = (StackedVector) x;
		StackedVector stackedY = (StackedVector) y;
		if (stackedX.vectors.size() != matrices.size()
				|| stackedY.vectors.size() != matrices.size())
			throw new IllegalArgumentException();
		for (int i = 0; i < matrices.size(); i++)
			matrices.get(i).transMultAdd(alpha, stackedX.vectors.get(i),
					stackedY.vectors.get(i));
		return stackedY;
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
		return DiagonalStackOperator.stack(mT);
	}
}