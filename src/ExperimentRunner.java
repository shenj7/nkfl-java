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
		int childrenPercentage = 50;
		double mutationPercentage = 2;
		int strategyLength = 15;
		int simulations = 1;
		int n = 20;
		int k = 6;
		int incrementCSVoutput = 10;
		boolean hillClimbSteepest = true;
		long seed = SeededRandom.rnd.nextLong();
		SeededRandom.rnd.setSeed(seed);
		
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
		File csvFile = new File("src/sim_data.csv");
		try {
			csvFile.createNewFile();
		} catch (IOException e) {
			System.err.println("CSV file not created");
		}
		try {
			csvWriter = new PrintWriter(csvFile);

			// first row (output simulation params to the csv)
			csvWriter.printf("generations:%d,popsPerGen:%d,childrenPercentage:%d,mutationRate:%f,strategyLength:%d,SimulationsRun:%d,N:%d K:%d,HillClimbSteepest:%b,Seed:%d\n",numGenerations,popsPerGeneration,childrenPercentage,mutationPercentage,strategyLength,simulations,n,k,hillClimbSteepest,seed);
			
			
			//Writes average of multiple simulations (currently unused)--------------------------------------------------------------------------------
			// second row (column headers)
//			csvWriter.print("gen_num,avg_fit,");
//			for(int step = 0; step < strategyLength; step++)
//			{
//				csvWriter.print("step " + step + ",");
//			}
//			csvWriter.print("\n");
			
			// each row is a step, output the data from the average of the simulations
//			for (int gen = 0; gen < sims.get(0).generations.size(); gen+=incrementCSVoutput) {
//
//				//Calculate and output the average fitness from all simulations
//				double fitAvg = 0;
//				for(EvolutionSimulation sim : sims)
//				{
//					fitAvg += sim.generations.get(gen).averageFitness();
//				}
//				fitAvg /= sims.size();
//				csvWriter.printf("%d,%f,", gen, fitAvg);
//				
//
//				for(int step = 0; step < 14; step++)
//				{
//					double percentAtIndexAvg = 0;
//					for(EvolutionSimulation sim : sims)
//					{
//						percentAtIndexAvg += sim.generations.get(gen).getPercentWithStepAtIndex(1, step);
//					}
//					percentAtIndexAvg /= sims.size();
//					
//					csvWriter.printf("%f,", percentAtIndexAvg);
//				}
//				double percentAtIndexAvg = 0;
//				for(EvolutionSimulation sim : sims)
//				{
//					percentAtIndexAvg += sim.generations.get(gen).getPercentWithStepAtIndex(1, 14);
//				}
//				percentAtIndexAvg /= sims.size();
//				csvWriter.printf("%f\n", percentAtIndexAvg);
//			}
//			csvWriter.print("\n\n\n\n");
//			--------------------------------------------------------------------------------------------------------------------------------------------
			
			for(int i = 0; i < sims.size(); i++)
			{
				csvWriter.printf("Experiment Number: %d\n", i);
				ExperimentRunner.writeExperimentToCSV(csvWriter, sims.get(i), incrementCSVoutput);
				ExperimentRunner.writeExpermentPureHCComparison(csvWriter, sims.get(i), hillClimbSteepest);
			}

			//
			System.out.println();
			System.out.println("Successfully written to sim_data.csv");
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
