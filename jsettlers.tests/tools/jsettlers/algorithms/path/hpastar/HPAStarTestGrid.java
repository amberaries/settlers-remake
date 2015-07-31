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

import jsettlers.algorithms.path.astar.IAStarPathMap;
import jsettlers.algorithms.path.hpastar.graph.generation.HPAStarFactoryGrid;
import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.EDebugColorModes;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.logic.map.grid.MainGridDataAccessor;
import jsettlers.logic.map.grid.flags.FlagsGrid;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;

public class HPAStarTestGrid extends HPAStarFactoryGrid {
	public static boolean ENABLE_DEBUG_COLORS = true;

	private final short width;
	private final short height;
	private final boolean[][] blocked;
	private final int[][] debugColors;
	private final short[][] blockedPartition;

	public HPAStarTestGrid(int width, int height) {
		this.width = (short) width;
		this.height = (short) height;
		blocked = new boolean[width][height];
		blockedPartition = new short[width][height];

		if (ENABLE_DEBUG_COLORS) {
			debugColors = new int[width][height];
		} else {
			debugColors = null;
		}
	}

	public HPAStarTestGrid(final int width, final int height, float blockedPercentage) {
		this(width, height);

		Random r = new Random(1234);
		for (int i = 0; i < width * height * blockedPercentage; i++) {
			int x = r.nextInt(width);
			int y = r.nextInt(height);
			blocked[x][y] = true;
			blockedPartition[x][y] = 1;
		}
	}

	public HPAStarTestGrid(MainGridDataAccessor grid) {
		this(grid.getWidth(), grid.getHeight());

		FlagsGrid flagsGrid = grid.getFlagsGrid();
		LandscapeGrid landscapeGrid = grid.getLandscapeGrid();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				blocked[x][y] = flagsGrid.isBlocked(x, y);
				blockedPartition[x][y] = landscapeGrid.getBlockedPartitionAt(x, y);
			}
		}
	}

	public short getWidth() {
		return width;
	}

	public short getHeight() {
		return height;
	}

	float getCost(Object requirements, int sx, int sy, int tx, int ty) {
		return isBlocked(tx, ty) ? Float.MAX_VALUE : 1;
	}

	@Override
	public void setDebugColor(int x, int y, Color color) {
		if (ENABLE_DEBUG_COLORS)
			debugColors[x][y] = color == null ? 0 : color.getARGB();
	}

	public short getBlockedPartition(int x, int y) {
		return blockedPartition[x][y];
	}

	@Override
	public boolean isBlocked(int x, int y) {
		return blocked[x][y];
	}

	@Override
	public void clearDebugColors() {
		if (ENABLE_DEBUG_COLORS) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					debugColors[x][y] = 0;
				}
			}
		}
	}

	public IAStarPathMap<Object> getAStarMap() {
		return new IAStarPathMap<Object>() {

			@Override
			public void setDebugColor(int x, int y, Color color) {
				HPAStarTestGrid.this.setDebugColor(x, y, color);
			}

			@Override
			public void markAsOpen(int x, int y) {
				HPAStarTestGrid.this.setDebugColor(x, y, Color.ORANGE.colorWithAlpha(0.2f));
			}

			@Override
			public void markAsClosed(int x, int y) {
				HPAStarTestGrid.this.setDebugColor(x, y, Color.RED.colorWithAlpha(0.2f));
			}

			@Override
			public boolean isBlocked(Object requirements, int x, int y) {
				return HPAStarTestGrid.this.isBlocked(x, y);
			}

			@Override
			public float getCost(int sx, int sy, int tx, int ty) {
				return HPAStarTestGrid.this.getCost(null, sx, sy, tx, ty);
			}

			@Override
			public short getBlockedPartition(int x, int y) {
				return 1;
			}
		};
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
				return width;
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
				return height;
			}

			@Override
			public int getDebugColorAt(int x, int y, EDebugColorModes debugColorMode) {
				int debugColor = ENABLE_DEBUG_COLORS ? debugColors[x][y] : 0;

				return debugColor == 0 ? blocked[x][y] ? Color.BLACK.getARGB() : 0 : debugColor;
			}
		};
	}

	@Override
	public float getCost(int sx, int sy, int tx, int ty) {
		return getCost(null, sx, sy, tx, ty);
	}
}
