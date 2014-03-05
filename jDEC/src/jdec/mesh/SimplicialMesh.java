package jdec.mesh;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Set;

import jdec.math.Combinatorial;
import jdec.math.Volume;

public class SimplicialMesh {

	private final double[][] vertices;
	private final int[][] elements;

	private final int manifoldDimension;
	private final int embeddingDimension;

	private final Object2IntMap<Simplex> simplexToIndex;
	private final Set<Simplex> faces;
	private final Object2ObjectMap<Simplex, IntSet> faceToSimplex;
	private final IntSet[] simplexNeigbors;

	public SimplicialMesh(double[][] points, int[][] elements) {
		this.vertices = points;
		this.embeddingDimension = points[0].length;
		this.elements = elements;
		// TODO see if defensive copy is needed
		int min = Integer.MAX_VALUE;
		int max = 0;
		manifoldDimension = elements[0].length;
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
		if (min < 0 || max > points.length)
			throw new IllegalArgumentException("Invalid index in elements");

		simplexToIndex = new Object2IntOpenHashMap<Simplex>();
		for (int i = 0; i < this.elements.length; i++)
			simplexToIndex.put(new Simplex(elements[i]), i);

		this.faces = skeleton(manifoldDimension() - 1);

		faceToSimplex = new Object2ObjectOpenHashMap<Simplex, IntSet>();
		for (Entry<Simplex> simplexAndIndex : simplexToIndex
				.object2IntEntrySet()) {
			for (Simplex b : simplexAndIndex.getKey().boundary()) {
				IntSet indices;
				if ((indices = faceToSimplex.get(b)) == null) {
					indices = new IntOpenHashSet();
					faceToSimplex.put(b, indices);
				}
				indices.add(simplexAndIndex.getValue());
			}
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

	public double[][] getPoints() {
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
			double[][] points = new double[manifoldDimension + 1][];
			for (int[] element : elements) {
				int c = 0;
				for (int point : element)
					points[c++] = this.vertices[point];
				if (Volume.signedVolume(points) < 0) {
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
