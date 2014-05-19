package jdec.linalg;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import no.uib.cipr.matrix.AbstractVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;

public class StackedVector extends AbstractVector {
	final List<Vector> vectors;

	private final IntList vLengths;

	private final int size;

	private StackedVector(List<Vector> vectors, int size) {
		super(size);
		this.vectors = vectors;
		vLengths = new IntArrayList();
		for (Vector v : this.vectors)
			vLengths.add(v.size());
		this.size = size;
	}

	public StackedVector stack(Vector... vectors) {
		if (vectors.length == 0)
			throw new IllegalArgumentException(
					"Must specify at least one vector");
		int size = 0;
		List<Vector> vs = new ArrayList<Vector>();
		for (Vector v : vectors) {
			size += v.size();
			vs.add(v);
		}
		return new StackedVector(vs, size);
	}

	public StackedVector stack(List<Vector> vectors) {
		if (vectors.size() == 0)
			throw new IllegalArgumentException(
					"Must specify at least one vector");
		int size = 0;
		for (Vector v : vectors)
			size += v.size();
		return new StackedVector(new ArrayList<Vector>(vectors), size);
	}

	@Override
	public Iterator<VectorEntry> iterator() {
		throw new UnsupportedOperationException("Not yet supported");
	}

	@Override
	public void set(int index, double value) {
		int i = 0;
		for (; i < vLengths.size(); i++) {
			int size = vLengths.get(i);
			if (index >= size)
				index -= size;
			else
				break;
		}
		vectors.get(i).set(index, value);
	}

	@Override
	public double get(int index) {
		int i = 0;
		for (; i < vLengths.size(); i++) {
			int size = vLengths.get(i);
			if (index >= size)
				index -= size;
			else
				break;
		}
		return vectors.get(i).get(index);
	}

	@Override
	public StackedVector copy() {
		List<Vector> vectorsCopy = new ArrayList<Vector>();
		for (Vector v : vectors)
			vectorsCopy.add(v.copy());
		return new StackedVector(vectorsCopy, size);
	}

	@Override
	public StackedVector zero() {
		for (Vector v : vectors)
			v.zero();
		return this;
	}

	@Override
	public StackedVector scale(double alpha) {
		for (Vector v : vectors)
			v.scale(alpha);
		return this;
	}

	@Override
	public StackedVector set(double alpha, Vector y) {
		if (!(y instanceof StackedVector))
			throw new UnsupportedOperationException();
		StackedVector stackedY = (StackedVector) y;
		if (vectors.size() != stackedY.vectors.size())
			throw new IllegalArgumentException();
		for (int i = 0; i < vectors.size(); i++)
			vectors.get(i).set(alpha, stackedY.vectors.get(i));
		return this;
	}

	@Override
	public StackedVector add(double alpha, Vector y) {
		if (!(y instanceof StackedVector))
			throw new UnsupportedOperationException();
		StackedVector stackedY = (StackedVector) y;
		if (vectors.size() != stackedY.vectors.size())
			throw new IllegalArgumentException();
		for (int i = 0; i < vectors.size(); i++)
			vectors.get(i).add(alpha, stackedY.vectors.get(i));
		return this;
	}

	@Override
	public double dot(Vector y) {
		if (!(y instanceof StackedVector))
			throw new UnsupportedOperationException();
		StackedVector stackedY = (StackedVector) y;
		if (vectors.size() != stackedY.vectors.size())
			throw new IllegalArgumentException();
		double dot = 0;
		for (int i = 0; i < vectors.size(); i++)
			dot += vectors.get(i).dot(stackedY.vectors.get(i));
		return dot;
	}

	@Override
	public double norm(Norm type) {
		double norm = 0, normV;
		switch (type) {
		case Infinity:
			for (Vector v : vectors)
				norm = Math.max(norm, v.norm(Norm.Infinity));
			return norm;
		case One:
			for (Vector v : vectors)
				norm += v.norm(Norm.One);
			return norm;
		case Two:
			for (Vector v : vectors) {
				normV = v.norm(Norm.Two);
				norm += normV * normV;
			}
			return Math.sqrt(norm);
		case TwoRobust:
			for (Vector v : vectors) {
				normV = v.norm(Norm.TwoRobust);
				norm += normV * normV;
			}
			return Math.sqrt(norm);
		default:
			throw new UnsupportedOperationException();
		}
	}

}
