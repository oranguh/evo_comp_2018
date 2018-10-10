import java.util.List;

public interface MutationOperator
{
	public void mutate (List<Individual> children);
}