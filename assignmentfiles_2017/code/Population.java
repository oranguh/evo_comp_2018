import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

public class Population {
	public List<Individual> individuals;

	public Population (int populationSize) {
		this.individuals = new ArrayList<Individual>();
		for (int i=0; i<populationSize; i++) {
			this.individuals.add(new Individual());
		}
	}

	public void addAll (List<Individual> newcomers) {
		this.individuals.addAll(newcomers);
	}

    public void print ()
    {
    	Debug.println("Population:");
    	for (Individual individual : this.individuals) {
    		Debug.println(individual);
    	}
    }

    public void evaluate () {
    	for (Individual individual : this.individuals) {
    		individual.evaluate();
    	}
    }

    public List<Individual> weightedRandomDraw(int drawCount) {
		List<Individual> populationCopy = new ArrayList<Individual>(this.individuals);
		List<Individual> chosenOnes = new ArrayList<Individual>();
		
    	// Randomly select from pie, where pie is distributed according to fitness
		for (int i=0; i<drawCount; i++) {
			// Compute the total weight of all items together
			double totalWeight = 0.0;
			for (Individual individual : populationCopy) {
			    totalWeight += individual.fitness;
			}

			// Now choose a random item
			double random = player34.rnd_.nextDouble() * totalWeight;
			for (Individual individual : populationCopy) {
			    random -= individual.fitness;
			    if (random <= 0.0) {
			    	populationCopy.remove(individual); // prevent replacement 
			        chosenOnes.add(individual);
			        break;
			    }
			}
		}
		return chosenOnes;
    }

    public double getMaxFitness () {
    	double maxFitness = 0.0;
    	for (Individual individual : this.individuals) {
    		maxFitness = Math.max(maxFitness, individual.fitness);
    	}
    	return maxFitness;
    }

    public double getAverageFitness () {
    	double totalFitness = 0.0;
    	for (Individual individual : this.individuals) {
    		totalFitness += individual.fitness;
    	}
    	return totalFitness / this.individuals.size();
    }

    public double getAverageDistanceFromMean () {
    	// As some sort of diversity metric
    	int n = this.individuals.size();
    	// Find mean
    	double[] mean = new double[10];
    	Arrays.fill(mean, 0.0);
    	for (Individual individual : this.individuals) {
    		Arrays.setAll(mean, i -> mean[i] + individual.genes[i]/n);
    	}

    	// Calculate distance from mean
    	double averageDistanceFromMean = 0.0;
    	for (Individual individual : this.individuals) {
	    	double[] distances = new double[10];
	    	Arrays.fill(distances, 0.0);
    		Arrays.setAll(distances, i -> Math.pow(mean[i] - individual.genes[i],2));
    		double euclideanDistance = Math.sqrt(DoubleStream.of(distances).sum());
    		averageDistanceFromMean += euclideanDistance/n;
    	}
    	return averageDistanceFromMean;
    }
}