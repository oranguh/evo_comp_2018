import java.util.List;

public class SelectAll implements SelectionOperator
{
    public List<Individual> select (Population population, int drawCount) 
    {
    	return population.individuals;
    }
}