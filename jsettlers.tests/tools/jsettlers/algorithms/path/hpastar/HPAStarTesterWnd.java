package jsettlers.algorithms.path.hpastar;

import java.util.Arrays;

import jsettlers.TestUtils;
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
		final HPAStarBaseGrid grid;
		if (RANDOM_MAP) {
			grid = new HPAStarBaseGrid(60, 60, 1f / 3);
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
		HPAStar hpaStar = new HPAStar(grid);
		hpaStar.calculateTransitions(CELL_SIZE);
		watch.stop("calculating transitions needed");
	}

	private static HPAStarBaseGrid getGridByMap(String mapName) throws MapLoadException {
		TestUtils.setupSwingResources();

		MapLoader map = MapList.getDefaultList().getMapByName(mapName);
		MatchConstants.clock = new NetworkTimer();
		RandomSingleton.load(1);

		boolean[] players = new boolean[map.getMaxPlayers()];
		Arrays.fill(players, true);

		MainGrid grid = map.loadMainGrid(players).getMainGrid();
		return new HPAStarBaseGrid(new MainGridDataAccessor(grid));
	}
}
