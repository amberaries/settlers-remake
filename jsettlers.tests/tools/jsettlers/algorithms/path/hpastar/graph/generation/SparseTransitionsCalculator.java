/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.algorithms.path.hpastar.graph.generation;

import java.util.HashMap;

import jsettlers.common.Color;
import jsettlers.common.position.ShortPoint2D;

public class SparseTransitionsCalculator extends TransitionsCalculator {
	public static class SparseTransitionsCalculatorFactory extends TransitionCalculatorFactory {
		@Override
		public TransitionsCalculator createCalculator(HPAStarFactoryGrid grid, short width, short height) {
			return new SparseTransitionsCalculator(grid, width, height);
		}
	}

	private final HPAStarFactoryGrid grid;
	private final short width;
	private final short height;

	public SparseTransitionsCalculator(HPAStarFactoryGrid grid, short width, short height) {
		this.grid = grid;
		this.width = width;
		this.height = height;
	}

	@Override
	public HashMap<ShortPoint2D, Transition> calculateTransitions(int cellSize) {
		HashMap<ShortPoint2D, Transition> transitions = new HashMap<>();

		xCalculateTransitions(cellSize, transitions);
		yCalculateTransitions(cellSize, transitions);
		return transitions;
	}

	// xCalculateTransitions ========================================================================================

	private void xCalculateTransitions(int cellSize, HashMap<ShortPoint2D, Transition> transitions) {
		for (int y = cellSize; y < height; y += cellSize) {
			int yLow = y - 1;

			for (int x = 0, cellMax = cellSize; x < width; cellMax = Math.min(width, cellMax + cellSize)) {
				int startX = -1;

				for (; x < cellMax; x++) { // work in cell
					if (!grid.isBlocked(x, y) && !grid.isBlocked(x, yLow)) {
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
					if (!grid.isBlocked(x, y) && !grid.isBlocked(xLow, y)) {
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
