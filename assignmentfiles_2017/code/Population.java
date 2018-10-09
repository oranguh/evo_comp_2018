import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.Collections;
import java.util.Comparator;

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

    public void print (){
    	Debug.println("Population:");
    	for (Individual individual : this.individuals) {
    		Debug.println(individual);
    	}
    }

    public void evaluate (boolean shareFitness, double sigmaShare) {
    	for (Individual individual : this.individuals) {
    		individual.evaluate();
        }
        if (shareFitness == true) {
            fitnessSharing(sigmaShare);
        }
    }
    
    private void fitnessSharing(double sigmaShare){
        for (Individual individual_i : this.individuals){
            double sharedSum = 0.0;
            for (Individual individual_j : this.individuals){
                double distance = calculateEuclideanDistance(individual_i.genes, individual_j.genes);
                sharedSum += sharedD(distance, sigmaShare);
            }
            individual_i.fitnessShared = individual_i.fitness / sharedSum;
        }
    }

    private double calculateEuclideanDistance(double[] individual_i, double[] individual_j){
        double Sum = 0.0;
        for(int i=0;i<individual_i.length;i++) {
           Sum = Sum + Math.pow((individual_i[i]-individual_j[i]),2.0);
        }
        return Math.sqrt(Sum);
    }

    private double sharedD(double distance, double sigmaShare){
        double output = 0.0;
        if (distance <= sigmaShare) output = 1 - (distance / sigmaShare);
        return output;
    }

    public List<Individual> fitnessProportionalSelection (int drawCount) {
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
    
    public List<Individual> tournamentSelectionWithReplacement (int drawCount, int k) {
        return tournamentSelection(drawCount, k, true, true);
    }

    public List<Individual> tournamentSelectionWithoutReplacement (int drawCount, int k) {
        return tournamentSelection(drawCount, k, false, true);
    }

    public List<Individual> tournamentSelection (int drawCount, int k, boolean withReplacement, boolean sharedFitness) {
        List<Individual> populationCopy = new ArrayList<Individual>(this.individuals);
        List<Individual> competitionPool = new ArrayList<Individual>();
        List<Individual> chosenOnes = new ArrayList<Individual>();
        Individual randomIndividual, winner;
        
        // drawCount and k should not be bigger than the population size
        //if (drawCount > individuals.size() || k > individuals.size()) {
        	//throw new IllegalArgumentException("drawCount and k should not exceed population size.");
        //}

        for (int i = 0; i < drawCount; i++) {
        	// If not with replacement, individuals are removed from the population
        	// so population should be reset before every tournament iteration
        	if (!withReplacement) {
        		populationCopy.clear();
        		populationCopy.addAll(this.individuals);
        	}
            competitionPool.clear();
            
            // Choose k random individuals and add to competition pool,
            // withReplacement bool indicates whether individuals can be
            // added repeatedly to the same tournament
            for (int j = 0; j < k; j++) {
                randomIndividual = populationCopy.get(player34.rnd_.nextInt(populationCopy.size()));
                if (!withReplacement)  populationCopy.remove(randomIndividual);
                competitionPool.add(randomIndividual);
            }

            // Compare these k individuals and select the best of them;
            winner = competitionPool.get(0);
            for (Individual individual : competitionPool) {
                if (sharedFitness == true) {
                    if (individual.fitnessShared> winner.fitnessShared)  winner = individual;
                } else{
                    if (individual.fitness> winner.fitness)  winner = individual;
                }
            }
            chosenOnes.add(winner);
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
    public List<Individual> returnBestn (int amount) {

        List<Individual> sortedPop = new ArrayList<Individual>();

        for (Individual individual : this.individuals) {
            sortedPop.add(individual);
        }

//        Sort descending, largest fitness first
        Collections.sort(sortedPop, new Comparator<Individual>() {
            public int compare(Individual i1, Individual i2) {

                if(i1.fitness > i2.fitness) {
                    return -1;
                }
                else if(i1.fitness < i2.fitness) {
                    return 1;
                }
                return 0;
            }
        });
        return sortedPop.subList(0, amount);
    }

}