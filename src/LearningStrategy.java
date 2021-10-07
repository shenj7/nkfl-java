import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The Learning Strategy class is a single strategy to navigate around a NKFL.
 * 
 * The strategy is represented as an array of integers, and each integer
 * corresponds to a single step in a strategy. Ex: strategyArray [0, 0, 0, 1, 1,
 * 1, 0] will first take three steps of strategy zero, then three steps of
 * strategy 1, then another one step of strategy zero.
 * 
 * Strategies: 0 = random walk 1 = steepest climb
 * 
 * The Learning Strategy currently is capable of only random walking or steepest
 * climbing. This is to attempt to reproduce the results in Dr. Yoder's original
 * code. The code can easily be expanded to accommodate additional possible
 * strategies (such as doing nothing or non-steepest climbing)
 * 
 * @author Jacob Ashworth, Edward Kim, Lyra Lee
 *
 */
public class LearningStrategy {

	public static void main(String[] args) {

		FitnessLandscape landscape = new FitnessLandscape(20, 3);

		// all same genotypes
		int[] genotype1 = new NDArrayManager().array1dNums(30, 0);
		int[] genotype2 = new NDArrayManager().array1dNums(30, 1);

		// half half genotypes
		int[] genotype3 = new int[30];
		int[] genotype4 = new int[30];
		for (int i = 0; i < 15; i++) {
			genotype3[i] = 0;
			genotype4[i] = 1;
		}
		for (int i = 15; i < 30; i++) {
			genotype3[i] = 1;
			genotype4[i] = 0;
		}

		// three section genotypes
		int[] genotype5 = new int[30];
		int[] genotype6 = new int[30];
		for (int i = 0; i < 10; i++) {
			genotype5[i] = 0;
			genotype6[i] = 1;
		}
		for (int i = 10; i < 20; i++) {
			genotype5[i] = 1;
			genotype6[i] = 0;
		}
		for (int i = 20; i < 30; i++) {
			genotype5[i] = 0;
			genotype6[i] = 1;
		}

		// construct all
		LearningStrategy strat1 = new LearningStrategy(landscape, genotype1);
		LearningStrategy strat2 = new LearningStrategy(landscape, genotype2);
		LearningStrategy strat3 = new LearningStrategy(landscape, genotype3);
		LearningStrategy strat4 = new LearningStrategy(landscape, genotype4);
		LearningStrategy strat5 = new LearningStrategy(landscape, genotype5);
		LearningStrategy strat6 = new LearningStrategy(landscape, genotype6);

		// execute all
		strat1.executeStrategy();
		strat2.executeStrategy();
		strat3.executeStrategy();
		strat4.executeStrategy();
		strat5.executeStrategy();
		strat6.executeStrategy();

		// instantiate writer and file
		PrintWriter csvWriter;
		File csvFile = new File("src/plot_data.csv");

		// create file, disregard overwriting
		try {
			csvFile.createNewFile();
		} catch (IOException e) {
			System.err.println("CSV file not created");
		}

		// write data
		try {
			csvWriter = new PrintWriter(csvFile);

			// first row
			csvWriter.print("step,fitness\n");

			// each row is a step
			for (int stepNum = 0; stepNum < strat1.fitnessArray.length; stepNum++) {

				csvWriter.printf("%d,%f,%f,%f,%f,%f,%f\n", stepNum, strat1.fitnessArray[stepNum],
						strat2.fitnessArray[stepNum], strat3.fitnessArray[stepNum], strat4.fitnessArray[stepNum],
						strat5.fitnessArray[stepNum], strat6.fitnessArray[stepNum]);
			}

			//
			System.out.println();
			System.out.println("Learning data successfully written to plot_data.csv");
			System.out.println();

			// close writer
			csvWriter.flush();
			csvWriter.close();

		} catch (FileNotFoundException e) {
			System.err.println("CSV file not found");
		}

	}

	public static final int highestNumStrategy = 1;

	public FitnessLandscape landscape; // This LearningStrategy's NKFL
	public int currentStep = -1; // The most recently executed step of the strategy (starts at -1 because step 0
									// hasn't been run yet)
	public int[] strategyArray; // integer string representing the strategy (0=random walk, 1=steepest climb)
	public double[] fitnessArray;
	public int[] genotype; //
	public double currentFitness; // the current fitness of the genotype
	public int[] originalGenotype;
	public double originalFitness; // save this data so we don't have to recompute it every time we reset

	/**
	 * Initializes a LearningStrategy with the specified strategyArray
	 * 
	 * @param landscape     the FitnessLandscape of the LearningStrategy
	 * @param strategyArray the array representing the strategy
	 */
	public LearningStrategy(FitnessLandscape landscape, int[] strategyArray) {
		this.landscape = landscape;
		this.strategyArray = strategyArray;

		genotype = NDArrayManager.array1dRandInt(landscape.n, 1);
		this.currentFitness = landscape.fitness(genotype);

		this.originalGenotype = NDArrayManager.copyArray1d(genotype);
		this.originalFitness = currentFitness;

		this.fitnessArray = new double[strategyArray.length];
	}

	/**
	 * Initializes a LearningStrategy with a random strategy
	 * 
	 * @param landscape      the FitnessLandscape of the LearningStrategy
	 * @param strategyLength the desired length of the strategy
	 */
	public LearningStrategy(FitnessLandscape landscape, int strategyLength) {
		this(landscape, null);

		strategyArray = NDArrayManager.array1dRandInt(strategyLength, highestNumStrategy);

		fitnessArray = new double[strategyLength];
	}

	/**
	 * Executes steps of the LearningStrategy
	 * 
	 * @param steps number of steps to execute
	 * @return the fitness once the steps are executed
	 */
	public double executeSteps(int steps) {
		for (int step = 0; step < steps; step++) {
			if (currentStep + 1 >= strategyArray.length) {
				System.err.println("A learning strategy took too many steps");
				return -1;
			}
			currentStep++;

			if (strategyArray[currentStep] == 0) {
				this.randomWalk(1);
			} else if (strategyArray[currentStep] == 1) {
				this.steepestClimb(1);
			} else if (strategyArray[currentStep] > highestNumStrategy) {
				System.err.println(
						"Did not recognize bit " + strategyArray[currentStep] + " in strategy[" + currentStep + "]");
				return -1;
			} else {
				System.err.println("highestNumStrategy is incorrect, please correct method executeSteps");
				return -1;
			}

			fitnessArray[currentStep] = currentFitness;
		}

		return currentFitness;
	}

	/**
	 * Executes the entire remainder of the LearningStrategy
	 * 
	 * @return the fitness once the strategy is executed
	 */
	public double executeStrategy() {
		return executeSteps(strategyArray.length - currentStep - 1);
	}

	/**
	 * Executes a single step of the LearningStrategy
	 * 
	 * @return the fitness once the step is executed
	 */
	public double executeStep() {
		return executeSteps(1);
	}

	/**
	 * Randomly walks around the FitnessLandscape
	 * 
	 * @param steps the number of random steps to take
	 */
	private void randomWalk(int steps) {
		for (int step = 0; step < steps; step++) {
			int changeIndex = Math.abs(SeededRandom.rnd.nextInt() % (genotype.length)); // pick a random index
			genotype[changeIndex] = (genotype[changeIndex] + 1) % 2; // flip the location
		}
		this.currentFitness = landscape.fitness(genotype);
	}

	/**
	 * Climbs to the greatest fitness of neighboring genotypes (neighbor means 1 bit
	 * is different in the genotype).
	 * 
	 * @param steps the number of steepest climb steps to take
	 */
	private void steepestClimb(int steps) {
		for (int step = 0; step < steps; step++) {
			genotype = landscape.greatestNeighbor(genotype);
		}
		this.currentFitness = landscape.fitness(genotype);
	}

	public void resetStrategy() {
		this.genotype = NDArrayManager.copyArray1d(this.originalGenotype);
		this.currentStep = -1;
		this.currentFitness = originalFitness;
	}

	public void setOriginalGenotype(int[] genotype) {
		this.originalGenotype = genotype;
		this.originalFitness = landscape.fitness(originalGenotype);
		this.resetStrategy();
	}

	// writeToCSV()
	public void writeToCSV() {

		// instantiate writer and file
		PrintWriter csvWriter;
		File csvFile = new File("src/plot_data.csv");

		// create file, disregard overwriting
		try {
			csvFile.createNewFile();
		} catch (IOException e) {
			System.err.println("CSV file not created");
		}

		// write data
		try {
			csvWriter = new PrintWriter(csvFile);

			// first row
			csvWriter.print("step,fitness\n");

			// each row is a step
			for (int stepNum = 0; stepNum < this.fitnessArray.length; stepNum++) {

				csvWriter.printf("%d,%f\n", stepNum, fitnessArray[stepNum]);
			}

			//
			System.out.println();
			System.out.println("Learning data successfully written to plot_data.csv");
			System.out.println();

			// close writer
			csvWriter.flush();
			csvWriter.close();

		} catch (FileNotFoundException e) {
			System.err.println("CSV file not found");
		}
	}
}
