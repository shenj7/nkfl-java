import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExperimentRunner {
	
	
	public static void main(String[] args) {
		//Strategy Parameters
		int strategyLength = 15;

		//Landscape Parameters
		int n = 15;
		int k = 6;
		
		//Evolution Parameters
		String selectionType = "mutation"; //Options are truncation, ranked_linear and ranked_exponential
		int numGenerations = 100;
		int popsPerGeneration = 100;
		int childrenPercentage = 50; //always set to 100 for ranked
		double mutationPercentage = 2;
		
		//Seed parameters
		long seed = 462; //Set Seed
		//long seed = SeededRandom.rnd.nextInt(); //Random seed
		SeededRandom.rnd.setSeed(seed);

		//Data Reporting Parameters
		int incrementCSVoutput = 10;
		String experimentName = "Experiment_" + seed + "_" + selectionType;
		PrintWriter csvWriter;
		File csvFile = new File(experimentName);

		
		//Num Simulation Parameters
		int simulations = 1;
		int starts = 1;
		int runs = 1;
		
		if(selectionType.contains("ranked"))
		{
			childrenPercentage = 100;
		}
		
		//Setup CSV writer
		try {
            csvFile.createNewFile();
        } catch (IOException e) {
            System.err.println("CSV file not created");
        }
		
		try {
			csvWriter = new PrintWriter(csvFile + ".csv");
		} catch (FileNotFoundException e) {
			System.err.println("could not create csv writer");
			e.printStackTrace();
			return;
		}
		
		//Setup comparison strategies
		Map<String, LearningStrategy> comparisonStrategies = new HashMap<String, LearningStrategy>();
		
		//Run Simulation
		for(int simulation = 0; simulation < simulations; simulation++)
		{
			FitnessLandscape landscape = new FitnessLandscape(n, k, SeededRandom.rnd.nextInt());
			for(int start = 0; start < starts; start++)
			{
				int[] startingLocation = NDArrayManager.array1dRandInt(strategyLength, 2);
				for(int run = 0; run < runs; run++)
				{
					String simNum = "" + simulation + "." + start + "." + run;
					
					EvolutionSimulation sim = new EvolutionSimulation(
							landscape,
							popsPerGeneration,
							numGenerations,
							mutationPercentage,
							strategyLength,
							childrenPercentage,
							startingLocation,
							selectionType
							);
					sim.setStringNum(simNum);
					
					sim.runSimulation();
					System.out.println(simNum + " complete");
					
					sim.writeExperimentToCSV(csvWriter, comparisonStrategies, incrementCSVoutput);
					
				}
			}
		}
		
		System.out.println("Data successfully written to " + experimentName + ".csv");
		
		//cleanup
		csvWriter.flush();
        csvWriter.close();
	}
}
