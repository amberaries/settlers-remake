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
package jsettlers.algorithms.path.benchmark;

import java.io.IOException;
import java.util.Random;

import jsettlers.TestUtils;
import jsettlers.algorithms.path.astar.Heuristics;
import jsettlers.algorithms.path.hpastar.HPAStarTestGrid;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.MainGridDataAccessor;

public class Benchmark {

	public static void main(String args[]) throws MapLoadException, IOException {
		HPAStarTestGrid.ENABLE_DEBUG_COLORS = false;
		final HPAStarTestGrid grid = getGridByMapName("big map");

		Benchmarkable[] benchmarkables = new Benchmarkable[] {
				new AStarBenchmarkable(grid),
				new HPAStarBenchmarkable(grid, 10),
				new HPAStarBenchmarkable(grid, 15),
				new HPAStarBenchmarkable(grid, 16),
				new HPAStarBenchmarkable(grid, 17),
				new HPAStarBenchmarkable(grid, 20),
				new HPAStarBenchmarkable(grid, 32),
				new HPAStarBenchmarkable(grid, 64),
		};

		Tuple<ShortPoint2D, ShortPoint2D>[] pathChallenges = generateChallenges(grid, 5000);
		benchmark(pathChallenges, benchmarkables);

		System.exit(0);
	}

	private static void benchmark(Tuple<ShortPoint2D, ShortPoint2D>[] pathChallenges, Benchmarkable[] benchmarkables) throws IOException {
		BenchmarkLogger logger = new BenchmarkLogger();

		for (Benchmarkable benchmarkable : benchmarkables) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}

			System.out.println("\nBenchmarking " + benchmarkable + " .....");

			MilliStopWatch watch = new MilliStopWatch();
			benchmarkable.prepare();
			long duration = watch.stop(benchmarkable + ": preparation needed");
			logger.logData(benchmarkable, "preparation", (int) duration);

			watch = new MilliStopWatch();
			MilliStopWatch shortWatch = new MilliStopWatch();
			for (Tuple<ShortPoint2D, ShortPoint2D> challenge : pathChallenges) {
				benchmarkable.executeChallenge(challenge);

				logger.logData(benchmarkable, "executeChallenge", (int) shortWatch.restart(),
						Heuristics.getHexGridNoObstaclesDistance(challenge.e1.x, challenge.e1.y, challenge.e2.x, challenge.e2.y));
			}
			duration = watch.stop(benchmarkable + ": paths: " + pathChallenges.length + " needed");
			logger.logData(benchmarkable, "overallChallenge", (int) duration);
		}

		logger.close();
	}

	private static Tuple<ShortPoint2D, ShortPoint2D>[] generateChallenges(HPAStarTestGrid grid, int numberOfChallenges) {
		final Random r = new Random(1);
		@SuppressWarnings("unchecked")
		Tuple<ShortPoint2D, ShortPoint2D>[] pathChallenges = new Tuple[numberOfChallenges];

		for (int i = 0; i < numberOfChallenges; i++) {
			short sx = (short) r.nextInt(grid.getWidth());
			short sy = (short) r.nextInt(grid.getHeight());
			short tx = (short) r.nextInt(grid.getWidth());
			short ty = (short) r.nextInt(grid.getHeight());

			if (!grid.isBlocked(sx, sy) && !grid.isBlocked(tx, ty) && grid.getBlockedPartition(sx, sy) == grid.getBlockedPartition(tx, ty)
					&& Heuristics.getHexGridNoObstaclesDistance(sx, sy, tx, ty) > 400) {
				pathChallenges[i] = new Tuple<>(new ShortPoint2D(sx, sy), new ShortPoint2D(tx, ty));
			} else {
				i--; // this wasn't a valid path, try again
			}
		}
		return pathChallenges;
	}

	private static HPAStarTestGrid getGridByMapName(String mapName) throws MapLoadException {
		MainGrid mainGrid = TestUtils.getGridByMapName(mapName);
		return new HPAStarTestGrid(new MainGridDataAccessor(mainGrid));
	}

	public static abstract class Benchmarkable {
		public abstract void prepare();

		public abstract void executeChallenge(Tuple<ShortPoint2D, ShortPoint2D> challenge);

		@Override
		public abstract String toString();
	}
}
