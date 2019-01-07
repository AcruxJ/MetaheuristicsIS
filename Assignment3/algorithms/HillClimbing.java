package algorithms;

import java.awt.List;
import java.util.HashSet;
import java.util.LinkedList;

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
		Configuration initSolution= problem.genRandomConfiguration();
		double currentscore = evaluate(initSolution);
		boolean improves = true;
		LinkedList<Configuration> neighbours = new LinkedList<>();
		while (improves){
			improves = false;
			neighbours = generateNeighbours(bestSolution);
			for(Configuration neighbour : neighbours) {
				double newscore = evaluate(neighbour);
				if(newscore<currentscore) {
					currentscore=newscore;
					improves = true;
				}
			}
		}
		// Algorithms must call this function always!
		stopSearch();
	}

	private LinkedList<Configuration> generateNeighbours(Configuration conf) {
		LinkedList<Configuration> neighbours = new LinkedList<>();
		int[] values= conf.getValues().clone();
		for (int i = 0; i < values.length; i++) {
			for (int j = i+1; j < values.length; j++) {
				values = conf.getValues().clone();
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
