package jdec.dec;

import jdec.mesh.SimplicialMesh;

public class SimplicialComplex {

	private final SimplicialMesh mesh;

	private final double[][] vertices;
	private final int[][] simplices;

	public SimplicialComplex(SimplicialMesh mesh) {
		this.mesh = mesh;
		this.vertices = mesh.getVertices();
		this.simplices = mesh.getElements();
		buildComplex(this.simplices);
	}

	public SimplicialComplex(double[][] points, int[][] elements) {
		this.mesh = new SimplicialMesh(points, elements);
		this.vertices = mesh.getVertices();
		this.simplices = mesh.getElements();
		buildComplex(this.simplices);
	}

	@Override
	public String toString() {
		String output = "simplicial complex\n  complex:\n";
		for (int i = this.list.size() - 1; i >= 0; i--) {
			output += "   " + list.get(i).numSimplices() + ": " + i
					+ "d-simplices\n";
		}
		return output;
	}

	private void buildComplex(int[][] simplices) {
		int n = simplexArray.length;
		int k = simplexArray[0].length;
		int[][] s = new int[n][k];
		for (int i = 0; i < n; i++)
			System.arraycopy(simplexArray[i], 0, s[i], 0, k);
		s.sort();
		
		while()

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
