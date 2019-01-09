package algorithms;

import java.util.LinkedList;
import java.util.Random;
import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;

import optimization.Configuration;
import optimization.OptimizationAlgorithm;

/**
 * Implements random search. Generates several random configurations and stores
 * the best.
 */
public class GeneticAlgorithm extends OptimizationAlgorithm {

	/** Size of the population */
	private int popSize;
	/** Number of generations */
	private int generations;
	/** Probability of crossover */
	private double probCrossover;
	/** Probability of mutation */
	private double probMutation;

	@Override
	public void search() {
		// Algorithms must call this function always!
		initSearch();

		// Populations.
		LinkedList<Configuration> population = new LinkedList<>();
		LinkedList<Configuration> selectedPopulation;
		// Generates "popSize" configurations.
		population = generateRandomPopulation();
		// Evaluates the configurations.
		evaluatePopulation(population);
		for (int iteration = 0; iteration < generations; iteration++) {
			selectedPopulation = selectPopulation(population);
			crossover(selectedPopulation);
			mutate(selectedPopulation);
			evaluatePopulation(selectedPopulation);
			//Use replacement of the populations
			population = selectedPopulation;
		}
		// Algorithms must call this function always!
		stopSearch();
	}


	// Changes the position of 2 cities if it mutates
	private void mutate(LinkedList<Configuration> selectedPopulation) {
		int x, y, aux;
		int[] values;
		Random rand = new Random();
		LinkedList<Configuration> mutatedPopulation = new LinkedList<>();
		for (Configuration conf : selectedPopulation) {
			if (Math.random() < probMutation) {
				x = rand.nextInt(problem.size());
				y = rand.nextInt(problem.size());
				values = conf.getValues().clone();
				aux = values[x];
				values[x] = values[y];
				values[y] = aux;
				mutatedPopulation.add(new Configuration(values));
			}
			else {
				mutatedPopulation.add(conf);
			}
		}
	}

	// Performs 2PCS
	private void crossover(LinkedList<Configuration> selectedPopulation) {
		Configuration parent1;
		Configuration parent2;
		Configuration child1;
		Configuration child2;
		int[] values1 = new int[problem.size()];
		int[] values2 = new int[problem.size()];
		ArrayList<Integer> ordered1;
		ArrayList<Integer> ordered2;
		ArrayList<Integer> valuesToOrder1;
		ArrayList<Integer> valuesToOrder2;
		Random rand = new Random();
		int l;
		int r;
		for (int i = 0; i < popSize / 2; i++) {
			if (Math.random() < probCrossover) {
				parent1 = selectedPopulation.get(0);
				parent2 = selectedPopulation.get(1);
				values1 = parent1.getValues().clone();
				values2 = parent2.getValues().clone();
				ordered1 = new ArrayList<>();
				ordered2 = new ArrayList<>();
				valuesToOrder1 = new ArrayList<>();
				valuesToOrder2 = new ArrayList<>();
				// Generates values for the 2PCS
				l = 0;
				r = 0;
				while (l >= r) {
					l = rand.nextInt(problem.size() - 1) + 1;
					r = rand.nextInt(problem.size() - 1) + 1;
				}
				// Extracts the values we need to order
				for (int j = l; j < r; j++) {
					valuesToOrder1.add(values1[j]);
					valuesToOrder2.add(values2[j]);
				}
				// Sort the values in the same order as in parent 2
				while (!valuesToOrder1.isEmpty()) {
					for (int j = 0; j < values2.length; j++) {
						if (valuesToOrder1.contains(values2[j])) {
							valuesToOrder1.remove(new Integer(values2[j]));
							ordered1.add(values2[j]);
						}
					}
				}
				// Sort the values in the same order as in parent 1
				while (!valuesToOrder2.isEmpty()) {
					for (int j = 0; j < values1.length; j++) {
						if (valuesToOrder2.contains(values1[j])) {
							valuesToOrder2.remove(new Integer(values1[j]));
							ordered2.add(values1[j]);
						}
					}
				}
				// Updates the previous values with the ordered ones
				for (int j = l; j < r; j++) {
					valuesToOrder1.add(values1[j]);
					valuesToOrder2.add(values2[j]);
				}

				// Replace the values with the ordered numbers
				for (int j = l; j < r; j++) {
					values1[j] = ordered1.get(j - l);
					values2[j] = ordered2.get(j - l);
				}

				child1 = new Configuration(values1);
				child2 = new Configuration(values2);

				// Updates the selected population
				selectedPopulation.removeFirst();
				selectedPopulation.removeFirst();
				selectedPopulation.add(child1);
				selectedPopulation.add(child2);
			}
			// Moves the parent to the end
			else {
				parent1 = selectedPopulation.get(0);
				parent2 = selectedPopulation.get(1);
				selectedPopulation.removeFirst();
				selectedPopulation.removeFirst();
				selectedPopulation.add(parent1);
				selectedPopulation.add(parent2);
			}
		}
	}

	// Rank-based selection
	private LinkedList<Configuration> selectPopulation(LinkedList<Configuration> population) {
		double rankSum = 0;
		double random;
		LinkedList<Configuration> selectedPopulation = new LinkedList<>();
		for (int i = 0; i < popSize; i++) {
			rankSum += i+1;
		}
		// Orders the population by ascending score
		Collections.sort(population);
		// Creates probabilities for each rank (cumulatives)
		ArrayList<Double> probabilities = new ArrayList<>();
		probabilities.add((popSize) / rankSum);
		for (int i = 1; i < popSize; i++) {
			probabilities.add((popSize - i) / rankSum + probabilities.get(i-1));
		}
		
		// adds the "popSize" elements obtain by randoms
		for (int i = 0; i < popSize; i++) {
			int size = selectedPopulation.size();
			random = Math.random();
			for (int j = 0; j < popSize; j++) {
				if (probabilities.get(j) > random) {
					selectedPopulation.add(population.get(j));
					break;
				}
			}
		}
		return selectedPopulation;
	}

	private void evaluatePopulation(LinkedList<Configuration> population) {
		for (Configuration conf : population) {
			evaluate(conf);
		}
	}

	private LinkedList<Configuration> generateRandomPopulation() {
		LinkedList<Configuration> randomPopulation = new LinkedList<>();
		for (int i = 0; i < popSize; i++) {
			randomPopulation.add(problem.genRandomConfiguration());
		}
		return randomPopulation;
	}

	/**
	 * Displays the statistics of the search. In this case, only the number of
	 * random solutions that have been generated.
	 */
	@Override
	public void showAlgorithmStats() {
		// For this algorithm, it does not show any additional information.
	}

	/**
	 * In this algorithm, the only parameter is the number of generated solutions.
	 */
	@Override
	public void setParams(String[] args) {
		if (args.length > 3) {
			try {
				popSize = Integer.parseInt(args[0]);
				generations = Integer.parseInt(args[1]);
				probCrossover = Double.parseDouble(args[2]);
				probMutation = Double.parseDouble(args[3]);

				if (popSize % 2 == 1) {
					popSize++;
				}
			} catch (Exception e) {
				System.out.println("Generating 1000 random solutions (\"default\").");
			}
		}
	}

}
