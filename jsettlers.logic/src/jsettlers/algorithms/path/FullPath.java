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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.common.position.ShortPoint2D;

/**
 * A path a movable can follow.
 * 
 * @author Andreas Eberle
 * 
 */
public final class FullPath extends Path {
	private static final long serialVersionUID = 1869164120660594918L;

	private transient short[] pathX;
	private transient short[] pathY;

	private transient int idx = -1;

	private void writeObject(ObjectOutputStream oos) throws IOException {
		short[] path = new short[pathX.length * 2];
		for (int i = 0; i < pathX.length; i++) {
			path[2 * i] = pathX[i];
			path[2 * i + 1] = pathY[i];
		}
		oos.writeObject(path);
		oos.writeInt(idx);
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		short[] path = (short[]) ois.readObject();
		idx = ois.readInt();

		pathX = new short[path.length / 2];
		pathY = new short[pathX.length];

		for (int i = 0; i < pathX.length; i++) {
			pathX[i] = path[2 * i];
			pathY[i] = path[2 * i + 1];
		}
	}

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
		int length = (oldPath.getSteps() - (oldPath.getStep() + 1)) + pathPrefix.length;
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

	public FullPath(ShortPoint2D... path) {
		this(path.length);

		for (int i = 0; i < path.length; i++) {
			ShortPoint2D position = path[i];
			insertAt(i, position.x, position.y);
		}
	}

	public FullPath append(FullPath appendix) {
		int thisRemainingLength = getRemainingSteps();
		int appendixRemainingLength = appendix.getRemainingSteps();
		FullPath newPath = new FullPath(thisRemainingLength + appendixRemainingLength);
		System.arraycopy(this.pathX, idx + 1, newPath.pathX, 0, thisRemainingLength);
		System.arraycopy(this.pathY, idx + 1, newPath.pathY, 0, thisRemainingLength);

		System.arraycopy(appendix.pathX, appendix.idx + 1, newPath.pathX, thisRemainingLength, appendixRemainingLength);
		System.arraycopy(appendix.pathY, appendix.idx + 1, newPath.pathY, thisRemainingLength, appendixRemainingLength);

		return newPath;
	}

	@Override
	public Path prependPositions(ShortPoint2D... pathPrefix) {
		return new FullPath(this, pathPrefix);
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
		return getStep() + 1 < getSteps();
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
		return getStep() >= getSteps();
	}

	@Override
	public short getTargetX() {
		return pathX[pathX.length - 1];
	}

	@Override
	public short getTargetY() {
		return pathY[pathY.length - 1];
	}

	public int getRemainingSteps() {
		return pathX.length - idx - 1;
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

	public int getSteps() {
		return pathX.length;
	}

	@Override
	public boolean hasOverNextStep() {
		return getRemainingSteps() >= 2;
	}

	@Override
	public ShortPoint2D getOverNextPos() {
		return new ShortPoint2D(pathX[idx + 2], pathY[idx + 2]);
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		for (short idx = 0; idx < pathX.length; idx++) {
			res.append("(" + pathX[idx] + "|" + pathY[idx] + ")");
		}
		return res.toString();
	}
}
