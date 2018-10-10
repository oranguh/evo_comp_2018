import java.util.List;
import java.lang.Math;

public class AdaptiveGaussianPerturbation implements MutationOperator
{
    public void mutate (List<Individual> children)
    {
        // Each parent generates one child by just mutation (gaussian noise)
    	for (Individual child : children) {
    		// Reset fitness so that it'll be evaluated later
            child.resetFitness();
            // First, re-sample mutation rate (sigma)
            double tau = Math.sqrt(player34.PROBLEM_DIMENSIONALITY);
            child.mutationRate *= Math.exp(tau * player34.rnd_.nextGaussian());
            child.mutationRate = Math.min(child.mutationRate, 3.0);
            // Then, sample gaussian and apply to each gene
			for (int i = 0; i < child.genes.length; i++) {
				double mutation = 0.0;
                boolean willGoOutOfBounds = true;
                // Keep re-sampling gaussian until mutation stays within problem domain
				do {
                    mutation = player34.rnd_.nextGaussian() * child.mutationRate;
                    double x = child.genes[i] + mutation;
                    willGoOutOfBounds = x <= player34.PROBLEM_RANGE_MIN || x >= player34.PROBLEM_RANGE_MAX;
                } while (willGoOutOfBounds);
                // Apply mutation
				child.genes[i] += mutation;
			}
    	}
    }
}