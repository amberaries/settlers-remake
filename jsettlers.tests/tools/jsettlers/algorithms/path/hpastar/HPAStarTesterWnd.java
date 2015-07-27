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
import java.util.Random;

import jsettlers.TestUtils;
import jsettlers.algorithms.path.astar.BucketQueueAStar;
import jsettlers.algorithms.path.hpastar.graph.HPAStarAbstractedGrid;
import jsettlers.algorithms.path.hpastar.graph.generation.HPAStarAbstractedGridCalculator;
import jsettlers.algorithms.path.hpastar.graph.generation.SparseTransitionsCalculator.SparseTransitionsCalculatorFactory;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.MainGridDataAccessor;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.network.synchronic.random.RandomSingleton;
import jsettlers.network.synchronic.timer.NetworkTimer;

public class HPAStarTesterWnd {

	private static final boolean RANDOM_MAP = false;
	private static final int CELL_SIZE = 16;

	public static void main(String args[]) throws MapLoadException {
		final HPAStarTestGrid grid;
		if (RANDOM_MAP) {
			grid = new HPAStarTestGrid(60, 60, 1f / 3);
		} else {
			grid = getGridByMap("big map");
		}

		MapInterfaceConnector connector = TestUtils.openTestWindow(grid.getGraphicsGrid());

		connector.scrollTo(new ShortPoint2D(25, 25), false);
		connector.fireAction(new Action(EActionType.TOGGLE_DEBUG));

		System.out.println("starting calculation of hpaStar grid...");
		MilliStopWatch watch = new MilliStopWatch();
		HPAStarAbstractedGridCalculator hpaStarGridCalculator = new HPAStarAbstractedGridCalculator(grid, grid.getWidth(), grid.getHeight(),
				new SparseTransitionsCalculatorFactory());
		HPAStarAbstractedGrid abstractedGrid = hpaStarGridCalculator.calculateAbstractedGrid(CELL_SIZE);
		watch.stop("calculating abstracted grid (cellSize=" + CELL_SIZE + ") needed");

		// calculate path
		BucketQueueAStar<Object> aStar = new BucketQueueAStar<Object>(grid.getAStarMap(), grid.getWidth(), grid.getHeight());
		HPAStar hpaStar = new HPAStar(abstractedGrid, grid, grid.getWidth(), grid.getHeight());
		// hpaStar.findPath((short) 15, (short) 39, (short) 18, (short) 33);

		grid.clearDebugColors();
		benchmark(grid, hpaStar, aStar);
	}

	private static void benchmark(HPAStarTestGrid grid, HPAStar hpaStar, BucketQueueAStar<Object> aStar) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		int seed = 1;
		int paths = 1000;
		{
			Random r = new Random(seed);
			MilliStopWatch watch = new MilliStopWatch();
			for (int i = 0; i < paths; i++) {
				short sx = (short) r.nextInt(grid.getWidth());
				short sy = (short) r.nextInt(grid.getHeight());
				short tx = (short) r.nextInt(grid.getWidth());
				short ty = (short) r.nextInt(grid.getHeight());

				if (!grid.isBlocked(sx, sy) && !grid.isBlocked(tx, ty) && grid.getBlockedPartition(sx, sy) == grid.getBlockedPartition(tx, ty)) {
					aStar.findPath(null, sx, sy, tx, ty);
				} else {
					i--;
				}
			}
			watch.stop("aStar: paths: " + paths + ", seed: " + seed + " needed");
		}

		{
			Random r = new Random(seed);
			MilliStopWatch watch = new MilliStopWatch();
			for (int i = 0; i < paths; i++) {
				short sx = (short) r.nextInt(grid.getWidth());
				short sy = (short) r.nextInt(grid.getHeight());
				short tx = (short) r.nextInt(grid.getWidth());
				short ty = (short) r.nextInt(grid.getHeight());

				if (!grid.isBlocked(sx, sy) && !grid.isBlocked(tx, ty) && grid.getBlockedPartition(sx, sy) == grid.getBlockedPartition(tx, ty)) {
					hpaStar.findPath(sx, sy, tx, ty);
				} else {
					i--;
				}
			}
			watch.stop("hpaStar: paths: " + paths + ", seed: " + seed + " needed");
		}
	}

	private static HPAStarTestGrid getGridByMap(String mapName) throws MapLoadException {
		TestUtils.setupSwingResources();

		MapLoader map = MapList.getDefaultList().getMapByName(mapName);
		MatchConstants.clock = new NetworkTimer();
		RandomSingleton.load(1);

		boolean[] players = new boolean[map.getMaxPlayers()];
		Arrays.fill(players, true);

		MainGrid grid = map.loadMainGrid(players).getMainGrid();
		return new HPAStarTestGrid(new MainGridDataAccessor(grid));
	}
}
