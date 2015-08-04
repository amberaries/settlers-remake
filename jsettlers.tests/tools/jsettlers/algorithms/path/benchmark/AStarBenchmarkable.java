package jsettlers.algorithms.path.benchmark;

import jsettlers.algorithms.path.astar.BucketQueueAStar;
import jsettlers.algorithms.path.astar.IAStarPathMap;
import jsettlers.algorithms.path.benchmark.Benchmark.Benchmarkable;
import jsettlers.algorithms.path.hpastar.HPAStarTestGrid;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;

public class AStarBenchmarkable extends Benchmarkable {

	private final IAStarPathMap map;
	private final short width;
	private final short height;

	private BucketQueueAStar aStar;

	public AStarBenchmarkable(HPAStarTestGrid grid) {
		this.map = grid.getAStarMap();
		this.width = grid.getWidth();
		this.height = grid.getHeight();
	}

	@Override
	public void prepare() {
		this.aStar = new BucketQueueAStar(map, width, height);
	}

	@Override
	public void executeChallenge(Tuple<ShortPoint2D, ShortPoint2D> challenge) {
		aStar.findPath(challenge.e1, challenge.e2, false, (byte) -1);
	}

	@Override
	public String toString() {
		return "AStar";
	}
}
