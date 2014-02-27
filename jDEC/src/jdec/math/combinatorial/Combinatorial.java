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

					private void swap(T[] data, int i, int j) {
						T t = data[i];
						data[i] = data[j];
						data[j] = t;
					}

					public boolean hasNext() {
						return hasNext;
					}

					public T[] next() {
						if (!hasNext)
							throw new NoSuchElementException();
						if (!(c < 0)) {
							// do the swaps: place s in currently first
							// position,
							// and reverse the part of the list to the right
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

					private void swap(int[] data, int i, int j) {
						data[i] ^= data[j];
						data[j] ^= data[i];
						data[i] ^= data[j];
					}

					public boolean hasNext() {
						return hasNext;
					}

					public int[] next() {
						if (!hasNext)
							throw new NoSuchElementException();
						if (!(c < 0)) {
							// do the swaps: place s in currently first
							// position,
							// and reverse the part of the list to the right
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
			;
		return permutations(indices, true);
	}

}
