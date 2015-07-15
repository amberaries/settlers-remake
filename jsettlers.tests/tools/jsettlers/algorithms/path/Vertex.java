package jsettlers.algorithms.path;

import java.util.ArrayList;
import java.util.List;

import jsettlers.common.position.ShortPoint2D;

public class Vertex extends ShortPoint2D {
	private static final long serialVersionUID = -2605651034142074418L;
	private final List<Vertex> neighbors;

	public Vertex(int x, int y) {
		super(x, y);
		neighbors = new ArrayList<Vertex>();
	}

	public void addEdge(Vertex v) {
		this.neighbors.add(v);
		v.neighbors.add(this);
	}

	public List<Vertex> getNeighbors() {
		return neighbors;
	}
}
