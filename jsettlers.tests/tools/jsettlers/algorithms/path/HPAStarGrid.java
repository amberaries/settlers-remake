package jsettlers.algorithms.path;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.EDebugColorModes;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;

public class HPAStarGrid {

	private static final int WIDTH = 60;
	private static final int HEIGHT = 60;

	private final boolean[][] blocked = new boolean[WIDTH][HEIGHT];
	private final int[][] debugColors = new int[WIDTH][HEIGHT];

	public HPAStarGrid() {

	}

	float getCost(IPathCalculatable requester, int sx, int sy, int tx, int ty) {
		return isBlocked(requester, tx, ty) ? Float.MAX_VALUE : 1;
	}

	void setDebugColor(int x, int y, Color color) {
		debugColors[x][y] = color == null ? 0 : color.getARGB();
	}

	short getBlockedPartition(int x, int y) {
		return 1;
	}

	boolean isBlocked(IPathCalculatable requester, int x, int y) {
		return blocked[x][y];
	}

	public IGraphicsGrid getGraphicsGrid() {
		return new IGraphicsGrid() {

			@Override
			public void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
			}

			@Override
			public int nextDrawableX(int x, int y, int maxX) {
				return x + 1;
			}

			@Override
			public boolean isBorder(int x, int y) {
				return false;
			}

			@Override
			public short getWidth() {
				return WIDTH;
			}

			@Override
			public byte getVisibleStatus(int x, int y) {
				return CommonConstants.FOG_OF_WAR_VISIBLE;
			}

			@Override
			public byte getPlayerIdAt(int x, int y) {
				return 1;
			}

			@Override
			public IPartitionData getPartitionData(int x, int y) {
				return null;
			}

			@Override
			public IMovable getMovableAt(int x, int y) {
				return null;
			}

			@Override
			public IMapObject getMapObjectsAt(int x, int y) {
				return null;
			}

			@Override
			public ELandscapeType getLandscapeTypeAt(int x, int y) {
				return ELandscapeType.GRASS;
			}

			@Override
			public byte getHeightAt(int x, int y) {
				return 0;
			}

			@Override
			public short getHeight() {
				return HEIGHT;
			}

			@Override
			public int getDebugColorAt(int x, int y, EDebugColorModes debugColorMode) {
				return debugColors[x][y];
			}
		};
	}
}
