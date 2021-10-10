import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class EvolutionSimulation {

	//Currently runs random sim with given params
	
	//Sumulation Paramaters (move to config file eventually)
	int popsPerGeneration;
	int numGenerations;
	int childrenPerGeneration;
	double mutationPercentage; //% mutation rate
	int strategyLength;
	FitnessLandscape landscape;
	
	//Instance variables
	public ArrayList<StrategyGeneration> generations = new ArrayList<StrategyGeneration>();
	//generations ArrayList contains which step we are on
	
	public EvolutionSimulation(FitnessLandscape landscape, int popsPerGeneration, int numGenerations, double mutationPercentage, int strategyLength, double percentNewPerGeneration)
	{
		this.landscape = landscape;
		this.popsPerGeneration = popsPerGeneration;
		this.numGenerations = numGenerations;
		this.mutationPercentage = mutationPercentage;
		this.childrenPerGeneration = (int) ((double)popsPerGeneration * (double)percentNewPerGeneration / 100);
		this.strategyLength = strategyLength;
		setupSimulation();
	}
	
	public void setupSimulation()
	{
		StrategyGeneration gen0 = new StrategyGeneration(landscape, popsPerGeneration, strategyLength);
		generations.add(gen0);
		gen0.runAllStrategies();
	}
	
	public void runSimulation()
	{
		for(int i = generations.size(); i < numGenerations; i++)
		{
			generations.get(generations.size() - 1).sortStrategies();
			String exgen = NDArrayManager.array1dAsString(generations.get(generations.size() - 1).getStrategyAtIndex(0).strategyArray);
//			System.out.println("Running gen " + i + " of " + numGenerations + ", average fitness: " + generations.get(generations.size() - 1).averageFitness() + "  " + exgen);
			//Make the next generation
			StrategyGeneration nextGen = StrategyGenerationFactory.generateTruncation(generations.get(generations.size() - 1), childrenPerGeneration);
			nextGen.mutateGeneration(mutationPercentage);
			generations.add(nextGen);
			//Run the next generation
			nextGen.runAllStrategies();
		}
//		writeExperimentToCSV();
	}
	
	public void writeExperimentToCSV() {
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
			csvWriter.printf("generations:%d,popsPerGen:%d,childrenPerGen:%d,mutationRate:%f,strategyLength:%d\n",numGenerations,popsPerGeneration,childrenPerGeneration,mutationPercentage,strategyLength);
			// second row (column headers)
			csvWriter.print("gen_num,avg_fit,s0,s1,s2,s3,s4,s5,s6,s7,s8,s9,s10,s11,s12,s13,s14\n");

			int incrementBy = 10;
			// each row is a step
			for (int gen = 0; gen < generations.size(); gen+=incrementBy) {

				csvWriter.printf("%d,%f,", gen, generations.get(gen).averageFitness());
				for(int step = 0; step < 14; step++)
				{
					csvWriter.printf("%f,", generations.get(gen).getPercentWithStepAtIndex(1, step));
				}
				csvWriter.printf("%f\n", generations.get(gen).getPercentWithStepAtIndex(1, 14));
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
	}
}
