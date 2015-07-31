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

import java.util.Random;

import jsettlers.TestUtils;
import jsettlers.algorithms.path.astar.BucketQueueAStar;
import jsettlers.algorithms.path.hpastar.graph.HPAStarAbstractedGrid;
import jsettlers.algorithms.path.hpastar.graph.generation.HPAStarAbstractedGridCalculator;
import jsettlers.algorithms.path.hpastar.graph.generation.SparseTransitionsCalculator.SparseTransitionsCalculatorFactory;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.MainGridDataAccessor;

public class HPAStarTesterWnd {

	private static final boolean RANDOM_MAP = false;
	private static final int CELL_SIZE = 64;

	public static void main(String args[]) throws MapLoadException {
		final HPAStarTestGrid grid;
		if (RANDOM_MAP) {
			grid = new HPAStarTestGrid(60, 60, 1f / 3);
		} else {
			grid = getGridByMapName("big map");
		}

		MapInterfaceConnector connector = TestUtils.openTestWindow(grid.getGraphicsGrid());

		connector.scrollTo(new ShortPoint2D(25, 25), false);
		connector.fireAction(new Action(EActionType.TOGGLE_DEBUG));

		System.out.println("starting calculation of hpaStar grid...");
		MilliStopWatch watch = new MilliStopWatch();
		HPAStarAbstractedGridCalculator hpaStarGridCalculator = new HPAStarAbstractedGridCalculator(grid, grid.getWidth(), grid.getHeight(),
				// new SparseTransitionsCalculatorFactory());
				new SparseTransitionsCalculatorFactory());
		HPAStarAbstractedGrid abstractedGrid = hpaStarGridCalculator.calculateAbstractedGrid(CELL_SIZE);
		watch.stop("calculating abstracted grid (cellSize=" + CELL_SIZE + ") needed");

		// calculate path
		BucketQueueAStar<Object> aStar = new BucketQueueAStar<Object>(grid.getAStarMap(), grid.getWidth(), grid.getHeight());
		HPAStar hpaStar = new HPAStar(abstractedGrid, grid, grid.getWidth(), grid.getHeight());
		// hpaStar.findPath((short) 47, (short) 31, (short) 18, (short) 45);

		grid.clearDebugColors();
		benchmark(grid, hpaStar, aStar);
	}

	private static HPAStarTestGrid getGridByMapName(String mapName) throws MapLoadException {
		MainGrid mainGrid = TestUtils.getGridByMapName(mapName);
		return new HPAStarTestGrid(new MainGridDataAccessor(mainGrid));
	}

	private static void benchmark(HPAStarTestGrid grid, HPAStar hpaStar, BucketQueueAStar<Object> aStar) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		int seed = 1;
		int paths = 1000;
		Random r = new Random(seed);
		@SuppressWarnings("unchecked")
		Tuple<ShortPoint2D, ShortPoint2D>[] pathChallanges = new Tuple[paths];

		for (int i = 0; i < paths; i++) {
			short sx = (short) r.nextInt(grid.getWidth());
			short sy = (short) r.nextInt(grid.getHeight());
			short tx = (short) r.nextInt(grid.getWidth());
			short ty = (short) r.nextInt(grid.getHeight());

			if (!grid.isBlocked(sx, sy) && !grid.isBlocked(tx, ty) && grid.getBlockedPartition(sx, sy) == grid.getBlockedPartition(tx, ty)) {
				pathChallanges[i] = new Tuple<>(new ShortPoint2D(sx, sy), new ShortPoint2D(tx, ty));
			} else {
				i--; // this wasn't a valid path, try again
			}
		}

		{
			MilliStopWatch watch = new MilliStopWatch();
			for (Tuple<ShortPoint2D, ShortPoint2D> challange : pathChallanges) {
				aStar.findPath(null, challange.e1.x, challange.e1.y, challange.e2.x, challange.e2.y);
			}
			watch.stop("aStar: paths: " + paths + ", seed: " + seed + " needed");
		}

		{
			MilliStopWatch watch = new MilliStopWatch();
			for (Tuple<ShortPoint2D, ShortPoint2D> challange : pathChallanges) {
				hpaStar.findPath(challange.e1.x, challange.e1.y, challange.e2.x, challange.e2.y);
			}
			watch.stop("hpaStar: paths: " + paths + ", seed: " + seed + " needed");
		}
	}

}
