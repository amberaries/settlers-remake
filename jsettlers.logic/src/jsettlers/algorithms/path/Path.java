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

import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;

/**
 * A path a movable can follow.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class Path implements Serializable {
	private static final long serialVersionUID = 1869164120660594918L;

	/**
	 * Adds the given prefix to this path.
	 * 
	 * @param pathPrefix
	 *            The path prefix. NOTE: The prefix must start with the current position of the movable!
	 */
	public abstract Path prependPositions(ShortPoint2D... pathPrefix);

	/**
	 * Creates a path of length 1 with that's just containing to the given position.<br>
	 * 
	 * @param position
	 *            the single path position.
	 */
	public static Path createPath(ShortPoint2D position) {
		FullPath path = new FullPath(1);
		path.insertAt(0, position.x, position.y);
		return path;
	}

	public abstract boolean hasNextStep();

	public abstract short nextX();

	public abstract short nextY();

	public abstract ShortPoint2D getNextPos();

	public abstract boolean isFinished();

	@Override
	public abstract String toString();

	public abstract short getFirstX();

	public abstract short getFirstY();

	public abstract short getTargetX();

	public abstract short getTargetY();

	public abstract void goToNextStep();

	public abstract int getStep();

	public abstract boolean hasOverNextStep();

	public abstract ShortPoint2D getOverNextPos();

	public final ShortPoint2D getFirstPos() {
		return new ShortPoint2D(getFirstX(), getFirstY());
	}

	public final ShortPoint2D getTargetPos() {
		return new ShortPoint2D(getTargetX(), getTargetY());
	}
}
