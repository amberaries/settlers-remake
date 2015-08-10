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
package jsettlers.algorithms.path.astar;

import java.util.BitSet;

import jsettlers.algorithms.path.FullPath;
import jsettlers.algorithms.path.InvalidStartPositionException;
import jsettlers.algorithms.queues.bucket.AbstractMinBucketQueue;
import jsettlers.algorithms.queues.bucket.ListMinBucketQueue;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * AStar algorithm to find paths from A to B on a hex grid
 * 
 * @author Andreas Eberle
 * 
 */
public final class BucketQueueAStar extends AbstractAStar {
	private static final byte[] xDeltaArray = EDirection.DIRECTION_DELTAS_X;
	private static final byte[] yDeltaArray = EDirection.DIRECTION_DELTAS_Y;

	private final IAStarPathMap map;

	private final short height;
	private final short width;

	private final BitSet openBitSet;
	private final BitSet closedBitSet;

	private final float[] costs;

	private final int[] depthParentHeap;

	private final AbstractMinBucketQueue open;

	public BucketQueueAStar(IAStarPathMap map, short width, short height) {
		this.map = map;
		this.width = width;
		this.height = height;

		this.open = new ListMinBucketQueue(width * height);

		this.openBitSet = new BitSet(width * height);
		this.closedBitSet = new BitSet(width * height);
		this.costs = new float[width * height];

		this.depthParentHeap = new int[width * height * 2];
	}

	@Override
	public final FullPath findPath(ShortPoint2D start, ShortPoint2D target, boolean needsPlayersGround, byte playerId) {
		return findPath(start.x, start.y, target.x, target.y, needsPlayersGround, playerId);
	}

	@Override
	public final FullPath findPath(final short sx, final short sy, final short tx, final short ty, boolean needsPlayersGround, byte playerId) {
		final boolean blockedAtStart;
		if (!isInBounds(sx, sy)) {
			throw new InvalidStartPositionException("Start position is out of bounds!", sx, sy);
		} else if (!isInBounds(tx, ty) || map.isBlocked(tx, ty, needsPlayersGround, playerId)
				|| map.getBlockedPartition(sx, sy) != map.getBlockedPartition(tx, ty)) {
			return null; // target can not be reached
		} else if (sx == tx && sy == ty) {
			return null;
		} else if (map.isBlocked(sx, sy, needsPlayersGround, playerId)) {
			blockedAtStart = true;
		} else {
			blockedAtStart = false;
		}

		final int targetFlatIdx = getFlatIdx(tx, ty);

		closedBitSet.clear();
		openBitSet.clear();
		open.clear();

		initStartNode(sx, sy, tx, ty);

		while (!open.isEmpty()) {
			// get next position to close
			final int currFlatIdx = open.deleteMin();
			final int x = getX(currFlatIdx);
			final int y = getY(currFlatIdx);

			// close current position
			closedBitSet.set(currFlatIdx);
			map.markAsClosed(x, y);

			// is this the target position?
			if (targetFlatIdx == currFlatIdx) {
				break;
			}

			// explore neighbors
			final float currPositionCosts = costs[currFlatIdx];

			for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
				final int neighborX = x + xDeltaArray[i];
				final int neighborY = y + yDeltaArray[i];

				if (isValidPosition(neighborX, neighborY, blockedAtStart, needsPlayersGround, playerId)) {
					final int flatNeighborIdx = getFlatIdx(neighborX, neighborY);

					if (!closedBitSet.get(flatNeighborIdx)) {
						final float newCosts = currPositionCosts + map.getCost(x, y, neighborX, neighborY);

						if (openBitSet.get(flatNeighborIdx)) {
							final float oldCosts = costs[flatNeighborIdx];

							if (oldCosts > newCosts) {
								costs[flatNeighborIdx] = newCosts;
								depthParentHeap[getDepthIdx(flatNeighborIdx)] = depthParentHeap[getDepthIdx(currFlatIdx)] + 1;
								depthParentHeap[getParentIdx(flatNeighborIdx)] = currFlatIdx;

								int heuristicCosts = Heuristics.getHexGridNoObstaclesDistance(neighborX, neighborY, tx, ty);
								open.increasedPriority(flatNeighborIdx, oldCosts + heuristicCosts, newCosts + heuristicCosts);
							}

						} else {
							costs[flatNeighborIdx] = newCosts;
							depthParentHeap[getDepthIdx(flatNeighborIdx)] = depthParentHeap[getDepthIdx(currFlatIdx)] + 1;
							depthParentHeap[getParentIdx(flatNeighborIdx)] = currFlatIdx;
							openBitSet.set(flatNeighborIdx);
							open.insert(flatNeighborIdx, newCosts + Heuristics.getHexGridNoObstaclesDistance(neighborX, neighborY, tx, ty));

							map.markAsOpen(neighborX, neighborY);
						}
					}
				}
			}
		}

		if (closedBitSet.get(targetFlatIdx)) {
			int pathlength = depthParentHeap[getDepthIdx(getFlatIdx(tx, ty))];
			FullPath path = new FullPath(pathlength);

			int idx = pathlength;
			int parentFlatIdx = targetFlatIdx;

			while (idx > 0) {
				idx--;
				path.insertAt(idx, (short) getX(parentFlatIdx), (short) getY(parentFlatIdx));
				parentFlatIdx = depthParentHeap[getParentIdx(parentFlatIdx)];
			}

			return path;
		}

		return null;
	}

	private static final int getDepthIdx(int flatIdx) {
		return 2 * flatIdx;
	}

	private static final int getParentIdx(int flatIdx) {
		return 2 * flatIdx + 1;
	}

	private final void initStartNode(int sx, int sy, int tx, int ty) {
		int flatIdx = getFlatIdx(sx, sy);
		depthParentHeap[getDepthIdx(flatIdx)] = 0;
		depthParentHeap[getParentIdx(flatIdx)] = -1;
		costs[flatIdx] = 0;

		open.insert(flatIdx, 0 + Heuristics.getHexGridNoObstaclesDistance(sx, sy, tx, ty));
		openBitSet.set(flatIdx);
	}

	private final boolean isValidPosition(int x, int y, boolean blockedAtStart, boolean needsPlayersGround, byte playerId) {
		return isInBounds(x, y) && (!map.isBlocked(x, y, needsPlayersGround, playerId) || blockedAtStart);
	}

	private final boolean isInBounds(int x, int y) {
		return 0 <= x && x < width && 0 <= y && y < height;
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
