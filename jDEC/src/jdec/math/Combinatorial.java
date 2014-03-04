package jdec.math;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * Simple algorithms for iterating over combinations and permutations
 * 
 * @author Stefano Frambati
 * 
 */
public class Combinatorial {

	private Combinatorial() {
	}

	private static <T> void swap(T[] data, int i, int j) {
		T t = data[i];
		data[i] = data[j];
		data[j] = t;
	}

	private static void swap(int[] data, int i, int j) {
		data[i] ^= data[j];
		data[j] ^= data[i];
		data[i] ^= data[j];
	}

	private static void reverse(int[] data, int start, int end) {
		while (end > start)
			swap(data, start++, end--);
	}

	private static <T> void reverse(T[] data, int start, int end) {
		while (end > start)
			swap(data, start++, end--);
	}

	private static void rotateLeft(int[] data, int k) {
		k %= data.length;
		reverse(data, 0, k - 1);
		reverse(data, k, data.length - 1);
		reverse(data, 0, data.length - 1);
	}

	private static <T> void rotateLeft(T[] data, int k) {
		k %= data.length;
		reverse(data, 0, k - 1);
		reverse(data, k, data.length - 1);
		reverse(data, 0, data.length - 1);
	}

	public static void rotateLeftDisjoint(int[] data, int start1, int end1,
			int start2, int end2, int k) {
		int interval = (end1 - start1 + 1) + (end2 - start2 + 1);
		if (interval == 0)
			return;
		k %= interval;
		if (k == 0)
			return;
		if (start1 + k <= end1) {
			reverse(data, start1, start1 + k - 1);
			reverseDisjoint(data, start1 + k, end1, start2, end2);
		} else {
			k -= (end1 - start1 + 1);
			reverseDisjoint(data, start1, end1, start2, start2 + k - 1);
			reverse(data, start2 + k, end2);
		}
		reverseDisjoint(data, start1, end1, start2, end2);
	}

	public static <T> void rotateLeftDisjoint(T[] data, int start1, int end1,
			int start2, int end2, int k) {
		int interval = (end1 - start1 + 1) + (end2 - start2 + 1);
		if (interval == 0)
			return;
		k %= interval;
		if (k == 0)
			return;
		if (start1 + k <= end1) {
			reverse(data, start1, start1 + k - 1);
			reverseDisjoint(data, start1 + k, end1, start2, end2);
		} else {
			k -= (end1 - start1 + 1);
			reverseDisjoint(data, start1, end1, start2, start2 + k - 1);
			reverse(data, start2 + k, end2);
		}
		reverseDisjoint(data, start1, end1, start2, end2);
	}

	/**
	 * Reverse two disjoint regions, skipping all in between and outside the
	 * regions
	 * 
	 * @param data
	 * @param start1
	 * @param end1
	 * @param start2
	 * @param end2
	 */
	private static void reverseDisjoint(int[] data, int start1, int end1,
			int start2, int end2) {
		if (end2 < start2)
			reverse(data, start1, end1);
		else if (end1 < start1)
			reverse(data, start2, end2);
		else
			while (end2 > start1) {
				if (end2 < start2) {
					end2 = end1;
					start2 = start1;
				} else if (start1 > end1) {
					start1 = start2;
					end1 = end2;
				} else
					swap(data, end2--, start1++);
			}
	}

	private static <T> void reverseDisjoint(T[] data, int start1, int end1,
			int start2, int end2) {
		if (end2 < start2)
			reverse(data, start1, end1);
		else if (end1 < start1)
			reverse(data, start2, end2);
		else
			while (end2 > start1) {
				if (end2 < start2) {
					end2 = end1;
					start2 = start1;
				} else if (start1 > end1) {
					start1 = start2;
					end1 = end2;
				} else
					swap(data, end2--, start1++);
			}
	}

	private static void rotateRight(int[] data, int k) {
		reverse(data, 0, data.length - 1);
		reverse(data, 0, k - 1);
		reverse(data, k, data.length - 1);
	}

	private static <T> void rotateRight(T[] data, int k) {
		reverse(data, 0, data.length - 1);
		reverse(data, 0, k - 1);
		reverse(data, k, data.length - 1);
	}

	/**
	 * Adapted from <a href=http://rosettacode.org/wiki/User:Margusmartsepp
	 * /Contributions/Java/Utils.java>RosettaCode</a>
	 * 
	 * @param array
	 * @param inPlace
	 * @return
	 */
	public static <T extends Comparable<? super T>> Iterable<T[]> permutations(
			T[] array, boolean inPlace) {
		final int dim = array.length;
		final T[] data = inPlace ? array : Arrays.copyOf(array, dim);
		Arrays.sort(data);
		return new Iterable<T[]>() {
			public Iterator<T[]> iterator() {
				return new Iterator<T[]>() {
					private int c = -1, d, s;
					private boolean hasNext = data.length != 0;

					public boolean hasNext() {
						return hasNext;
					}

					public T[] next() {
						if (!hasNext)
							throw new NoSuchElementException();
						if (!(c < 0)) {
							// do the swaps: place s in currently first
							// position, and reverse the part of the list to the
							// right
							swap(data, c, s);
							while (--d > ++c)
								swap(data, c, d);
							c = -1;
						}
						// prepare for next iteration
						// find the first element that is not in descending
						// order, starting from the end
						for (int i = dim - 2; i >= 0; i--)
							if (data[i].compareTo(data[i + 1]) < 0) {
								c = i;
								break;
							}

						// if none found, i.e., all is in descending order,
						// we have finished the permutations
						if (c < 0)
							hasNext = false;

						else {
							// find the smallest value, starting from next
							// element
							// (c + 1), larger than j
							s = c + 1;
							d = dim;
							for (int j = c + 2; j < d; j++)
								if (data[j].compareTo(data[s]) < 0 && //
										data[j].compareTo(data[c]) > 0)
									s = j;
						}
						return data;
					}

					public void remove() {
						throw new UnsupportedOperationException(
								"Cannot remove a permutation");
					}
				};
			}
		};
	}

	/**
	 * Type-specific version for int arrays
	 * 
	 * @param data
	 *            the int array on which to generate permutation.
	 * @return
	 */
	public static Iterable<int[]> permutations(int[] array, boolean inPlace) {
		final int dim = array.length;
		final int[] data = inPlace ? array : Arrays.copyOf(array, dim);
		Arrays.sort(data);
		return new Iterable<int[]>() {
			public Iterator<int[]> iterator() {
				return new Iterator<int[]>() {
					private int c = -1, d, s;
					private boolean hasNext = data.length != 0;

					public boolean hasNext() {
						return hasNext;
					}

					public int[] next() {
						if (!hasNext)
							throw new NoSuchElementException();
						if (!(c < 0)) {
							// do the swaps: place s in currently first
							// position, and reverse the part of the list to the
							// right
							swap(data, c, s);
							while (--d > ++c)
								swap(data, c, d);
							c = -1;
						}
						// prepare for next iteration
						// find the first element that is not in descending
						// order, starting from the end
						for (int i = dim - 2; i >= 0; i--)
							if (data[i] < data[i + 1]) {
								c = i;
								break;
							}

						// if none found, i.e., all is in descending order,
						// we have finished the permutations
						if (c < 0)
							hasNext = false;

						else {
							// find the smallest value, starting from next
							// element
							// (c + 1), larger than j
							s = c + 1;
							d = dim;
							for (int j = c + 2; j < d; j++)
								if (data[j] < data[s] && //
										data[j] > data[c])
									s = j;
						}
						return data;
					}

					public void remove() {
						throw new UnsupportedOperationException(
								"Cannot remove a permutation");

					}

				};
			}

		};
	}

	public static Iterable<int[]> permutationIndices(int n) {
		int[] indices = new int[n];
		for (int i = 0; i < n; indices[i] = i++)
			; // fill starting array
		return permutations(indices, true);
	}

	/**
	 * Returns an iterator over all k-combinations. Created by Hannu Helminem
	 * basing on C++'s std::next_permutation; ported to Java and adapted by
	 * Stefano Frambati.
	 * 
	 * 
	 * @param data
	 *            the int array on which to generate permutation.
	 * @return
	 */
	public static Iterable<int[]> combinations(int[] array, final int k,
			boolean inPlace) {
		final int dim = array.length;
		if (k < 1 || k > dim)
			throw new IllegalArgumentException(
					"Invalid value k for combinations");
		final int[] data = inPlace ? array : Arrays.copyOf(array, dim);
		Arrays.sort(data);
		return new Iterable<int[]>() {
			public Iterator<int[]> iterator() {
				return new Iterator<int[]>() {
					private int h = -1, t;
					private boolean hasNext = data.length != 0;

					public boolean hasNext() {
						return hasNext;
					}

					public int[] next() {
						if (!hasNext)
							throw new NoSuchElementException();
						if (!(h < 0)) {
							// do the swaps: swap head with tail
							swap(data, h, t);
							// rotate the rest
							rotateLeftDisjoint(data, h + 1, k - 1, t + 1,
									dim - 1, k - 1 - h);
							h = -1;
						}
						// prepare for next iteration
						// find first element in head section less than the last
						// one
						for (int i = k - 1; i >= 0; i--)
							if (data[i] < data[dim - 1]) {
								h = i;
								break;
							}

						// if none found, we have finished the combinations
						if (h < 0)
							hasNext = false;
						else {
							// find last element in tail section larger than the
							// head one;
							for (t = dim - 2; t >= k; t--)
								if (data[t] <= data[h])
									break;
							t++;
						}

						return data;
					}

					public void remove() {
						throw new UnsupportedOperationException(
								"Cannot remove a permutation");

					}

				};
			}

		};
	}

	/**
	 * Returns an iterator over all k-combinations. Created by Hannu Helminem
	 * basing on C++'s std::next_permutation; ported to Java and adapted by
	 * Stefano Frambati.
	 * 
	 * 
	 * @param data
	 *            the int array on which to generate permutation.
	 * @return
	 */
	public static <T extends Comparable<? super T>> Iterable<T[]> combinations(
			T[] array, final int k, boolean inPlace) {
		final int dim = array.length;
		if (k < 1 || k > dim)
			throw new IllegalArgumentException(
					"Invalid value k for combinations");
		final T[] data = inPlace ? array : Arrays.copyOf(array, dim);
		Arrays.sort(data);
		return new Iterable<T[]>() {
			public Iterator<T[]> iterator() {
				return new Iterator<T[]>() {
					private int h = -1, t;
					private boolean hasNext = data.length != 0;

					public boolean hasNext() {
						return hasNext;
					}

					public T[] next() {
						if (!hasNext)
							throw new NoSuchElementException();
						if (!(h < 0)) {
							// do the swaps: swap head with tail
							swap(data, h, t);
							// rotate the rest
							rotateLeftDisjoint(data, h + 1, k - 1, t + 1,
									dim - 1, k - 1 - h);
							h = -1;
						}
						// prepare for next iteration
						// find first element in head section less than the last
						// one
						for (int i = k - 1; i >= 0; i--)
							if (data[i].compareTo(data[dim - 1]) < 0) {
								h = i;
								break;
							}

						// if none found, we have finished the combinations
						if (h < 0)
							hasNext = false;
						else {
							// find last element in tail section larger than the
							// head one;
							for (t = dim - 2; t >= k; t--)
								if (data[t].compareTo(data[h]) <= 0)
									break;
							t++;
						}

						return data;
					}

					public void remove() {
						throw new UnsupportedOperationException(
								"Cannot remove a permutation");

					}

				};
			}

		};
	}

	public static Iterable<int[]> combinationIndices(int n, int k) {
		int[] indices = new int[n];
		for (int i = 0; i < n; indices[i] = i++)
			; // fill starting array
		return combinations(indices, k, true);
	}

}
