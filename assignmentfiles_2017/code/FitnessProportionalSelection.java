import java.util.List;
import java.util.ArrayList;

public class FitnessProportionalSelection implements SelectionOperator
{
    public List<Individual> select (Population population, int drawCount) 
    {
    	// Copy the population list because we will be altering it to avoid replacement
		List<Individual> populationCopy = new ArrayList<Individual>(population.individuals);
		List<Individual> chosenOnes = new ArrayList<Individual>();
		
    	// Randomly select from pie, where pie is distributed according to fitness
		for (int i=0; i<drawCount; i++) {
			// Compute the total weight of all items together
			double totalWeight = 0.0;
			for (Individual individual : populationCopy) {
			    totalWeight += individual.getFitness();
			}

			// Now choose a random item
			double random = player34.rnd_.nextDouble() * totalWeight;
			for (Individual individual : populationCopy) {
			    random -= individual.getFitness();
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