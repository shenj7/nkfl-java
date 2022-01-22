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
	int[] startingLocation;
	FitnessLandscape landscape;
	String simNum = "N/A";
	String evolutionType = "N/A";
	
	//Instance variables
	public ArrayList<StrategyGeneration> generations = new ArrayList<StrategyGeneration>();
	//generations ArrayList contains which step we are on
	
	public EvolutionSimulation(FitnessLandscape landscape, int popsPerGeneration, int numGenerations, double mutationPercentage, int strategyLength, double percentNewPerGeneration, int[] startingLocation, String evolutionType)
	{
		this.landscape = landscape;
		this.popsPerGeneration = popsPerGeneration;
		this.numGenerations = numGenerations;
		this.mutationPercentage = mutationPercentage;
		this.childrenPerGeneration = (int) ((double)popsPerGeneration * (double)percentNewPerGeneration / 100);
		this.strategyLength = strategyLength;
		this.startingLocation = startingLocation;
		this.evolutionType = evolutionType;
		setupSimulation();
	}
	
	public void setStringNum(String simNum)
	{
		this.simNum = simNum;
	}
	
	public String getSimNum()
	{
		return simNum;
	}
	
	public void setupSimulation()
	{
		StrategyGeneration gen0 = new StrategyGeneration(landscape, popsPerGeneration, strategyLength, startingLocation);
		generations.add(gen0);
		gen0.runAllStrategies();
	}
	
	public void runSimulation()
	{
		for(int i = generations.size(); i < numGenerations; i++)
		{
			generations.get(generations.size() - 1).sortStrategies();
//			System.out.println("Running gen " + i + " of " + numGenerations + ", average fitness: " + generations.get(generations.size() - 1).averageFitness() + "  " + exgen);
			//Make the next generation
			StrategyGeneration nextGen;
			if(evolutionType.toLowerCase().equals("mutation"))
			{
				nextGen = StrategyGenerationFactory.generateMutation(generations.get(generations.size() - 1), childrenPerGeneration, mutationPercentage);
			}
//			if(evolutionType.toLowerCase().equals("truncation"))
//			{
//				nextGen = StrategyGenerationFactory.generateTruncation(generations.get(generations.size() - 1), childrenPerGeneration, startingLocation);
//			}
//			else if(evolutionType.toLowerCase().equals("ranked_linear"))
//			{
//				nextGen = StrategyGenerationFactory.generateRankedLinear(generations.get(generations.size() - 1), childrenPerGeneration, startingLocation);
//			}
//			else if(evolutionType.toLowerCase().equals("ranked_exponential"))
//			{
//				nextGen = StrategyGenerationFactory.generateRankedExponential(generations.get(generations.size() - 1), childrenPerGeneration, startingLocation);
//			}
			else
			{
				System.err.println("No evolution type chosen");
				nextGen = null;
			}
			generations.add(nextGen);
			//Run the next generation
			nextGen.runAllStrategies();
			System.out.println(nextGen.averageFitness());
		}
//		writeExperimentToCSV();
	}

}
