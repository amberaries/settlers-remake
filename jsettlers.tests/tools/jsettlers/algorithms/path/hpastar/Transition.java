package jsettlers.algorithms.path.hpastar;

import java.util.ArrayList;
import java.util.List;

import jsettlers.common.position.ShortPoint2D;

public class Transition extends ShortPoint2D {
	private static final long serialVersionUID = -2605651034142074418L;
	private final List<Transition> neighbors;

	public Transition(int x, int y) {
		super(x, y);
		neighbors = new ArrayList<Transition>();
	}

	public void addEdge(Transition v) {
		this.neighbors.add(v);
		v.neighbors.add(this);
	}

	public List<Transition> getNeighbors() {
		return neighbors;
	}
}
