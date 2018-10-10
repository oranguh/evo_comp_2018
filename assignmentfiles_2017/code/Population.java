import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.Collections;
import java.util.Comparator;

public class Population 
{
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
            individual_i.fitnessShare = 1.0 / sharedSum;
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

    public double getMaxFitness () {
    	double maxFitness = 0.0;
    	for (Individual individual : this.individuals) {
    		maxFitness = Math.max(maxFitness, individual.getFitness());
    	}
    	return maxFitness;
    }

    public double getAverageFitness () {
    	double totalFitness = 0.0;
    	for (Individual individual : this.individuals) {
    		totalFitness += individual.getFitness();
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

                if(i1.getFitness() > i2.getFitness()) {
                    return -1;
                }
                else if(i1.getFitness() < i2.getFitness()) {
                    return 1;
                }
                return 0;
            }
        });
        return sortedPop.subList(0, amount);
    }

}