/**
 * The Learning Strategy class is a single strategy to navigate around a NKFL.
 * 
 * The strategy is represented as an array of integers, and each integer corresponds to a single step in a
 * strategy.  Ex: strategyArray [0, 0, 0, 1, 1, 1, 0] will first take three steps of strategy zero, then
 * three steps of strategy 1, then another one step of strategy zero.
 * 
 * Strategies:
 * 0 = random walk
 * 1 = steepest climb
 * 
 * The Learning Strategy currently is capable of only random walking or steepest climbing.  This is to attempt
 * to reproduce the results in Dr. Yoder's original code.  The code can easily be expanded to accommodate
 * additional possible strategies (such as doing nothing or non-steepest climbing)
 * 
 * @author Jacob Ashworth
 *
 */
public class LearningStrategy {
	public FitnessLandscape landscape;//This LearningStrategy's NKFL
	public int currentStep = -1; //The most recently executed step of the strategy (starts at -1 because step 0 hasn't been run yet)
	public int[] strategyArray; //integer string representing the strategy (0=random walk, 1=steepest climb)
	public double currentFitness; //the current fitness of the genotype
	public int[] genotype;
	
	/**
	 * Initializes a LearningStrategy with the specified strategyArray
	 * @param landscape the FitnessLandscape of the LearningStrategy
	 * @param strategyArray the array representing the strategy
	 */
	public LearningStrategy(FitnessLandscape landscape, int[] strategyArray)
	{
		this.landscape = landscape;
		
		this.strategyArray = strategyArray;
		
		genotype = new int[landscape.n];
		for(int i = 0; i < landscape.n; i++)
		{
			genotype[i] = Math.abs(SeededRandom.rnd.nextInt() % 2); //Bitstring of zeroes and ones
		}
		this.currentFitness = landscape.fitness(genotype);
	}
	
	/**
	 * Initializes a LearningStrategy with a random strategy
	 * @param landscape the FitnessLandscape of the LearningStrategy
	 * @param strategyLength the desired length of the strategy
	 */
	public LearningStrategy(FitnessLandscape landscape, int strategyLength)
	{
		this(landscape, null);
		
		strategyArray = new int[strategyLength];
		for(int i = 0; i < strategyLength; i++)
		{
			strategyArray[i] = Math.abs(SeededRandom.rnd.nextInt() % 2); //0 (random walk) or 1 (steepest climb)
		}
	}
	
	/**
	 * Executes steps of the LearningStrategy
	 * @param steps number of steps to execute
	 * @return the fitness once the steps are executed
	 */
	public double executeSteps(int steps)
	{
		for(int step = 0; step < steps; step++)
		{
			if(currentStep + 1 >= strategyArray.length)
			{
				System.err.println("A learning strategy took too many steps");
				return -1;
			}
			currentStep++;
			
			if(strategyArray[currentStep] == 0)
			{
				this.randomWalk(1);
			}
			else if(strategyArray[currentStep] == 1)
			{
				this.steepestClimb(1);
			}
			else
			{
				System.err.println("Did not recognize bit " + strategyArray[currentStep] + " in strategy[" + currentStep + "]");
				return -1;
			}
		}
		return currentFitness;
	}
	
	/**
	 * Executes the entire remainder of the LearningStrategy
	 * @return the fitness once the strategy is executed
	 */
	public double executeStrategy()
	{
		return executeSteps(strategyArray.length - currentStep - 1);
	}
	
	/**
	 * Executes a single step of the LearningStrategy
	 * @return the fitness once the step is executed
	 */
	public double executeStep()
	{
		return executeSteps(1);
	}
	
	/**
	 * Randomly walks around the FitnessLandscape
	 * @param steps the number of random steps to take
	 */
	private void randomWalk(int steps)
	{
		for(int step = 0; step < steps; step++)
		{
			int changeIndex = Math.abs(SeededRandom.rnd.nextInt() % (genotype.length)); //pick a random index
			genotype[changeIndex] = (genotype[changeIndex] + 1) % 2; //flip the location
		}
		this.currentFitness = landscape.fitness(genotype);
	}
	
	/**
	 * Climbs to the greatest fitness of neighboring genotypes (neighbor means 1 bit is different in the genotype).
	 * @param steps the number of steepest climb steps to take
	 */
	private void steepestClimb(int steps)
	{
		for(int step = 0; step < steps; step++)
		{
			genotype = landscape.greatestNeighbor(genotype);
		}
		this.currentFitness = landscape.fitness(genotype);
	}
}
