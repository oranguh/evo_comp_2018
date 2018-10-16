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
        
        List<double[]> samplesGenes = covariSampli(cov, lambda);
        for (int n = 0; n < lambda; n++) {
        	Individual sample = new Individual();
			for (int j = 0; j < sample.genes.length; j++)  {
				sample.genes[j] = samplesGenes.get(n)[j];
			}
			samples.add(sample);
        }
        
//		for (int n=0; n<lambda; n++) {
//			PVectorMatrix L = new PVectorMatrix(d_);
//			L.v = new PVector(d_);
//			L.v.array = mean;
//			L.M = new PMatrix(d_);
//			L.M.array = cov;
//			PVector sampleVector = mvg.drawRandomPoint(L);
//			// // Draw random vector Z
//			// double [] z = new double[d_];
//			// for (int i=0; i<d_; i++) {
//			// 	z[i] = player34.rnd_.nextGaussian();
//			// }
//
//			Individual sample = new Individual();
//			sample.genes = sampleVector.array;
//			// // Perform matrix operation Lz + m
//			// for (int i=0; i<d_; i++) {
//			// 	sample.genes[i] = mean[i];
//			// 	for (int j=0; j<d_; j++) {
//			// 		sample.genes[i] += l[i][j] * z[j];
//			// 	}
//			// }
//			samples.add(sample);
//		}

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
	
	public List<double[]> covariSampli(double[][] cov, int lambda) {
		List<double[]> output = new ArrayList<double[]>();
		int N = Individual.NUM_GENES;
		double[] sigmaList = new double[N];
		for (int i = 0; i < N; i++) {
			sigmaList[i] = cov[i][i];
		}
		double[][] angleMatrix = new double[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				double x = 2 * cov[i][j] / ( sigmaList[i] - sigmaList[j] );
				angleMatrix[i][j] = Math.atan(x) / 2;
			}
		}
		List<double[][]> rotationMatrices = new ArrayList<double[][]>();
		for (int i = 0; i < N - 1; i++) {
			for (int j = i + 1; j < N; j++) {
				double[][] rotationMatrix = new double[N][N];
				for (int k = 0; k < rotationMatrix.length; k++) { 
					Arrays.fill(rotationMatrix[k], 0.0);
					rotationMatrix[k][k] = 1.0;
				}
				rotationMatrix[i][i] = Math.cos(angleMatrix[i][j]);
				rotationMatrix[j][j] = Math.cos(angleMatrix[i][j]);
				rotationMatrix[i][j] = -Math.sin(angleMatrix[i][j]);
				rotationMatrix[j][i] = Math.sin(angleMatrix[i][j]);
				rotationMatrices.add(rotationMatrix);
			}
		}
		double[][] sigmaMatrix = new double[N][N];
		for (int k = 0; k < sigmaMatrix.length; k++) {
			Arrays.fill(sigmaMatrix[k], 0.0);
			sigmaMatrix[k][k] = sigmaList[k];
		}
		double[][] rotationMatrixProduct = rotationMatrices.get(0);
		for (int i = 1; i < rotationMatrices.size(); i++) {
			rotationMatrixProduct = multiply(rotationMatrixProduct, rotationMatrices.get(i));
		}

		for (int i = 0; i < lambda; i++) {
			double[] noiseVector = new double[N];
			for (int j = 0; j < N; j++) {
				noiseVector[j] = player34.rnd_.nextGaussian() * sigmaList[j];
			}
			output.add(multiplyMatrixWithVector(rotationMatrixProduct, noiseVector));
		}
		return output;
	}
	
	private double[][] multiply(double[][] A, double[][] B) {
		int n = A.length;
		double[][] product = new double[n][n];
        for (int i = 0; i < n; i++) { // aRow
        	Arrays.fill(product[i], 0.0);
            for (int j = 0; j < n; j++) { // bColumn
                for (int k = 0; k < n; k++) { // aColumn
                    product[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return product;
	}
	
	private double[] multiplyMatrixWithVector(double[][] A, double[] b) {
		int n = b.length;
		double[] product = new double[n];
		for (int i = 0; i < n; i++){
		    int value = 0;
		    for (int j = 0; j < n; j++){
		        value += A[i][j] * b[j]; 
		    }
		    product[i] = value;
		}
		return product;
	}
}