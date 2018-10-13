import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import jMEF.MultivariateGaussian;
import jMEF.PVector;
import jMEF.PMatrix;
import jMEF.PVectorMatrix;
// import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

public class CmaEs implements RecombinationOperator, SelectionOperator
{
    private static final double EPSILON = 1e-2;

	private static double[] mean;
	private static double[][] cov;
	private static int lambda = 30; // number of children/samples
	private static int mu; // estimate new mean from this many best children
	private static int d_;

	public CmaEs ()
	{
        // set how many parents are selected each generation (also number of children)
        if (System.getProperty("lambda") != null) {
            lambda = Integer.parseInt(System.getProperty("lambda"));
        }
		mu = lambda / 2;

		d_ = player34.PROBLEM_DIMENSIONALITY;
	}

	private void init (List<Individual> initialPopulation) {
    	mean = new double[d_];
    	cov = new double[d_][d_];
		// Find mean
    	int n = initialPopulation.size();
    	Arrays.fill(mean, 0.0);
    	for (Individual individual : initialPopulation) {
    		Arrays.setAll(mean, i -> mean[i] + individual.genes[i]/n);
    	}
    	// estimate covariance
    	// for (int i=0; i<d_; i++) {
	    // 	Arrays.fill(cov[i], 0.0);
    	// 	cov[i][i] = 1.0;
    	// }
    	for (int i=0; i<d_; i++) {
    		for (int j=0; j<d_; j++) {
    			cov[i][j] = 0.0;
    			for (Individual individual : initialPopulation) {
    				cov[i][j] += (individual.genes[i] - mean[i]) * (individual.genes[j] - mean[j]);
    			}
    			cov[i][j] /= n;
    			if (cov[i][j] < EPSILON) {
    				cov[i][j] = EPSILON;
    			}
    		}
    	}
	}

	public List<Individual> recombine (List<Individual> parents)
	{
		if (mean == null) {
			init(parents);
		}

        // for (int i = 0; i < d_; i++) {
        //     for (int j = 0; j < d_; j++) {
        //         System.out.printf("%8.5f ", cov[i][j]);
        //     }
        //     System.out.println();
        // }
        // System.out.println();

		//MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(mean, cov);
		//double vals[] = mnd.sample();
		// Cholesky mydude = new Cholesky();
		// double[][] l = mydude.cholesky(cov);

		MultivariateGaussian mvg = new MultivariateGaussian();
		
        List<Individual> samples = new ArrayList<Individual>();

		// sample from multivariate gauss with mean and cov
		for (int n=0; n<lambda; n++) {
			PVectorMatrix L = new PVectorMatrix(d_);
			L.v = new PVector(d_);
			L.v.array = mean;
			L.M = new PMatrix(d_);
			L.M.array = cov;
			PVector sampleVector = mvg.drawRandomPoint(L);
			// // Draw random vector Z
			// double [] z = new double[d_];
			// for (int i=0; i<d_; i++) {
			// 	z[i] = player34.rnd_.nextGaussian();
			// }

			Individual sample = new Individual();
			sample.genes = sampleVector.array;
			// // Perform matrix operation Lz + m
			// for (int i=0; i<d_; i++) {
			// 	sample.genes[i] = mean[i];
			// 	for (int j=0; j<d_; j++) {
			// 		sample.genes[i] += l[i][j] * z[j];
			// 	}
			// }
			samples.add(sample);
		}

		return samples;
	}

	public List<Individual> select (Population population, int drawCount)
	{
		// Sort individuals
		// Select best mu of them
		List<Individual> best = population.returnBestn(mu);

		// Calculate weights

		// Weighted sum for new mean
		double[] newMean = new double[d_];
    	Arrays.fill(newMean, 0.0);
    	for (Individual individual : best) {
    		Arrays.setAll(newMean, i -> newMean[i] + individual.genes[i]/mu);
    	}

		// Update isotropic evolution path

		// Update anisotropic evolution path

		// Update covariances
    	for (int i=0; i<d_; i++) {
	    	Arrays.fill(cov[i], 0.0);
	    }
    	for (int i=0; i<d_; i++) {
    		for (int j=0; j<d_; j++) {
    			cov[i][j] = 0.0;
    			for (Individual individual : best) {
    				cov[i][j] += (individual.genes[i] - mean[i]) * (individual.genes[j] - mean[j]);
    			}
    			cov[i][j] /= mu;
    			if (cov[i][j] < EPSILON) {
    				cov[i][j] = EPSILON;
    			}
    		}
    	}

    	mean = newMean;

		// Update step-size using isotropic path length

		return best;
	}
}