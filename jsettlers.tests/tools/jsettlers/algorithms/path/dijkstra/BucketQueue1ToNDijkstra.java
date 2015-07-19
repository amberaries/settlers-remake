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
package jsettlers.algorithms.path.dijkstra;

import java.util.BitSet;
import java.util.List;

import jsettlers.algorithms.queues.bucket.AbstractMinBucketQueue;
import jsettlers.algorithms.queues.bucket.ListMinBucketQueue;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;

/**
 * Dijkstra algorithm to find paths from start to several targets in a given boundary
 * 
 * @author Andreas Eberle
 * 
 */
public final class BucketQueue1ToNDijkstra {
	private static final byte[] xDeltaArray = EDirection.DIRECTION_DELTAS_X;
	private static final byte[] yDeltaArray = EDirection.DIRECTION_DELTAS_Y;

	private final DijkstraGrid map;

	private final short width;

	private final BitSet openBitSet;
	private final BitSet closedBitSet;

	private final float[] costs;

	private final AbstractMinBucketQueue open;

	public BucketQueue1ToNDijkstra(DijkstraGrid map, short width, short height) {
		this.map = map;
		this.width = width;

		this.open = new ListMinBucketQueue(width * height);

		this.openBitSet = new BitSet(width * height);
		this.closedBitSet = new BitSet(width * height);
		this.costs = new float[width * height];
	}

	public final Tuple<Integer, float[]> calculateCosts(final ShortPoint2D minCorner, final ShortPoint2D maxCorner, final ShortPoint2D start,
			final List<? extends ShortPoint2D> targets) {
		closedBitSet.clear();
		openBitSet.clear();
		open.clear();

		initStartNode(start);

		while (!open.isEmpty()) {
			// get next position to close
			final int currFlatIdx = open.deleteMin();
			final int x = getX(currFlatIdx);
			final int y = getY(currFlatIdx);

			// close current position
			closedBitSet.set(currFlatIdx);
			map.markAsClosed(x, y);

			// explore neighbors
			final float currPositionCosts = costs[currFlatIdx];

			for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
				final int neighborX = x + xDeltaArray[i];
				final int neighborY = y + yDeltaArray[i];

				if (neighborX < minCorner.x || maxCorner.x < neighborX || neighborY < minCorner.y || maxCorner.y < neighborY) {
					continue; // skip positions out of bounds
				}

				float stepCosts = map.getCosts(x, y, neighborX, neighborY);

				if (stepCosts < Float.MAX_VALUE) {
					final int flatNeighborIdx = getFlatIdx(neighborX, neighborY);

					if (!closedBitSet.get(flatNeighborIdx)) {
						final float newCosts = currPositionCosts + stepCosts;

						if (openBitSet.get(flatNeighborIdx)) {
							final float oldCosts = costs[flatNeighborIdx];

							if (oldCosts > newCosts) {
								costs[flatNeighborIdx] = newCosts;
								open.increasedPriority(flatNeighborIdx, oldCosts, newCosts);
							}

						} else {
							costs[flatNeighborIdx] = newCosts;
							openBitSet.set(flatNeighborIdx);
							open.insert(flatNeighborIdx, newCosts);

							map.markAsOpen(neighborX, neighborY);
						}
					}
				}
			}
		}

		float[] costsOfTargets = new float[targets.size()];
		int idx = 0;
		int connectedTargets = 0;
		for (ShortPoint2D target : targets) {
			int flatIdx = getFlatIdx(target.x, target.y);

			if (closedBitSet.get(flatIdx)) {
				costsOfTargets[idx] = costs[flatIdx];
				connectedTargets++;
			}
			idx++;
		}

		return new Tuple<>(connectedTargets, costsOfTargets);
	}

	private final void initStartNode(ShortPoint2D start) {
		int flatIdx = getFlatIdx(start.x, start.y);
		costs[flatIdx] = 0;

		open.insert(flatIdx, 0);
		openBitSet.set(flatIdx);
	}

	private final int getFlatIdx(int x, int y) {
		return y * width + x;
	}

	private final int getX(int flatIdx) {
		return flatIdx % width;
	}

	private final int getY(int flatIdx) {
		return flatIdx / width;
	}
}
