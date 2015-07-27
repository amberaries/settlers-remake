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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import jsettlers.algorithms.path.dijkstra.BucketQueue1ToNDijkstra;
import jsettlers.algorithms.path.hpastar.graph.HPAStarAbstractedGrid;
import jsettlers.algorithms.path.hpastar.graph.Vertex;
import jsettlers.algorithms.path.hpastar.graph.generation.TransitionsCalculator.TransitionCalculatorFactory;
import jsettlers.common.Color;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;

public class HPAStarAbstractedGridCalculator {

	private final HPAStarFactoryGrid grid;
	private final short width;
	private final short height;
	private final TransitionsCalculator transitionsCalculator;

	public HPAStarAbstractedGridCalculator(HPAStarFactoryGrid grid, short width, short height, TransitionCalculatorFactory calculatorFactory) {
		this.grid = grid;
		this.width = width;
		this.height = height;
		this.transitionsCalculator = calculatorFactory.createCalculator(grid, width, height);
	}

	public HPAStarAbstractedGrid calculateAbstractedGrid(int cellSize) {
		HashMap<ShortPoint2D, Transition> transitions = transitionsCalculator.calculateTransitions(cellSize);

		// printCell(transitions, cellSize, 2, 3);

		System.out.println("number of transitions: " + transitions.size());

		HashMap<ShortPoint2D, List<Transition>> cells = calculateCells(transitions, cellSize);

		MilliStopWatch watch = new MilliStopWatch();
		HPAStarAbstractedGrid abstractedGrid = calculateAbstractedGrid(cellSize, cells);
		watch.stop("costs calculation took");

		return abstractedGrid;
	}

	private HPAStarAbstractedGrid calculateAbstractedGrid(int cellSize, HashMap<ShortPoint2D, List<Transition>> cells) {
		BucketQueue1ToNDijkstra dijkstra = new BucketQueue1ToNDijkstra(grid, width, height);

		HashMap<ShortPoint2D, List<Vertex>> vertexGrid = new HashMap<>();
		HashMap<ShortPoint2D, Vertex> vertices = new HashMap<>();

		for (Entry<ShortPoint2D, List<Transition>> cell : cells.entrySet()) {
			List<Vertex> vertexList = new ArrayList<Vertex>();
			vertexGrid.put(cell.getKey(), vertexList);

			ShortPoint2D minCorner = cell.getKey().multiply(cellSize);
			ShortPoint2D maxCorner = minCorner.add(cellSize - 1);

			List<Transition> cellTransitions = cell.getValue();
			for (Transition transition : cellTransitions) {
				grid.clearDebugColors();

				Tuple<Integer, float[]> dijkstraResult = dijkstra.calculateCosts(minCorner, maxCorner, transition, cellTransitions);

				float[] costs = new float[dijkstraResult.e1 + transition.getNeighbors().size() - 1]; // -1 as own position can be excluded
				Vertex[] neighbors = new Vertex[costs.length];

				int idx = 0;
				float[] dijkstraCosts = dijkstraResult.e2;

				for (int i = 0; i < dijkstraCosts.length; i++) {
					if (dijkstraCosts[i] > 0) {
						costs[idx] = dijkstraCosts[i];
						neighbors[idx] = cellTransitions.get(i).getVertex();
						idx++;
					}
				}

				for (Transition neighbor : transition.getNeighbors()) {
					costs[idx] = grid.getCost(transition.x, transition.y, neighbor.x, neighbor.y);
					neighbors[idx] = neighbor.getVertex();
					idx++;
				}

				Vertex vertex = transition.getVertex();
				vertex.setNeighbors(neighbors, costs);
				vertexList.add(vertex);
				vertices.put(vertex, vertex);
			}
		}
		return new HPAStarAbstractedGrid(vertexGrid, vertices, cellSize);
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

}
