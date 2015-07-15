package jsettlers.algorithms.path;

import java.util.LinkedList;

import jsettlers.common.Color;
import jsettlers.common.position.ShortPoint2D;

public class HPAStar {

	private final HPAStarGrid grid;
	private final int width;
	private final int height;

	public HPAStar(HPAStarGrid grid) {
		this.grid = grid;
		this.width = grid.getWidth();
		this.height = grid.getHeight();
	}

	public void calculateTransitions(int cellSize) {
		// for (int y = cellSize; y < height; y += cellSize) {
		// int yLow = y - 1;
		//
		// for (int x = 0; x < width;) {
		// int clusterStartX = -1;
		// for (int xEnd = x + cellSize; x < xEnd; x++) {
		// if (!grid.isBlocked(null, x, y) && !grid.isBlocked(null, x, yLow)) {
		// if (clusterStartX < 0) {
		// clusterStartX = x;
		// grid.setDebugColor(x, y, Color.GREEN);
		// grid.setDebugColor(x, yLow, Color.GREEN);
		// } else {
		// grid.setDebugColor(x, y, Color.LIGHT_GREEN);
		// grid.setDebugColor(x, yLow, Color.LIGHT_GREEN);
		// }
		// } else {
		// if (clusterStartX >= 0) {
		// grid.setDebugColor(x - 1, y, Color.GREEN);
		// grid.setDebugColor(x - 1, yLow, Color.GREEN);
		// }
		// clusterStartX = -1;
		// }
		// }
		// if (clusterStartX >= 0) {
		// grid.setDebugColor(x - 1, y, Color.GREEN);
		// grid.setDebugColor(x - 1, yLow, Color.GREEN);
		// }
		// }
		// }

		LinkedList<Vertex> vertices = new LinkedList<>();

		for (int x = cellSize; x < width; x += cellSize) {
			int xLow = x - 1;
			int startY = -1;

			for (int y = 0; y < height; y++) {
				if (!grid.isBlocked(null, x, y) && !grid.isBlocked(null, xLow, y)) {
					if (startY < 0) {
						startY = y;
						setColors(x, y, Color.BLUE);
					} else {
						setColors(x, y, Color.LIGHT_GREEN);
					}
				} else {
					if (startY >= 0) {
						int lastY = y - 1;
						setColors(x, lastY, Color.BLUE);

						int distance = lastY - startY;
						if (distance >= 6) {
							addVertices(vertices, x, startY);
							addVertices(vertices, x, lastY);
						} else {
							addVertices(vertices, x, startY + distance / 2);
						}

						startY = -1;
					}
				}
			}
		}
	}

	private void addVertices(LinkedList<Vertex> vertices, int x, int y) {
		Vertex v1 = new Vertex(x, y);
		Vertex v2 = new Vertex(x - 1, y);
		v1.setBuddy(v2);

		setColor(v1, Color.RED);
		setColor(v2, Color.RED);

		vertices.add(v1);
		vertices.add(v2);
	}

	private void setColor(ShortPoint2D pos, Color color) {
		grid.setDebugColor(pos.x, pos.y, color);
	}

	private void setColors(int x, int y, Color color) {
		grid.setDebugColor(x, y, color);
		grid.setDebugColor(x - 1, y, color);
	}
}
