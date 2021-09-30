import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Timer;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Jacob Ashworth
 *
 */
class Tests_FitnessLandscape {
	
	
	/**
	 * testNKLandscape_Basic just makes sure the fitTable is the right size, and
	 * makes sure it initializes with varying values in the table
	 */
	@Test
	public void testNKLandscape_Basic() {
		FitnessLandscape landscape = new FitnessLandscape(9, 2);
		// landscape.n = 10;
		// landscape.k = 2;

		double[] table = landscape.fitTable;
		// Make sure the table is the right size
		assertEquals(Math.pow(2, landscape.n), table.length);

		// Make sure the table has multiple different values
		ArrayList<Double> seenValues = new ArrayList<Double>();
		int numDifferentValuesExpected = 10; // We should be able to get at least 10 values out of the landscape
		for (int i = 0; i < table.length; i++) {
			if (!seenValues.contains(table[i])) {
				seenValues.add(table[i]);

				if (seenValues.size() >= numDifferentValuesExpected) {
					break; // Don't need to keep looking if we've seen enough different values
				}
			}
		}
		Assertions.assertTrue(seenValues.size() >= numDifferentValuesExpected);
	}

	/**
	 * testNumPeaksBasic just makes sure a k=0 landscape has 1 peak, and a k=3 has several peaks
	 */
	@Test
	public void testNumPeaksBasic() {
		int n = 15;
		FitnessLandscape landscape = new FitnessLandscape(n, 0); // Should have 1 peak
		int numPeaks = findNumPeaks(landscape);

		Assertions.assertTrue(numPeaks == 1);
		
		FitnessLandscape landscape2 = new FitnessLandscape(n, 3); //Should have more than 1 peak
		int numPeaks2 = findNumPeaks(landscape2);

		Assertions.assertTrue(numPeaks2 > 1); //Should be 1 peak in a K=0 landscape
	}
	
	/**
	 * runLandscapeAnalysis generates 10 NK landscapes with n=15 at each possible k level, then prints data describing the landscapes to console
	 */
	//@Test
	public void runLandscapeAnalysis() {
		int n = 20;
		int[] ks = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
		int samples = 10;
		
		for(int k : ks)
		{
			double startTime = System.currentTimeMillis();
			double[] kOptimaTable = new double[samples];
			for(int s = 0; s < samples; s++)
			{
				FitnessLandscape landscape = new FitnessLandscape(n, k);
				int peaks = findNumPeaks(landscape);
				kOptimaTable[s] = peaks;
			}
			
			double averageNumPeaks = 0;
			for(double i : kOptimaTable)
			{
				averageNumPeaks += i;
			}
			averageNumPeaks /= samples;
			
			double standardDeviation = calculateSD(kOptimaTable);
			
			double chanceToLand = (averageNumPeaks/Math.pow(2, n) * 100.0);
			double endTime = System.currentTimeMillis();
			System.out.println("Generated " + samples + " landscapes with k=" + k + " in " + ((endTime - startTime)/1000) + " seconds");
			//System.out.println("k:"+k+ "   local peaks:" + averageNumPeaks + " +\\- " + standardDeviation + "   chance of landing on a peak: " + chanceToLand + "%");
		}
	}
	
	//runLandscapeAnalysis Helper Methods--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Finds the number of peaks in the landscape
	 * @param landscape FitnessLandscape to be tested upon
	 * @return number of local maxima
	 */
	public int findNumPeaks(FitnessLandscape landscape) {
		int n = landscape.n;
		int[] genotype = new int[n];
		int numPeaks = 0;

		for (int i = 0; i < Math.pow(2, n); i++) {
			genotype = FitnessLandscape.ind2gen(i, n);

			if (greaterThanNeighbors(genotype, landscape)) {
				numPeaks++;
			}
		}
		return numPeaks;
	}

	/**
	 * Finds out if a specific genotype in a landscape is greater than all its neighbors
	 * @param genotype bitstring of the genotype
	 * @param landscape landscape the genotype is in
	 * @return boolean, true if it is a local maxima, false if it is not
	 */
	public boolean greaterThanNeighbors(int[] genotype, FitnessLandscape landscape) {
		double fitness = landscape.fitness(genotype);
		// To make this a little more efficient, this modifies the genotype array, and
		// sets it back afterwards

		for (int i = 0; i < genotype.length; i++) {
			genotype[i] = (genotype[i] + 1) % 2;

			if (landscape.fitness(genotype) > fitness) {
				return false;
			}

			genotype[i] = (genotype[i] + 1) % 2;
		}

		return true;
	}
	
	
	//General Helper Methods --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Calculates standard deviation
	 * @param numArray
	 * @return standard deviation
	 */
	public static double calculateSD(double numArray[])
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }
	
	/**
	 * Makes a copy of the source array
	 * @param source
	 * @return a copy of the source array
	 */
	public static int[] copyArray(int[] source)
	{
		int[] cpy = new int[source.length];
		for(int i = 0; i < source.length; i++)
		{
			cpy[i] = source[i];
		}
		return cpy;
	}
}