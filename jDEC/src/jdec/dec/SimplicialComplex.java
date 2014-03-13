package jdec.dec;

import java.util.Arrays;

import jdec.math.LexicographicalComparator;
import jdec.mesh.SimplicialMesh;

import org.apache.commons.lang3.ArrayUtils;

public class SimplicialComplex {

	private final SimplicialMesh mesh;

	private final double[][] vertices;
	private int[][][] simplices;

	private int[][] parities;

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
		this.simplices = new int[s[0].length - 1][][];
		this.simplices[0] = s;
		// this.chainComplex =
		this.parities = new int[s[0].length][];
		this.parities[0] = parity;
		while (s[0].length > 1) {
			s = SimplexArray.simplexArrayBoundary(s, parity, boundaryOperator);
			parity = new int[s.length];
			simplices[s[0].length] = (s);
			chainComplex.append(bundary);
			parities = ArrayUtils.reverse(parity);
		}

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
