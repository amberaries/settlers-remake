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

	private transient short[] path;
	private transient int idx = -2;

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject(path);
		oos.writeInt(getStep()); // serialize the path's step
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		path = (short[]) ois.readObject();
		idx = 2 * ois.readInt();
	}

	public FullPath(int length) {
		path = new short[2 * length];
	}

	/**
	 * Concatenates a path and a prefix of {@link ShortPoint2D} objects.
	 * 
	 * @param pathPrefix
	 *            The path prefix. NOTE: The prefix must start with the current position of the movable!
	 * @param oldPath
	 *            The path to be appended to the prefix.
	 */
	private FullPath(ShortPoint2D[] pathPrefix, FullPath oldPath) {
		this((oldPath.getSteps() - (oldPath.getStep() + 1)) + pathPrefix.length);

		for (int i = 0; i < pathPrefix.length; i++) {
			insertAt(i, pathPrefix[i].x, pathPrefix[i].y);
		}

		System.arraycopy(oldPath.path, oldPath.idx + 2, this.path, 2 * pathPrefix.length, 2 * oldPath.getRemainingSteps());
	}

	public FullPath(ShortPoint2D... path) {
		this(path.length);

		for (int i = 0; i < path.length; i++) {
			ShortPoint2D position = path[i];
			insertAt(i, position.x, position.y);
		}
	}

	@Override
	public Path prependPositions(ShortPoint2D... pathPrefix) {
		return new FullPath(pathPrefix, this);
	}

	public FullPath append(FullPath appendix) {
		int thisRemainingSteps = getRemainingSteps();
		int appendixRemainingSteps = appendix.getRemainingSteps();
		FullPath newPath = new FullPath(thisRemainingSteps + appendixRemainingSteps);

		System.arraycopy(this.path, idx + 2, newPath.path, 0, 2 * thisRemainingSteps);
		System.arraycopy(appendix.path, appendix.idx + 2, newPath.path, 2 * thisRemainingSteps, 2 * appendixRemainingSteps);

		return newPath;
	}

	/**
	 * sets the given position to the given index of the path
	 * 
	 * @param index
	 *            NOTE: this must be in the integer interval [0, pathlength -1]!
	 * @param x
	 *            x position of the step
	 * @param y
	 *            y position of the step
	 */
	public void insertAt(int index, short x, short y) {
		path[2 * index] = x;
		path[2 * index + 1] = y;
	}

	@Override
	public boolean hasNextStep() {
		return getStep() + 1 < getSteps();
	}

	@Override
	public short nextX() {
		return path[idx + 2];
	}

	@Override
	public short nextY() {
		return path[idx + 3];
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
		return path[path.length - 2];
	}

	@Override
	public short getTargetY() {
		return path[path.length - 1];
	}

	public int getRemainingSteps() {
		return getSteps() - getStep() - 1;
	}

	/**
	 * increases the path counter
	 */
	@Override
	public void goToNextStep() {
		idx += 2;
	}

	@Override
	public int getStep() {
		return idx / 2;
	}

	public int getSteps() {
		return path.length / 2;
	}

	@Override
	public boolean hasOverNextStep() {
		return getRemainingSteps() >= 2;
	}

	@Override
	public ShortPoint2D getOverNextPos() {
		return new ShortPoint2D(path[idx + 4], path[idx + 5]);
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		for (short idx = 0; idx < path.length; idx += 2) {
			res.append("(" + path[idx] + "|" + path[idx + 1] + ")");
		}
		return res.toString();
	}
}
