import java.util.ArrayList;

/**
 * Java Implementation of https://github.com/jasonayoder/EvoDevoNKFL
 * 
 * Generation of NKFL is very computationally expensive, and should realistically be limited
 * to n<25 for reasonable generation times.
 * 
 * @author Shen Jackson and Jacob Ashworth
 *
 */
public class FitnessLandscape {
	// Instance Variables
	private int[] visitedTable; // not sure if should keep this, or make private and add accessor methods
	private double[] fitTable; // This is set in generateFitnessTable
	private double[][] interactionTable; // Most likely won't need to access this, but it won't hurt
	public int n; // This is set in constructor
	public int k; // This is set in constructor

	/**
	 * Initializes a NK fitness landscape with the given n and k values
	 * 
	 * @param n gen length (in bits)
	 * @param k number of interactions per bit
	 */
	public FitnessLandscape(int n, int k) {
		this.n = n;
		this.k = k;
		
		this.interactionTable = generateRandomInteractionTable(n, k);
		generateFitnessTable(interactionTable); //this has no return value because it stores its data in the landscape globals
		
		this.visitedTable = new int[(int) Math.pow(2, n)];
		init_visited();
	}

	/**
	 * Generates a completely random table of interactions, does not store any data
	 * in globals as it is unnecessary to save the interaction table
	 * 
	 * @param n standard 'n' value in a NKFL
	 * @param k standard 'k' value in a NKFL
	 * @return random table size [n][2^(k+1)]
	 */
	public double[][] generateRandomInteractionTable(int n, int k) {
		double[][] interactions = new double[n][(int) Math.pow(2, (k + 1))];

		for (int x = 0; x < n; x++) {
			for (int y = 0; y < (int) Math.pow(2, (k + 1)); y++) {
				interactions[x][y] = SeededRandom.rnd.nextDouble();
			}
		}

		return interactions;
	}

	/**
	 * Generates a fitness table (length n) from the passed interaction table
	 * 
	 * Saves the minFitness, maxFitness, and fitnessTable in globals as this information will be useful later
	 * 
	 * @param interactionTable the table of interactions that will be used to create
	 *                         the fitness table
	 */
	public void generateFitnessTable(double[][] interactionTable) {
		double[] fitnessTable = new double[(int) Math.pow(2, n)];
		double minFitness = 100000;
		double maxFitness = 0;

		for (int genotypeInt = 0; genotypeInt < ((int) Math.pow(2, n)); genotypeInt++) // Genotype bitstrings are being
																						// represented as ints =(ex
																						// genotype [1, 0, 1] == 5)
		{
			double fitness = 0;
			int[] genotype = ind2gen(genotypeInt, this.n); // Gets the bitstring for the genotype

			for (int gene = 0; gene < n; gene++) // loops through each gene (bit)
			{
				int[] interactions = new int[k + 1]; // called 'subgen' in python code
				for (int neighbor = 0; neighbor < k + 1; neighbor++) // loop through all our neighbors
				{
					int neighborIndex = (gene + neighbor) % n;
					interactions[neighbor] = genotype[neighborIndex];
				}
				int index = gen2ind(interactions);
				fitness += interactionTable[gene][index]; // add our neighbor's fitness to our own
			}
			fitnessTable[genotypeInt] = fitness; // store our calculated fitness

			// This updates the min and max fitnesses
			if (minFitness > fitness) {
				minFitness = fitness;
			}
			if (maxFitness < fitness) {
				maxFitness = fitness;
			}
		}
		
		this.fitTable = scaleTable(fitnessTable, maxFitness, minFitness);
	}
	
	/**
	 * Scales a table to range from 0-1 instead of (max-min)
	 * called by generateFitnessTable
	 * @param table
	 * @param max
	 * @param min
	 * @return scaled table with max 1 and min 0
	 */
	public double[] scaleTable(double[] table, double max, double min)
	{
		double[] scaledTable = new double[table.length];
		for(int index = 0; index < table.length; index++)
		{
			scaledTable[index] = (table[index] - min)/(max - min); //makes all table values 0-1
			scaledTable[index] = Math.pow(scaledTable[index], 8); //scale
		}
		return scaledTable;
	}

	/**
	 * Brute force method that finds the maximum fitness of the landscape (should be 1 once normalized)
	 * @return
	 */
	public double maxFit() {
		double maxFit = 0.0;
		for (int x = 0; x < getFitTable().length; x++) {
			if (getFitTable()[x] > maxFit) {
				maxFit = getFitTable()[x];
			}
		}
		return maxFit;
	}

	/**
	 * Brute force method that finds the minimum fitness of the landscape (should be 0 once normalized)
	 * @return
	 */
	public double minFit() {
		double minFit = 0.0;
		for (int x = 0; x < getFitTable().length; x++) {
			if (getFitTable()[x] < minFit) {
				minFit = getFitTable()[x];
			}
		}
		return minFit;
	}

	/**
	 * takes a bitstring (genotype) and returns its fitness
	 * @param genotype bitstring representation of genotype
	 * @return
	 */
	public double fitness(int[] genotype) {
		int index = FitnessLandscape.gen2ind(genotype);
		return getFitTable()[index];
	}

	/**
	 * takes a bitstring (genotype) and marks its location on the landscape as 'visited'
	 * @param genotype
	 */
	public void visited(int[] genotype) {
		int index = FitnessLandscape.gen2ind(genotype);
		visitedTable[index] = 1;
	}

	/**
	 * Initializes the visited table to all zeroes
	 */
	public void init_visited() {
		for (int i = 0; i < visitedTable.length; i++) {
			visitedTable[i] = 0;
		}
	}
	
	//Not used by the landscape itself, but called by learning strategies to find their greatest neighbor
	//Copies a lot of arrays to avoid altering location data
	public int[] greatestNeighbor(int[] location)
	{
		int[] greatest = copyArray(location);
		double greatestFitness = this.fitness(greatest);
		
		if(location.length != n) //invalid location
		{
			return null;
		}
		int[] testArray = copyArray(location);
		double testFitness;
		for(int i = 0; i < n; i++)
		{
			testArray[i] = (testArray[i] + 1) % 2; //flip bit to test

			testFitness = this.fitness(testArray);
			if (testFitness > greatestFitness) {
				greatest = copyArray(testArray);
				greatestFitness = testFitness;
			}

			testArray[i] = (testArray[i] + 1) % 2; //flip it back
		}
		return greatest;
	}
	
	public boolean isLocalMaxima(int[] sourceGenotype)
	{
		int[] genotype = copyArray(sourceGenotype);//Don't want to corrupt our source
		double fitness = this.fitness(genotype);

		for (int i = 0; i < genotype.length; i++) {
			genotype[i] = (genotype[i] + 1) % 2;

			if (this.fitness(genotype) > fitness) {
				return false;
			}

			genotype[i] = (genotype[i] + 1) % 2;
		}

		return true;
	}
	
	//Static Helper Methods ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Takes an index (int) and turns it into a binary bitstring
	 * @param index the genotype as an int
	 * @param n standard 'N' of the NKFL
	 * @return the genotype as a bitstring
	 */
	public static int[] ind2gen(int index, int n) {
		int[] genotype = new int[n]; // not correct. need to find how to use np
		if (index >= Math.pow(2, n)) {
			System.out.println("ind2gen error");
			return genotype;
		}
		while (n > 0) {
			n = n - 1;
			if (index % 2 == 0) {
				genotype[n] = 0;
			} else {
				genotype[n] = 1;
			}
			index = index / 2; // this is floor division right?
		}
		return genotype;
	}

	/**
	 * Takes a bitstring (genotype) and turns it into an int
	 * @param genotype bitstring that is the genotype
	 * @return the genotype as an integer
	 */
	public static int gen2ind(int[] genotype) {
		int i = 0;
		int index = 0;
		int amount = genotype.length;
		while (i < amount) {
			index += genotype[i] * Math.pow(2, (amount - i - 1));
			i++;
		}
		return (int) (index);
	}

	public double[] getFitTable() {
		return fitTable;
	}
	
	public int[] getVisitedTable() {
		return visitedTable;
	}
	
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
