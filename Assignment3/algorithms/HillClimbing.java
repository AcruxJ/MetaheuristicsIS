package algorithms;

import java.util.HashSet;

import optimization.Configuration;
import optimization.OptimizationAlgorithm;

/** 
 * Implements random search. Generates several random 
 * configurations and stores the best.
 */
public class HillClimbing extends OptimizationAlgorithm {

	@Override
	public void search() {
		// Algorithms must call this function always!
		initSearch();
		
		// Generates all the configurations.
		bestSolution= problem.genRandomConfiguration();
		bestScore = evaluate(bestSolution);
		boolean improves = true;
		HashSet<Configuration> neighbours = new HashSet<>();
		
		while (improves){
			improves = false;
			neighbours = generateNeighbours(bestSolution);
		}
		
		// Algorithms must call this function always!
		stopSearch();
	}

	private HashSet<Configuration> generateNeighbours(Configuration conf) {
		HashSet<Configuration> neighbours = new HashSet<>();
		int[] values= conf.getValues();
		for (int i = 0; i < values.length; i++) {
			for (int j = i+1; j < values.length; j++) {
				values= conf.getValues();
				values[i] = conf.getValues()[j];
				values[j] = conf.getValues()[i];
				neighbours.add(new Configuration(values));
			}
		}
		return neighbours;
	}

	/** Displays the statistics of the search. In this case, only the number of random 
	 *  solutions that have been generated. */
	@Override
	public void showAlgorithmStats() {
		// For this algorithm, it does  not show any additional information.
	}

	/** In this algorithm, the only parameter is the number of generated solutions.*/
	@Override
	public void setParams(String[] args) {
		//No params
	}

}
