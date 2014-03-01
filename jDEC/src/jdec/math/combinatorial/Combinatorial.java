package jdec.math.combinatorial;

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

	public static void rotateLeftDisjoint(int[] data, int start1, int end1,
			int start2, int end2, int k) {
		k %= (end1 - start1 + 1) + (end2 - start2 + 1);
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

	private static <T> void rotateLeft(T[] data, int k) {
		k %= data.length;
		reverse(data, 0, k - 1);
		reverse(data, k, data.length - 1);
		reverse(data, 0, data.length - 1);
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
		while (end2 > start1) {
			swap(data, end2--, start1++);
			if (end2 < start2) {
				end2 = end1;
				start2 = start1;
			} else if (start1 > end1) {
				start1 = start2;
				end1 = end2;
			}
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

	//
	// /**
	// * Greatest common divisor using Euclid's algorithm
	// *
	// * @param n
	// * number > 0
	// * @param m
	// * number > 0
	// * @return gcd
	// */
	// private static int gcd(int n, int m) {
	// int gcd = n;
	// for (int div = m; div != 0;) {
	// gcd %= div;
	// // swap gcd and div
	// div ^= gcd;
	// gcd ^= div;
	// div ^= gcd;
	// }
	// return gcd;
	// }
	//
	// /**
	// * Rotate an array over two disjoint sections. Created by Hannu Helminem.
	// *
	// * @param data
	// * input array
	// * @param start1
	// * start of first interval
	// * @param end1
	// * end of first interval
	// * @param start2
	// * start of second interval
	// * @param end2
	// * end of second interval
	// * @param k
	// * amount of rotation
	// */
	// private static void disjointRotate(int[] data, int start1, int end1,
	// int start2, int end2, int k) {
	// int size1 = end1 - start1;
	// int size2 = end2 - start2;
	// int total = size1 + size2;
	// int gcd = gcd(total, size2);
	// int skip = total / gcd - 1;
	// for (int i = 0; i < gcd; i++) {
	// int curr = i < size1 ? start1 + i : start2 + (i - size1);
	// int ctr = i;
	// int v = curr;
	// for (int j = 0; j < skip; j++) {
	// ctr = (ctr + size1) % total;
	// int next = ctr < size1 ? start1 + ctr : start2 + (ctr - size1);
	// swap(data, curr, next);
	// curr = next;
	// }
	// swap(data, v, curr);
	// }
	// }
	//
	//
	//
	// /**
	// * Rotate an array over two disjoint sections. Created by Hannu Helminem.
	// *
	// * @param data
	// * input array
	// * @param start1
	// * start of first interval
	// * @param end1
	// * end of first interval
	// * @param start2
	// * start of second interval
	// * @param end2
	// * end of second interval
	// * @param k
	// * amount of rotation
	// */
	// private static <T> void disjointRotate(T[] data, int start1, int end1,
	// int start2, int end2) {
	// int size1 = end1 - start1;
	// int size2 = end2 - start2;
	// int total = size1 + size2;
	// int gcd = gcd(total, size2);
	// int skip = total / gcd - 1;
	// for (int i = 0; i < gcd; i++) {
	// int curr = i < size1 ? start1 + i : start2 + (i - size1);
	// int ctr = i;
	// int v = curr;
	// for (int j = 0; j < skip; j++) {
	// ctr = (ctr + size1) % total;
	// int next = ctr < size1 ? start1 + ctr : start2 + (ctr - size1);
	// swap(data, curr, next);
	// curr = next;
	// }
	// swap(data, v, curr);
	// }
	// }

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
	public static Iterable<int[]> combinations(int[] array, int k,
			boolean inPlace) {
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
							swap(c, s);
							while (--d > ++c)
								swap(c, d);
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
}
