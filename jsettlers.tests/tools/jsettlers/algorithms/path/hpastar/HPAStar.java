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
package jsettlers.algorithms.path.hpastar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jsettlers.algorithms.path.astar.Heuristics;
import jsettlers.algorithms.path.dijkstra.BucketQueue1ToNDijkstra;
import jsettlers.algorithms.path.dijkstra.DijkstraGrid;
import jsettlers.algorithms.path.hpastar.graph.HPAStarAbstractedGrid;
import jsettlers.algorithms.path.hpastar.graph.Vertex;
import jsettlers.common.Color;
import jsettlers.common.position.ShortPoint2D;

public class HPAStar extends BucketQueue1ToNDijkstra {

	private final HPAStarAbstractedGrid abstractedGrid;

	private final int[] depthParentHeap;

	public HPAStar(HPAStarAbstractedGrid abstractedGrid, DijkstraGrid dijkstraGrid, short width, short height) {
		super(dijkstraGrid, width, height);
		this.abstractedGrid = abstractedGrid;
		this.depthParentHeap = new int[width * height * 2];
	}

	public HPAStarPath findPath(short sx, short sy, short tx, short ty) {
		map.clearDebugColors();

		boolean targetIsVertex = abstractedGrid.getVertex(new ShortPoint2D(tx, ty)) != null;

		final ShortPoint2D targetCell;
		final HashMap<ShortPoint2D, Float> vertexToTargetCosts;
		if (!targetIsVertex) {
			targetCell = abstractedGrid.getCell(tx, ty);
			vertexToTargetCosts = calculateTargetCellCosts(tx, ty, targetCell);
		} else {
			targetCell = null;
			vertexToTargetCosts = null;
		}

		super.clearState();

		// init first vertices
		initStartNodes(sx, sy, tx, ty);

		// mark target and star (for debugging)
		map.setDebugColor(sx, sy, Color.CYAN);
		map.setDebugColor(tx, ty, Color.BLUE);

		// run astar
		while (!open.isEmpty()) {
			int flatIndex = open.deleteMin();
			int x = getX(flatIndex);
			int y = getY(flatIndex);
			ShortPoint2D position = new ShortPoint2D(x, y);

			closedBitSet.set(flatIndex);
			map.setDebugColor(x, y, CLOSED_COLOR);

			if (x == tx && y == ty) {
				break;
			}

			ShortPoint2D cell = abstractedGrid.getCell(x, y); // current vertex is part of target cell
			if (cell.equals(targetCell)) {
				Float targetCosts = vertexToTargetCosts.get(position);
				if (targetCosts != null) { // if there exstis a path
					exploreVertex(tx, ty, getFlatIdx(tx, ty), targetCosts, flatIndex, tx, ty);
				}
			}

			// insert neighbors of current vertex
			Vertex vertex = abstractedGrid.getVertex(position);
			Vertex[] neighbors = vertex.getNeighbors();
			float[] neighborCosts = vertex.getCosts();

			for (int i = 0; i < neighbors.length; i++) {
				Vertex neighbor = neighbors[i];
				int flatNeighborIndex = getFlatIdx(neighbor.x, neighbor.y);

				if (!closedBitSet.get(flatNeighborIndex)) {
					float neighborCost = neighborCosts[i];
					exploreVertex(neighbor.x, neighbor.y, flatNeighborIndex, neighborCost, flatIndex, tx, ty);
				}
			}
		}

		return createPath(tx, ty);
	}

	private void initStartNodes(short sx, short sy, short tx, short ty) {
		// make actual start node closed
		int flatStartIndex = getFlatIdx(sx, sy);
		closedBitSet.set(flatStartIndex);
		depthParentHeap[getParentIndex(flatStartIndex)] = -1;

		// insert neighbors of start node
		Vertex startVertex = abstractedGrid.getVertex(new ShortPoint2D(sx, sy));

		final List<Vertex> startCellVertices;
		final float[] startCellCosts;
		if (startVertex == null) { // start position is not a vertex => calculate costs to vertices of cell
			final ShortPoint2D startCell = abstractedGrid.getCell(sx, sy);
			startCellVertices = abstractedGrid.getCellVertices(startCell);
			startCellCosts = super.calculateCosts(abstractedGrid.getMinCorner(startCell), abstractedGrid.getMaxCorner(startCell),
					new ShortPoint2D(sx, sy), startCellVertices).e2;
			super.clearState();
		} else {
			startCellVertices = Arrays.asList(startVertex.getNeighbors());
			startCellCosts = startVertex.getCosts();
		}
		map.clearDebugColors();
		map.setDebugColor(sx, sy, CLOSED_COLOR);

		int index = 0;
		for (Vertex neighbor : startCellVertices) {
			final int flatIndex = getFlatIdx(neighbor.x, neighbor.y);
			final float cost = startCellCosts[index];
			if (cost <= 0) {
				continue;
			}

			costs[flatIndex] = cost;
			depthParentHeap[getDepthIndex(flatIndex)] = 1;
			depthParentHeap[getParentIndex(flatIndex)] = flatStartIndex;
			openBitSet.set(flatIndex);
			float costsWithHeuristic = cost + Heuristics.getHexGridNoObstaclesDistance(neighbor.x, neighbor.y, tx, ty);
			// System.out.println(neighbor + ": " + costsWithHeuristic);
			open.insert(flatIndex, costsWithHeuristic);

			map.setDebugColor(neighbor.x, neighbor.y, OPEN_COLOR);
			index++;
		}

	}

	private void exploreVertex(short x, short y, int flatIndex, float neighborCost, int parentFlatIndex, short tx, short ty) {
		float newCosts = costs[parentFlatIndex] + neighborCost;

		if (openBitSet.get(flatIndex)) { // check if new path better
			float oldCosts = costs[flatIndex];
			if (oldCosts > newCosts) { // update path
				costs[flatIndex] = newCosts;
				depthParentHeap[getDepthIndex(flatIndex)] = depthParentHeap[getDepthIndex(parentFlatIndex)] + 1;
				depthParentHeap[getParentIndex(flatIndex)] = parentFlatIndex;

				int heuristicCosts = Heuristics.getHexGridNoObstaclesDistance(x, y, tx, ty);
				open.increasedPriority(flatIndex, oldCosts + heuristicCosts, newCosts + heuristicCosts);
			}
		} else {
			costs[flatIndex] = newCosts;
			depthParentHeap[getDepthIndex(flatIndex)] = depthParentHeap[getDepthIndex(parentFlatIndex)] + 1;
			depthParentHeap[getParentIndex(flatIndex)] = parentFlatIndex;
			openBitSet.set(flatIndex);
			open.insert(flatIndex, newCosts + Heuristics.getHexGridNoObstaclesDistance(x, y, tx, ty));

			map.setDebugColor(x, y, OPEN_COLOR);
		}
	}

	private HashMap<ShortPoint2D, Float> calculateTargetCellCosts(int tx, int ty, final ShortPoint2D targetCell) {
		final List<Vertex> targetCellVertices = abstractedGrid.getCellVertices(targetCell);
		float[] targetCellCosts = super.calculateCosts(abstractedGrid.getMinCorner(targetCell),
				abstractedGrid.getMaxCorner(targetCell), new ShortPoint2D(tx, ty), targetCellVertices).e2;
		HashMap<ShortPoint2D, Float> vertexToTargetCosts = new HashMap<>();
		int index = 0;
		for (Vertex v : targetCellVertices) {
			float targetCost = targetCellCosts[index++];
			if (targetCost > 0) { // only add vertex if path to target exists
				vertexToTargetCosts.put(v, targetCost);
			}
		}
		return vertexToTargetCosts;
	}

	private static final int getDepthIndex(int flatIndex) {
		return 2 * flatIndex;
	}

	private static final int getParentIndex(int flatIndex) {
		return 2 * flatIndex + 1;
	}

	private HPAStarPath createPath(short tx, short ty) {
		int flatIndex = getFlatIdx(tx, ty);

		if (!closedBitSet.get(flatIndex)) { // no path found
			return null;
		}

		int length = depthParentHeap[getDepthIndex(flatIndex)] + 1;
		HPAStarPath path = new HPAStarPath(length);

		path.insertAbstractPosition(length - 1, tx, ty);

		for (int index = length - 2; index >= 0; index--) {
			flatIndex = depthParentHeap[getParentIndex(flatIndex)];
			path.insertAbstractPosition(index, (short) getX(flatIndex), (short) getY(flatIndex));
		}

		return path;
	}
}
