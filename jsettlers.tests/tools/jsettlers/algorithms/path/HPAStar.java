package jsettlers.algorithms.path;

import java.util.HashMap;
import java.util.Map.Entry;

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
		// for (int x = 0; x < width; x++) {
		// grid.setDebugColor(x, y, Color.WHITE);
		// grid.setDebugColor(x, y - 1, Color.WHITE);
		// }
		// }

		HashMap<ShortPoint2D, Vertex> vertices = new HashMap<>();

		xCalculateTransitions(cellSize, vertices);
		yCalculateTransitions(cellSize, vertices);

		// grid.clearDebugColors();
		// printCell(vertices, cellSize, 2, 3);

		System.out.println("number of vertices: " + vertices.size());
	}

	private void printCell(HashMap<ShortPoint2D, Vertex> vertices, int cellSize, int cellX, int cellY) {
		for (Entry<ShortPoint2D, Vertex> vertexEntry : vertices.entrySet()) {
			Vertex vertex = vertexEntry.getValue();
			if (vertex.x / cellSize == cellX && vertex.y / cellSize == cellY) {
				grid.setDebugColor(vertex.x, vertex.y, Color.ORANGE);
				for (Vertex neighbor : vertex.getNeighbors()) {
					grid.setDebugColor(neighbor.x, neighbor.y, Color.CYAN);
				}
			}
		}
	}

	// xCalculateTransitions ========================================================================================

	private void xCalculateTransitions(int cellSize, HashMap<ShortPoint2D, Vertex> vertices) {
		for (int y = cellSize; y < height; y += cellSize) {
			int yLow = y - 1;

			for (int x = 0, cellMax = cellSize; x < width; cellMax = Math.min(width, cellMax + cellSize)) {
				int startX = -1;

				for (; x < cellMax; x++) { // work in cell
					if (!grid.isBlocked(null, x, y) && !grid.isBlocked(null, x, yLow)) {
						if (startX < 0) {
							startX = x;
							xSetColors(x, y, Color.BLUE);
						} else {
							xSetColors(x, y, Color.LIGHT_GREEN);
						}
					} else {
						xCheckEntranceEnd(vertices, x, y, startX);
						startX = -1;
					}
				}
				xCheckEntranceEnd(vertices, x, y, startX);
			}
		}
	}

	private void xCheckEntranceEnd(HashMap<ShortPoint2D, Vertex> vertices, int x, int y, int entranceStartX) {
		if (entranceStartX >= 0) {
			int lastX = x - 1;
			xSetColors(lastX, y, Color.BLUE);

			int distance = x - entranceStartX;
			if (distance >= 6) {
				xAddVertices(vertices, entranceStartX, y);
				xAddVertices(vertices, lastX, y);
			} else {
				xAddVertices(vertices, entranceStartX + distance / 2, y);
			}
		}
	}

	private void xAddVertices(HashMap<ShortPoint2D, Vertex> vertices, int x, int y) {
		addVertices(vertices, x, y, x, y - 1);
	}

	private void xSetColors(int x, int y, Color color) {
		grid.setDebugColor(x, y, color);
		grid.setDebugColor(x, y - 1, color);
	}

	// yCalculateTransitions ========================================================================================

	private void yCalculateTransitions(int cellSize, HashMap<ShortPoint2D, Vertex> vertices) {
		for (int x = cellSize; x < width; x += cellSize) {
			int xLow = x - 1;

			for (int y = 0, cellMax = cellSize; y < height; cellMax = Math.min(height, cellMax + cellSize)) {
				int startY = -1;

				for (; y < cellMax; y++) { // work in cell
					if (!grid.isBlocked(null, x, y) && !grid.isBlocked(null, xLow, y)) {
						if (startY < 0) {
							startY = y;
							ySetColors(x, y, Color.BLUE);
						} else {
							ySetColors(x, y, Color.LIGHT_GREEN);
						}
					} else {
						yCheckEntranceEnd(vertices, x, y, startY);
						startY = -1;
					}
				}
				yCheckEntranceEnd(vertices, x, y, startY);
			}
		}
	}

	private void yCheckEntranceEnd(HashMap<ShortPoint2D, Vertex> vertices, int x, int y, int entranceStartY) {
		if (entranceStartY >= 0) {
			int lastY = y - 1;
			ySetColors(x, lastY, Color.BLUE);

			int distance = y - entranceStartY;
			if (distance >= 6) {
				yAddVertices(vertices, x, entranceStartY);
				yAddVertices(vertices, x, lastY);
			} else {
				yAddVertices(vertices, x, entranceStartY + distance / 2);
			}
		}
	}

	private void yAddVertices(HashMap<ShortPoint2D, Vertex> vertices, int x, int y) {
		addVertices(vertices, x, y, x - 1, y);
	}

	private void ySetColors(int x, int y, Color color) {
		grid.setDebugColor(x, y, color);
		grid.setDebugColor(x - 1, y, color);
	}

	private void addVertices(HashMap<ShortPoint2D, Vertex> vertices, int x1, int y1, int x2, int y2) {
		Vertex v1 = getVertex(vertices, x1, y1);
		Vertex v2 = getVertex(vertices, x2, y2);
		v1.addEdge(v2);

		setColor(v1, Color.RED);
		setColor(v2, Color.RED);
	}

	private Vertex getVertex(HashMap<ShortPoint2D, Vertex> vertices, int x, int y) {
		Vertex v = new Vertex(x, y);
		Vertex existingV = vertices.get(v);
		if (existingV != null) {
			return existingV;
		} else {
			vertices.put(v, v);
			return v;
		}
	}

	private void setColor(ShortPoint2D pos, Color color) {
		grid.setDebugColor(pos.x, pos.y, color);
	}

}
