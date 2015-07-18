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
package jsettlers.algorithms;

import jsettlers.algorithms.path.Path;
import jsettlers.algorithms.path.astar.BucketQueueAStar;
import jsettlers.algorithms.path.astar.DummyEmptyAStarMap;
import jsettlers.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.algorithms.path.dijkstra.IDijkstraPathMap;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.occupying.PathRequirements;
import jsettlers.logic.map.grid.IPathRequirements;

public class SimpleDijkstraTester {
	private static final short WIDTH = (short) 200;
	private static final short HEIGHT = (short) 200;

	public static void main(String args[]) {
		IDijkstraPathMap<IPathRequirements> map = new IDijkstraPathMap<IPathRequirements>() {
			@Override
			public boolean fitsSearchType(int x, int y, ESearchType type, IPathRequirements requirements) {
				if (x == 120 && y == 100)
					return true;
				if (x == 110 && y == 110)
					return true;
				if (x == 118 && y == 115)
					return true;

				return false;
			}

			@Override
			public void setDijkstraSearched(int x, int y) {
			}
		};
		DummyEmptyAStarMap aStarMap = new DummyEmptyAStarMap(WIDTH, HEIGHT);
		aStarMap.setBlocked(120, 100, true);

		DijkstraAlgorithm<IPathRequirements> dijkstra = new DijkstraAlgorithm<>(map,
				new BucketQueueAStar<IPathRequirements>(aStarMap, WIDTH, HEIGHT), WIDTH, HEIGHT);
		Path path = dijkstra.find(new PathRequirements((byte) 0, false), new ShortPoint2D(100, 100), (short) 100, (short) 100, (short) 1, (short) 30,
				null);
		System.out.println("path:  " + path);
	}
}
