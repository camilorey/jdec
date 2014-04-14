package jdec.dec;

import jdec.dec.SimplicialComplex.Subspace;
import jdec.mesh.Simplex;
import no.uib.cipr.matrix.DenseVector;

public class Cochain {

	private final SimplicialComplex complex;
	private final Subspace space;
	private final Subspace dualSpace;
	private final int k;
	private final int n;
	private final boolean isPrimal;
	private final double[] v;

	public Cochain(SimplicialComplex complex, int dimension, boolean isPrimal) {
		this.complex = complex;
		this.k = dimension;
		this.n = complex.complexDimension();
		this.space = complex.getSubspace(k);
		this.dualSpace = complex.getSubspace(n - k);
		this.isPrimal = isPrimal;
		this.v = new double[complex.numberOfNSimplices(k)];
	}

	public Cochain add(Cochain other) {
		assert k == other.k && complex == other.complex;
		Cochain f = new Cochain(complex, k, isPrimal);
		for (int i = 0; i < v.length; i++)
			f.v[i] = v[i] + other.v[i];
		return f;
	}

	public Cochain sub(Cochain other) {
		assert k == other.k && complex == other.complex;
		Cochain f = new Cochain(complex, k, isPrimal);
		for (int i = 0; i < v.length; i++)
			f.v[i] = v[i] - other.v[i];
		return f;
	}

	public double get(Simplex key) {
		int index = space.simplexToIndex(key);
		double value = v[index];
		return space.simplex(index).compareParity(key) ? value : -value;
	}

	public double get(int key) {
		return v[key];
	}

	public void set(Simplex key, double value) {
		int index = space.simplexToIndex(key);
		if (space.simplex(index).compareParity(key))
			v[index] = value;
		else
			v[index] = -value;
	}

	public void set(int key, double value) {
		v[key] = value;
	}

	@Override
	public String toString() {
		String ret = "cochain(k=" + k + ",n=" + n + ",isPrimal=" + isPrimal
				+ "\n [";
		for (int i = 0; i < v.length - 1; i++)
			ret += v[i] + ", ";
		ret += v[v.length - 1] + "])";
		return ret;
	}

	public static Cochain d(Cochain f) {
		Cochain df = new Cochain(f.complex, f.k + 1, f.isPrimal);
		;
		if (f.isPrimal) {
			f.space.d().mult(new DenseVector(f.v, false),
					new DenseVector(df.v, false));
		} else {
			f.dualSpace.boundary().mult(f.k % 2 == 0 ? 1 : -1,
					new DenseVector(f.v, false), new DenseVector(df.v, false));
		}

		return df;
	}

	public static Cochain star(Cochain f) {
		Cochain starf = new Cochain(f.complex, f.n - f.k, !f.isPrimal);
		if (f.isPrimal) {
			f.space.star().mult(new DenseVector(f.v, false),
					new DenseVector(starf.v, false));
		} else {
			f.dualSpace.starInv().mult(new DenseVector(f.v, false),
					new DenseVector(starf.v, false));
		}
		return starf;
	}

	public static Cochain delta(Cochain f) {
		Cochain sdsf = star(d(star(f)));
		if ((f.n * (f.k - 1) + 1) % 2 != 0)
			for (int i = 0; i < sdsf.v.length; i++)
				sdsf.v[i] = -sdsf.v[i];
		return sdsf;
	}

	public static Cochain laplaceDeRham(Cochain f) {
		return d(delta(f)).add(delta(d(f)));
	}

	public static Cochain laplaceBeltrami(Cochain f) {
		return delta(d(f));
	}

}
