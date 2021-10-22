import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class ExperimentRunner {
	
	
	public static void main(String[] args) {
		//Experiment Variables
		int numGenerations = 100;
		int popsPerGeneration = 100;
		int childrenPercentage = 75;
		double mutationPercentage = 2;
		int strategyLength = 15;
		int simulations = 1;
		int n = 15;
		int k = 6;
		int incrementCSVoutput = 10;
		boolean hillClimbSteepest = true;
		long seed = SeededRandom.rnd.nextLong();
		String experimentName = "Experiment_" + seed;
		SeededRandom.rnd.setSeed(seed);
		
		final String CSVOutputParamatersHeader = "EXPERIMENT_PARAMS";
//		final String New
		
		ArrayList<EvolutionSimulation> sims = new ArrayList<EvolutionSimulation>();
		for(int sample = 0; sample < simulations; sample++)
		{
			//Run a EvoSimulation
			FitnessLandscape landscape = new FitnessLandscape(n, k); //Make a landscape
			
			//Make the simulation (from constructor EvolutionSimulation(FitnessLandscape landscape, int popsPerGeneration, int numGenerations, double mutationPercentage, int strategyLength, double percentNewPerGeneration)
			EvolutionSimulation sim = new EvolutionSimulation(landscape, popsPerGeneration, numGenerations, mutationPercentage, strategyLength, childrenPercentage, hillClimbSteepest);
			
			//Run the simulation
			sim.runSimulation();
			
			//Get the final generation and print its average fitness
			StrategyGeneration res = sim.generations.get(sim.generations.size() -1);
			System.out.println("Final average fitness for simulation "+sample+": " + res.averageFitness());
			
			//Put the simulation in our sim list
			sims.add(sim);
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
			csvWriter.printf(CSVOutputParamatersHeader + ",%d,%d,%d,generations:%d,popsPerGen:%d,childrenPercentage:%d,mutationRate:%f,strategyLength:%d,SimulationsRun:%d,N:%d K:%d,HillClimbSteepest:%b,Seed:%d\n",simulations,numGenerations/incrementCSVoutput,strategyLength,numGenerations,popsPerGeneration,childrenPercentage,mutationPercentage,strategyLength,simulations,n,k,hillClimbSteepest,seed);
			
					
			for(int i = 0; i < sims.size(); i++)
			{
				csvWriter.printf("Experiment Number: ,%d\n", i);
				ExperimentRunner.writeExperimentToCSV(csvWriter, sims.get(i), incrementCSVoutput);
				ExperimentRunner.writeExpermentPureHCComparison(csvWriter, sims.get(i), hillClimbSteepest);
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
	
	public static void writeExperimentToCSV(PrintWriter csvWriter, EvolutionSimulation sim, int incrementCSVoutput) {
		
		ArrayList<StrategyGeneration> generations = sim.generations;
		// each row is a step
		for (int gen = 0; gen < generations.size(); gen+=incrementCSVoutput) {

			csvWriter.printf("%d,%f,", gen, generations.get(gen).averageFitness());
			for(int step = 0; step < 14; step++)
			{
				csvWriter.printf("%f,", generations.get(gen).getPercentWithStepAtIndex(1, step));
			}
			csvWriter.printf("%f\n", generations.get(gen).getPercentWithStepAtIndex(1, 14));
		}
	}
	
	public static void writeExpermentPureHCComparison(PrintWriter csvWriter, EvolutionSimulation sim, boolean hillClimbSteepest) {
		csvWriter.printf("Comparison: Pure SHC,");
		
		int[] constructorArray = new int[15];
		ArrayList<int[]> strategies = new ArrayList<int[]>();
		for(int j = 0; j < 15; j++)
		{
			constructorArray[j] = 1;
		}
		for(int j = 0; j < 10000; j++)
		{
			strategies.add(NDArrayManager.copyArray1d(constructorArray));
		}
		ArrayList<LearningStrategy> strats = new ArrayList<LearningStrategy>();
		for(int x = 0; x < 10000; x++)
		{
			strats.add(new LearningStrategy(sim.generations.get(0).landscape, strategies.get(x), hillClimbSteepest));
		}
		StrategyGeneration gen = new StrategyGeneration(strats, hillClimbSteepest);
		gen.runAllStrategies();
		csvWriter.printf("%f", gen.averageFitness());
		
		csvWriter.printf("\n\n");
	}
	
}
