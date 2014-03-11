package jdec.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import jdec.math.LexicographicalComparator;
import jdec.math.Parity;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Simplex implements Comparable<Simplex> {
	private final int parity;
	private final int[] array;
	private final int arrayDim;
	private final Comparator<int[]> comparator;
	private int hashCode;
	private String toString;

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
		this.arrayDim = array.length;
		this.array = Arrays.copyOf(array, arrayDim);
		Arrays.sort(this.array);
		this.parity = (parity + Parity.relativeParity(this.array, array)) % 2;
		this.comparator = LexicographicalComparator.getComparator(arrayDim);
	}

	public List<Simplex> boundary() {
		List<Simplex> boundary = new ArrayList<Simplex>();
		int[] boundaryElement = new int[arrayDim - 1];
		for (int n = 0; n < arrayDim; n++) {
			System.arraycopy(this.array, 0, boundaryElement, 0, n);
			System.arraycopy(this.array, n + 1, boundaryElement, n, arrayDim
					- n);
			boundary.add(new Simplex(boundaryElement, (this.parity + n) % 2));
		}
		return boundary;
	}

	@Override
	public String toString() {
		if (toString == null) {
			toString = "simplex([";
			for (int i = 0; i < arrayDim - 1; i++) {
				toString += array[i] + ", ";
				toString += array[arrayDim - 1] + "]";
			}
			toString += ", parity=" + parity + ")";
		}
		return toString;
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
			for (int p : array)
				hashCodeBuilder.append(p);
			hashCode = hashCodeBuilder.append(parity).build();
		}
		return hashCode;
	}

	@Override
	public int compareTo(Simplex o) {
		int compareParity = o.parity - this.parity;
		if (compareParity != 0)
			return compareParity;
		else
			return comparator.compare(this.array, o.array);
	}

}
