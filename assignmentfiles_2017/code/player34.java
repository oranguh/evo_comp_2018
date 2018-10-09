
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
    public static int epoch_ = 25;
    public static int migrationSize_ = 1;
    public static int populationSize_ = 10;
    public static int parentCountPerGeneration_ = 5;
    public static boolean sharedFitness = true;
    public static double sigmaShare = 0.001;

    // provided fields (do not touch)
	public static Random rnd_;
	public static ContestEvaluation evaluation_;
    private int evaluations_limit_;
	
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
        }

        // set how many iterations go into an epoch (basically migration interval)
        if (System.getProperty("epoch") != null) {
            epoch_ = Integer.parseInt(System.getProperty("epoch"));
        }

        // set how many iterations go into an epoch (basically migration interval)
        if (System.getProperty("migrationsize") != null) {
            migrationSize_ = Integer.parseInt(System.getProperty("migrationsize"));
        }
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

    private List<Individual> reproduce (List<Individual> parents)
    {
    	List<Individual> children = new ArrayList<Individual>();
        // Each parent generates one child by just mutation (gaussian noise)
    	for (Individual parent : parents) {
            // Copy parent
    		Individual child = new Individual(parent);
            // Reset fitness causing the child fitness to be evaluated later
            child.resetFitness();
            // First, re-sample mutation rate (sigma)
            double tau = Math.sqrt(PROBLEM_DIMENSIONALITY);
            child.mutationRate = parent.mutationRate * Math.exp(tau * rnd_.nextGaussian());
            child.mutationRate = Math.min(child.mutationRate, 3.0);
            // Then, sample gaussian and apply to each gene
			for (int i=0; i<child.genes.length; i++) {
				double mutation = 0.0;
                boolean willGoOutOfBounds = true;
                // Keep re-sampling gaussian until mutation stays within problem domain
				do {
                    mutation = rnd_.nextGaussian() * child.mutationRate;
                    double x = child.genes[i] + mutation;
                    willGoOutOfBounds = x <= PROBLEM_RANGE_MIN || x >= PROBLEM_RANGE_MAX;
                } while (willGoOutOfBounds);
                // Apply mutation
				child.genes[i] += mutation;
			}
			children.add(child);
    	}
    	return children;
    }
    
	public void run()
	{
        // Initialize islands
        Population[] islandList = new Population[islandAmount_];

        // Initialize population(s)
        for (int i=0; i<islandAmount_; i++){
            islandList[i] = new Population(populationSize_);
            islandList[i].evaluate(sharedFitness, sigmaShare);
            islandList[i].print();
        }

        // Print CSV header
        Csv.printHeader("Evaluations", "Max fitness", "Diversity", "Mutation rate");

        // Add data point of initial population
        // Consider the individuals on different islands as one giant population for stats
        Population continent = new Population(0);
        for (Population pop : islandList) {
            continent.individuals.addAll(pop.individuals);
        }
        Debug.printf("Evaluation count: %d / %d\n", populationSize_, evaluations_limit_);
        Csv.printData(populationSize_, 
            continent.getMaxFitness(), 
            continent.getAverageDistanceFromMean(), 
            continent.returnBestn(1).get(0).mutationRate);

        int evaluationCount = populationSize_*islandAmount_;
        boolean hasRunOutOfEvaluations = false;

        do {
            // do for each island
            for (int i=0; i<islandAmount_; i++) {
                // Select parents
                List<Individual> parents = islandList[i].tournamentSelection(parentCountPerGeneration_, 5, true, sharedFitness);
                // Apply crossover / mutation operators
                List<Individual> children = reproduce(parents);
                islandList[i].addAll(children);
                // Check fitness of unknown fuction
                islandList[i].evaluate(sharedFitness, sigmaShare); // skips those who already have been evaluated
                evaluationCount += parentCountPerGeneration_; // same as number of children atm
                // Select survivors
                islandList[i].individuals = islandList[i].tournamentSelection(populationSize_, 5, true, sharedFitness);

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

                boolean isTimeForMigration = evaluationCount % (epoch_*islandAmount_*populationSize_) == 0;
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
