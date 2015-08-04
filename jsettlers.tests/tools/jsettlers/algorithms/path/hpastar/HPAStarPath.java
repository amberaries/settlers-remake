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

import jsettlers.algorithms.path.FullPath;
import jsettlers.algorithms.path.Path;
import jsettlers.common.position.ShortPoint2D;

class HPAStarPath extends Path {
	private static final long serialVersionUID = -7950105340565322678L;

	private final short[] abstractPath;
	private FullPath path;

	public HPAStarPath(int length) {
		abstractPath = new short[2 * length];
	}

	public void insertAbstractPosition(int index, short x, short y) {
		int twoTimesIndex = 2 * index;
		abstractPath[twoTimesIndex] = x;
		abstractPath[twoTimesIndex + 1] = y;
	}

	private void ensurePath() {

	}

	@Override
	public Path prependPositions(ShortPoint2D... pathPrefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNextStep() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public short nextX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short nextY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ShortPoint2D getNextPos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getFirstX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getFirstY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getTargetX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getTargetY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void goToNextStep() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getStep() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasOverNextStep() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ShortPoint2D getOverNextPos() {
		// TODO Auto-generated method stub
		return null;
	}
}
