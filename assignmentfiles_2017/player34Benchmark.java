import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;

public class player34 implements ContestSubmission
{
	Random rnd_;
	ContestEvaluation evaluation_;
    private int evaluations_limit_;
	
	public player34()
	{
		rnd_ = new Random();
	}
	
	public void setSeed(long seed)
	{
		// Set seed of algortihms random process
		rnd_.setSeed(seed);
	}

	private double[][] initializePopulation(int populationSize)
	{
        double[][] populationMatrix = new double[populationSize][10];
    
        for (int row = 0; row < populationMatrix.length; row++)
        {	
        	for (int col = 0; col < populationMatrix[row].length; col++)
        	{
				populationMatrix[row][col] = (int)(Math.random()*11) - 5;
        	}
        }
        // System.out.println(Arrays.toString(fitnessArray));
        return populationMatrix;
	}


	private double[] evaluatePopulation(double[][] populationMatrix)
	{
		double[] fitnessArray = new double[populationMatrix.length];

        for (int row = 0; row < fitnessArray.length; row++)
        {	
        	fitnessArray[row] = (double) evaluation_.evaluate(populationMatrix[row]);
        }
        return fitnessArray;
	}


	private double[] selectTop5inPopulation(double[] evaluationArray)
	{ //TODO: In case of array with same numbers - how to treat? 

		double[] hitList = new double[evaluationArray.length];
		double median = calculateMedian(evaluationArray);
		int count = 0;

		for (int row = 0; row < evaluationArray.length; row++)
        {	
        	if (evaluationArray[row] < median)
        	{
        		hitList[row] = 0;
        	}
        	if (evaluationArray[row] >= median && count <=  evaluationArray.length / 2 - 1)
        	{
        		hitList[row] = 1;
        		count++;
        	}
        }
        return hitList;
	}


	private double[] deepCopy(double[] numArray)
	{
		double[] copyArray = new double [numArray.length];
		for (int row = 0; row < copyArray.length; row++)
        {	
        	copyArray[row] = numArray[row];
        }
        return copyArray;
	}


	private double calculateMedian(double[] numArray)
	{
		// Create a copy of numArray
		double[] sortedArray = deepCopy(numArray);

        // Calculate median
		Arrays.sort(sortedArray);
		double median;
		if (sortedArray.length % 2 == 0)
		    median = ((double)sortedArray[sortedArray.length/2] + (double)sortedArray[sortedArray.length/2 - 1])/2;
		else
		    median = (double) sortedArray[sortedArray.length/2];
  
        return median;
	}


	private double[][] selectNewGeneration(double[][] populationMatrix, double[] hitList)
	{
		double[][] newPopulation = new double[populationMatrix.length][10];
		int counter = 0;

		for (int idx = 0; idx < hitList.length; idx++)
        {
        	if (hitList[idx] == 1)
        	{
        		newPopulation[counter] = deepCopy(populationMatrix[idx]);
        		// create babies
        		newPopulation[counter + 1] = mutantChild(newPopulation[counter]);

        		counter += 2;
        	}
		}
		return newPopulation;
	}


	private double[] mutantChild(double[] parentArray)
	{
		int threshold = (int)(Math.random()*9);
		double[] childArray = new double[parentArray.length];

		for (int idx = 0; idx < parentArray.length; idx++)
		{
			if (idx == threshold)
			{
				childArray[idx] = (int)(Math.random()*11) - 5;
			}
			else
			{
				childArray[idx] = parentArray[idx];
			}

		}
		return childArray;
	}


	public void setEvaluation(ContestEvaluation evaluation)
	{
		// Set evaluation problem used in the run
		evaluation_ = evaluation;
		
		// Get evaluation properties
		Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

		// Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }
    }
    

	public void run()
	{
		// Run your algorithm here
        
        int evals = 0;

        // init population
        int populationSize = 10;
        double[][] populationMatrix = initializePopulation(populationSize);
        double[] evalArray = new double[populationSize];
        double[] hitList = new double[populationSize];

        // calculate fitness
        for (int idx = 0; idx < 1000; idx++)
        {
        	evalArray = evaluatePopulation(populationMatrix);

        	hitList = selectTop5inPopulation(evalArray);
        	System.out.println(Arrays.toString(evalArray));
        	populationMatrix = selectNewGeneration(populationMatrix, hitList);
        	System.out.println(evals);
        	evals++;
        }



        // while(evals < evaluations_limit_){
        //     // Select parents
        //     // Apply crossover / mutation operators

            
        //     // Check fitness of unknown fuction
        //     // Double fitness = (double) evaluation_.evaluate(child);
        //     evals++;
        //     // Select survivors
        // }

	}
}





