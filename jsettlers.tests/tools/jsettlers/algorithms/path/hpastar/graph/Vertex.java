package jsettlers.algorithms.path.hpastar.graph;

import jsettlers.common.position.ShortPoint2D;

public class Vertex extends ShortPoint2D {
	private static final long serialVersionUID = -2990316033605974231L;
	private Vertex[] neighbors;
	private float[] costs;

	public Vertex(int x, int y) {
		super(x, y);
	}

	public void setNeighbors(Vertex[] neighbors, float[] costs) {
		assert neighbors.length == costs.length;

		this.neighbors = neighbors;
		this.costs = costs;
	}
}
