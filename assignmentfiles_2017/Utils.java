import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.File;

public class Utils{

	public static double calculateMedian(double[] numArray)
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


	public static double mean(double[] m) 
	{
	    double sum = 0;
	    for (int i = 0; i < m.length; i++) {
	        sum += m[i];
	    }
	    return sum / m.length;
	}


	public static double[] deepCopy(double[] numArray)
	{
		double[] copyArray = new double [numArray.length];
		for (int row = 0; row < copyArray.length; row++)
        {	
        	copyArray[row] = numArray[row];
        }
        return copyArray;
	}


	public static double evaluateSimilarity(double[][] populationMatrix)
	{
		double[][] similarityMatrix = new double[populationMatrix.length][populationMatrix.length];
		double[] rowSimilarity = new double[populationMatrix.length];
		double diversity;

		for (int row = 0; row < similarityMatrix.length; row++)
		{	for (int col = 0; col < similarityMatrix.length; col++)
			{
				similarityMatrix[row][col] = cosineSimilarity(populationMatrix[row], populationMatrix[col]);
			}
		}

		//Take mean per row
		for (int row = 0; row < similarityMatrix.length; row++)
		{
			rowSimilarity[row] = mean(similarityMatrix[row]);
		}

		diversity = mean(rowSimilarity);



		return diversity;
	}


	public static double cosineSimilarity(double[] vectorA, double[] vectorB)
	 {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;

	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   

	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}


	public static double[][] initializePopulation(int populationSize)
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


	// public static void write2csv(String fileName, double[] array)
	// {
	// 	String slash = File.separator;
	// 	FileWriter writer = new FileWriter("output" + slash + fileName + ".csv");

	// 	for (int j = 0; j < array.length; j++) {
	// 	    writer.append(String.valueOf(array[j]));
	// 	    writer.append("\n");
	// 	}
	// 	writer.close();
	// }





}