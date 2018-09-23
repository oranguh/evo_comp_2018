import java.util.ArrayList;
import java.util.List;

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
}