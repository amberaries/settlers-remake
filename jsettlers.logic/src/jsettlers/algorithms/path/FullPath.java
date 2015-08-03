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
package jsettlers.algorithms.path;

import jsettlers.common.position.ShortPoint2D;

/**
 * A path a movable can follow.
 * 
 * @author Andreas Eberle
 * 
 */
public final class FullPath extends Path {
	private static final long serialVersionUID = 1869164120660594918L;

	private final short[] pathX;
	private final short[] pathY;

	private int idx = -1;

	public FullPath(int length) {
		pathX = new short[length];
		pathY = new short[length];
	}

	/**
	 * Concatenates a path and a prefix of {@link ShortPoint2D} objects.
	 * 
	 * @param oldPath
	 *            The path to be appended to the prefix.
	 * @param pathPrefix
	 *            The path prefix. NOTE: The prefix must start with the current position of the movable!
	 */
	public FullPath(FullPath oldPath, ShortPoint2D... pathPrefix) {
		int length = (oldPath.getLength() - (oldPath.idx + 1)) + pathPrefix.length;
		pathX = new short[length];
		pathY = new short[length];

		int i;
		for (i = 0; i < pathPrefix.length; i++) {
			insertAt(i, pathPrefix[i].x, pathPrefix[i].y);
		}

		for (; i < length; i++) {
			insertAt(i, oldPath.nextX(), oldPath.nextY());
			oldPath.goToNextStep();
		}
	}

	/**
	 * Creates a path of length 1 with that's just containing to the given position.<br>
	 * 
	 * @param position
	 *            the single path position.
	 */
	public FullPath(ShortPoint2D position) {
		this(1);
		insertAt(0, position.x, position.y);
	}

	/**
	 * sets the given position to the given index of the path
	 * 
	 * @param idx
	 *            NOTE: this must be in the integer interval [0, pathlength -1]!
	 * @param x
	 *            x position of the step
	 * @param y
	 *            y position of the step
	 */
	public void insertAt(int idx, short x, short y) {
		pathX[idx] = x;
		pathY[idx] = y;
	}

	@Override
	public boolean hasNextStep() {
		return idx + 1 < pathX.length;
	}

	@Override
	public short nextX() {
		return pathX[idx + 1];
	}

	@Override
	public short nextY() {
		return pathY[idx + 1];
	}

	@Override
	public ShortPoint2D getNextPos() {
		return new ShortPoint2D(nextX(), nextY());
	}

	@Override
	public boolean isFinished() {
		return idx >= pathX.length;
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		for (short idx = 0; idx < pathX.length; idx++) {
			res.append("(" + pathX[idx] + "|" + pathY[idx] + ")");
		}
		return res.toString();
	}

	@Override
	public short getFirstX() {
		return pathX[0];
	}

	@Override
	public short getFirstY() {
		return pathY[0];
	}

	@Override
	public short getTargetX() {
		return pathX[pathX.length - 1];
	}

	@Override
	public short getTargetY() {
		return pathY[pathY.length - 1];
	}

	public int getLength() {
		return pathX.length;
	}

	/**
	 * increases the path counter
	 */
	@Override
	public void goToNextStep() {
		idx++;
	}

	@Override
	public int getStep() {
		return idx;
	}

	@Override
	public boolean hasOverNextStep() {
		return idx + 2 < pathX.length;
	}

	@Override
	public ShortPoint2D getOverNextPos() {
		return new ShortPoint2D(pathX[idx + 2], pathY[idx + 2]);
	}

	@Override
	public Path prependPositions(ShortPoint2D... pathPrefix) {
		return new FullPath(this, pathPrefix);
	}
}
