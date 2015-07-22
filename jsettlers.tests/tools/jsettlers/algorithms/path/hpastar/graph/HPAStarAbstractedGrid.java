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
package jsettlers.algorithms.path.hpastar.graph;

import java.util.HashMap;
import java.util.List;

import jsettlers.common.position.ShortPoint2D;

public class HPAStarAbstractedGrid {

	private final HashMap<ShortPoint2D, List<Vertex>> vertexGrid;
	private final HashMap<ShortPoint2D, Vertex> vertices;
	private final int cellSize;

	public HPAStarAbstractedGrid(HashMap<ShortPoint2D, List<Vertex>> vertexGrid, HashMap<ShortPoint2D, Vertex> vertices, int cellSize) {
		this.vertexGrid = vertexGrid;
		this.vertices = vertices;
		this.cellSize = cellSize;
	}

	public ShortPoint2D getCell(int x, int y) {
		return new ShortPoint2D(x / cellSize, y / cellSize);
	}

	public ShortPoint2D getMinCorner(ShortPoint2D cell) {
		return cell.multiply(cellSize);
	}

	public ShortPoint2D getMaxCorner(ShortPoint2D cell) {
		return cell.multiply(cellSize).add(cellSize - 1);
	}

	public List<Vertex> getCellVertices(ShortPoint2D cell) {
		return vertexGrid.get(cell);
	}

	public Vertex getVertex(ShortPoint2D position) {
		return vertices.get(position);
	}
}
