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

public class FullTransitionsCalculator extends TransitionsCalculator {
	public static class FullTransitionsCalculatorFactory extends TransitionCalculatorFactory {
		@Override
		public TransitionsCalculator createCalculator(HPAStarFactoryGrid grid, short width, short height) {
			return new FullTransitionsCalculator(grid, width, height);
		}
	}

	private final HPAStarFactoryGrid grid;
	private final short width;
	private final short height;

	public FullTransitionsCalculator(HPAStarFactoryGrid grid, short width, short height) {
		this.grid = grid;
		this.width = width;
		this.height = height;
	}

	@Override
	public HashMap<ShortPoint2D, Transition> calculateTransitions(int cellSize) {
		HashMap<ShortPoint2D, Transition> transitions = new HashMap<>();

		// calculate x transitions
		for (int y = cellSize; y < height; y += cellSize) {
			int yLow = y - 1;

			for (int x = 0; x < width; x++) {
				if (x > 0) {
					addEdgeIfPossible(transitions, x, y, x - 1, yLow);
				}
				addEdgeIfPossible(transitions, x, y, x, yLow);
			}
		}

		for (int x = cellSize; x < width; x += cellSize) {
			int xLow = x - 1;

			for (int y = 0; y < height; y++) {
				if (y > 0) {
					addEdgeIfPossible(transitions, x, y, xLow, y - 1);
				}
				addEdgeIfPossible(transitions, x, y, xLow, y);
			}
		}

		return transitions;
	}

	private void addEdgeIfPossible(HashMap<ShortPoint2D, Transition> transitions, int x1, int y1, int x2, int y2) {
		if (!grid.isBlocked(x1, y1) && !grid.isBlocked(x2, y2) && grid.getCost(x1, y1, x2, y2) < Float.MAX_VALUE) {
			Transition transition1 = getTransition(transitions, x1, y1);
			Transition transition2 = getTransition(transitions, x2, y2);
			transition1.addEdge(transition2);
			grid.setDebugColor(x1, y1, Color.RED);
			grid.setDebugColor(x2, y2, Color.RED);
		}
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
}
