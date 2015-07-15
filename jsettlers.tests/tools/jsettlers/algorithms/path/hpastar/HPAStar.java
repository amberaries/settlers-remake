package jsettlers.algorithms.path.hpastar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

		HashMap<ShortPoint2D, Transition> transitions = new HashMap<>();

		xCalculateTransitions(cellSize, transitions);
		yCalculateTransitions(cellSize, transitions);

		// printCell(transitions, cellSize, 2, 3);

		System.out.println("number of transitions: " + transitions.size());

		HashMap<ShortPoint2D, List<Transition>> cells = calculateCells(transitions, cellSize);
	}

	private HashMap<ShortPoint2D, List<Transition>> calculateCells(HashMap<ShortPoint2D, Transition> transitions, int cellSize) {
		HashMap<ShortPoint2D, List<Transition>> cells = new HashMap<>();

		for (Entry<ShortPoint2D, Transition> transition : transitions.entrySet()) {
			int cellX = transition.getKey().x / cellSize;
			int cellY = transition.getKey().y / cellSize;

			ShortPoint2D cellPosition = new ShortPoint2D(cellX, cellY);
			List<Transition> cellTransitions = cells.get(cellPosition);
			if (cellTransitions == null) {
				cellTransitions = new ArrayList<Transition>();
				cells.put(cellPosition, cellTransitions);
			}
			cellTransitions.add(transition.getValue());
		}

		return cells;
	}

	private void printCell(HashMap<ShortPoint2D, Transition> transitions, int cellSize, int cellX, int cellY) {
		grid.clearDebugColors();

		for (Entry<ShortPoint2D, Transition> vertexEntry : transitions.entrySet()) {
			Transition vertex = vertexEntry.getValue();
			if (vertex.x / cellSize == cellX && vertex.y / cellSize == cellY) {
				grid.setDebugColor(vertex.x, vertex.y, Color.ORANGE);
				for (Transition neighbor : vertex.getNeighbors()) {
					grid.setDebugColor(neighbor.x, neighbor.y, Color.CYAN);
				}
			}
		}
	}

	// xCalculateTransitions ========================================================================================

	private void xCalculateTransitions(int cellSize, HashMap<ShortPoint2D, Transition> transitions) {
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
						xCheckEntranceEnd(transitions, x, y, startX);
						startX = -1;
					}
				}
				xCheckEntranceEnd(transitions, x, y, startX);
			}
		}
	}

	private void xCheckEntranceEnd(HashMap<ShortPoint2D, Transition> transitions, int x, int y, int entranceStartX) {
		if (entranceStartX >= 0) {
			int lastX = x - 1;
			xSetColors(lastX, y, Color.BLUE);

			int distance = x - entranceStartX;
			if (distance >= 6) {
				xAddTransition(transitions, entranceStartX, y);
				xAddTransition(transitions, lastX, y);
			} else {
				xAddTransition(transitions, entranceStartX + distance / 2, y);
			}
		}
	}

	private void xAddTransition(HashMap<ShortPoint2D, Transition> transitions, int x, int y) {
		addtransitions(transitions, x, y, x, y - 1);
	}

	private void xSetColors(int x, int y, Color color) {
		grid.setDebugColor(x, y, color);
		grid.setDebugColor(x, y - 1, color);
	}

	// yCalculateTransitions ========================================================================================

	private void yCalculateTransitions(int cellSize, HashMap<ShortPoint2D, Transition> transitions) {
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
						yCheckEntranceEnd(transitions, x, y, startY);
						startY = -1;
					}
				}
				yCheckEntranceEnd(transitions, x, y, startY);
			}
		}
	}

	private void yCheckEntranceEnd(HashMap<ShortPoint2D, Transition> transitions, int x, int y, int entranceStartY) {
		if (entranceStartY >= 0) {
			int lastY = y - 1;
			ySetColors(x, lastY, Color.BLUE);

			int distance = y - entranceStartY;
			if (distance >= 6) {
				yAddtransitions(transitions, x, entranceStartY);
				yAddtransitions(transitions, x, lastY);
			} else {
				yAddtransitions(transitions, x, entranceStartY + distance / 2);
			}
		}
	}

	private void yAddtransitions(HashMap<ShortPoint2D, Transition> transitions, int x, int y) {
		addtransitions(transitions, x, y, x - 1, y);
	}

	private void ySetColors(int x, int y, Color color) {
		grid.setDebugColor(x, y, color);
		grid.setDebugColor(x - 1, y, color);
	}

	private void addtransitions(HashMap<ShortPoint2D, Transition> transitions, int x1, int y1, int x2, int y2) {
		Transition v1 = getTransition(transitions, x1, y1);
		Transition v2 = getTransition(transitions, x2, y2);
		v1.addEdge(v2);

		setColor(v1, Color.RED);
		setColor(v2, Color.RED);
	}

	private Transition getTransition(HashMap<ShortPoint2D, Transition> transitions, int x, int y) {
		Transition v = new Transition(x, y);
		Transition existingV = transitions.get(v);
		if (existingV != null) {
			return existingV;
		} else {
			transitions.put(v, v);
			return v;
		}
	}

	private void setColor(ShortPoint2D pos, Color color) {
		grid.setDebugColor(pos.x, pos.y, color);
	}
}
