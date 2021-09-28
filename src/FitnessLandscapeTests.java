import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Scanner;

class FitnessLandscapeTests {
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

	@Test
	public void testNumPeaks() {
		int n = 15;
		FitnessLandscape landscape = new FitnessLandscape(n, 0); // Should have 1 peak
		int numPeaks = findNumPeaks(landscape, n);

		Assertions.assertTrue(numPeaks == 1);

		FitnessLandscape landscape2 = new FitnessLandscape(n, 3); // Should have more than 1 peak
		int numPeaks2 = findNumPeaks(landscape2, n);

		Assertions.assertTrue(numPeaks2 > 1); // Should be 1 peak in a K=0 landscape

		// Uncomment the following line to run the landscape analysis
		runLandscapeAnalysis();
	}

	public void runLandscapeAnalysis() {
		int n = 15;
		int[] ks = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
		int samples = 10;

		for (int k : ks) {
			int[] kOptimaTable = new int[samples];
			for (int s = 0; s < samples; s++) {
				FitnessLandscape landscape = new FitnessLandscape(n, k);
				int peaks = findNumPeaks(landscape, n);
				kOptimaTable[s] = peaks;
			}

			double averageNumPeaks = 0;
			for (int i : kOptimaTable) {
				averageNumPeaks += i;
			}
			averageNumPeaks /= samples;

			double standardDeviation = 0;
			for (int i : kOptimaTable) {
				standardDeviation += Math.abs((double) i - averageNumPeaks);
			}
			standardDeviation /= samples;

			double chanceToLand = (averageNumPeaks / Math.pow(2, n) * 100.0);
			System.out.println("k:" + k + "   local peaks:" + averageNumPeaks + " +\\- " + standardDeviation
					+ "   chance of landing on a peak: " + chanceToLand);
		}
	}

	public int[] copyArray(int[] source) {
		int[] cpy = new int[source.length];
		for (int i = 0; i < source.length; i++) {
			cpy[i] = source[i];
		}
		return cpy;
	}

	public int findNumPeaks(FitnessLandscape landscape, int n) {
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

}