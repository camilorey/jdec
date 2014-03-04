package jdec.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jdec.math.Parity;

public class Simplex {
	private final int parity;
	private final int[] array;
	private final int dimension;

	public Simplex(int[] array) {
		this(array, 0);
	}

	public Simplex(Simplex s) {
		this(s.array);
	}

	public Simplex(Simplex s, int parity) {
		this(s.array, parity);
	}

	public Simplex(int[] array, int parity) {
		this.dimension = array.length;
		this.array = Arrays.copyOf(array, dimension);
		Arrays.sort(this.array);
		this.parity = (parity + Parity.relativeParity(this.array, array)) % 2;
	}

	public List<Simplex> boundary() {
		List<Simplex> boundary = new ArrayList<Simplex>();
		int[] boundaryElement = new int[dimension - 1];
		for (int n = 0; n < dimension; n++) {
			System.arraycopy(this.array, 0, boundaryElement, 0, n);
			System.arraycopy(this.array, n + 1, boundaryElement, n, dimension
					- n);
			boundary.add(new Simplex(boundaryElement, (this.parity + n) % 2));
		}
		return boundary;
	}
}
