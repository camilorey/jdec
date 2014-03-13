package jdec.linalg;

import java.util.LinkedList;
import java.util.Queue;

import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

public class OperatorAtA extends AbstractOperator {

	private final Matrix A;

	private final Queue<Vector> vectorPool;

	protected OperatorAtA(Matrix A) {
		super(A.numColumns(), A.numColumns());
		this.A = A;
		vectorPool = new LinkedList<Vector>();
	}

	@Override
	public Vector multAdd(double alpha, Vector x, Vector y) {
		Vector Ax;
		if ((Ax = vectorPool.poll()) == null)
			Ax = x.copy();
		// compute Ax
		A.mult(x, Ax);
		// compue At(Ax)+y
		Vector result = A.transMultAdd(alpha, Ax, y);
		vectorPool.offer(Ax);
		return result;
	}

	@Override
	public Vector transMultAdd(double alpha, Vector x, Vector y) {
		// symmetric operator -- same thing as multAdd
		return multAdd(alpha, x, y);
	}

	@Override
	public Matrix scale(double alpha) {
		if (alpha < 0)
			throw new UnsupportedOperationException();
		A.scale(Math.sqrt(alpha));
		return this;
	}

	@Override
	public Matrix transpose() {
		return this;
	}

}
