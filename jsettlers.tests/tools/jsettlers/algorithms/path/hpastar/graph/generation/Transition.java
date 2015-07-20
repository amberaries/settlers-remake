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
package jsettlers.algorithms.path.hpastar.graph.generation;

import java.util.ArrayList;
import java.util.List;

import jsettlers.algorithms.path.hpastar.graph.Vertex;
import jsettlers.common.position.ShortPoint2D;

public class Transition extends ShortPoint2D {
	private static final long serialVersionUID = -2605651034142074418L;

	private final List<Transition> neighbors;
	private final Vertex vertex;

	public Transition(int x, int y) {
		super(x, y);
		neighbors = new ArrayList<Transition>();
		vertex = new Vertex(x, y);
	}

	public void addEdge(Transition v) {
		this.neighbors.add(v);
		v.neighbors.add(this);
	}

	public List<Transition> getNeighbors() {
		return neighbors;
	}

	public Vertex getVertex() {
		return vertex;
	}
}
