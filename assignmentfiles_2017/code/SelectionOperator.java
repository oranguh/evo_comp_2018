import java.util.List;

public interface SelectionOperator
{
	public List<Individual> select (Population population, int drawCount);
}