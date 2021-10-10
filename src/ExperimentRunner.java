import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ExperimentRunner {
	
	
	public static void main(String[] args) {
		int numGenerations = 200;
		int popsPerGeneration = 10000;
		int childrenPercentage = 50;
		double mutationPercentage = 2;
		int strategyLength = 15;
		int simulations = 6;
		
		
		ArrayList<EvolutionSimulation> sims = new ArrayList<EvolutionSimulation>();
		for(int sample = 0; sample < simulations; sample++)
		{
			//Run a EvoSimulation
			FitnessLandscape landscape = new FitnessLandscape(15, 6);
			EvolutionSimulation sim = new EvolutionSimulation(landscape, popsPerGeneration, numGenerations, mutationPercentage, strategyLength, childrenPercentage);
			sim.runSimulation();
			StrategyGeneration res = sim.generations.get(sim.generations.size() -1);
			System.out.println("Final average fitness for simulation "+sample+": " + res.averageFitness());
			sims.add(sim);
		}
		
		PrintWriter csvWriter;
		File csvFile = new File("src/sim_data.csv");
		
		try {
			csvFile.createNewFile();
		} catch (IOException e) {
			System.err.println("CSV file not created");
		}
		
		try {
			csvWriter = new PrintWriter(csvFile);

			// first row (simulation params)
			csvWriter.printf("generations:%d,popsPerGen:%d,childrenPercentage:%d,mutationRate:%f,strategyLength:%d,SimulationsRun:%d\n",numGenerations,popsPerGeneration,childrenPercentage,mutationPercentage,strategyLength,simulations);
			// second row (column headers)
			csvWriter.print("gen_num,avg_fit,s0,s1,s2,s3,s4,s5,s6,s7,s8,s9,s10,s11,s12,s13,s14\n");

			int incrementBy = 10;
			// each row is a step
			for (int gen = 0; gen < sims.get(0).generations.size(); gen+=incrementBy) {

				double fitAvg = 0;
				for(EvolutionSimulation sim : sims)
				{
					fitAvg += sim.generations.get(gen).averageFitness();
				}
				fitAvg /= sims.size();
				
				csvWriter.printf("%d,%f,", gen, fitAvg);
				
				for(int step = 0; step < 14; step++)
				{
					double percentAtIndexAvg = 0;
					for(EvolutionSimulation sim : sims)
					{
						percentAtIndexAvg += sim.generations.get(gen).getPercentWithStepAtIndex(1, step);
					}
					percentAtIndexAvg /= sims.size();
					
					csvWriter.printf("%f,", percentAtIndexAvg);
				}
				double percentAtIndexAvg = 0;
				for(EvolutionSimulation sim : sims)
				{
					percentAtIndexAvg += sim.generations.get(gen).getPercentWithStepAtIndex(1, 14);
				}
				percentAtIndexAvg /= sims.size();
				csvWriter.printf("%f\n", percentAtIndexAvg);
			}
			
			csvWriter.print("\n\n\n\n");
			for(int i = 0; i < sims.size(); i++)
			{
				csvWriter.printf("%d\n", i);
				
				ArrayList<StrategyGeneration> generations = sims.get(i).generations;
				// each row is a step
				for (int gen = 0; gen < generations.size(); gen+=incrementBy) {

					csvWriter.printf("%d,%f,", gen, generations.get(gen).averageFitness());
					for(int step = 0; step < 14; step++)
					{
						csvWriter.printf("%f,", generations.get(gen).getPercentWithStepAtIndex(1, step));
					}
					csvWriter.printf("%f\n", generations.get(gen).getPercentWithStepAtIndex(1, 14));
				}
				csvWriter.printf("Comparison: Pure SHC,");
				
				int[] constructorArray = new int[15];
				ArrayList<int[]> strategies = new ArrayList<int[]>();
				for(int j = 0; j < 15; j++)
				{
					constructorArray[j] = 1;
				}
				for(int k = 0; k < 10000; k++)
				{
					strategies.add(NDArrayManager.copyArray1d(constructorArray));
				}
				ArrayList<LearningStrategy> strats = new ArrayList<LearningStrategy>();
				for(int x = 0; x < 10000; x++)
				{
					strats.add(new LearningStrategy(sims.get(i).generations.get(0).landscape, strategies.get(x)));
				}
				StrategyGeneration gen = new StrategyGeneration(strats);
				gen.runAllStrategies();
				csvWriter.printf("%f", gen.averageFitness());
				
				csvWriter.printf("\n\n");
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
}
