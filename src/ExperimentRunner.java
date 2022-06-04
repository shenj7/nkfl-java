import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExperimentRunner {
	
	
	public static void main(String[] args) {
//		//Strategy Parameters
		int strategyLength = 200;
		int sensitivity = 1;
		int maxSensitivity = 1; //Set this equal to sensitivity for a single sensitiviry run
		int sensitivityInceremet = 1;
		
		//Set to 0 if you don't want to control
		int numberOfWalks = 40;
		LearningStrategy.setNumberOfWalks = numberOfWalks;
		LearningStrategy.usingWait = false;

		//Landscape Parameters
		int n = 15;
		int setk = 14;
		int k = setk;
		int maxk = setk; //Set this equal to k for a single k run
		int kincrement = 1;
		
		//Evolution Parameters
		String selectionType = "mutation"; 
		int numGenerations = 50;
		int popsPerGeneration = 100;
		int childrenPercentage = 50; //always set to 100 for ranked
		double mutationPercentage = 2;
		
		//Seed parameters
		long seed = 500; //Set Seed
//		long seed = SeededRandom.rnd.nextInt(); //Random seed
		SeededRandom.rnd.setSeed(seed);

		//Data Reporting Parameters
		int incrementCSVoutput = 50;
		String experimentName = "Experiment_" + seed + "_" + selectionType + "k=" + k;
		PrintWriter csvWriter;
		File csvFile = new File(experimentName);

		
		//Num Simulation Parameters
		int simulations = 500;
		int starts = 1;
		int runs = 1;
		int strategyRuns = 25;
		
		
		
		
//		//Setup CSV writer
//		try {
//            csvFile.createNewFile();
//        } catch (IOException e) {
//            System.err.println("CSV file not created");
//        }
		
		try {
			csvWriter = new PrintWriter(csvFile + ".csv");
		} catch (FileNotFoundException e) {
			System.err.println("could not create csv writer");
			e.printStackTrace();
			return;
		}
		
		HashMap<String, ArrayList<Step>> strats = new HashMap<String, ArrayList<Step>>();
		
		ArrayList<Step> pureWalk = new ArrayList<Step>();
		for(int i = 0; i < strategyLength; i++)
		{
			pureWalk.add(new WalkStep());
		}
		strats.put("PureWalk", pureWalk);
		
//		ArrayList<Step> alternateLookWalk = new ArrayList<Step>();
//		for(int i = 0; i < strategyLength/2; i++)
//		{
//			alternateLookWalk.add(new LookStep());
//			alternateLookWalk.add(new WalkStep());
//		}
//		strats.put("AlternateLookWalk", alternateLookWalk);
//		
		//New, smarter alternate
		ArrayList<Step> alternateLookWalk = new ArrayList<Step>();
		int looksperwalka = (int) Math.floor((n * 1.0/sensitivity));
		
		while(alternateLookWalk.size() + looksperwalka*2 + 3 < strategyLength)
		{
			alternateLookWalk.add(new WalkStep());
			for(int j = 0; j < looksperwalka; j++)
			{
				alternateLookWalk.add(new LookStep());
			}
			alternateLookWalk.add(new WalkStep());
			for(int j = 0; j < looksperwalka; j++)
			{
				alternateLookWalk.add(new LookStep());
			}
			alternateLookWalk.add(new WalkStep());
			
		}
		strats.put("AlternateLookWalk", alternateLookWalk);
		
		ArrayList<Step> SHC = new ArrayList<Step>();
		int looksperwalk = (int) Math.floor((n * 1.0/sensitivity));
		
		for(int i = 0; i < strategyLength/(looksperwalk + 1); i++)
		{
			for(int j = 0; j < looksperwalk; j++)
			{
				SHC.add(new LookStep());
			}
			SHC.add(new WalkStep());
		}
		strats.put("Steep Hill Climb", SHC);
		
		if(numberOfWalks != 0)
		{
			ArrayList<Step> balanced = new ArrayList<Step>();
			int avgLooksPerWalk = (strategyLength - numberOfWalks) / numberOfWalks;
			
			for(int i = 0; i < numberOfWalks; i++)
			{
				for(int j = 0; j < avgLooksPerWalk; j++)
				{
					balanced.add(new LookStep());
				}
				balanced.add(new WalkStep());
			}
			
			strats.put("Balanced", balanced);
		}
		
		double numSimsTotal = (((maxk-k)/kincrement)+1) * (((maxSensitivity-sensitivity)/sensitivityInceremet)+1) * simulations * starts * runs;
		double numSim = 0;
		
		//Run Simulation
		for(int thisk = k; thisk <= maxk; thisk+=kincrement)
		{
			for(int sense = sensitivity; sense <= maxSensitivity; sense+=sensitivityInceremet)
			{
				LookStep.DEFAULT_NUM_CHECKS = sense;
				for(int simulation = 0; simulation < simulations; simulation++)
				{
					FitnessLandscape landscape = new FitnessLandscape(n, thisk, SeededRandom.rnd.nextInt());
					for(int start = 0; start < starts; start++)
					{
						int[] startingLocation = NDArrayManager.array1dRandInt(n, 2);
						//Setup comparison strategies
//						Map<String, LearningStrategy> comparisonStrategies = new HashMap<String, LearningStrategy>();
//						
//						for(String name : strats.keySet())
//						{
//							LearningStrategy comparison = new LearningStrategy(landscape, strats.get(name), startingLocation);
//							comparisonStrategies.put(name, comparison);
//						}
						
						
						for(int run = 0; run < runs; run++)
						{
							long startTime = System.currentTimeMillis()/1000;
							numSim++;
							String simNum = "" + thisk + "." + sense + "." + simulation + "." + start + "." + run;
							
							EvolutionSimulation sim = new EvolutionSimulation(
									landscape,
									popsPerGeneration,
									numGenerations,
									mutationPercentage,
									strategyLength,
									childrenPercentage,
									startingLocation,
									selectionType,
									strategyRuns
									);
							sim.setStringNum(simNum);
							
							sim.runSimulation();
							long endTime = System.currentTimeMillis()/1000;
							long timeOfLastRun = endTime - startTime;
							
							double simsLeft = numSimsTotal-numSim;
							System.out.println(simNum + " complete, progress = " + 100*numSim/numSimsTotal + "%, estimated time remaning: " + timeOfLastRun*simsLeft/60 + " minutes");
							
							sim.writeExperimentToCSV(csvWriter, strats, incrementCSVoutput);
							
						}
					}
				}
			}
		}
		
		System.out.println("Data successfully written to " + experimentName + ".csv");
		
		//cleanup
		csvWriter.flush();
        csvWriter.close();
	}
}
