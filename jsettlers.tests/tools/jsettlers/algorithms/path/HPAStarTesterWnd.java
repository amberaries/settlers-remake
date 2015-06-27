package jsettlers.algorithms.path;

import jsettlers.TestUtils;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.MapInterfaceConnector;

public class HPAStarTesterWnd {

	public static void main(String args[]) {
		HPAStarGrid grid = new HPAStarGrid();
		MapInterfaceConnector connector = TestUtils.openTestWindow(grid.getGraphicsGrid());

		connector.scrollTo(new ShortPoint2D(25, 25), false);
		connector.fireAction(new Action(EActionType.TOGGLE_DEBUG));
	}
}
