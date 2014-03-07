package jdec.math;

import it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Arrays;

public class Parity {

	private Parity() {
	}

	public static int relativeParity(int[] a, int[] b) {
		if (a == null || b == null)
			throw new NullPointerException();
		if (a.length != b.length)
			throw new IllegalArgumentException(
					"A and B are not a permutation of each other");
		Int2IntMap aMap = new Int2IntOpenHashMap();
		int c = 0;
		for (int aEntry : a)
			aMap.put(aEntry, c++);
		if (aMap.size() != a.length)
			throw new IllegalArgumentException("A contains duplicate values");
		int[] permutation = new int[a.length];
		c = 0;
		for (int bEntry : b)
			permutation[c++] = aMap.get(bEntry);

		return permutationParity(permutation, false);
	}

	/**
	 * Compute the relative parity between an array and the sorted version of
	 * itself TODO check performance w.r.t. other version of this algorithm
	 * 
	 * @param a
	 * @return
	 */
	public static int parityWRToSorted(int[] a) {
		if (a == null)
			throw new NullPointerException();
		Int2IntSortedMap aMap = new Int2IntAVLTreeMap();
		int c = 0;
		for (int aEntry : a)
			aMap.put(aEntry, c++);
		return permutationParity(aMap.values().toIntArray(), false);
	}

	/**
	 * Compute the relative parity between an array and the sorted version of
	 * itself TODO check performance w.r.t. other version of this algorithm
	 * 
	 * @param a
	 * @return
	 */
	public static int parityWRToSorted2(int[] a) {
		if (a == null)
			throw new NullPointerException();
		int k = a.length;
		int parity = 0;
		for (int start = 0; start < k - 1; start++) {
			// find position of minimum value in [start, k)
			int argmin = start;
			int valmin = a[start];
			for (int j = start; j < k; j++)
				if (a[j] < valmin) {
					argmin = j;
					valmin = a[j];
				}
			// if needed, put the smallest element in first place
			if (argmin > start) {
				Combinatorial.swap(a, argmin, start);
				parity++;
			}
		}
		return parity % 2;
	}

	public static int permutationParity(int[] permutation, boolean checkInput) {
		int n = permutation.length;
		if (checkInput) {
			int[] identity = Arrays.copyOf(permutation, n);
			Arrays.sort(identity);
			for (int i = 0; i < n; i++)
				if (identity[i] != i)
					throw new IllegalArgumentException("Invalid permutation");
		}

		// Decompose the permutation into elementary disjoint cycles and count
		// their number: if we have c cycles, the parity of the permutation will
		// be (-1)^(n-c). In fact, odd-length cycles do not change the
		// permutation sign, while even-length cycles do. since all cycles are
		// disjoint, \sum_{i=0}^c l_i = n ==> o_i = n mod 2 with o_i the number
		// of odd cycles. Since e_i = c - o_i, it follows e_i = (n - c) mod 2.
		int c = 0;
		IntSet seen = new IntOpenHashSet();
		for (int i = 0; i < n; i++) {
			// start a new cycle using a starting position not yet traversed
			// (i.e., belonging to a new cycle)
			if (seen.contains(i))
				continue;
			c++;
			int j = i;
			// follow a cycle and mark the positions which have been traversed
			do {
				seen.add(j);
				j = permutation[j];
			} while (j != i);
		}
		return (n - c) % 2;

	}
}
