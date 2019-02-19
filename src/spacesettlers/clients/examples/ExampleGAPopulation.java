package spacesettlers.clients.examples;

import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * Stores a whole population of individuals for genetic algorithms / evolutionary computation
 * 
 * @author amy
 *
 */
public class ExampleGAPopulation {
	private ExampleGAChromosome[] population;
	
	private int currentPopulationCounter;
	
	private double[] fitnessScores;

	/**
	 * Make a new empty population
	 */
	public ExampleGAPopulation(int populationSize) {
		super();
		
		// start at member zero
		currentPopulationCounter = 0;
		
		// make an empty population
		population = new ExampleGAChromosome[populationSize];
		
		for (int i = 0; i < populationSize; i++) {
			population[i] = new ExampleGAChromosome();
		}
		
		// make space for the fitness scores
		fitnessScores = new double[populationSize];
	}

	/**
	 * Currently scores all members as zero (the student must implement this!)
	 * 
	 * @param space
	 */
	public void evaluateFitnessForCurrentMember(Toroidal2DPhysics space) {
		fitnessScores[currentPopulationCounter] = 0;
	}

	/**
	 * Return true if we have reached the end of this generation and false otherwise
	 * 
	 * @return
	 */
	public boolean isGenerationFinished() {
		if (currentPopulationCounter == population.length) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the next member of the population (handles wrapping around by going
	 * back to the start but then the assumption is that you will reset with crossover/selection/mutation
	 * 
	 * @return
	 */
	public ExampleGAChromosome getNextMember() {
		currentPopulationCounter++;
		
		return population[currentPopulationCounter % population.length];
	}

	/**
	 * Does crossover, selection, and mutation using our current population.
	 * Note, none of this is implemented as it is up to the student to implement it.
	 * Right now all it does is reset the counter to the start.
	 */
	public void makeNextGeneration() {
		currentPopulationCounter = 0;
	}

	/**
	 * Return the first member of the popualtion
	 * @return
	 */
	public ExampleGAChromosome getFirstMember() {
		return population[0];
	}
}
	

