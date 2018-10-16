import java.util.List;
import java.util.ArrayList;

public class CloneParents implements RecombinationOperator
{
	public List<Individual> recombine (List<Individual> parents)
	{
		List<Individual> children = new ArrayList<Individual>();
        // Just copy the parents using copy constructor
        for (Individual parent : parents) {
            children.add(new Individual(parent));
        }
        return children;
	}
}