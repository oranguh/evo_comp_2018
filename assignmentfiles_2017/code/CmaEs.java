import java.util.List;
import java.util.ArrayList;

public class CmaEs implements RecombinationOperator, SelectionOperator
{
	private static double[] mean;
	private static double[][] cov;
	private static double lambda;

	public List<Individual> recombine (List<Individual> parents)
	{
        List<Individual> samples = new ArrayList<Individual>();

		// sample from multivariate gauss with mean and cov
		for (int i=0; i<lambda; i++) {
			Individual sample = new Individual(); // sample here
			samples.add(sample);
		}

		return samples;
	}

	public List<Individual> select (Population population, int drawCount)
	{
		// Sort individuals
		// Select best mu of them
		int mu = (int) lambda / 2;
		List<Individual> best = population.returnBestn(mu);

		// Calculate weights

		// Weighted sum for new mean

		// Update isotropic evolution path

		// Update anisotropic evolution path

		// Update covariances

		// Update step-size using isotropic path length

		return best;
	}
}