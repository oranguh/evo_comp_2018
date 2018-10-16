import java.util.List;
import java.util.ArrayList;

public class TournamentSelection implements SelectionOperator
{
	// Don't use replacement for now. If it should be changeable, 
	// it should be configurable per instance (parent or survivor 
	// selection) which is actual a generic property of selection 
	// methods, so we would have to change all of them.
	private boolean useReplacement_ = false; 
	private int k_ = 5;

	public TournamentSelection ()
	{
		// This is actually fucked up, since k will be set for 
		// parent and survivor selection if used.
        if (System.getProperty("k") != null) {
            k_ = Integer.parseInt(System.getProperty("k"));
        }
	}

    public List<Individual> select (Population population, int drawCount) {
        List<Individual> populationCopy = new ArrayList<Individual>(population.individuals);
        List<Individual> competitionPool = new ArrayList<Individual>();
        List<Individual> chosenOnes = new ArrayList<Individual>();
        Individual randomIndividual, winner;
        
        // drawCount and k should not be bigger than the population size
        if (drawCount > population.individuals.size() || k_ > population.individuals.size()) {
        	throw new IllegalArgumentException("drawCount and k should not exceed population size.");
        }

        for (int i = 0; i < drawCount; i++) {
        	// If not with replacement, individuals are removed from the population
        	// so population should be reset before every tournament iteration
        	if (!useReplacement_) {
        		populationCopy.clear();
        		populationCopy.addAll(population.individuals);
        	}
            competitionPool.clear();
            
            // Choose k random individuals and add to competition pool,
            // useReplacement_ bool indicates whether individuals can be
            // added repeatedly to the same tournament
            for (int j = 0; j < k_; j++) {
                randomIndividual = populationCopy.get(player34.rnd_.nextInt(populationCopy.size()));
                if (!useReplacement_) {
                	populationCopy.remove(randomIndividual);
                }
                competitionPool.add(randomIndividual);
            }

            // Compare these k individuals and select the best of them;
            winner = competitionPool.get(0);
            for (Individual individual : competitionPool) {
                if (individual.getFitness() > winner.getFitness()) {
                	winner = individual;
                }
            }
            chosenOnes.add(winner);
        }
        return chosenOnes;       
    }    
}