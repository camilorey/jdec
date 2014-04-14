package jdec.dec;

import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;

import java.util.Arrays;
import java.util.Set;

import jdec.dec.SimplexArray.BoundaryOperator;
import jdec.linalg.CSRMatrix;
import jdec.math.Circumcenter;
import jdec.math.LexicographicalComparator;
import jdec.math.Parity;
import jdec.math.Volume;
import jdec.mesh.Simplex;
import jdec.mesh.SimplicialMesh;
import no.uib.cipr.matrix.Matrix;

public class SimplicialComplex {

	private final SimplicialMesh mesh;

	private final double[][] vertices;
	private int[][][] simplices;
	private Matrix[] chainComplex;
	private Matrix[] cochainComplex;

	private int[][] parities;

	private Subspace[] subspaces;

	class Subspace {
		private int dimension;
		private Matrix boundary;
		private Matrix d;
		private Matrix star;
		private Matrix starInv;
		private int[][] simplices;
		private int[] simplexParity;
		private int nSimplices;
		private SimplicialComplex complex;
		private double[] primalVolume;
		private double[] dualVolume;
		private double[][] circumcenter;

		private Simplex[] indexToSimplex;
		private Object2IntMap<Simplex> simplexToIndex;

		public Simplex simplex(int i) {
			if (indexToSimplex == null)
				buildSimplices();
			return indexToSimplex[i];
		}

		private void buildSimplices() {
			indexToSimplex = new Simplex[simplices.length];
			simplexToIndex = new Object2IntAVLTreeMap<Simplex>();
			for (int i = 0; i < nSimplices; i++) {
				Simplex simplex = new Simplex(simplices[i], simplexParity[i]);
				indexToSimplex[i] = simplex;
				simplexToIndex.put(simplex, i);
			}

		}

		public int simplexToIndex(Simplex s) {
			if (simplexToIndex == null)
				buildSimplices();
			return simplexToIndex.get(s);
		}

		public Matrix d() {
			return d;
		}

		public Matrix boundary() {
			return boundary;
		}

		public double[] circumcenter(int index) {
			if (circumcenter == null)
				computeCircumcenters();
			return circumcenter[index];
		}

		public double primalVolume(int index) {
			if (primalVolume == null)
				computePrimalVolume();
			return primalVolume[index];
		}

		public double dualVolume(int index) {
			if (dualVolume == null)
				computeDualVolumes();
			return dualVolume[index];
		}

		public Matrix star() {
			if (star == null)
				constructHodge();
			return star;
		}

		public Matrix starInv() {
			if (starInv == null)
				constructHodge();
			return starInv;
		}

		public void resetGeometry() {
			this.circumcenter = null;
			this.primalVolume = null;
			this.dualVolume = null;
			this.star = null;
			this.starInv = null;
		}

		private void computeCircumcenters() {
			if (circumcenter == null)
				circumcenter = new double[nSimplices][embeddingDimension()];
			for (int i = 0; i < nSimplices; i++)
				circumcenter[i] = Circumcenter.circumcenter(
						SimplicialComplex.this.vertices, simplices[i]);
		}

		private void computePrimalVolume() {
			if (primalVolume == null)
				primalVolume = new double[nSimplices];
			if (dimension == embeddingDimension())
				for (int i = 0; i < nSimplices; i++)
					primalVolume[i] = Volume.signedVolume(
							SimplicialComplex.this.vertices, simplices[i]);
			else
				for (int i = 0; i < nSimplices; i++)
					primalVolume[i] = Volume.unsignedVolume(
							SimplicialComplex.this.vertices, simplices[i]);

		}

		private void computeDualVolumes() {
			for (Subspace sp : subspaces)
				sp.dualVolume = new double[sp.nSimplices];
			double[][] centers = new double[complexDimension() + 1][];
			for (int[] s : simplices)
				computeDualVolume(new Simplex(s), centers, complexDimension());
		}

		/**
		 * Compute all the dual volume of this simplex and also add all the
		 * contributions to the dual volumes of sub-elements that are related to
		 * this one by a bounding relationship
		 * 
		 * @param s
		 * @param centers
		 * @param dimension
		 */
		private void computeDualVolume(Simplex s, double[][] centers,
				int dimension) {
			Subspace sp = subspaces[dimension];
			int index = sp.simplexToIndex(s);
			centers[dimension] = sp.circumcenter(index);
			sp.dualVolume[index] += Volume.unsignedVolume(centers, dimension,
					centers.length);
			if (dimension > 0) {
				for (Simplex bs : s.boundary())
					computeDualVolume(bs, centers, dimension - 1);
			}
		}

		private void constructHodge() {
			double[] dataStar = new double[nSimplices];
			double[] dataStarInv = new double[nSimplices];
			int[] rowptr = new int[nSimplices + 1];
			int[] cols = new int[nSimplices];
			double sign = 1;
			if (dimension * (complexDimension() - dimension) % 2 != 0)
				sign = -1;
			for (int i = 0; i < nSimplices; i++) {
				dataStar[i] = dualVolume(i) / primalVolume(i);
				dataStarInv[i] = sign / dataStar[i];
				rowptr[i] = i;
				cols[i] = i;
			}
			rowptr[nSimplices] = nSimplices;
			star = new CSRMatrix(nSimplices, nSimplices, dataStar, cols, rowptr);
			starInv = new CSRMatrix(nSimplices, nSimplices, dataStarInv, cols,
					rowptr);
		}
	}

	public SimplicialComplex(SimplicialMesh mesh) {
		this.mesh = mesh;
		this.vertices = mesh.getVertices();
		buildComplex(mesh.getElements());
	}

	public SimplicialComplex(double[][] points, int[][] elements) {
		this.mesh = new SimplicialMesh(points, elements);
		this.vertices = mesh.getVertices();
		buildComplex(mesh.getElements());
	}

	public int numberOfNSimplices(int n) {
		return subspaces[n].simplices.length;
	}

	Subspace getSubspace(int n) {
		return subspaces[n];
	}

	@Override
	public String toString() {
		String output = "simplicial complex\n  complex:\n";
		for (int i = this.simplices.length - 1; i >= 0; i--) {
			output += "   " + this.simplices[i].length + ": " + i
					+ "d-simplices\n";
		}
		return output;
	}

	private void buildComplex(int[][] simplexArray) {
		int n = simplexArray.length;
		int k = simplexArray[0].length;
		int[][] s = new int[n][k];
		for (int i = 0; i < n; i++)
			System.arraycopy(simplexArray[i], 0, s[i], 0, k);
		int[] parity = SimplexArray.simplexParity(s);
		Arrays.sort(s, LexicographicalComparator.getComparator(k));
		this.simplices = new int[n][][];
		this.simplices[n - 1] = s;
		this.chainComplex = new Matrix[n];
		this.parities = new int[n][];
		this.parities[n - 1] = parity;
		int l;
		while ((l = s[0].length - 1) >= 0) {
			BoundaryOperator boundary = SimplexArray.simplexArrayBoundary(s,
					parity);
			s = boundary.uniqueFaces;
			parity = new int[s.length];
			simplices[l] = s;
			chainComplex[l] = boundary.operator;
			parities[l] = parity;
		}
		// zeroth boundary operator (= null operator)
		chainComplex[0] = new CSRMatrix(1, s.length);

		// build cochain complex (i.e., exterior derivative operators)
		cochainComplex = new Matrix[n];
		for (int i = 0; i < n - 1; i++)
			cochainComplex[i] = chainComplex[i + 1].transpose();
		// TODO: maybe use symbolic transpose ??

		// nth exterior derivative operator (= null operator)
		cochainComplex[n - 1] = cochainComplex[0];

		subspaces = new Subspace[n];

		for (int i = 0; i < n; i++) {
			subspaces[i] = new Subspace();
			subspaces[i].dimension = i;
			subspaces[i].d = cochainComplex[i];
			subspaces[i].boundary = chainComplex[i];
			subspaces[i].complex = this;
			subspaces[i].simplices = simplices[i];
			subspaces[i].nSimplices = subspaces[i].simplices.length;
			subspaces[i].simplexParity = parities[i];
		}

	}

	public int complexDimension() {
		return simplices.length - 1;
	}

	public int embeddingDimension() {
		return mesh.embeddingDimension();
	}

	public chainComplex() {
	for(Subspace sp : subspaces)
		sp.boundary
	}

	public cochainComplex() {

	}

	public complex() {

	}

	public Cochain getCochain(int dimension, boolean isPrimal) {
		if (dimension < 0 || dimension > complexDimension())
			throw new IllegalArgumentException("Invalid dimension " + dimension);
			new Cochain()
	}

	public Set<Simplex> boundary() {
		Object2IntMap<Simplex> faceCount = new Object2IntAVLTreeMap<Simplex>();
		for (Simplex s : subspaces[complexDimension()].simplexToIndex.keySet())
			for (Simplex f : s.boundary())
				if (!faceCount.containsKey(f))
					faceCount.put(f, 1);
				else
					faceCount.put(f, faceCount.getInt(f) + 1);
		Set<Simplex> boundary = new ObjectAVLTreeSet<Simplex>();
		for (Entry<Simplex> e : faceCount.object2IntEntrySet())
			if (e.getIntValue() == 1)
				boundary.add(e.getKey());
		return boundary;

	}

}
