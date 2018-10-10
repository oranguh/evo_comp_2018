
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.lang.Math;
import java.util.Random;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import java.net.URL;
import java.net.URLClassLoader;

import java.io.OutputStream;
import java.io.PrintStream;

public class player34 implements ContestSubmission
{
    // fixed parameters
    public static final int PROBLEM_DIMENSIONALITY = 10;
    public static final int PROBLEM_RANGE_MIN = -5;
    public static final int PROBLEM_RANGE_MAX = 5;

    // configurable parameters
    public static int islandAmount_ = 5;
    public static int epoch_ = 50;
    public static int migrationSize_ = 1;
    public static int populationSize_ = 200;
    public static int parentCountPerGeneration_ = 6;
    public static boolean sharedFitness = true;
    public static double sigmaShare = 0.001;
    public static double recombinationProbability = 1.0;  // Added by Jon
    public static int recombinationArity = 2;             // Added by Jon

    // provided fields (do not touch)
	public static Random rnd_;
	public static ContestEvaluation evaluation_;
    private int evaluations_limit_;
    
    // Added by Jon
    public static List<Integer> crossoverBoundaries;    
    
	public player34()
	{
		rnd_ = new Random();

        // Disable console output for competition environment
        // Start with "java -Ddebug -jar ..." to enable
        if (System.getProperty("debug") != null) {
            Debug.isOutputEnabled = true;
        }

        // Use to write to file through redirected console output
        // Start with "java -Dcsv -jar ... > filename.csv" to enable
        if (System.getProperty("csv") != null) {
            Csv.isOutputEnabled = true;
        }

        // Since file writing uses redirected console output, Debug and Csv conflict
        if (Debug.isOutputEnabled && Csv.isOutputEnabled) {
            System.err.println("Cannot use -Ddebug and -Dcsv at the same time.");
            System.exit(-1);
        }

        // set population size
        if (System.getProperty("popsize") != null) {
            populationSize_ = Integer.parseInt(System.getProperty("popsize"));
        }

        // set how many parents are selected each generation
        if (System.getProperty("parentcount") != null) {
            parentCountPerGeneration_ = Integer.parseInt(System.getProperty("parentcount"));
        }

        // set how many islands we use
        if (System.getProperty("islands") != null) {
            islandAmount_ = Integer.parseInt(System.getProperty("islands"));
            if (populationSize_ % islandAmount_ != 0) {
                System.err.println("Population size is not divisible by number of islands.");
                System.exit(-1);
            }
        }

        // set how many iterations go into an epoch (basically migration interval)
        if (System.getProperty("epoch") != null) {
            epoch_ = Integer.parseInt(System.getProperty("epoch"));
        }

        // set how many iterations go into an epoch (basically migration interval)
        if (System.getProperty("migrationsize") != null) {
            migrationSize_ = Integer.parseInt(System.getProperty("migrationsize"));
        }

        // set how many iterations go into an epoch (basically migration interval)
        if (System.getProperty("nofitnesssharing") != null) {
            sharedFitness = false;
        }

        if (parentCountPerGeneration_ % recombinationArity != 0) {
            System.err.println("Parents per generation is not divisible by recombination arity.");
            System.exit(-1);
        }
        
        // Added by Jon. Compute (m-1) points used for crossover once, but allow for recomputing
        // them later in case we want to modify it on-the-fly
        setCrossoverBoundaries(recombinationArity);
	}

    private static void disableConsolePrinting () {
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                //DO NOTHING
            }
        }));
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

    private void mutate(List<Individual> individuals)
    {
        // Each parent generates one child by just mutation (gaussian noise)
    	for (Individual individual : individuals) {
    		// Reset fitness so that it'll be evaluated later
            individual.resetFitness();
            // First, re-sample mutation rate (sigma)
            double tau = Math.sqrt(PROBLEM_DIMENSIONALITY);
            individual.mutationRate *= Math.exp(tau * rnd_.nextGaussian());
            individual.mutationRate = Math.min(individual.mutationRate, 3.0);
            // Then, sample gaussian and apply to each gene
			for (int i = 0; i < individual.genes.length; i++) {
				double mutation = 0.0;
                boolean willGoOutOfBounds = true;
                // Keep re-sampling gaussian until mutation stays within problem domain
				do {
                    mutation = rnd_.nextGaussian() * individual.mutationRate;
                    double x = individual.genes[i] + mutation;
                    willGoOutOfBounds = x <= PROBLEM_RANGE_MIN || x >= PROBLEM_RANGE_MAX;
                } while (willGoOutOfBounds);
                // Apply mutation
				individual.genes[i] += mutation;
			}
    	}
    }
    
    // (m-1) point recombination for m parents where m is the arity. This function also
    // subdivides the input group 'parents' of any size into subsets of size m
    public static List<Individual> recombine(List<Individual> parents, int arity)
    {
        if (arity > parents.size() || arity < 2) {
            throw new IllegalArgumentException("Jon: recombine() called with illegal arguments.");
        }        
        
        // Initialize empty list of kids
        List<Individual> children = new ArrayList<Individual>();        
        
        // parentGroups are like pairs of parents but generalized to m
        // members, where m = arity. parentGroups is a list of these.
        List<Individual> parentGroup  = new ArrayList<Individual>();
        List<List<Individual>> parentGroups = new ArrayList<List<Individual>>();
         
        // Get number of parents in total recombination pool. If there's an 
        // 'unpairable' set of size < m then return these without changing 'em
        int parentCount = parents.size();
        int nIgnored = parentCount % arity;
        parentCount = parentCount - nIgnored;
        for (int i = parentCount; i < parents.size(); i++) {
        	children.add(parents.get(i));
        }
        
        // Add parents to reproductive groups of size m (FOR SEX)
        int counter = 0;
        for (int i = 0; i < parentCount; i++) {
            parentGroup.add(parents.get(i));
            counter++;
            if (counter == arity) {
                parentGroups.add(parentGroup);
                counter = 0;
                parentGroup  = new ArrayList<Individual>();
            }
        }
 
        // Perform (m-1) point crossover
        double r;  // random probability
        for (List<Individual> pg : parentGroups) {
            // Do the crossover with recombination probability defined at top
            r = rnd_.nextDouble();
            if (r < recombinationProbability) {
                children.addAll(mMinusOnePointCrossover(pg, arity));
            }
            // Else just add the unchanged parents to the kids 
            else  { children.addAll(pg); }
        }
        
        // Reset fitness of children for evaluation later
        for (Individual child : children)  { child.resetFitness(); }
        
        // Return list of kids with kids.size == parents.size
        return children;
    }
    
    // Helper function to recombine(). Actually performs the (m-1) crossover for some
    // input group of parents
    private static List<Individual> mMinusOnePointCrossover(List<Individual> parentGroup, int arity) {
        List<Individual> children = new ArrayList<Individual>();
        Individual child;
        int childIndex, parentIndex, geneIndex;
        for (childIndex = 0; childIndex < arity; childIndex++) {
            child = new Individual();
            geneIndex = 0;
            parentIndex = childIndex;
            // Iterate towards each boundary and iteratively change the 'parentIndex'
            // after each boundary so that alleles are selected from alternating parents
            for (int boundary : crossoverBoundaries) {  // boundaries always include last index of genes
                while (geneIndex <= boundary) {
                    child.genes[geneIndex] = parentGroup.get(parentIndex).genes[geneIndex];
                    geneIndex++;
                }
                parentIndex = (parentIndex + 1) % arity;  // (loop around)
            }
            children.add(child);
        }
        return children;
    }

    // This is a a setter for the public static crossoverBoundaries list
    private static void setCrossoverBoundaries(int arity) {
        List<Integer> boundaries = new ArrayList<Integer>();
        // Initialize boundaries below. If the number of genes aren't evenly
        // divisible then that'll be fixed afterwards
        for (int i = 1; i <= arity; i++) {
            boundaries.add((Individual.NUM_GENES / arity) * i - 1);
        }
        // Correct boundaries if there's genes left
        int remainder = Individual.NUM_GENES % arity;
        for (int i = 0; i < boundaries.size(); i++) {
            if (i < remainder)  { boundaries.set(i, boundaries.get(i) + i + 1); }
            else  { boundaries.set(i,  boundaries.get(i) + remainder); }
        }
        crossoverBoundaries = boundaries;
    }    
    
	public void run()
	{
        // Spread population of islands
        int populationPerIsland = populationSize_ / islandAmount_;

        // Initialize islands
        Population[] islandList = new Population[islandAmount_];

        // Initialize population(s)
        for (int i=0; i<islandAmount_; i++){
            islandList[i] = new Population(populationPerIsland);
            islandList[i].evaluate(sharedFitness, sigmaShare);
            islandList[i].print();
        }

        // Print CSV header
        Csv.printHeader("Evaluations", "Max fitness", "Diversity", "Mutation rate");

        // Add data point of initial population
        // Consider the individuals on different islands as one giant population for stats
        Debug.printf("Evaluation count: %d / %d\n", populationSize_, evaluations_limit_);
        Population continent = new Population(0);
        for (Population pop : islandList) {
            continent.individuals.addAll(pop.individuals);
        }
        Csv.printData(populationSize_, 
            continent.getMaxFitness(), 
            continent.getAverageDistanceFromMean(), 
            continent.returnBestn(1).get(0).mutationRate);

        int evaluationCount = populationSize_;
        boolean hasRunOutOfEvaluations = false;

        do {
            // do for each island
            for (int i=0; i<islandAmount_; i++) {
                // Select parents
                List<Individual> parents = islandList[i].tournamentSelection(parentCountPerGeneration_, 5, true, sharedFitness);
                List<Individual> children = recombine(parents, 2);
                // Apply crossover / mutation operators
                mutate(children);
                islandList[i].addAll(children);
                // Check fitness of unknown fuction
                islandList[i].evaluate(sharedFitness, sigmaShare); // skips those who already have been evaluated
                evaluationCount += parentCountPerGeneration_; // same as number of children atm
                // Select survivors
                islandList[i].individuals = islandList[i].tournamentSelection(populationPerIsland, 5, true, sharedFitness);

                // Debug print 10 times
                if (evaluationCount % (evaluations_limit_ / 10) == 0) {
                    Debug.printf("Evaluation count: %d / %d\n", evaluationCount, evaluations_limit_);
                    islandList[0].print();
                }

                // Print to file 100 times
                if (evaluationCount % (evaluations_limit_ / 100) == 0) {
                    // Consider the individuals on different islands as one giant population for stats
                    continent = new Population(0);
                    for (Population pop : islandList) {
                        continent.individuals.addAll(pop.individuals);
                    }
                    Csv.printData(evaluationCount, 
                        continent.getMaxFitness(), 
                        continent.getAverageDistanceFromMean(), 
                        continent.returnBestn(1).get(0).mutationRate);
                }

                boolean isTimeForMigration = islandAmount_ > 1 && evaluationCount % (epoch_*populationSize_) == 0;
                if (isTimeForMigration) {
                    // Get the best individuals from each island and put in list
                    List<List<Individual>> listBestIndividuals= new ArrayList<List<Individual>>();
                    for (int j=0; j<islandAmount_; j++) {
                        listBestIndividuals.add(islandList[j].returnBestn(migrationSize_));

                    }
                    // forcefully re-allocate them to the next island.
                    for (int j=0; j<islandAmount_; j++) {
                        for (int k=0; k<listBestIndividuals.get(j).size();k++){
                            islandList[j].individuals.remove(0);
                            islandList[j].individuals.add(listBestIndividuals.get((j+1) % islandAmount_).get(k));
                        }
                    }
                }
            }

            // Predict if we run out of evaluations before the end of the next iteration
            hasRunOutOfEvaluations = evaluationCount + parentCountPerGeneration_ * islandAmount_ > evaluations_limit_;
        } while (!hasRunOutOfEvaluations);

        // When printing to csv is enabled, prevent the end of
        // the program from adding garbage to it by printing stuff (results etc)
        if (Csv.isOutputEnabled) {
            disableConsolePrinting();
        }
	}
}
