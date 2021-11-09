import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExperimentRunner {
	static boolean enableReliabilityTest = false;
	static int reliabilitySampleSize = 0;
	static final String CSVOutputParamatersHeader = "EXPERIMENT_PARAMS";
	static final String CSVOutputFitnessAtStepsHeader = "FITNESS_AT_STEPS";
	static final String CSVOutputStuckAtLocalOptimaHeader = "STUCK_AT_LOCAL_OPTIMA";
	static final String CSVOutputStepFrequencyHeader = ":STEP_FREQUENCIES_OF";//put step number at beginning
	
	
	public static void main(String[] args) {
		//Experiment Variables
		int numGenerations = 100;
		int popsPerGeneration = 100;
		int childrenPercentage = 100;
		double mutationPercentage = 2;
		int strategyLength = 15;
		int simulations = 1;
		int numDifferentStarts = 1;
		int numRunsFromEachStart = 1;
		int n = 15;
		int k = 6;
		int incrementCSVoutput = 10;
		boolean hillClimbSteepest = true;
		String selectionType = "ranked_exponential"; //Options are truncation, ranked_linear and ranked_exponential
		long seed = 803;//SeededRandom.rnd.nextInt();
		String experimentName = "Experiment_" + seed + "_" + selectionType;
		SeededRandom.rnd.setSeed(seed);
		double reportPercentage = 10; //Reports the top X% of each generation as 'data'
		boolean enableReliabilityTest = true;
		int reliabilitySampleSize = 100;
		boolean useDependentStrategySteps = false;
		boolean useOnlyDependentStrategySteps = false;
		
		
		
		LearningStrategy.useDependentStrategySteps = useDependentStrategySteps;
		LearningStrategy.useOnlyDependentStrategySteps = useOnlyDependentStrategySteps;
		ExperimentRunner.enableReliabilityTest = enableReliabilityTest;
		ExperimentRunner.reliabilitySampleSize = reliabilitySampleSize;
		numGenerations++;//account for gen 0
		ArrayList<EvolutionSimulation> sims = new ArrayList<EvolutionSimulation>();
		for(int sample = 0; sample < simulations; sample++)
		{
			//Run a EvoSimulation
			FitnessLandscape landscape = new FitnessLandscape(n, k); //Make a landscape
			
			for(int start = 0; start < numDifferentStarts; start++)
			{
				int[] startingLocation = NDArrayManager.array1dRandInt(n, 2);
				
				for(int runNum = 0; runNum < numRunsFromEachStart; runNum++)
				{
					//Make the simulation (from constructor EvolutionSimulation(FitnessLandscape landscape, int popsPerGeneration, int numGenerations, double mutationPercentage, int strategyLength, double percentNewPerGeneration)
					String simNum = sample + "." + start + "." + runNum;
					EvolutionSimulation sim = new EvolutionSimulation(landscape, popsPerGeneration, numGenerations, mutationPercentage, strategyLength, childrenPercentage, hillClimbSteepest, startingLocation, selectionType);
					sim.setStringNum(simNum);
					//Run the simulation
					sim.runSimulation();
					
					//Get the final generation and print its average fitness
					StrategyGeneration res = sim.generations.get(sim.generations.size() -1);
					System.out.println("Final average fitness for simulation "+ simNum + ": " + res.averageFitness());
					
					//Put the simulation in our sim list
					sims.add(sim);
				}
			}
		}
		
		//CSV export setup
		PrintWriter csvWriter;
		File csvFile = new File("data/" + experimentName + ".csv");
		try {
			csvFile.createNewFile();
		} catch (IOException e) {
			System.err.println("CSV file not created");
		}
		try {
			csvWriter = new PrintWriter(csvFile);

			// first row (output simulation params to the csv)
			csvWriter.printf(CSVOutputParamatersHeader + ",%d,%d,%d,%d,%d,%d,%f,%d,%d,%b,%d,%f\n",simulations,(int)Math.ceil((double)numGenerations/(double)incrementCSVoutput),strategyLength,numGenerations,popsPerGeneration,childrenPercentage,mutationPercentage,n,k,hillClimbSteepest,seed,reportPercentage);
			
					
			for(int i = 0; i < sims.size(); i++)
			{
				EvolutionSimulation simulation = sims.get(i);
				csvWriter.printf("Experiment Number: ," + simulation.simNum + "," + i + "\n");
				ExperimentRunner.writeExperimentToCSV(csvWriter, simulation, incrementCSVoutput, reportPercentage);
				ExperimentRunner.writeExpermentPureHCComparison(csvWriter, simulation, hillClimbSteepest);
				System.out.println("Sucessfully wrote experiment " + i);
			}

			//
			System.out.println();
			System.out.println("Successfully written to " + experimentName + ".csv");
			System.out.println();

			// close writer
			csvWriter.flush();
			csvWriter.close();

		} catch (FileNotFoundException e) {
			System.err.println("CSV file not found");
		}
		
//		System.out.println("");
	}
	
	public static void writeExperimentToCSV(PrintWriter csvWriter, EvolutionSimulation sim, int incrementCSVoutput, double reportPercentage) {
		
		ArrayList<StrategyGeneration> generations = new ArrayList<StrategyGeneration>();//sim.generations.getGenerationOfTopPercent(reportPercentage);
		for(StrategyGeneration gen : sim.generations)
		{
			generations.add(gen.getGenerationOfTopPercent(reportPercentage));
		}
		// each row is a step
		for (int gen = 0; gen < generations.size(); gen+=incrementCSVoutput) {

			if(enableReliabilityTest)
			{
				csvWriter.printf("%d,%f,%f,%f\n", gen, generations.get(gen).averageFitness(),generations.get(gen).averageFitnessRELIABILITY(reliabilitySampleSize),sim.generations.get(gen).averageFitness());
			}
			else//normal case
			{
				csvWriter.printf("%d,%f,%f\n", gen, generations.get(gen).averageFitness(),sim.generations.get(gen).averageFitness());
			}
			if(LearningStrategy.useDependentStrategySteps)
			{
				for(int i=0; i<4; i++)
				{
					ExperimentRunner.writeStepFrequenciesRow(csvWriter, generations.get(gen), sim.strategyLength, i);
				}
			}
			else
			{
				for(int i=0; i<2; i++)
				{
					ExperimentRunner.writeStepFrequenciesRow(csvWriter, generations.get(gen), sim.strategyLength, i);
				}
			}
			
			
//			for(int step = 0; step < sim.strategyLength - 1; step++)
//			{
//				csvWriter.printf("%f,", generations.get(gen).getPercentWithStepAtIndex(1, step));
//			}
//			csvWriter.printf("%f\n", generations.get(gen).getPercentWithStepAtIndex(1, sim.strategyLength - 1));
			csvWriter.printf(CSVOutputFitnessAtStepsHeader + ",");
			for(int step = 0; step < sim.strategyLength - 1; step++)
			{
				csvWriter.printf("%f,", generations.get(gen).averageFitnessAtStep(step));
			}
			csvWriter.printf("%f\n", generations.get(gen).averageFitnessAtStep(sim.strategyLength - 1));
			csvWriter.printf(CSVOutputStuckAtLocalOptimaHeader + ",");
			for(int step = 0; step < sim.strategyLength - 1; step++)
			{
				csvWriter.printf("%f,", generations.get(gen).getPercentStuckAtLocalOptima(step));
			}
			csvWriter.printf("%f\n", generations.get(gen).getPercentStuckAtLocalOptima(sim.strategyLength - 1));
		}
	}
	
	public static void writeStepFrequenciesRow(PrintWriter csvWriter, StrategyGeneration gen, int strategyLength, int stepnum)
	{
		csvWriter.print(stepnum + CSVOutputStepFrequencyHeader + ",");
		for(int step = 0; step < strategyLength-1; step++)
		{
			csvWriter.printf("%f,", gen.getPercentWithStepAtIndex(stepnum, step));
		}
		csvWriter.printf("%f\n", gen.getPercentWithStepAtIndex(stepnum, strategyLength-1));
	}
	
	public static void writeExpermentPureHCComparison(PrintWriter csvWriter, EvolutionSimulation sim, boolean hillClimbSteepest) {
		csvWriter.printf("Comparison: Pure SHC,");
		
		int[] constructorArray = new int[sim.strategyLength];
		
		for(int j = 0; j < sim.strategyLength; j++)
		{
			constructorArray[j] = 1;
		}
		
		double averageFitness = sim.landscape.testStrategyOnLandscape(constructorArray, sim.startingLocation, 1000, hillClimbSteepest);
		
		csvWriter.printf("%f", averageFitness);
		
		csvWriter.printf("\n");
	}
	
}
