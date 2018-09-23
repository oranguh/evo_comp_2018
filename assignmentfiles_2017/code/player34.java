
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
    public static int populationSize_ = 30;
    public static int parentCountPerGeneration_ = 5;

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
        // Initialize population
        Population population = new Population(populationSize_);
        population.evaluate();
        population.print();

        // Print CSV header
        Csv.printHeader("step", "totalsteps", "fitness0");

        int evaluationCount = populationSize_;
        boolean hasRunOutOfEvaluations = false;
        do {
            // Select parents
        	List<Individual> parents = population.weightedRandomDraw(parentCountPerGeneration_);
            // Apply crossover / mutation operators
            List<Individual> children = reproduce(parents);
            population.addAll(children);
            // Check fitness of unknown fuction
            population.evaluate(); // skips those who already have been evaluated
            evaluationCount += parentCountPerGeneration_; // same as number of children atm
            // Select survivors
            population.individuals = population.weightedRandomDraw(populationSize_);

            // optionally print statistics sometimes
	        if (evaluationCount % (evaluations_limit_/10) == 0) {
	        	Debug.printf("Evaluation count: %d / %d\n", evaluationCount, evaluations_limit_);
	        	population.print();
                Csv.printData(evaluationCount, evaluations_limit_, population.individuals.get(0).fitness);
	        }

            hasRunOutOfEvaluations = evaluationCount + parentCountPerGeneration_ > evaluations_limit_;
        } while (!hasRunOutOfEvaluations);

        // When printing to csv is enabled, prevent the end of 
        // the program from adding garbage to it by printing stuff (results etc)
        if (Csv.isOutputEnabled) {
            disableConsolePrinting();
        }
	}
}
