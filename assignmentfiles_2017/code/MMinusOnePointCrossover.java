import java.util.List;
import java.util.ArrayList;

public class MMinusOnePointCrossover implements RecombinationOperator
{
	private int arity_ = 2;
    private double recombinationProbability_ = 1.0;
    private List<Integer> crossoverBoundaries_;

	public MMinusOnePointCrossover ()
	{
        // set how many iterations go into an epoch (basically migration interval)
        if (System.getProperty("arity") != null) {
            arity_ = Integer.parseInt(System.getProperty("arity"));
        }

        // If not divisible, number of evaluations is not kept track of properly
        if (player34.parentCountPerIteration_ % arity_ != 0) {
            System.err.println("Parents per generation is not divisible by recombination arity.");
            System.exit(-1);
        }

        if (System.getProperty("recombinationProbability") != null) {
            recombinationProbability_ = Double.parseDouble(System.getProperty("recombinationProbability"));
        }

        // Compute (m-1) points used for crossover once, but allow for recomputing
        // them later in case we want to modify it on-the-fly
        setcrossoverBoundaries();
	}
    
    // (m-1) point recombination for m parents where m is the arity. This function also
    // subdivides the input group 'parents' of any size into subsets of size m
    public List<Individual> recombine (List<Individual> parents)
    {
        if (arity_ > parents.size() || arity_ < 2) {
            throw new IllegalArgumentException("Jon: recombine() called with illegal arguments.");
        }        
        
        // Initialize empty list of kids
        List<Individual> children = new ArrayList<Individual>();        
        
        // parentGroups are like pairs of parents but generalized to m
        // members, where m = arity. parentGroups is a list of these.
        List<Individual> parentGroup  = new ArrayList<Individual>();
        List<List<Individual>> parentGroups = new ArrayList<List<Individual>>();
         
        // Get number of parents in total recombination pool. If there's an 
        // 'unpairable' set of size < m then return these without changing 'em
        int pairableParentCount = parents.size();
        int nIgnored = pairableParentCount % arity_;
        pairableParentCount -= nIgnored;
        for (int i = pairableParentCount; i < parents.size(); i++) {
        	children.add(parents.get(i));
        }
        
        // Add parents to reproductive groups of size m (FOR SEX)
        int counter = 0;
        for (int i = 0; i < pairableParentCount; i++) {
            parentGroup.add(parents.get(i));
            counter++;
            if (counter == arity_) {
                parentGroups.add(parentGroup);
                counter = 0;
                parentGroup  = new ArrayList<Individual>();
            }
        }
 
        // Perform (m-1) point crossover
        double r;  // random probability
        for (List<Individual> pg : parentGroups) {
            // Do the crossover with recombination probability defined at top
            r = player34.rnd_.nextDouble();
            if (r < recombinationProbability_) {
                children.addAll(mMinusOnePointCrossover(pg));
            }
            // Else just add the unchanged parents to the kids 
            else  { children.addAll(pg); }
        }
        
        // Reset fitness of children for evaluation later
        for (Individual child : children)  { child.resetFitness(); }
        
        // Return list of kids with kids.size == parents.size
        return children;
    }
    
    // Helper function to recombine(). Actually performs the (m-1) crossover for some
    // input group of parents
    private List<Individual> mMinusOnePointCrossover(List<Individual> parentGroup) {
        List<Individual> children = new ArrayList<Individual>();
        Individual child, parent;
        int childIndex, parentIndex, geneIndex;
        for (childIndex = 0; childIndex < arity_; childIndex++) {
        	parentIndex = childIndex;
        	parent = parentGroup.get(parentIndex);
        	child = new Individual(parent);
            geneIndex = 0;
            // Iterate towards each boundary and iteratively change the 'parentIndex'
            // after each boundary so that alleles are selected from alternating parents
            for (int boundary : crossoverBoundaries_) {  // boundaries always include last index of genes
                while (geneIndex <= boundary) {
                    child.genes[geneIndex] = parent.genes[geneIndex];
                    child.mutationRates[geneIndex] = parent.mutationRates[geneIndex];
                    geneIndex++;
                }
                parentIndex = (parentIndex + 1) % arity_;  // (wrap around)
                parent = parentGroup.get(parentIndex);
            }
            children.add(child);
        }
        return children;
    }

    // This is a a setter for the public static crossoverBoundaries_ list
    private void setcrossoverBoundaries () {
        List<Integer> boundaries = new ArrayList<Integer>();
        // Initialize boundaries below. If the number of genes aren't evenly
        // divisible then that'll be fixed afterwards
        for (int i = 1; i <= arity_; i++) {
            boundaries.add((Individual.NUM_GENES / arity_) * i - 1);
        }
        // Correct boundaries if there's genes left
        int remainder = Individual.NUM_GENES % arity_;
        for (int i = 0; i < boundaries.size(); i++) {
            if (i < remainder)  { boundaries.set(i, boundaries.get(i) + i + 1); }
            else  { boundaries.set(i,  boundaries.get(i) + remainder); }
        }
        crossoverBoundaries_ = boundaries;
    }
}