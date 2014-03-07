package jdec.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Reversed<T> {
	private Reversed() {
	}

	public Iterable<T> reversedList(final List<T> list) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					ListIterator<T> listIterator = list.listIterator(list
							.size());

					public boolean hasNext() {
						return listIterator.hasPrevious();
					}

					public T next() {
						return listIterator.previous();
					}

					public void remove() {
						listIterator.remove();

					}
				};
			}

		};

	}
}
