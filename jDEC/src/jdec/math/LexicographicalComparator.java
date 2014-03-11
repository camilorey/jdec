package jdec.math;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Comparator;

public class LexicographicalComparator implements Comparator<int[]> {
	private final int dim;
	private static final Int2ObjectMap<LexicographicalComparator> comparatorPool = new Int2ObjectOpenHashMap<LexicographicalComparator>();

	public LexicographicalComparator(int dim) {
		this.dim = dim;
	}

	public int compare(int[] arg0, int[] arg1) {
		for (int i = 0; i < dim; i++)
			if (arg0[i] < arg1[i])
				return -1;
			else if (arg0[i] > arg1[i])
				return 1;
		return 0;
	}

	public static LexicographicalComparator getComparator(int dim) {
		LexicographicalComparator comparator;
		if ((comparator = comparatorPool.get(dim)) == null) {
			comparator = new LexicographicalComparator(dim);
			comparatorPool.put(dim, comparator);
		}
		return comparator;
	}
}