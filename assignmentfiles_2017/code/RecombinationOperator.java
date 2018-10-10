import java.util.List;

public interface RecombinationOperator
{
	public List<Individual> recombine (List<Individual> parents);
}