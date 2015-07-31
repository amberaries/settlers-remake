package jsettlers.algorithms.path.benchmark;

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

	public static void main(String args[]) throws MapLoadException {
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

	private static void benchmark(Tuple<ShortPoint2D, ShortPoint2D>[] pathChallenges, Benchmarkable[] benchmarkables) {

		for (Benchmarkable benchmarkable : benchmarkables) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}

			System.out.println("\nBenchmarking " + benchmarkable + " .....");

			MilliStopWatch watch = new MilliStopWatch();
			benchmarkable.prepare();
			watch.stop(benchmarkable + ": preparation needed");

			watch = new MilliStopWatch();
			for (Tuple<ShortPoint2D, ShortPoint2D> challenge : pathChallenges) {
				benchmarkable.executeChallenge(challenge);
			}
			watch.stop(benchmarkable + ": paths: " + pathChallenges.length + " needed");
		}
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
