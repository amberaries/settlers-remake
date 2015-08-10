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
package jsettlers.algorithms.path.hpastar.path;

import jsettlers.algorithms.path.FullPath;
import jsettlers.algorithms.path.Path;
import jsettlers.common.position.ShortPoint2D;

public class HPAStarPath extends Path {
	private static final long serialVersionUID = -7950105340565322678L;

	private final short[] abstractPath;
	private final IFullPathFinder pathFinder;
	private final boolean needsPlayersGround;
	private final byte playerId;

	private int abstractPathCounter = 0;
	private int overallStepCounter = 0;
	private FullPath currentPathPart;

	public HPAStarPath(int length, IFullPathFinder pathFinder, boolean needsPlayersGround, byte playerId) {
		this.pathFinder = pathFinder;
		this.needsPlayersGround = needsPlayersGround;
		this.playerId = playerId;
		abstractPath = new short[2 * length];
	}

	public void insertAbstractPosition(int index, short x, short y) {
		int twoTimesIndex = 2 * index;
		abstractPath[twoTimesIndex] = x;
		abstractPath[twoTimesIndex + 1] = y;
	}

	private void ensurePath() {
		if ((currentPathPart == null || currentPathPart.hasOverNextStep()) && abstractPathCounter + 3 < abstractPath.length) {
			short sx = abstractPath[abstractPathCounter];
			short sy = abstractPath[abstractPathCounter + 1];
			short tx = abstractPath[abstractPathCounter + 2];
			short ty = abstractPath[abstractPathCounter + 3];

			FullPath nextPathPart = pathFinder.findPath(sx, sy, tx, ty, needsPlayersGround, playerId);
			currentPathPart.append(nextPathPart);

			abstractPathCounter += 2;
		}
	}

	@Override
	public Path prependPositions(ShortPoint2D... pathPrefix) {
		if (currentPathPart == null) {
			return new FullPath(pathPrefix);
		} else {
			return currentPathPart.prependPositions(pathPrefix);
		}
	}

	@Override
	public boolean hasNextStep() {
		ensurePath();
		return currentPathPart.hasNextStep();
	}

	@Override
	public short nextX() {
		return currentPathPart.nextX();
	}

	@Override
	public short nextY() {
		return currentPathPart.nextY();
	}

	@Override
	public ShortPoint2D getNextPos() {
		return currentPathPart.getNextPos();
	}

	@Override
	public boolean isFinished() {
		ensurePath();
		return currentPathPart.isFinished();
	}

	@Override
	public short getTargetX() {
		return abstractPath[abstractPath.length - 2];
	}

	@Override
	public short getTargetY() {
		return abstractPath[abstractPath.length - 1];
	}

	@Override
	public void goToNextStep() {
		ensurePath();
		currentPathPart.goToNextStep();
		overallStepCounter++;
	}

	@Override
	public int getStep() {
		return overallStepCounter;
	}

	@Override
	public boolean hasOverNextStep() {
		ensurePath();
		return currentPathPart.hasOverNextStep();
	}

	@Override
	public ShortPoint2D getOverNextPos() {
		ensurePath();
		return currentPathPart.getOverNextPos();
	}

	@Override
	public String toString() {
		StringBuffer abstractPathString = new StringBuffer();
		for (short idx = 0; idx < abstractPath.length; idx += 2) {
			if (idx == abstractPathCounter) {
				abstractPathString.append(">>");
			}
			abstractPathString.append("(" + abstractPath[idx] + "|" + abstractPath[idx + 1] + ")");
		}

		return "fully calculated path: " + currentPathPart + " abstract path: " + abstractPathString.toString();
	}
}
