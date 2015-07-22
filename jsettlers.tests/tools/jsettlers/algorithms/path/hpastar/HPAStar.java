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

import java.util.HashMap;
import java.util.List;

import jsettlers.algorithms.path.astar.Heuristics;
import jsettlers.algorithms.path.dijkstra.BucketQueue1ToNDijkstra;
import jsettlers.algorithms.path.dijkstra.DijkstraGrid;
import jsettlers.algorithms.path.hpastar.graph.HPAStarAbstractedGrid;
import jsettlers.algorithms.path.hpastar.graph.HPAStarPath;
import jsettlers.algorithms.path.hpastar.graph.Vertex;
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
		final ShortPoint2D startCell = abstractedGrid.getCell(sx, sy);

		map.clearDebugColors();
		final List<Vertex> startCellVertices = abstractedGrid.getCellVertices(startCell);
		float[] startCellCosts = super.calculateCosts(abstractedGrid.getMinCorner(startCell), abstractedGrid.getMaxCorner(startCell),
				new ShortPoint2D(sx, sy), startCellVertices).e2;

		final ShortPoint2D targetCell = abstractedGrid.getCell(tx, ty);
		HashMap<ShortPoint2D, Float> vertexToTargetCosts = calculateTargetCellCosts(tx, ty, targetCell);

		map.clearDebugColors();
		super.clearState();

		// make actual start node closed
		int flatStartIndex = getFlatIdx(sx, sy);
		openBitSet.set(flatStartIndex);
		closedBitSet.set(flatStartIndex);
		map.markAsClosed(sx, sy);
		depthParentHeap[getParentIdx(flatStartIndex)] = -1;

		// init first vertices
		int index = 0;
		for (Vertex neighbor : startCellVertices) {
			int flatIndex = getFlatIdx(neighbor.x, neighbor.y);
			costs[flatIndex] = startCellCosts[index];
			depthParentHeap[getDepthIdx(flatIndex)] = 1;
			depthParentHeap[getParentIdx(flatIndex)] = flatStartIndex;
			openBitSet.set(flatIndex);
			open.insert(flatIndex, startCellCosts[index] + Heuristics.getHexGridNoObstaclesDistance(neighbor.x, neighbor.y, tx, ty));

			map.markAsOpen(neighbor.x, neighbor.y);
			index++;
		}

		// run astar
		while (!open.isEmpty()) {
			int flatIndex = open.deleteMin();
			int x = getX(flatIndex);
			int y = getY(flatIndex);
			ShortPoint2D position = new ShortPoint2D(x, y);

			closedBitSet.set(flatIndex);
			map.markAsClosed(x, y);

			if (x == tx && y == ty) {
				break;
			}

			ShortPoint2D cell = abstractedGrid.getCell(x, y); // current vertex is part of target cell
			if (targetCell.equals(cell)) {
				float targetCosts = vertexToTargetCosts.get(position);
				exploreVertex(tx, ty, targetCosts, flatIndex, tx, ty);
			}

			// insert neighbors of current vertex
			Vertex vertex = abstractedGrid.getVertex(position);
			Vertex[] neighbors = vertex.getNeighbors();
			float[] neighborCosts = vertex.getCosts();

			for (int i = 0; i < neighbors.length; i++) {
				Vertex neighbor = neighbors[i];
				float neighborCost = neighborCosts[i];
				exploreVertex(neighbor.x, neighbor.y, neighborCost, flatIndex, tx, ty);
			}
		}

		return null;
	}

	private void exploreVertex(short neighborX, short neighborY, float neighborCost, int parentFlatIndex, short tx, short ty) {
		float newCosts = costs[parentFlatIndex] + neighborCost;
		int flatNeighborIndex = getFlatIdx(neighborX, neighborY);

		if (openBitSet.get(flatNeighborIndex)) { // check if new path better
			float oldCosts = costs[flatNeighborIndex];
			if (oldCosts > newCosts) { // update path
				costs[flatNeighborIndex] = newCosts;
				depthParentHeap[getDepthIdx(flatNeighborIndex)] = depthParentHeap[getDepthIdx(parentFlatIndex)] + 1;
				depthParentHeap[getParentIdx(flatNeighborIndex)] = parentFlatIndex;

				int heuristicCosts = Heuristics.getHexGridNoObstaclesDistance(neighborX, neighborY, tx, ty);
				open.increasedPriority(flatNeighborIndex, oldCosts + heuristicCosts, newCosts + heuristicCosts);
			}
		} else {
			costs[flatNeighborIndex] = newCosts;
			depthParentHeap[getDepthIdx(flatNeighborIndex)] = depthParentHeap[getDepthIdx(parentFlatIndex)] + 1;
			depthParentHeap[getParentIdx(flatNeighborIndex)] = parentFlatIndex;
			openBitSet.set(flatNeighborIndex);
			open.insert(flatNeighborIndex, newCosts + Heuristics.getHexGridNoObstaclesDistance(neighborX, neighborY, tx, ty));

			map.markAsOpen(neighborX, neighborY);
		}
	}

	private HashMap<ShortPoint2D, Float> calculateTargetCellCosts(int tx, int ty, final ShortPoint2D targetCell) {
		final List<Vertex> targetCellVertices = abstractedGrid.getCellVertices(targetCell);
		float[] targetCellCosts = super.calculateCosts(abstractedGrid.getMinCorner(targetCell),
				abstractedGrid.getMaxCorner(targetCell), new ShortPoint2D(tx, ty), targetCellVertices).e2;
		HashMap<ShortPoint2D, Float> vertexToTargetCosts = new HashMap<>();
		int index = 0;
		for (Vertex v : targetCellVertices) {
			vertexToTargetCosts.put(v, targetCellCosts[index++]);
		}
		return vertexToTargetCosts;
	}

	private static final int getDepthIdx(int flatIdx) {
		return 2 * flatIdx;
	}

	private static final int getParentIdx(int flatIdx) {
		return 2 * flatIdx + 1;
	}
}
