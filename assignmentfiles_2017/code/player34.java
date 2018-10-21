
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

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
    public static int islandAmount_ = 1;
    public static int iterationsPerEpoch_ = 50;
    public static int migrationSize_ = 2;
    public static int populationSize_ = 100;
    public static int parentCountPerIteration_ = 6;
    public static boolean shareFitness_ = false;
    public static double sigmaShare_ = 0.001;
    public static boolean isGenerationalModel_ = false;

    // Tableau components
    private SelectionOperator parentSelection;
    private SelectionOperator survivorSelection;
    private RecombinationOperator recombination;
    private MutationOperator mutation;

    // provided fields (do not touch)
	public static Random rnd_;
	public static ContestEvaluation evaluation_;
    private int evaluations_limit_;
    
	public player34()
	{
		rnd_ = new Random();

        // Administrative parameters
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


        // General model parameters
        // set population size
        if (System.getProperty("popsize") != null) {
            populationSize_ = Integer.parseInt(System.getProperty("popsize"));
        }
        // set how many parents are selected each generation (also number of children)
        if (System.getProperty("parentcount") != null) {
            parentCountPerIteration_ = Integer.parseInt(System.getProperty("parentcount"));
        }
        // In generational models, the offspring replace the population every iterations
        if (System.getProperty("generational") != null) {
            isGenerationalModel_ = true;
        }


        // Island model parameters
        // set how many islands we use
        if (System.getProperty("islands") != null) {
            islandAmount_ = Integer.parseInt(System.getProperty("islands"));
        }
        // set how many iterations go into an epoch (basically migration interval)
        if (System.getProperty("epochsize") != null) {
            iterationsPerEpoch_ = Integer.parseInt(System.getProperty("epochsize"));
        }
        // set how many iterations go into an epoch (basically migration interval)
        if (System.getProperty("migrationsize") != null) {
            migrationSize_ = Integer.parseInt(System.getProperty("migrationsize"));
        }
        // Population count is split up into islands, which must be of equal size to make sense
        if (populationSize_ % islandAmount_ != 0) {
            System.err.println("Population size is not divisible by number of islands.");
            System.exit(-1);
        }


        // Fitness sharing parameters
        // set how many iterations go into an epoch (basically migration interval)
        if (System.getProperty("sharefitness") != null) {
            shareFitness_ = true;
        }
        // set how large a neighbourhood is (sharing threshold)
        if (System.getProperty("sigma") != null) {
            sigmaShare_ = Double.parseDouble(System.getProperty("sigma"));
        }


        // Set up tableau components
        if (System.getProperty("parentselection") != null) {
            switch (System.getProperty("parentselection")) {
                case "tournament":
                    parentSelection = new TournamentSelection();
                    break;

                case "fitnessproportional":
                    parentSelection = new FitnessProportionalSelection();
                    break;

                case "all":
                    parentSelection = new SelectAll();
                    break;
            }
        } else {
            // default
            parentSelection = new TournamentSelection();
        }

        if (System.getProperty("survivorselection") != null) {
            switch (System.getProperty("survivorselection")) {
                case "tournament":
                    survivorSelection = new TournamentSelection();
                    break;

                case "fitnessproportional":
                    survivorSelection = new FitnessProportionalSelection();
                    break;

                case "CMA-ES":
                    survivorSelection = new CmaEs();
                    break;
            }
        } else {
            // default
            survivorSelection = new TournamentSelection();
        }

        if (System.getProperty("recombination") != null) {
            switch (System.getProperty("recombination")) {
                case "copy":
                    recombination = new CloneParents();
                    break;

                case "m-1crossover":
                    recombination = new MMinusOnePointCrossover();
                    break;

                case "CMA-ES":
                    recombination = new CmaEs();
                    break;
            }
        } else {
            // default
            recombination = new CloneParents();
        }

        if (System.getProperty("mutation") != null) {
            switch (System.getProperty("mutation")) {
                case "adaptivegauss":
                    mutation = new AdaptiveGaussianPerturbation();
                    break;

                case "none":
                    mutation = new NoMutation();
                    break;
            }
        } else {
            // default
            mutation = new AdaptiveGaussianPerturbation();
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
        // evaluations_limit_ = 100000;
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

    private void printCsv (Population[] islands, int evaluationCount)
    {
        // Consider the individuals on different islands as one giant population for stats
        Population continent = new Population(0);
        for (Population pop : islands) {
            continent.individuals.addAll(pop.individuals);
        }
        Csv.printData(evaluationCount, 
            continent.getMaxFitness(), 
            continent.getAverageDistanceFromMean(),
            // JON: ONLY PRINTING FIRST MUTATION RATE
            continent.returnBestn(1).get(0).mutationRates[0]);
    }
    
	public void run()
	{
        // Spread population of islands
        int populationPerIsland = populationSize_ / islandAmount_;

        // Initialize islands
        Population[] islands = new Population[islandAmount_];

        // Initialize population(s)
        for (int i=0; i<islandAmount_; i++){
            islands[i] = new Population(populationPerIsland);
            islands[i].evaluate(shareFitness_, sigmaShare_);
            islands[i].print();
        }

        // Print CSV header
        Csv.printHeader("Evaluations", "Max fitness", "Diversity", "Mutation rate");

        int evaluationCount = populationSize_;
        boolean hasRunOutOfEvaluations = false;

        // Add data point of initial population
        printCsv(islands, evaluationCount);
        int oldStepCheck = 0;
        do {
            for (Population island : islands) {
                // Core evolutionary algorithm
                // Select parents
                List<Individual> parents = parentSelection.select(island, parentCountPerIteration_);
                // Generate offspring
                List<Individual> children = recombination.recombine(parents);
                // Apply random mutation
                mutation.mutate(children);
                // Add new individuals to population
                if (isGenerationalModel_) {
                    island.individuals = children;
                } else {
                    island.individuals.addAll(children);
                }
                // Calculate fitness of new individuals
                island.evaluate(shareFitness_, sigmaShare_);
                evaluationCount += children.size();
                // Select survivors
                island.individuals = survivorSelection.select(island, populationPerIsland);

                // Since we use an island model, apply migration after epoch
                int evaluationsPerIteration = parentCountPerIteration_*islandAmount_;
                boolean hasEpochPassed = evaluationCount % iterationsPerEpoch_*evaluationsPerIteration == 0;
                boolean isTimeForMigration = islandAmount_ > 1 && hasEpochPassed;
                if (isTimeForMigration) {
                    // Get the best individuals from each island and put in list
                    List<List<Individual>> listBestIndividuals = new ArrayList<List<Individual>>();
                    for (Population otherIsland : islands) {
                        listBestIndividuals.add(otherIsland.returnBestn(migrationSize_));
                    }
                    // forcefully re-allocate them to the next island.
                    for (int j=0; j<islandAmount_; j++) {
                        for (int k=0; k<listBestIndividuals.get(j).size();k++){
                            islands[j].individuals.remove(0);
                            islands[j].individuals.add(listBestIndividuals.get((j+1) % islandAmount_).get(k));
                        }
                    }
                }

                // Administrative stuff
                // Debug print 10 times
                if (evaluationCount % (evaluations_limit_ / 10) == 0) {
                    Debug.printf("Evaluation count: %d / %d\n", evaluationCount, evaluations_limit_);
                    Population continent = new Population(0);
                    for (Population pop : islands) {
                        continent.individuals.addAll(pop.individuals);
                    }
                    //System.out.println(evaluationCount);
                    continent.print();
                }

                // Print to file 1000 times
                int newStepCheck = evaluationCount % (evaluations_limit_ / 1000);
                if (newStepCheck < oldStepCheck) {
                    printCsv(islands, evaluationCount);
                }
                oldStepCheck = newStepCheck;
            }
            // Predict if we run out of evaluations before the end of the next iteration
            hasRunOutOfEvaluations = evaluationCount + parentCountPerIteration_ * islandAmount_ > evaluations_limit_;
        } while (!hasRunOutOfEvaluations);
        
        printCsv(islands, evaluationCount);

        // When printing to csv is enabled, prevent the end of
        // the program from adding garbage to it by printing stuff (results etc)
        if (Csv.isOutputEnabled) {
            disableConsolePrinting();
        }
	}
}
