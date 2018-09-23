
public class Individual {
	public double[] genes;
	public double mutationRate;
	public double fitness;

	public Individual () {
		this.fitness = -1.0;
		this.mutationRate = 1.0;
		this.genes = new double[10];
		// initialize with random genes
		double problemRange = player34.PROBLEM_RANGE_MAX - player34.PROBLEM_RANGE_MIN;
		for (int i=0; i<genes.length; i++) {
			double allele = player34.rnd_.nextDouble() * problemRange - (problemRange/2.0);
    		this.genes[i] = allele;
		}
	}

	// copy constructor used for creating offspring
	public Individual (Individual that) {
		this.genes = new double[10];
		this.mutationRate = that.mutationRate;
		for (int i=0; i<this.genes.length; i++) {
			this.genes[i] = that.genes[i];
		}
		this.fitness = that.fitness;
	}

	public void evaluate () {
		if (this.fitness == -1.0) {
			double x = (double) player34.evaluation_.evaluate(this.genes);
			this.fitness = Math.max(0, x);
		}
	}
}