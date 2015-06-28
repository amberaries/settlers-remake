package jsettlers.algorithms.path;

import jsettlers.TestUtils;
import jsettlers.algorithms.path.astar.BucketQueueAStar;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;

public class HPAStarTesterWnd {

	public static void main(String args[]) {
		final HPAStarGrid grid = new HPAStarGrid();
		MapInterfaceConnector connector = TestUtils.openTestWindow(grid.getGraphicsGrid());

		connector.scrollTo(new ShortPoint2D(25, 25), false);
		connector.fireAction(new Action(EActionType.TOGGLE_DEBUG));

		connector.addListener(new IMapInterfaceListener() {
			ShortPoint2D start;
			BucketQueueAStar aStar = new BucketQueueAStar(grid.getAStarMap(), (short) HPAStarGrid.WIDTH, (short) HPAStarGrid.HEIGHT);

			@Override
			public void action(Action action) {
				switch (action.getActionType()) {
				case SELECT_POINT:
					start = ((PointAction) action).getPosition();
					break;

				case MOVE_TO:
					grid.clearDebugColors();
					ShortPoint2D target = ((PointAction) action).getPosition();
					aStar.findPath(null, start.x, start.y, target.x, target.y);
					break;

				default:
					break;
				}
			}
		});
	}
}
