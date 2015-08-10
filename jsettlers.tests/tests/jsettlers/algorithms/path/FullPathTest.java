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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Assume;
import org.junit.Test;

public class FullPathTest {

	@Test
	public void testStepping1() {
		FullPath path = createPath(1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6);

		assertPathSteps(path, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6);
		assertFalse(path.hasNextStep());
	}

	@Test
	public void testStepping2() {
		FullPath path = createPath(1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6);

		assertPathSteps(path, 1, 1, 2, 2, 3, 3);

		assertTrue(path.hasNextStep());
		assertEquals(new ShortPoint2D(4, 4), path.getNextPos());
		path.goToNextStep();

		assertPathSteps(path, 5, 5, 6, 6);
		assertFalse(path.hasNextStep());
	}

	@Test
	public void testAppendNewPathToNewPath() {
		FullPath path1 = createPath(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		FullPath path2 = createPath(11, 11, 12, 12, 13, 13, 14, 14, 15, 15);
		FullPath path = path1.append(path2);

		assertPathSteps(path, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15);
		assertFalse(path.hasNextStep());
	}

	@Test
	public void testAppendStartedPathToStartedPath() {
		FullPath path1 = createPath(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		assertPathSteps(path1, 1, 1, 2, 2);

		FullPath path2 = createPath(11, 11, 12, 12, 13, 13, 14, 14, 15, 15);
		assertPathSteps(path2, 11, 11);

		FullPath path = path1.append(path2);
		assertPathSteps(path, 3, 3, 4, 4, 5, 5, 12, 12, 13, 13, 14, 14, 15, 15);

		assertFalse(path.hasNextStep());
	}

	@Test
	public void testAppendStartedPathToFinishedPath() {
		FullPath path1 = createPath(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		assertPathSteps(path1, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);

		FullPath path2 = createPath(11, 11, 12, 12, 13, 13, 14, 14, 15, 15);
		assertPathSteps(path2, 11, 11);

		FullPath path = path1.append(path2);
		assertPathSteps(path, 12, 12, 13, 13, 14, 14, 15, 15);

		assertFalse(path.hasNextStep());
	}

	@Test
	public void testAppendFinishedPathToStartedPath() {
		FullPath path1 = createPath(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		assertPathSteps(path1, 1, 1, 2, 2, 3, 3);

		FullPath path2 = createPath(11, 11, 12, 12, 13, 13, 14, 14, 15, 15);
		assertPathSteps(path2, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15);

		FullPath path = path1.append(path2);
		assertPathSteps(path, 4, 4, 5, 5);

		assertFalse(path.hasNextStep());
	}

	@Test
	public void testPrependToNewPath() {
		FullPath path1 = createPath(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		Path path = path1.prependPositions(new ShortPoint2D(-2, -2), new ShortPoint2D(-1, -1), new ShortPoint2D(0, 0));

		assertPathSteps(path, -2, -2, -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		assertFalse(path.hasNextStep());
	}

	@Test
	public void testPrependToStartedPath() {
		FullPath path1 = createPath(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		assertPathSteps(path1, 1, 1, 2, 2, 3, 3);
		Path path = path1.prependPositions(new ShortPoint2D(-2, -2), new ShortPoint2D(-1, -1), new ShortPoint2D(0, 0));

		assertPathSteps(path, -2, -2, -1, -1, 0, 0, 4, 4, 5, 5);
		assertFalse(path.hasNextStep());
	}

	@Test
	public void testPrependToFinishedPath() {
		FullPath path1 = createPath(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		assertPathSteps(path1, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		Path path = path1.prependPositions(new ShortPoint2D(-2, -2), new ShortPoint2D(-1, -1), new ShortPoint2D(0, 0));

		assertPathSteps(path, -2, -2, -1, -1, 0, 0);
		assertFalse(path.hasNextStep());
	}

	@Test
	public void testPrependNothing() {
		FullPath path1 = createPath(1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		assertPathSteps(path1, 1, 1, 2, 2, 3, 3);
		Path path = path1.prependPositions();

		assertPathSteps(path, 4, 4, 5, 5);
		assertFalse(path.hasNextStep());
	}

	private void assertPathSteps(Path path, int... expectedPositions) {
		Assume.assumeTrue("You need to supply enough path positions!", expectedPositions.length % 2 == 0);

		for (int i = 0; i < expectedPositions.length; i += 2) {
			assertPathStep(path, expectedPositions[i], expectedPositions[i + 1]);
		}
	}

	private void assertPathStep(Path path, int x, int y) {
		assertTrue(path.hasNextStep());
		assertEquals(x, path.nextX());
		assertEquals(y, path.nextY());
		path.goToNextStep();
	}

	private FullPath createPath(int... pathPositions) {
		Assume.assumeTrue("You need to supply enough path positions!", pathPositions.length % 2 == 0);
		ShortPoint2D[] pathPoints = new ShortPoint2D[pathPositions.length / 2];
		for (int i = 0; i < pathPoints.length; i++) {
			pathPoints[i] = new ShortPoint2D(pathPositions[2 * i], pathPositions[2 * i + 1]);
		}
		return new FullPath(pathPoints);
	}
}