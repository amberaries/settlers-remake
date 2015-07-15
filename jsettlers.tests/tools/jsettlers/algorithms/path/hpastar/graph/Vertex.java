package jsettlers.algorithms.path.hpastar.graph;

import jsettlers.common.position.ShortPoint2D;

public class Vertex extends ShortPoint2D {
	private static final long serialVersionUID = -2990316033605974231L;
	private Vertex[] neighbors;
	private int[] distances;

	public Vertex(short x, short y) {
		super(x, y);
	}

	public void setNeighbors(Vertex[] neighbors, int[] distances) {
		assert neighbors.length == distances.length;

		this.neighbors = neighbors;
		this.distances = distances;
	}

}
