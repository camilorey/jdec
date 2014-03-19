package jdec.dec;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.Arrays;

import jdec.dec.SimplexArray.BoundaryOperator;
import jdec.linalg.CSRMatrix;
import jdec.math.Circumcenter;
import jdec.math.LexicographicalComparator;
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

	private class Subspace {
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
			simplices[l] = (s);
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

	private void constructHodge() {
		for (int i = 0; i < subspaces.length; i++) {
			int formSize = subspaces[i].nSimplices;
			for(int i=0; i<)
			double diagonalElement
		}
	}

	private void computeCircumcenters(int dimension) {
		Subspace sp = subspaces[dimension];
		if (sp.circumcenter == null)
			sp.circumcenter = new double[sp.nSimplices][embeddingDimension()];
		else
			for (double[] circ : sp.circumcenter)
				Arrays.fill(circ, 0);
		for (int i = 0; i < sp.nSimplices; i++)
			sp.circumcenter[i] = Circumcenter.circumcenter(this.vertices,
					sp.simplices[i]);
	}

	private void computePrimalVolume(int dimension) {
		Subspace sp = subspaces[dimension];
		if (sp.primalVolume == null)
			sp.primalVolume = new double[sp.nSimplices];
		else
			Arrays.fill(sp.primalVolume, 0);
		if (dimension == embeddingDimension())
			for (int i = 0; i < sp.nSimplices; i++)
				sp.primalVolume[i] = Volume.signedVolume(this.vertices,
						sp.simplices[i]);
		else
			for (int i = 0; i < sp.nSimplices; i++)
				sp.primalVolume[i] = Volume.unsignedVolume(this.vertices,
						sp.simplices[i]);

	}

	private void computeDualVolume(int dimension) {
		Subspace sp = subspaces[dimension];
		if (sp.dualVolume == null)
			sp.dualVolume = new double[sp.nSimplices];
		else
			Arrays.fill(sp.dualVolume, 0);
		if (dimension == embeddingDimension())
			for (int i = 0; i < sp.nSimplices; i++)
				sp.dualVolume[i] = Volume.signedVolume(this.vertices,
						sp.simplices[i]);
		else
			for (int i = 0; i < sp.nSimplices; i++)
				sp.primalVolume[i] = Volume.unsignedVolume(this.vertices,
						sp.simplices[i]);

	}

	private double dualVolume(Simplex s, double[][] points, int dimension) {
		Subspace sp = subspaces[dimension];
		int index = sp.simplexToIndex(s);

	}

	public int complexDimension() {
		return simplices[0].length;
	}

	public int embeddingDimension() {
		return mesh.embeddingDimension();
	}

	public chainComplex() {

	}

	public cochainComplex() {

	}

	public complex() {

	}

	public Cochain getCochain() {

	}

}
