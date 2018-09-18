
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.lang.Math;
import java.util.Random;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.net.URL;
import java.net.URLClassLoader;


public class player34 implements ContestSubmission
{
	private static final int POPULATION_SIZE = 30;
	private static final int PROBLEM_DIMENSIONALITY = 10;
	private static final int PROBLEM_RANGE_MIN = -5;
	private static final int PROBLEM_RANGE_MAX = 5;
	private static final int NUMBER_OF_PARENTS_EACH_GEN = 5;

	Random rnd_;
	ContestEvaluation evaluation_;
    private int evaluations_limit_;
	
	public player34()
	{
		rnd_ = new Random();
	}
	
	public void setSeed(long seed)
	{
		// Set seed of algortihms random process
		rnd_.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation)
	{
		// Set evaluation problem used in the run
		evaluation_ = evaluation;
		
		// Get evaluation properties
		Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

		// Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }
    }

    private List<Individual> initPopulation ()
    {
    	List<Individual> population = new ArrayList<Individual>();
    	for (int i=0; i < POPULATION_SIZE; i++) {
    		Individual newGuy = new Individual();
    		newGuy.genes = new double[PROBLEM_DIMENSIONALITY];
			for (int j=0; j<newGuy.genes.length; j++) {
    			double allele = rnd_.nextDouble() * (PROBLEM_RANGE_MAX - PROBLEM_RANGE_MIN);
    			newGuy.genes[j] = allele;
    		}
    		population.add(newGuy);
    	}
    	return population;
    }

    private void printPopulation (List<Individual> population) { printPopulation(population, false); }
    private void printPopulation (List<Individual> population, boolean withGenes)
    {
    	System.out.println("Population:");
    	for (Individual individual : population) {
    		if (withGenes) {
    			System.out.printf("Fitness: %g Genes: %s\n", individual.fitness, Arrays.toString(individual.genes));
    		} else {
    			System.out.printf("Fitness: %g\n", individual.fitness);
    		}
    	}
    }

    private void evalIndividuals (List<Individual> individuals)
    {
    	for (Individual individual : individuals) {
    		individual.fitness = Math.max(0, (double) evaluation_.evaluate(individual.genes));
    	}
    }

    private List<Individual> weightedRandomDraw (List<Individual> population, int drawCount) {
		List<Individual> populationCopy = new ArrayList<Individual>(population);
		List<Individual> chosenOnes = new ArrayList<Individual>();
		
    	// Weighted random selection from https://stackoverflow.com/questions/6737283/weighted-randomness-in-java
		for (int i=0; i<drawCount; i++) {
			// Compute the total weight of all items together
			double totalWeight = 0.0;
			for (Individual individual : populationCopy) {
			    totalWeight += individual.fitness;
			}

			// Now choose a random item
			double random = rnd_.nextDouble() * totalWeight;
			for (Individual individual : populationCopy) {
			    random -= individual.fitness;
			    if (random <= 0.0) {
			    	populationCopy.remove(individual); // prevent replacement 
			        chosenOnes.add(individual);
			        break;
			    }
			}
		}
		return chosenOnes;
    }

    private List<Individual> reproduce (List<Individual> parents)
    {
    	List<Individual> children = new ArrayList<Individual>();
    	for (Individual parent : parents) {
   			// double median = 0.2;
			// double lamb = Math.log(2.0)/median;
			// double u = rnd_.nextDouble();
			// double mutationFactor = -Math.log(1 - Math.pow(u,2))/lamb; // drawn from exponential distribution
    		double mutationFactor = Math.pow(rnd_.nextDouble(),3);

    		Individual child = new Individual(parent);
			for (int i=0; i<child.genes.length; i++) {
				double mutation = rnd_.nextGaussian() * mutationFactor;
				if (Math.abs(child.genes[i] + mutation) >= 5.0) {
					mutation *= -1.0;
				}
				child.genes[i] += mutation;
			}
			children.add(child);
    	}
    	return children;
    }
    
	public void run()
	{
        // System.out.println("Ohi");
        // ClassLoader cl = ClassLoader.getSystemClassLoader();
        // URL[] urls = ((URLClassLoader)cl).getURLs();
        // for(URL url: urls){
        // 	System.out.println(url.getFile());
        // }
        // System.out.println("uhuh");
        // System.out.println(System.getProperty("java.classpath"));
        // System.out.println("hmmm");
        // Individual bla = new Individual();

		// testing
		// int individualCounter = 0;
  //       List<Individual> population = initPopulation();
		// for (Individual individual : population) {
		// 	individual.fitness = individualCounter++;
		// }
		// printPopulation(population, true);
		// population = weightedRandomDraw(population, POPULATION_SIZE);
		// printPopulation(population, true);
		// if (true) {
		// 	return;
		// }

		// Run your algorithm here
        // init population
        List<Individual> population = initPopulation();
        // calculate fitness
        int evaluationCount = 0;
        evalIndividuals(population);
		evaluationCount += POPULATION_SIZE;
        printPopulation(population);
        while (evaluationCount+NUMBER_OF_PARENTS_EACH_GEN < evaluations_limit_) {
            // Select parents
        	List<Individual> parents = weightedRandomDraw(population, NUMBER_OF_PARENTS_EACH_GEN);
            // Apply crossover / mutation operators
            List<Individual> children = reproduce(parents);
            // Check fitness of unknown fuction
	        evalIndividuals(children);
            population.addAll(children);
            evaluationCount += NUMBER_OF_PARENTS_EACH_GEN; // same as number of children atm
            // Select survivors
            population = weightedRandomDraw(population, POPULATION_SIZE);

	        if (evaluationCount % (evaluations_limit_/100) == 0) {
	        	System.out.printf("Evaluation count: %d / %d\n", evaluationCount, evaluations_limit_);
	        	printPopulation(population);
	        }
        }
        System.out.println("We're done here");
        printPopulation(population, true);
	}
}
