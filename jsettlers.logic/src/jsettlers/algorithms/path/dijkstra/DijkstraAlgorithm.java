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

import jsettlers.algorithms.path.InvalidStartPositionException;
import jsettlers.algorithms.path.Path;
import jsettlers.algorithms.path.astar.AbstractAStar;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;

/**
 * this class implements a strict dijkstra algorithm
 * 
 * @author Andreas Eberle
 * 
 */
public final class DijkstraAlgorithm {
	private static final byte[] directionIncreaseX = { -1, 0, 1, 1, 0, -1 };
	private static final byte[] directionIncreaseY = { 0, 1, 1, 0, -1, -1 };

	private final IDijkstraPathMap map;
	private final short height, width;
	private final AbstractAStar aStar;

	public DijkstraAlgorithm(IDijkstraPathMap map, AbstractAStar aStar, short width, short height) {
		this.map = map;
		this.aStar = aStar;
		this.width = width;
		this.height = height;
	}

	public final Path find(ShortPoint2D pathStart, final short cX, final short cY, final short minRadius, final short maxRadius,
			final ESearchType type, boolean needsPlayersGround, byte playerId) {
		if (!isInBounds(cX, cY)) {
			throw new InvalidStartPositionException("dijkstra center position is not in bounds!", cX, cY);
		}

		// check center position (special case for minRadius <= 0
		if (minRadius <= 0) {
			map.setDijkstraSearched(cX, cY);
			if (map.fitsSearchType(cX, cY, type, needsPlayersGround, playerId)) {
				Path path = findPathTo(pathStart, cX, cY, needsPlayersGround, playerId);
				if (path != null)
					return path;
			}
		}

		for (short radius = minRadius; radius < maxRadius; radius++) {
			short x = cX, y = (short) (cY - radius);
			for (byte direction = 0; direction < 6; direction++) {
				byte dx = directionIncreaseX[direction];
				byte dy = directionIncreaseY[direction];
				for (short length = 0; length < radius; length++) {
					x += dx;
					y += dy;
					if (isInBounds(x, y)) {
						map.setDijkstraSearched(x, y);
						if (map.fitsSearchType(x, y, type, needsPlayersGround, playerId)) {
							Path path = findPathTo(pathStart, x, y, needsPlayersGround, playerId);
							if (path != null)
								return path;
						}
					}
				}
			}
		}

		return null;
	}

	private final Path findPathTo(ShortPoint2D start, short tx, short ty, boolean needsPlayersGround, byte playerId) {
		return aStar.findPath(start.x, start.y, tx, ty, needsPlayersGround, playerId);
	}

	private final boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

}
