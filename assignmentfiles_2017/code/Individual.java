
public class Individual {
	private static final double UNDETERMINED_FITNESS_VALUE = -1.0;
	private static final double DEFAULT_MUTATION_RATE = 1.0;
	public static final int NUM_GENES = 10;
	public static final double TAU = 1.0 / Math.sqrt(player34.PROBLEM_DIMENSIONALITY);
	public static final double TAU_PRIME = 1.0 / Math.pow(player34.PROBLEM_DIMENSIONALITY, (1/4));

	public double[] genes;
	public double[] mutationRates;
	private double fitness;
	public double fitnessShare;

	public Individual () {
		this.fitness = UNDETERMINED_FITNESS_VALUE;
		this.fitnessShare = 1.0;
		this.mutationRates = new double[NUM_GENES];
		for (int i = 0; i < mutationRates.length; i++) {
			this.mutationRates[i] = DEFAULT_MUTATION_RATE;
		}
		this.genes = new double[NUM_GENES];
		// Initialize with random genes
		double problemRange = player34.PROBLEM_RANGE_MAX - player34.PROBLEM_RANGE_MIN;
		for (int i=0; i<genes.length; i++) {
			// Sample uniformly from problem domain
			double allele = player34.rnd_.nextDouble() * problemRange - (problemRange/2.0);
    		this.genes[i] = allele;
		}
	}

	// Copy constructor used for creating offspring
	public Individual (Individual that) {
		this.genes = new double[NUM_GENES];
		for (int i=0; i<this.genes.length; i++) {
			this.genes[i] = that.genes[i];
		}
		this.mutationRates = new double[NUM_GENES];
		for (int i = 0; i < this.mutationRates.length; i++) {
			this.mutationRates[i] = that.mutationRates[i];
		}
		// Fitness should not be inherited, but copy it and 
		// undo it later because we're a motherfucking copy 
		// constructor over here
		this.fitness = that.fitness;
		this.fitnessShare = that.fitnessShare;
	}

	public double getFitness () {
		return this.fitness * this.fitnessShare;
	}

	public void evaluate () {
		// Only perform evaluation if fitness has not previously been determined
    	if (this.fitness == UNDETERMINED_FITNESS_VALUE) {
    		double x = (double) player34.evaluation_.evaluate(this.genes);
    		this.fitness = Math.max(0, x);
    	}
	}

	public void resetFitness () {
		// Individual fitness is not evaluated unless it is this value
		this.fitness = UNDETERMINED_FITNESS_VALUE;
		this.fitnessShare = 1.0;
	}

	@Override
	public String toString() {
		String geneString = "";
		for (double gene : this.genes) {
			geneString += String.format(" %.2f", gene);
		}
		//return java.util.Arrays.toString(this.genes);
		// JON CHANGE: IT'S NOT PRINTING MUTATION RATE RIGHT NOW
		return String.format("F %.3g   σ %.3g   [%s ]", this.fitness, this.mutationRates[0], geneString);
	}
}





