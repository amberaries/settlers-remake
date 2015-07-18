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
package jsettlers.logic.buildings.military.occupying;

import java.io.Serializable;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;

public final class DijkstraContinuableRequest implements Serializable {
	private static final long serialVersionUID = -1350601280043056439L;

	final short minRadius;
	final short maxRadius;
	final short cX;
	final short cY;
	ESearchType searchType;

	short radius;

	public DijkstraContinuableRequest(short cX, short cY, short minRadius, short maxRadius, ESearchType searchType) {
		this.cX = cX;
		this.cY = cY;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.searchType = searchType;

		this.radius = 0;
	}

	public DijkstraContinuableRequest(short cX, short cY, short minRadius, short maxRadius) {
		this(cX, cY, minRadius, maxRadius, null);
	}

	final short getRadiusSteps() {
		return 3;
	}

	public boolean isCenterAt(ShortPoint2D pos) {
		return pos != null && pos.x == cX && pos.y == cY;
	}

	void setRadius(short radius) {
		this.radius = (short) (radius - this.minRadius + 1);
	}

	public void setSearchType(ESearchType searchType) {
		this.searchType = searchType;
	}
}