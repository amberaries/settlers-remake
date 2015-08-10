package jsettlers.algorithms.path.benchmark;

import jsettlers.algorithms.path.benchmark.Benchmark.Benchmarkable;
import jsettlers.algorithms.path.hpastar.HPAStar;
import jsettlers.algorithms.path.hpastar.HPAStarTestGrid;
import jsettlers.algorithms.path.hpastar.graph.HPAStarAbstractedGrid;
import jsettlers.algorithms.path.hpastar.graph.generation.HPAStarAbstractedGridCalculator;
import jsettlers.algorithms.path.hpastar.graph.generation.HPAStarFactoryGrid;
import jsettlers.algorithms.path.hpastar.graph.generation.SparseTransitionsCalculator.SparseTransitionsCalculatorFactory;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;

public class HPAStarBenchmarkable extends Benchmarkable {

	private final HPAStarFactoryGrid grid;
	private final short width;
	private final short height;
	private final int cellSize;

	private HPAStar hpaStar;

	public HPAStarBenchmarkable(HPAStarTestGrid hpaTestGrid, int cellSize) {
		grid = hpaTestGrid;
		width = hpaTestGrid.getWidth();
		height = hpaTestGrid.getHeight();
		this.cellSize = cellSize;
	}

	@Override
	public void prepare() {
		HPAStarAbstractedGridCalculator hpaStarGridCalculator = new HPAStarAbstractedGridCalculator(grid, width, height,
				// new SparseTransitionsCalculatorFactory());
				new SparseTransitionsCalculatorFactory());
		HPAStarAbstractedGrid abstractedGrid = hpaStarGridCalculator.calculateAbstractedGrid(cellSize);
		hpaStar = new HPAStar(abstractedGrid, null, grid, width, height); // TODO supply aStar to HPAStar
	}

	@Override
	public void executeChallenge(Tuple<ShortPoint2D, ShortPoint2D> challenge) {
		hpaStar.findPath(challenge.e1.x, challenge.e1.y, challenge.e2.x, challenge.e2.y, false, (byte) -1);
	}

	@Override
	public String toString() {
		return "HPAStar(cellSize=" + cellSize + ")";
	}
}
