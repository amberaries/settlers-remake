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

import jsettlers.TestUtils;
import jsettlers.algorithms.path.hpastar.graph.HPAStarAbstractedGrid;
import jsettlers.algorithms.path.hpastar.graph.generation.HPAStarAbstractedGridFactory;
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

	private static final boolean RANDOM_MAP = true;
	private static final int CELL_SIZE = 10;

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

		// connector.addListener(new IMapInterfaceListener() {
		// ShortPoint2D start;
		// BucketQueueAStar aStar = new BucketQueueAStar(grid.getAStarMap(), (short) grid.getWidth(), (short) grid.getHeight());
		//
		// @Override
		// public void action(Action action) {
		// switch (action.getActionType()) {
		// case SELECT_POINT:
		// start = ((PointAction) action).getPosition();
		// break;
		//
		// case MOVE_TO:
		// if (start == null)
		// break;
		//
		// grid.clearDebugColors();
		// ShortPoint2D target = ((PointAction) action).getPosition();
		// Path path = aStar.findPath(null, start.x, start.y, target.x, target.y);
		// while (path != null && path.hasNextStep()) {
		// grid.setDebugColor(path.nextX(), path.nextY(), Color.BLUE);
		// path.goToNextStep();
		// }
		// break;
		//
		// default:
		// break;
		// }
		// }
		// });

		MilliStopWatch watch = new MilliStopWatch();
		HPAStarAbstractedGridFactory hpaStarGridFactory = new HPAStarAbstractedGridFactory(grid, grid.getWidth(), grid.getHeight());
		HPAStarAbstractedGrid abstractedGrid = hpaStarGridFactory.calculateAbstractedGrid(CELL_SIZE);
		watch.stop("calculating abstracted grid needed");

		// calculate path
		HPAStar hpaStar = new HPAStar(abstractedGrid, grid, grid.getWidth(), grid.getHeight());
		hpaStar.findPath((short) 15, (short) 15, (short) (grid.getWidth() - 15), (short) (grid.getHeight() - 15));
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
