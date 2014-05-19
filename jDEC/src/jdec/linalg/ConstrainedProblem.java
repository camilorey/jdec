package jdec.linalg;

import java.util.Iterator;

import no.uib.cipr.matrix.AbstractVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;

public class ConstrainedProblem {

	private ConstrainedOperator constrainedOperator;
	private final ConstrainedVector constrainedVector;

	/**
	 * Creates a constrained problem for Ax = b with constraints Cx = k
	 * 
	 * @param A
	 * @param C
	 * @param k
	 */
	public ConstrainedProblem(Matrix A, Matrix C, Vector b, Vector k) {
		this.constrainedVector = new ConstrainedVector(b, k);
	}

	public ConstrainedOperator getOperator() {
		return constrainedOperator;
	}

	public ConstrainedVector getVector() {
		return constrainedVector;
	}

	public ConstrainedVector createConstrainedVector(Vector a, Vector b) {
		if (a.size() != constrainedVector.v1.size()
				|| b.size() != constrainedVector.v2.size())
			throw new IllegalArgumentException("Incompatible vector sizes");
		return new ConstrainedVector(a, b);
	}

	public static class ConstrainedOperator extends AbstractOperator {

		private final Matrix A;
		private final Matrix C;

		protected ConstrainedOperator(Matrix A, Matrix C) {
			super(A.numRows() + C.numRows(), A.numColumns() + C.numRows());
			if (A.numRows() != A.numColumns())
				throw new IllegalArgumentException(
						"Constrained problems can only be defined on square operators");
			if (C.numColumns() != A.numColumns())
				throw new IllegalArgumentException(
						"Constraine matrix incompatible with problm operator");
			this.A = A;
			this.C = C;
		}

		@Override
		public Vector multAdd(double alpha, Vector x, Vector y) {
			if (!(x instanceof ConstrainedVector)
					|| !(y instanceof ConstrainedVector))
				throw new IllegalArgumentException(
						"Constrained operators can only be used with constrained vectors");

			ConstrainedVector compositeX = (ConstrainedVector) x;
			ConstrainedVector compositeY = (ConstrainedVector) y;
			// compute y.v1 += alpha*A*x.v1
			A.multAdd(alpha, compositeX.v1, compositeY.v1);
			// compute y.v1 += alpha*Ct*x.v2
			C.transMultAdd(alpha, compositeX.v2, compositeY.v1);
			// compute y.v2 += alpha*C*x.v1
			C.multAdd(alpha, compositeX.v1, compositeY.v2);
			return compositeY;
		}

		@Override
		public Vector transMultAdd(double alpha, Vector x, Vector y) {
			if (!(x instanceof ConstrainedVector)
					|| !(y instanceof ConstrainedVector))
				throw new IllegalArgumentException(
						"Constrained operators can only be used with constrained vectors");

			ConstrainedVector compositeX = (ConstrainedVector) x;
			ConstrainedVector compositeY = (ConstrainedVector) y;
			// compute y.v1 += alpha*At*x.v1
			A.transMultAdd(alpha, compositeX.v1, compositeY.v1);
			// compute y.v1 += alpha*Ct*x.v2
			C.transMultAdd(alpha, compositeX.v2, compositeY.v1);
			// compute y.v2 += alpha*C*x.v1
			C.multAdd(alpha, compositeX.v1, compositeY.v2);
			return compositeY;
		}

		@Override
		public Matrix scale(double alpha) {
			A.scale(alpha);
			return this;
		}

		@Override
		public Matrix transpose() {
			return new ConstrainedOperator(A.transpose(), C);
		}

	}

	public static class ConstrainedVector extends AbstractVector {
		private final Vector v1;
		private final Vector v2;

		private final int v2start;

		private ConstrainedVector(Vector v1, Vector v2) {
			super(v1.size() + v2.size());
			this.v1 = v1;
			this.v2 = v2;
			this.v2start = v1.size();
		}

		@Override
		public Iterator<VectorEntry> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void set(int index, double value) {
			if (index < v2start)
				v1.set(index, value);
			else
				v2.set(index - v2start, value);
		}

		@Override
		public double get(int index) {
			if (index < v2start)
				return v1.get(index);
			else
				return v2.get(index - v2start);
		}

		@Override
		public ConstrainedVector copy() {
			return new ConstrainedVector(v1.copy(), v2.copy());
		}

		@Override
		public ConstrainedVector zero() {
			v1.zero();
			v2.zero();
			return this;
		}

		@Override
		public ConstrainedVector scale(double alpha) {
			v1.scale(alpha);
			v2.scale(alpha);
			return this;
		}

		@Override
		public ConstrainedVector set(double alpha, Vector y) {
			if (!(y instanceof ConstrainedVector))
				throw new UnsupportedOperationException();
			ConstrainedVector compositeY = (ConstrainedVector) y;
			this.v1.set(alpha, compositeY.v1);
			this.v2.set(alpha, compositeY.v2);
			return this;
		}

		@Override
		public ConstrainedVector add(double alpha, Vector y) {
			if (!(y instanceof ConstrainedVector))
				throw new UnsupportedOperationException();
			ConstrainedVector compositeY = (ConstrainedVector) y;
			this.v1.add(alpha, compositeY.v1);
			this.v2.add(alpha, compositeY.v2);
			return this;
		}

		@Override
		public double dot(Vector y) {
			if (!(y instanceof ConstrainedVector))
				throw new UnsupportedOperationException();
			ConstrainedVector compositeY = (ConstrainedVector) y;
			return this.v1.dot(compositeY.v1) + this.v2.dot(compositeY.v2);
		}

		@Override
		public double norm(Norm type) {
			switch (type) {
			case Infinity:
				return Math.max(v1.norm(Norm.Infinity), v2.norm(Norm.Infinity));
			case One:
				return v1.norm(Norm.One) + v2.norm(Norm.One);
			case Two:
				double v1norm2 = v1.norm(Norm.Two);
				double v2norm2 = v2.norm(Norm.Two);
				return Math.sqrt(v1norm2 * v1norm2 + v2norm2 * v2norm2);
			case TwoRobust:
				v1norm2 = v1.norm(Norm.TwoRobust);
				v2norm2 = v2.norm(Norm.TwoRobust);
				return Math.sqrt(v1norm2 * v1norm2 + v2norm2 * v2norm2);
			default:
				throw new UnsupportedOperationException();
			}
		}

	}

}
