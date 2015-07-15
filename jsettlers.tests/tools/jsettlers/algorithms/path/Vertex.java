package jsettlers.algorithms.path;

import jsettlers.common.position.ShortPoint2D;

public class Vertex extends ShortPoint2D {
	private static final long serialVersionUID = -2605651034142074418L;
	private Vertex buddy;

	public Vertex(int x, int y) {
		super(x, y);
	}

	public void setBuddy(Vertex v) {
		this.buddy = v;
		v.buddy = this;
	}
}
