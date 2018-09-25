
public class Individual {
	private static final double UNDETERMINED_FITNESS_VALUE = -1.0;
	private static final double DEFAULT_MUTATION_RATE = 1.0;

	public double[] genes;
	public double mutationRate;
	public double fitness;

	public Individual () {
		this.fitness = UNDETERMINED_FITNESS_VALUE;
		this.mutationRate = DEFAULT_MUTATION_RATE;
		this.genes = new double[10];
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
		this.genes = new double[10];
		this.mutationRate = that.mutationRate;
		for (int i=0; i<this.genes.length; i++) {
			this.genes[i] = that.genes[i];
		}
		// Fitness should not be inherited, but copy it and 
		// undo it later because we're a motherfucking copy 
		// constructor over here
		this.fitness = that.fitness;
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
	}

	@Override
	public String toString() {
		String geneString = "";
		for (double gene : this.genes) {
			geneString += String.format(" %.2f", gene);
		}
		return String.format("F %.3g   σ %.3g   [%s ]", this.fitness, this.mutationRate, geneString);
	}
}