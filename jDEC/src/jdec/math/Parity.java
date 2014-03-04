package jdec.math;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
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
