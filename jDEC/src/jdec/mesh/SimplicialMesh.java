package jdec.mesh;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Collection;
import java.util.Set;

import jdec.math.Combinatorial;
import jdec.math.Volume;

public class SimplicialMesh {

	private final double[][] vertices;
	private final int[][] elements;

	private final int manifoldDimension;
	private final int embeddingDimension;

	private Object2IntMap<Simplex> simplexToIndex;
	private Set<Simplex> faces;
	private Object2ObjectMap<Simplex, IntSet> faceToSimplex;
	private IntSet[] simplexNeigbors;

	public SimplicialMesh(double[][] points, int[][] elements) {
		this.vertices = points;
		this.embeddingDimension = points[0].length;
		this.manifoldDimension = elements[0].length;
		this.elements = elements;
		// TODO see if defensive copy is needed
		build();
	}

	private void build() {
		int min = Integer.MAX_VALUE;
		int max = 0;
		for (int[] element : elements) {
			if (element.length != manifoldDimension)
				throw new IllegalArgumentException("Invalid element");
			for (int index : element) {
				if (min > index)
					min = index;
				if (max < index)
					max = index;
			}
		}
		if (min < 0 || max > vertices.length)
			throw new IllegalArgumentException("Invalid index in elements");

		simplexToIndex = new Object2IntOpenHashMap<Simplex>();
		for (int i = 0; i < this.elements.length; i++)
			simplexToIndex.put(new Simplex(elements[i]), i);

		this.faces = skeleton(manifoldDimension() - 1);

		faceToSimplex = new Object2ObjectOpenHashMap<Simplex, IntSet>();
		for (Simplex face : faces)
			faceToSimplex.put(face, new IntOpenHashSet());
		for (Entry<Simplex> simplexAndIndex : simplexToIndex
				.object2IntEntrySet()) {
			for (Simplex b : simplexAndIndex.getKey().boundary())
				faceToSimplex.get(b).add(simplexAndIndex.getIntValue());

		}
		simplexNeigbors = new IntSet[elements.length];
		for (Entry<Simplex> simplexAndIndex : simplexToIndex
				.object2IntEntrySet()) {
			for (Simplex b : simplexAndIndex.getKey().boundary()) {
				IntSet indices;
				int simplexIndex = simplexAndIndex.getIntValue();
				if ((indices = simplexNeigbors[simplexIndex]) == null) {
					indices = new IntOpenHashSet();
					simplexNeigbors[simplexIndex] = indices;
				}
				for (int index : faceToSimplex.get(b))
					if (index != simplexIndex)
						indices.add(index);

			}
		}
	}

	public SimplicialMesh(double[][] points, Collection<Simplex> simplices) {
		this.vertices = points;
		this.embeddingDimension = points[0].length;
		this.elements = new int[simplices.size()][];
		this.manifoldDimension = simplices.iterator().next().dimension();
		int c = 0;
		for (Simplex s : simplices) {
			if (manifoldDimension != s.dimension())
				throw new IllegalArgumentException(
						"Cannot build a simplicial mesh with highest-level simplices of different dimension");
			this.elements[c++] = s.toArray();

		}
		build();
	}

	public double[][] getVertices() {
		return vertices;
	}

	public int[][] getElements() {
		return elements;
	}

	@Override
	public String toString() {
		return "SimplicialMesh< " + this.manifoldDimension() + "D manifold, "
				+ this.embeddingDimension() + "D embedding, "
				+ this.vertices.length + " vertices, " + this.elements.length
				+ " elements";
	}

	public int manifoldDimension() {
		return this.manifoldDimension;
	}

	public int embeddingDimension() {
		return this.embeddingDimension;
	}

	/**
	 * Return a list of boundary faces, i.e., faces which belong to a single
	 * elements
	 * 
	 * @return The set of boundary faces
	 */
	public Set<Simplex> boundary() {
		Set<Simplex> boundarySet = new ObjectOpenHashSet<Simplex>();

		for (int[] element : elements) {
			Simplex s = new Simplex(element);
			for (Simplex b : s.boundary())
				if (boundarySet.contains(b)) // it occurs twice -- remove it
					boundarySet.remove(boundarySet);
				else
					boundarySet.add(b); // first occurrence
		}
		return boundarySet;
	}

	/**
	 * Returns the p-skeleton, i.e., the list of all the p-faces in the mesh
	 * 
	 * @param p
	 * @return
	 */
	public Set<Simplex> skeleton(int p) {
		Set<Simplex> skeleton = new ObjectOpenHashSet<Simplex>();
		for (int[] element : elements)
			for (int[] b : Combinatorial.combinations(element, p + 1, false))
				skeleton.add(new Simplex(b)); // the set will remove duplicates

		return skeleton;
	}

	/**
	 * Assure consistent orientation of the mesh. If the dimension of the
	 * manifold and that of the embedding space are the same, then the manifold
	 * will be oriented coherently with the embedding space. An
	 * ArithmeticException is thrown if the manifold iss not orientable.
	 */
	public void orient() {
		if (manifoldDimension == 0)
			return;
		if (manifoldDimension == embeddingDimension) {
			for (int[] element : elements) {
				if (Volume.signedVolume(this.vertices, element) < 0) {
					// change sign to the element by swapping the first two
					// elements
					element[0] ^= element[1];
					element[1] ^= element[0];
					element[0] ^= element[1];
				}

			}

		}
	}
}
